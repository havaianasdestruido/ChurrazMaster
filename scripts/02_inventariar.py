#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
02_inventariar.py — Fase 2: inventário integral e rastreável do XAPK.

Produz:
  docs/analise/inventarios/integridade.json     hashes dos volumes e do XAPK
  docs/analise/inventarios/inventario_xapk.json inventário completo por APK/membro
  docs/analise/inventarios/inventario_xapk.md   versão legível do inventário

Regras:
  * Somente leitura em bin/ — os originais nunca são modificados.
  * Todo artefato recebe SHA-256.
  * Denominadores (quantidades totais) são calculados, nunca estimados.

Uso:  python3 scripts/02_inventariar.py [--dir-trabalho DIR]
"""
from __future__ import annotations

import argparse
import hashlib
import io
import json
import os
import struct
import sys
import zipfile
from collections import Counter, defaultdict
from datetime import datetime, timezone

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
NOME_BASE_7Z = "KineMaster+-+AI+Video+Editor_8.2.14.37398.GP_APKPure.7z"
NOME_XAPK = "KineMaster+-+AI+Video+Editor_8.2.14.37398.GP_APKPure.xapk"
DIR_INVENTARIOS = os.path.join(RAIZ, "docs", "analise", "inventarios")

TEXTO_DE_LIMITACAO = (
    "Observação: os identificadores originais do aplicativo são preservados; "
    "as descrições são em português brasileiro."
)


def sha256_de(caminho: str) -> str:
    """SHA-256 em fluxo contínuo, sem carregar o arquivo inteiro em memória."""
    h = hashlib.sha256()
    with open(caminho, "rb") as f:
        for bloco in iter(lambda: f.read(1 << 20), b""):
            h.update(bloco)
    return h.hexdigest()


def sha256_de_bytes(dados: bytes) -> str:
    return hashlib.sha256(dados).hexdigest()


# ---------------------------------------------------------------------------
# ELF: leitura mínima do cabeçalho para ABI/arquitetura e do SONAME/DT_NEEDED
# ---------------------------------------------------------------------------
MAPA_MACHINE = {
    0x03: "i386", 0x28: "ARM", 0x3E: "x86-64", 0xB7: "AArch64", 0x08: "MIPS",
}
MAPA_CLASSE = {1: "ELF32", 2: "ELF64"}


def resumo_elf(dados: bytes) -> dict:
    """Extrai bitola, endianness, tipo, machine e ponto de entrada de um ELF."""
    if len(dados) < 52 or dados[:4] != b"\x7fELF":
        return {"erro": "não é um arquivo ELF válido"}
    classe = MAPA_CLASSE.get(dados[4], f"desconhecida({dados[4]})")
    little = dados[5] == 1
    fim = "<" if little else ">"
    e_type = struct.unpack_from(fim + "H", dados, 16)[0]
    e_machine = struct.unpack_from(fim + "H", dados, 18)[0]
    if classe == "ELF32":
        e_entry = struct.unpack_from(fim + "I", dados, 24)[0]
    else:
        e_entry = struct.unpack_from(fim + "Q", dados, 24)[0]
    return {
        "classe": classe,
        "endianness": "little" if little else "big",
        "tipo": {1: "ET_REL", 2: "ET_EXEC", 3: "ET_DYN"}.get(e_type, str(e_type)),
        "machine": MAPA_MACHINE.get(e_machine, f"desconhecida(0x{e_machine:x})"),
        "ponto_de_entrada": hex(e_entry),
    }


# ---------------------------------------------------------------------------
# Assinatura: detecção de esquemas v1/v2/v3 e certificado via androguard
# ---------------------------------------------------------------------------
def info_assinatura(caminho_apk: str) -> dict:
    """Obtém esquemas de assinatura e certificado do APK (fato observado)."""
    resultado: dict = {"esquemas": [], "certificados": []}
    try:
        from loguru import logger
        logger.remove()  # silencia o log verboso do androguard
    except Exception:
        pass
    try:
        from androguard.core.apk import APK
        apk = APK(caminho_apk)
        if apk.is_signed_v1():
            resultado["esquemas"].append("v1")
        if getattr(apk, "is_signed_v2", lambda: False)():
            resultado["esquemas"].append("v2")
        if getattr(apk, "is_signed_v3", lambda: False)():
            resultado["esquemas"].append("v3")
        for cert in apk.get_certificates():
            resultado["certificados"].append({
                "sha256": cert.sha256.hex() if hasattr(cert.sha256, "hex") else str(cert.sha256),
                "assunto": dict(cert.subject.native),
                "emissor": dict(cert.issuer.native),
                "numero_de_serie": str(cert.serial_number),
                "valido_de": str(cert.not_valid_before),
                "valido_ate": str(cert.not_valid_after),
            })
    except Exception as exc:  # pragma: no cover - dependência opcional
        resultado["erro"] = f"{type(exc).__name__}: {exc}"
    return resultado


# ---------------------------------------------------------------------------
# Inventário de um APK
# ---------------------------------------------------------------------------
def inventariar_apk(caminho: str) -> dict:
    dados = open(caminho, "rb").read()
    info = {
        "arquivo": os.path.basename(caminho),
        "tamanho": len(dados),
        "sha256": sha256_de_bytes(dados),
    }
    z = zipfile.ZipFile(io.BytesIO(dados))
    infos = z.infolist()
    info["quantidade_de_entradas"] = len(infos)
    info["tamanho_descomprimido"] = sum(i.file_size for i in infos)
    info["metodo_de_compressao"] = {
        {0: "armazenado", 8: "deflate"}.get(i.compress_type, str(i.compress_type)): 1
        for i in infos
    }
    info["metodo_de_compressao"] = dict(
        Counter({0: "armazenado", 8: "deflate"}.get(i.compress_type, str(i.compress_type)) for i in infos)
    )

    dex, elf, assinatura_v1 = [], [], []
    for i in infos:
        nome = i.filename
        if nome.endswith(".dex"):
            conteudo = z.read(nome)
            dex.append({"caminho": nome, "tamanho": len(conteudo), "sha256": sha256_de_bytes(conteudo)})
        elif nome.endswith(".so") or "/lib/" in nome:
            conteudo = z.read(nome)
            item = {"caminho": nome, "tamanho": len(conteudo), "sha256": sha256_de_bytes(conteudo)}
            item.update(resumo_elf(conteudo))
            elf.append(item)
        elif nome.startswith("META-INF/") and nome.upper().endswith((".RSA", ".DSA", ".EC", ".SF", ".MF")):
            assinatura_v1.append({"caminho": nome, "tamanho": i.file_size})

    info["dex"] = dex
    info["bibliotecas_nativas"] = elf
    info["arquivos_de_assinatura_v1"] = assinatura_v1
    info["possui_android_manifest"] = "AndroidManifest.xml" in z.namelist()
    info["possui_resources_arsc"] = "resources.arsc" in z.namelist()

    # Contagem por prefixo de diretório de primeiro nível (útil para ver o que cada split traz)
    prefixos = Counter()
    for i in infos:
        partes = i.filename.split("/")
        prefixos[partes[0] + ("/" if len(partes) > 1 else "")] += 1
    info["entradas_por_prefixo"] = dict(prefixos.most_common(15))

    total_dex = sum(d["tamanho"] for d in dex)
    info["total_dex_bytes"] = total_dex
    return info


def main() -> int:
    ap = argparse.ArgumentParser(description="Inventário do XAPK do KineMaster")
    ap.add_argument("--dir-trabalho", default=os.environ.get("DIR_TRABALHO", "/tmp/churraz-trabalho"))
    args = ap.parse_args()

    dir_xapk = os.path.join(args.dir_trabalho, "01_xapk")
    caminho_xapk = os.path.join(dir_xapk, NOME_XAPK)
    if not os.path.exists(caminho_xapk):
        print("[erro] XAPK não extraído. Execute antes: scripts/00_verificar_integridade.sh", file=sys.stderr)
        return 2

    os.makedirs(DIR_INVENTARIOS, exist_ok=True)

    # --- Integridade dos volumes ------------------------------------------
    volumes = []
    for sufixo in ("001", "002", "003", "004"):
        caminho = os.path.join(RAIZ, "bin", f"{NOME_BASE_7Z}.{sufixo}")
        volumes.append({"arquivo": os.path.basename(caminho), "tamanho": os.path.getsize(caminho),
                        "sha256": sha256_de(caminho)})

    import multivolumefile
    h = hashlib.sha256()
    total = 0
    with multivolumefile.open(os.path.join(RAIZ, "bin", NOME_BASE_7Z), mode="rb") as fluxo:
        for bloco in iter(lambda: fluxo.read(1 << 20), b""):
            h.update(bloco)
            total += len(bloco)

    integridade = {
        "gerado_em": datetime.now(timezone.utc).isoformat(),
        "arquivo_7z_reconstruido": {
            "sha256": h.hexdigest(),
            "tamanho": total,
            "observacao": "Concatenação lógica dos volumes .001-.004 (split simples do 7z).",
        },
        "volumes": volumes,
        "xapk": {
            "arquivo": NOME_XAPK,
            "tamanho": os.path.getsize(caminho_xapk),
            "sha256": sha256_de(caminho_xapk),
        },
    }
    with open(os.path.join(DIR_INVENTARIOS, "integridade.json"), "w", encoding="utf-8") as f:
        json.dump(integridade, f, ensure_ascii=False, indent=2)

    # --- Conteúdo do XAPK --------------------------------------------------
    zx = zipfile.ZipFile(caminho_xapk)
    manifesto_xapk = json.loads(zx.read("manifest.json").decode("utf-8"))
    membros = []
    for i in zx.infolist():
        membros.append({"caminho": i.filename, "tamanho": i.file_size,
                        "sha256": sha256_de_bytes(zx.read(i.filename))})

    # --- Inventário por APK ------------------------------------------------
    apks = []
    for membro in membros:
        if not membro["caminho"].endswith(".apk"):
            continue
        temporario = os.path.join(args.dir_trabalho, "02_apks", membro["caminho"])
        if not os.path.exists(temporario):
            os.makedirs(os.path.dirname(temporario), exist_ok=True)
            open(temporario, "wb").write(zx.read(membro["caminho"]))
        item = inventariar_apk(temporario)
        if membro["caminho"] == "com.nexstreaming.app.kinemasterfree.apk":
            item["assinatura"] = info_assinatura(temporario)
        apks.append(item)

    # --- Duplicidade entre APKs (mesmo SHA-256 em splits distintos) --------
    ocorrencias: dict[str, list[str]] = defaultdict(list)
    for apk in apks:
        for chave in ("dex", "bibliotecas_nativas"):
            for arq in apk[chave]:
                ocorrencias[arq["sha256"]].append(f"{apk['arquivo']}:{arq['caminho']}")
    duplicados = {k: v for k, v in ocorrencias.items() if len(v) > 1}

    # --- Totais ------------------------------------------------------------
    todos_dex = [d for apk in apks for d in apk["dex"]]
    todos_elf = [e for apk in apks for e in apk["bibliotecas_nativas"]]
    totais = {
        "apks": len(apks),
        "entradas_totais": sum(a["quantidade_de_entradas"] for a in apks),
        "bytes_descomprimidos_totais": sum(a["tamanho_descomprimido"] for a in apks),
        "arquivos_dex": len(todos_dex),
        "bytes_dex": sum(d["tamanho"] for d in todos_dex),
        "bibliotecas_nativas": len(todos_elf),
        "bytes_nativos": sum(e["tamanho"] for e in todos_elf),
        "abis": sorted({e.get("machine", "?") for e in todos_elf}),
        "assinaturas_por_esquema": {},
    }

    inventario = {
        "gerado_em": integridade["gerado_em"],
        "origem": "bin/ (volumes 7z) -> XAPK -> APKs",
        "aviso": TEXTO_DE_LIMITACAO,
        "manifesto_xapk": manifesto_xapk,
        "membros_do_xapk": membros,
        "apks": apks,
        "duplicados_entre_apks": duplicados,
        "totais": totais,
    }
    with open(os.path.join(DIR_INVENTARIOS, "inventario_xapk.json"), "w", encoding="utf-8") as f:
        json.dump(inventario, f, ensure_ascii=False, indent=2)

    # --- Relatório Markdown ------------------------------------------------
    md = []
    md.append("# Inventário do XAPK do KineMaster\n")
    md.append(f"Gerado em: `{inventario['gerado_em']}`\n")
    md.append(TEXTO_DE_LIMITACAO + "\n")
    md.append("## Identidade do pacote\n")
    for chave in ("package_name", "name", "version_name", "version_code",
                  "min_sdk_version", "target_sdk_version", "total_size"):
        md.append(f"- **{chave}**: `{manifesto_xapk.get(chave)}`")
    md.append(f"- **sha256 do XAPK**: `{integridade['xapk']['sha256']}`")
    md.append(f"- **tamanho do XAPK**: {integridade['xapk']['tamanho']} bytes")
    md.append(f"- **sha256 do 7z reconstruído**: `{integridade['arquivo_7z_reconstruido']['sha256']}`\n")

    md.append("### Volumes originais\n")
    md.append("| arquivo | bytes | SHA-256 |")
    md.append("|---|---:|---|")
    for v in volumes:
        md.append(f"| `{v['arquivo']}` | {v['tamanho']} | `{v['sha256']}` |")
    md.append("")

    md.append("## APKs contidos no XAPK\n")
    md.append("| APK | bytes | descomprimido | entradas | DEX | nativas | SHA-256 |")
    md.append("|---|---:|---:|---:|---:|---:|---|")
    for a in apks:
        md.append(f"| `{a['arquivo']}` | {a['tamanho']} | {a['tamanho_descomprimido']} | "
                  f"{a['quantidade_de_entradas']} | {len(a['dex'])} | "
                  f"{len(a['bibliotecas_nativas'])} | `{a['sha256']}` |")
    md.append("")

    md.append("## Arquivos DEX\n")
    md.append("| APK | caminho | bytes | SHA-256 |")
    md.append("|---|---|---:|---|")
    for a in apks:
        for d in a["dex"]:
            md.append(f"| `{a['arquivo']}` | `{d['caminho']}` | {d['tamanho']} | `{d['sha256']}` |")
    md.append("")

    md.append("## Bibliotecas nativas\n")
    md.append("| APK | caminho | bytes | classe | machine | tipo | SHA-256 |")
    md.append("|---|---|---:|---|---|---|---|")
    for a in apks:
        for e in a["bibliotecas_nativas"]:
            md.append(f"| `{a['arquivo']}` | `{e['caminho']}` | {e['tamanho']} | "
                      f"{e.get('classe')} | {e.get('machine')} | {e.get('tipo')} | `{e['sha256']}` |")
    md.append("")

    base = next((a for a in apks if "kinemasterfree" in a["arquivo"]), None)
    if base and base.get("assinatura"):
        md.append("## Assinatura do APK base\n")
        md.append(f"- Esquemas presentes: **{', '.join(base['assinatura'].get('esquemas', []))}**")
        for c in base["assinatura"].get("certificados", []):
            md.append(f"- Certificado SHA-256: `{c['sha256']}`")
            md.append(f"  - assunto: `{c['assunto']}`")
            md.append(f"  - emissor: `{c['emissor']}`")
            md.append(f"  - número de série: `{c['numero_de_serie']}`")
            md.append(f"  - validade: {c['valido_de']} → {c['valido_ate']}")
        md.append("")

    md.append("## Totais verificáveis\n")
    for chave, valor in totais.items():
        md.append(f"- **{chave}**: `{valor}`")
    md.append(f"- **grupos de arquivos duplicados entre APKs**: `{len(duplicados)}`")
    md.append("")
    if duplicados:
        md.append("### Duplicados\n")
        for sha, locais in duplicados.items():
            md.append(f"- `{sha}` → " + ", ".join(f"`{l}`" for l in locais))
        md.append("")

    with open(os.path.join(DIR_INVENTARIOS, "inventario_xapk.md"), "w", encoding="utf-8") as f:
        f.write("\n".join(md))

    print(f"[ok] integridade.json, inventario_xapk.json e inventario_xapk.md gravados em {DIR_INVENTARIOS}")
    print(f"[ok] {totais}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
