#!/usr/bin/env python3
"""Inventário de ativos (assets/, res/, lib/ e raiz) de um APK, com identificação de formato por assinatura.

Os formatos são identificados pelos primeiros bytes (magic numbers) e não pela extensão.
Arquivos de formato desconhecido são reportados como tal, sem especulação sobre seu conteúdo.

Saída:
  <saida_base>.md   resumo por formato e por diretório (pt-BR)
  <saida_base>.csv  um registro por entrada (caminho, tamanho, formato identificado, SHA-256)

Uso:
    python3 scripts/inventario_ativos.py <apk> <saida_base>
"""
import csv
import hashlib
import os
import sys
import zipfile
from collections import Counter, defaultdict

# (prefixo de bytes, deslocamento, rótulo)
ASSINATURAS = [
    (b"SQLite format 3\x00", 0, "sqlite3_banco_de_dados"),
    (b"PK\x03\x04", 0, "zip"),
    (b"\x89PNG\r\n\x1a\n", 0, "png"),
    (b"\xff\xd8\xff", 0, "jpeg"),
    (b"RIFF", 0, "riff_webp_ou_wav"),
    (b"\x00\x01\x00\x00\x00", 0, "truetype_ttf"),
    (b"OTTO", 0, "opentype_otf"),
    (b"wOFF", 0, "woff"),
    (b"wOF2", 0, "woff2"),
    (b"dex\n", 0, "dex"),
    (b"\x7fELF", 0, "elf"),
    (b"\x1f\x8b", 0, "gzip"),
    (b"\x28\xb5\x2f\xfd", 0, "zstd"),
    (b"\x00\x00\x00\x18ftyp", 0, "mp4_iso_base_media"),
    (b"\x00\x00\x00\x20ftyp", 0, "mp4_iso_base_media"),
    (b"ID3", 0, "mp3_id3"),
    (b"OggS", 0, "ogg"),
    (b"\x00asm", 0, "wasm"),
    (b"\xef\xbb\xbf", 0, "texto_utf8_bom"),
    (b"<?xml", 0, "xml"),
    (b"{", 0, "json_ou_texto_chave_valor"),
    (b"[", 0, "json_ou_texto_lista"),
    (b"\x0a\x00\x00\x00", 0, "possivel_protobuf_ou_binario"),
    (b"-----BEGIN", 0, "pem_chave_ou_certificado"),
    (b"\x03\x00", 0, "android_resources_arsc_ou_xml_binario"),
    (b"\x02\x00\x0c\x00", 0, "android_xml_binario"),
    (b"TFL3", 4, "tflite_flatbuffer"),
    (b"\x00\x00\x00\x00", 0, "zeros_iniciais"),
]


def identificar(dados):
    if not dados:
        return "vazio"
    for prefixo, desl, rotulo in ASSINATURAS:
        if dados[desl: desl + len(prefixo)] == prefixo:
            return rotulo
    if all(32 <= b < 127 or b in (9, 10, 13) for b in dados[:256]):
        return "texto_ascii_nao_identificado"
    return "binario_desconhecido"


def categoria_pasta(nome):
    partes = nome.split("/")
    if len(partes) == 1:
        return "raiz"
    return partes[0]


def main():
    if len(sys.argv) != 3:
        print("uso: inventario_ativos.py <apk> <saida_base>", file=sys.stderr)
        return 2
    apk, saida = sys.argv[1], sys.argv[2]
    registros = []
    with zipfile.ZipFile(apk) as z:
        for info in z.infolist():
            if info.is_dir():
                continue
            if info.filename.endswith(".dex") or info.filename.startswith("META-INF/"):
                # DEX e META-INF têm inventário próprio (ver cobertura_dex.py e manifesto-base).
                continue
            dados = z.read(info.filename)
            registros.append(
                {
                    "caminho": info.filename,
                    "tamanho": info.file_size,
                    "formato": identificar(dados[:64]),
                    "sha256": hashlib.sha256(dados).hexdigest(),
                    "pasta": categoria_pasta(info.filename),
                }
            )
    por_formato = Counter(r["formato"] for r in registros)
    por_pasta = defaultdict(Counter)
    for r in registros:
        por_pasta[r["pasta"]][r["formato"]] += 1
    with open(saida + ".csv", "w", encoding="utf-8", newline="") as f:
        escritor = csv.DictWriter(f, fieldnames=["caminho", "tamanho", "formato", "sha256", "pasta"])
        escritor.writeheader()
        for r in sorted(registros, key=lambda x: x["caminho"]):
            escritor.writerow(r)
    linhas = [
        "# Inventário de ativos do APK (gerado por scripts/inventario_ativos.py)",
        "",
        f"APK: `{os.path.basename(apk)}`. Entradas inventariadas (excluindo `.dex` e `META-INF/`): **{len(registros)}**.",
        "Formato identificado por assinatura binária (não por extensão). `desconhecido` = não identificado.",
        "",
        "## Por formato",
        "",
    ]
    for formato, n in por_formato.most_common():
        linhas.append(f"- `{formato}`: {n}")
    linhas += ["", "## Por pasta de primeiro nível", ""]
    for pasta in sorted(por_pasta):
        total = sum(por_pasta[pasta].values())
        detalhes = ", ".join(f"{f}: {n}" for f, n in por_pasta[pasta].most_common(6))
        linhas.append(f"- `{pasta}` ({total}): {detalhes}")
    with open(saida + ".md", "w", encoding="utf-8") as f:
        f.write("\n".join(linhas) + "\n")
    print(f"ok: {len(registros)} entradas -> {saida}.md e .csv")
    return 0


if __name__ == "__main__":
    sys.exit(main())
