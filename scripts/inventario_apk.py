#!/usr/bin/env python3
"""Inventário estático de APKs/XAPK do KineMaster.

Lê apenas os arquivos de entrada (somente leitura) e grava um relatório
em JSON e Markdown. Não extrai nem modifica nenhum artefato original.

Uso:
    python3 scripts/inventario_apk.py <diretorio_com_apks> <saida_base>

Exemplo:
    python3 scripts/inventario_apk.py ~/kinemaster-work/extraido/xapk \
        docs/analise/inventarios/apks
"""
import hashlib
import json
import os
import struct
import sys
import zipfile
from collections import Counter


def sha256_arquivo(caminho):
    h = hashlib.sha256()
    with open(caminho, "rb") as f:
        for bloco in iter(lambda: f.read(1 << 20), b""):
            h.update(bloco)
    return h.hexdigest()


def classificar(nome):
    """Classifica uma entrada do APK por categoria técnica."""
    if nome.startswith("META-INF/"):
        return "assinatura_ou_metadados_META-INF"
    if nome.endswith(".dex"):
        return "dex"
    if nome.startswith("lib/") and nome.endswith(".so"):
        return "biblioteca_nativa"
    if nome.startswith("assets/"):
        return "assets"
    if nome.startswith("res/"):
        return "recursos_xml_binarios_ou_midia"
    if nome == "resources.arsc":
        return "tabela_de_recursos"
    if nome == "AndroidManifest.xml":
        return "manifesto_binario"
    if nome.startswith("kotlin/") or nome.startswith("okhttp3/"):
        return "metadados_kotlin_ou_biblioteca"
    return "outros"


def ler_cabecalho_dex(dados):
    """Lê o cabeçalho mínimo de um DEX (magic, versão e contagens).

    Layout do cabeçalho (Android DEX, little-endian): cada tabela tem um campo
    `*_size` seguido de `*_off`. As contagens ficam nos deslocamentos 56, 64, 72,
    80, 88 e 96 (string_ids, type_ids, proto_ids, field_ids, method_ids, class_defs).
    """
    if len(dados) < 112 or dados[:4] != b"dex\n":
        return None
    versao = dados[4:8].decode("ascii", "replace").rstrip("\x00")
    campos = {
        nome: struct.unpack_from("<I", dados, deslocamento)[0]
        for nome, deslocamento in (
            ("string_ids", 56),
            ("type_ids", 64),
            ("proto_ids", 72),
            ("field_ids", 80),
            ("method_ids", 88),
            ("class_defs", 96),
        )
    }
    return {"versao_formato": versao, **campos}


def inventariar_apk(caminho):
    info = {
        "arquivo": os.path.basename(caminho),
        "tamanho_bytes": os.path.getsize(caminho),
        "sha256": sha256_arquivo(caminho),
        "entradas": 0,
        "categorias": {},
        "dex": [],
        "bibliotecas_nativas": [],
        "assinatura_META_INF": [],
        "erro": None,
    }
    try:
        with zipfile.ZipFile(caminho) as z:
            teste = z.testzip()
            info["teste_integridade_zip"] = "ok" if teste is None else f"falha em {teste}"
            contagem = Counter()
            for item in z.infolist():
                if item.is_dir():
                    continue
                info["entradas"] += 1
                cat = classificar(item.filename)
                contagem[cat] += 1
                if cat == "dex":
                    dados = z.read(item.filename)
                    cab = ler_cabecalho_dex(dados)
                    info["dex"].append(
                        {
                            "nome": item.filename,
                            "tamanho_descompactado": item.file_size,
                            "cabecalho": cab,
                            "cabecalho_valido": cab is not None,
                        }
                    )
                elif cat == "biblioteca_nativa":
                    info["bibliotecas_nativas"].append(
                        {
                            "nome": item.filename,
                            "tamanho_descompactado": item.file_size,
                            "tamanho_comprimido": item.compress_size,
                        }
                    )
                elif cat == "assinatura_ou_metadados_META-INF":
                    info["assinatura_META_INF"].append(item.filename)
            info["categorias"] = dict(sorted(contagem.items()))
    except zipfile.BadZipFile as exc:
        info["erro"] = f"arquivo ZIP inválido: {exc}"
    return info


def gerar_markdown(relatorio):
    linhas = [
        "# Inventário estático dos APKs (gerado por `scripts/inventario_apk.py`)",
        "",
        "Todos os dados são observações diretas dos arquivos de entrada.",
        "",
        "| Arquivo | Tamanho (bytes) | SHA-256 | Entradas | DEX | Bibliotecas nativas | Integridade ZIP |",
        "|---|---:|---|---:|---:|---:|---|",
    ]
    for apk in relatorio["apks"]:
        linhas.append(
            "| {arq} | {tam} | `{sha}` | {ent} | {dex} | {nat} | {zip} |".format(
                arq=apk["arquivo"],
                tam=apk["tamanho_bytes"],
                sha=apk["sha256"],
                ent=apk["entradas"],
                dex=len(apk["dex"]),
                nat=len(apk["bibliotecas_nativas"]),
                zip=apk.get("teste_integridade_zip", "n/a"),
            )
        )
    linhas.append("")
    for apk in relatorio["apks"]:
        linhas.append(f"## {apk['arquivo']}")
        linhas.append("")
        if apk["erro"]:
            linhas.append(f"**Erro:** {apk['erro']}")
            linhas.append("")
            continue
        linhas.append("Categorias de entradas:")
        linhas.append("")
        for cat, n in apk["categorias"].items():
            linhas.append(f"- `{cat}`: {n}")
        linhas.append("")
        if apk["dex"]:
            linhas.append("Arquivos DEX (nome, versão de formato, contagens principais):")
            linhas.append("")
            for d in apk["dex"]:
                cab = d["cabecalho"] or {}
                linhas.append(
                    "- `{n}` — formato {v}, classes {c}, métodos {m}, campos {f}, strings {s}".format(
                        n=d["nome"],
                        v=cab.get("versao_formato", "inválido"),
                        c=cab.get("class_defs", "?"),
                        m=cab.get("method_ids", "?"),
                        f=cab.get("field_ids", "?"),
                        s=cab.get("string_ids", "?"),
                    )
                )
            linhas.append("")
        if apk["bibliotecas_nativas"]:
            linhas.append("Bibliotecas nativas (.so):")
            linhas.append("")
            for lib in apk["bibliotecas_nativas"]:
                linhas.append(f"- `{lib['nome']}` ({lib['tamanho_descompactado']} bytes)")
            linhas.append("")
        if apk["assinatura_META_INF"]:
            linhas.append("Arquivos em META-INF:")
            linhas.append("")
            for nome in apk["assinatura_META_INF"]:
                linhas.append(f"- `{nome}`")
            linhas.append("")
    return "\n".join(linhas)


def main():
    if len(sys.argv) != 3:
        print("uso: inventario_apk.py <diretorio_com_apks> <saida_base>", file=sys.stderr)
        return 2
    diretorio, saida = sys.argv[1], sys.argv[2]
    apks = sorted(f for f in os.listdir(diretorio) if f.lower().endswith(".apk"))
    if not apks:
        print(f"erro: nenhum APK encontrado em {diretorio}", file=sys.stderr)
        return 1
    relatorio = {"diretorio": os.path.abspath(diretorio), "apks": []}
    for nome in apks:
        relatorio["apks"].append(inventariar_apk(os.path.join(diretorio, nome)))
    with open(saida + ".json", "w", encoding="utf-8") as f:
        json.dump(relatorio, f, ensure_ascii=False, indent=2)
    with open(saida + ".md", "w", encoding="utf-8") as f:
        f.write(gerar_markdown(relatorio))
    print(f"ok: {len(apks)} APK(s) inventariados -> {saida}.json e {saida}.md")
    return 0


if __name__ == "__main__":
    sys.exit(main())
