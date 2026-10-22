#!/usr/bin/env python3
"""Inventário do ZIP aninhado `assets/kmassets.zip` de um APK (sem extrair para o disco).

Coleta fatos observáveis:
- contagem por categoria (dois primeiros níveis após `kmassets/`) e por extensão;
- formato identificado por assinatura binária para arquivos sem extensão;
- conjunto de chaves de primeiro nível observado nos `_info.json`;
- conjunto de elementos raiz e de filhos diretos observados nos `kedl.xml`.

Não interpreta semanticamente os formatos: o significado de cada chave/tag é inferência
e deve ser tratado como tal nos relatórios.

Saídas:
  <saida_base>.md   resumo em pt-BR
  <saida_base>.csv  um registro por entrada do ZIP aninhado (caminho, tamanho, SHA-256, formato)

Uso:
    python3 scripts/inventario_kmassets.py <apk> <saida_base>
"""
import collections
import csv
import hashlib
import io
import json
import os
import sys
import xml.etree.ElementTree as ET
import zipfile

ALVO = "assets/kmassets.zip"


def identificar(dados):
    if dados[:8] == b"\x89PNG\r\n\x1a\n":
        return "png"
    if dados[:3] == b"\xff\xd8\xff":
        return "jpeg"
    if dados[:4] == b"\x00\x00\x00\x00" or not dados:
        return "vazio_ou_zeros"
    if dados[:1] == b"<":
        return "xml_ou_svg"
    if all(32 <= b < 127 or b in (9, 10, 13) for b in dados[:256]):
        return "texto_ascii"
    return "binario_nao_identificado"


def main():
    if len(sys.argv) != 3:
        print("uso: inventario_kmassets.py <apk> <saida_base>", file=sys.stderr)
        return 2
    apk, saida = sys.argv[1], sys.argv[2]
    with zipfile.ZipFile(apk) as z:
        dados_zip = z.read(ALVO)
    sha_zip = hashlib.sha256(dados_zip).hexdigest()
    registros = []
    chaves_info = collections.Counter()
    raizes_kedl = collections.Counter()
    filhos_kedl = collections.Counter()
    erros_xml = 0
    erros_json = 0
    categorias = collections.Counter()
    extensoes = collections.Counter()
    formatos = collections.Counter()
    with zipfile.ZipFile(io.BytesIO(dados_zip)) as k:
        teste = k.testzip()
        for info in k.infolist():
            if info.is_dir():
                continue
            nome = info.filename
            partes = nome.split("/")
            categorias["/".join(partes[1:3]) if len(partes) > 3 else "/".join(partes[1:])] += 1
            base = partes[-1]
            extensao = base.rsplit(".", 1)[-1].lower() if "." in base else "(sem extensão)"
            extensoes[extensao] += 1
            dados = k.read(nome)
            formato = identificar(dados[:64])
            formatos[formato] += 1
            if base == "_info.json":
                try:
                    chaves_info.update(json.loads(dados.decode("utf-8-sig")).keys())
                except (ValueError, AttributeError):
                    erros_json += 1
            if base.endswith(".xml") and base == "kedl.xml":
                try:
                    raiz = ET.fromstring(dados)
                    raizes_kedl[raiz.tag] += 1
                    for filho in raiz:
                        filhos_kedl[filho.tag] += 1
                except ET.ParseError:
                    erros_xml += 1
            registros.append({
                "caminho": nome,
                "tamanho": info.file_size,
                "sha256": hashlib.sha256(dados).hexdigest(),
                "formato": formato,
            })
    with open(saida + ".csv", "w", encoding="utf-8", newline="") as f:
        escritor = csv.DictWriter(f, fieldnames=["caminho", "tamanho", "sha256", "formato"])
        escritor.writeheader()
        escritor.writerows(registros)
    linhas = [
        "# Inventário de `assets/kmassets.zip` (gerado por scripts/inventario_kmassets.py)",
        "",
        f"- ZIP aninhado: `{ALVO}` ({len(dados_zip)} B), SHA-256 `{sha_zip}`",
        f"- Teste de integridade do ZIP aninhado (`testzip`): {'OK' if teste is None else 'falhou: ' + teste}",
        f"- Entradas de arquivo: **{len(registros)}** (denominador para as contagens abaixo)",
        f"- Tamanho descompactado total: {sum(r['tamanho'] for r in registros)} B",
        f"- Categorias (dois níveis após `kmassets/`): {len(categorias)}",
        "",
        "## Por extensão",
        "",
    ]
    linhas += [f"- `{e}`: {n}" for e, n in extensoes.most_common()]
    linhas += ["", "## Formato identificado por assinatura (todas as entradas)", ""]
    linhas += [f"- `{f}`: {n}" for f, n in formatos.most_common()]
    linhas += ["", "## Categorias", ""]
    linhas += [f"- `{c}`: {n}" for c, n in categorias.most_common()]
    linhas += ["", "## Chaves de primeiro nível observadas em `_info.json`", ""]
    linhas += [f"- `{c}`: presente em {n} arquivos" for c, n in chaves_info.most_common()]
    linhas.append(f"- Arquivos `_info.json` que não são JSON válido: {erros_json}")
    linhas += ["", "## `kedl.xml`", ""]
    linhas += [f"- Elemento raiz `<{t}>`: {n} arquivos" for t, n in raizes_kedl.most_common()]
    linhas += [f"- Filho direto `<{t}>`: {n} ocorrências" for t, n in filhos_kedl.most_common(30)]
    linhas.append(f"- `kedl.xml` que não são XML válido: {erros_xml}")
    with open(saida + ".md", "w", encoding="utf-8") as f:
        f.write("\n".join(linhas) + "\n")
    print(f"ok: {len(registros)} entradas -> {os.path.basename(saida)}.md/.csv")
    return 0


if __name__ == "__main__":
    sys.exit(main())
