#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
11_esquema_room.py — Fase 5: reconstrução do esquema de bancos Room a partir do código.

Não é preciso executar o aplicativo para conhecer o esquema: as anotações
`@Database(entities = {...})`, `@Entity(tableName = "...")`, `@PrimaryKey`,
`@ColumnInfo` e os campos declarados permanecem no bytecode. Este script as lê
da saída do jadx e monta o esquema em JSON e Markdown.

Limitação: nomes de coluna só são conhecidos quando `@ColumnInfo(name=...)` existe;
caso contrário, o nome da coluna é o nome do campo (regra do Room) — marcado como inferido.

Uso:
  python3 scripts/11_esquema_room.py --dir-jadx /tmp/churraz-trabalho/04_jadx_base/sources
"""
from __future__ import annotations

import argparse
import json
import os
import re
import sys
from datetime import datetime, timezone

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DIR_DESTINO = os.path.join(RAIZ, "docs", "analise")

RE_CLASSE_DO_BANCO = re.compile(r"class\s+(\w+)\s+extends\s+RoomDatabase")
RE_ENTIDADES = re.compile(r"entities\s*=\s*\{(?P<lista>[^}]*)\}")
RE_VERSAO = re.compile(r"version\s*=\s*(\d+)")
RE_ENTIDADE = re.compile(r'@Entity\((?P<args>[^)]*)\)')
RE_TABELA = re.compile(r'tableName\s*=\s*"([^"]+)"')
RE_CAMPO = re.compile(r'^\s*(?:private|public|protected)\s+(?:final\s+)?([\w$.<>\[\], ?]+?)\s+(\w+)\s*(?:=|;)',
                      re.MULTILINE)
RE_METODO = re.compile(r'^\s*(?:public|private|protected)\s+[\w$.<>\[\], ?]+\s+\w+\s*\(')
RE_COLUMNINFO = re.compile(r'@ColumnInfo\([^)]*name\s*=\s*"([^"]+)"')
RE_QUERY = re.compile(r'@Query\("(?P<sql>[^"]+)"\)')


def ler(caminho: str) -> str:
    return open(caminho, "r", encoding="utf-8", errors="replace").read()


def analisar_bancos(dir_jadx: str) -> list[dict]:
    bancos = []
    for raiz, _, arquivos in os.walk(dir_jadx):
        for nome in arquivos:
            if not nome.endswith(".java"):
                continue
            caminho = os.path.join(raiz, nome)
            texto = ler(caminho)
            if "@Database(" not in texto:
                continue
            pos = texto.find("@Database(")
            if pos < 0:
                continue
            # extrai os argumentos respeitando parênteses aninhados
            inicio = pos + len("@Database(")
            profundidade, fim = 1, inicio
            while fim < len(texto) and profundidade > 0:
                if texto[fim] == "(":
                    profundidade += 1
                elif texto[fim] == ")":
                    profundidade -= 1
                fim += 1
            args = texto[inicio:fim - 1]
            # o nome da classe vem depois de outras anotações (ex.: @Metadata do Kotlin)
            achado = RE_CLASSE_DO_BANCO.search(texto[fim:fim + 12000])
            if not achado:
                continue
            lista = RE_ENTIDADES.search(args)
            versao = RE_VERSAO.search(args)
            entidades = []
            if lista:
                for item in lista.group("lista").split(","):
                    item = item.strip()
                    if not item:
                        continue
                    entidades.append(item.replace(".class", "").strip())
            bancos.append({
                "arquivo": os.path.relpath(caminho, dir_jadx),
                "classe_do_banco": achado.group(1),
                "versao_do_esquema": int(versao.group(1)) if versao else None,
                "entidades_declaradas": entidades,
                "entidades": [],
            })
    # Resolve cada entidade em seu arquivo
    indice = {}
    for raiz, _, arquivos in os.walk(dir_jadx):
        for nome in arquivos:
            if nome.endswith(".java"):
                indice[nome[:-5]] = os.path.join(raiz, nome)
    for banco in bancos:
        for simples in banco["entidades_declaradas"]:
            caminho = indice.get(simples)
            if not caminho:
                banco["entidades"].append({"classe": simples, "erro": "arquivo não encontrado"})
                continue
            texto = ler(caminho)
            cabecalho = RE_ENTIDADE.search(texto)
            tabela = None
            if cabecalho:
                m = RE_TABELA.search(cabecalho.group("args"))
                tabela = m.group(1) if m else None
            # campos declarados após o cabeçalho da anotação
            inicio = cabecalho.end() if cabecalho else 0
            corpo = texto[inicio:]
            campos, chaves = [], []
            # Em arquivos do jadx os campos vêm antes dos métodos; paramos no primeiro
            # cabeçalho de método para não capturar variáveis locais.
            linhas_corpo = corpo.splitlines()
            for linha in linhas_corpo:
                if RE_METODO.match(linha):
                    break
                m = RE_CAMPO.match(linha)
                if not m:
                    continue
                tipo, nome_campo = m.group(1).strip(), m.group(2)
                if nome_campo in ("serialVersionUID",) or tipo in ("return", "new", "throw"):
                    continue
                campos.append({"campo": nome_campo, "tipo": tipo})
            if "@PrimaryKey" in corpo:
                # o campo imediatamente após a anotação
                idx = corpo.find("@PrimaryKey")
                seguinte = RE_CAMPO.search(corpo[idx:])
                if seguinte:
                    chaves.append(seguinte.group(2))
            for m in RE_COLUMNINFO.finditer(corpo):
                for campo in campos:
                    if campo["campo"] in m.group(0):
                        campo["coluna"] = m.group(1)
            banco["entidades"].append({
                "classe": simples,
                "arquivo": os.path.relpath(caminho, dir_jadx),
                "tabela": tabela or simples,
                "tabela_inferida": tabela is None,
                "chave_primaria": chaves,
                "campos": campos,
            })
    return bancos


def analisar_consultas(dir_jadx: str) -> list[dict]:
    """Extrai SQL embutido em @Query para documentar as operações por tabela."""
    consultas = []
    for raiz, _, arquivos in os.walk(dir_jadx):
        for nome in arquivos:
            if not nome.endswith(".java"):
                continue
            caminho = os.path.join(raiz, nome)
            texto = ler(caminho)
            if "@Query(" not in texto:
                continue
            for m in RE_QUERY.finditer(texto):
                consultas.append({
                    "arquivo": os.path.relpath(caminho, dir_jadx),
                    "sql": m.group("sql")[:400],
                })
    return consultas


def main() -> int:
    ap = argparse.ArgumentParser(description="Esquema dos bancos Room")
    ap.add_argument("--dir-jadx", default="/tmp/churraz-trabalho/04_jadx_base/sources")
    args = ap.parse_args()
    if not os.path.isdir(args.dir_jadx):
        print(f"[erro] diretório inexistente: {args.dir_jadx}", file=sys.stderr)
        return 2

    bancos = analisar_bancos(args.dir_jadx)
    consultas = analisar_consultas(args.dir_jadx)
    total_entidades = sum(len(b["entidades"]) for b in bancos)
    total_campos = sum(len(e.get("campos", [])) for b in bancos for e in b["entidades"])
    total_consultas = len(consultas)

    dados = {
        "gerado_em": datetime.now(timezone.utc).isoformat(),
        "total_bancos": len(bancos),
        "total_entidades": total_entidades,
        "total_campos": total_campos,
        "total_consultas_query": total_consultas,
        "bancos": bancos,
        "consultas": consultas,
        "limitacao": ("Esquema reconstruído por leitura estática das anotações. Colunas sem "
                      "@ColumnInfo têm o nome do campo (regra do Room) — marcadas como inferidas. "
                      "Versões reais de migração e DDL não foram executadas."),
    }
    os.makedirs(os.path.join(DIR_DESTINO, "inventarios"), exist_ok=True)
    json.dump(dados, open(os.path.join(DIR_DESTINO, "inventarios", "esquema_room.json"), "w",
                          encoding="utf-8"), ensure_ascii=False, indent=2)

    md = ["# Esquema dos bancos Room (reconstruído estaticamente)\n",
          f"Gerado em `{dados['gerado_em']}` por `scripts/11_esquema_room.py`.\n",
          f"- **Bancos (`@Database`)**: {len(bancos)}",
          f"- **Entidades (`@Entity`)**: {total_entidades}",
          f"- **Campos declarados**: {total_campos}",
          f"- **Consultas `@Query` encontradas**: {total_consultas}\n",
          "> Limitação: nomes de tabela/coluna vêm das anotações; quando `tableName` ou",
          "> `@ColumnInfo` estão ausentes, o valor é o nome da classe/campo (regra do Room) —",
          "> sinalizado como inferido. Nenhum banco foi aberto em execução.\n"]
    for banco in sorted(bancos, key=lambda b: b["classe_do_banco"]):
        md.append(f"## `{banco['classe_do_banco']}` (versão {banco['versao_do_esquema']})\n")
        md.append(f"Arquivo: `{banco['arquivo']}`\n")
        md.append("| tabela | classe | chave primária | campos |")
        md.append("|---|---|---|---:|")
        for e in banco["entidades"]:
            if "erro" in e:
                md.append(f"| — | `{e['classe']}` | — | 0 |")
                continue
            tabela = e["tabela"] + (" *(inferida)*" if e["tabela_inferida"] else "")
            chave = ", ".join(e["chave_primaria"]) or "—"
            md.append(f"| `{tabela}` | `{e['classe']}` | `{chave}` | {len(e['campos'])} |")
        md.append("")
        # detalha os campos das entidades do app (exclui dependências de bibliotecas)
        for e in banco["entidades"]:
            if "erro" in e or not e["campos"]:
                continue
            md.append(f"<details><summary><code>{e['tabela']}</code> — {len(e['campos'])} campos</summary>\n")
            md.append("| campo | tipo | coluna |")
            md.append("|---|---|---|")
            for c in e["campos"]:
                md.append(f"| `{c['campo']}` | `{c['tipo']}` | `{c.get('coluna', '(mesmo nome)')}` |")
            md.append("\n</details>\n")

    if consultas:
        md.append("## Amostra de `@Query` (SQL embutido)\n")
        md.append("```sql")
        for c in consultas[:40]:
            md.append(f"-- {c['arquivo']}\n{c['sql']}\n")
        md.append("```\n")

    open(os.path.join(DIR_DESTINO, "relatorios", "esquema_banco_de_dados.md"), "w",
         encoding="utf-8").write("\n".join(md))
    print(f"[ok] {len(bancos)} bancos, {total_entidades} entidades, {total_campos} campos, "
          f"{total_consultas} consultas @Query")
    return 0


if __name__ == "__main__":
    sys.exit(main())
