#!/usr/bin/env python3
"""Classifica os erros do log do JADX por tipo, DEX e método/classe.

Não considera a descompilação bem-sucedida apenas porque o JADX terminou. Lista
explicitamente cada linha `ERROR` (exceto a linha final de resumo), agrupando-as
por categoria e identificando o DEX e o método/classe afetados quando o log os informa.

Uso:
    python3 scripts/analisar_log_jadx.py <log.jadx> <saida_base>
Saída: <saida_base>.json (todos os registros) e <saida_base>.md (resumo em pt-BR).
"""
import json
import re
import sys
from collections import Counter

LINHA_ERRO = re.compile(r"^ERROR\s+-\s+(?P<mensagem>.*)$")
DEX = re.compile(r"dex:\s*(?P<dex>[^,\s]+)")
METODO = re.compile(r"in method:\s*(?P<metodo>[^\s(]+)")
RESUMO_FINAL = re.compile(r"finished with errors, count:\s*(\d+)")


def categoria(mensagem):
    if mensagem.startswith("Failed to decode 9-patch"):
        return "recurso_9patch_png_nao_decodificado"
    if "StackOverflow" in mensagem:
        return "estouro_de_pilha_em_passo_do_JADX"
    if "Method code generation error" in mensagem:
        return "falha_geracao_codigo_metodo"
    if "JadxRuntimeException" in mensagem:
        return "excecao_de_runtime_do_JADX"
    if "Class generation" in mensagem or "class generation" in mensagem:
        return "falha_geracao_classe"
    return "nao_classificado_outro_tipo"


CABECALHO_NOS = re.compile(r"(\d+) errors occurred in following nodes:")
NO_FALHO = re.compile(r"^ERROR\s+-\s+\s+(?P<tipo>Class|Method):\s+(?P<nome>\S+)")


def analisar(caminho):
    """Retorna (registros de erro, contagem final, nós com falha listados ao final)."""
    registros = []
    nos_com_falha = []
    resumo_contagem = None
    em_bloco_nos = False
    with open(caminho, encoding="utf-8", errors="replace") as f:
        for linha in f:
            linha = linha.rstrip("\n")
            m_cab = CABECALHO_NOS.search(linha)
            if m_cab:
                em_bloco_nos = True
                continue
            m_no = NO_FALHO.match(linha)
            if em_bloco_nos and m_no:
                nos_com_falha.append({"tipo": m_no.group("tipo"), "nome": m_no.group("nome")})
                continue
            m_resumo = RESUMO_FINAL.search(linha)
            if m_resumo:
                resumo_contagem = int(m_resumo.group(1))
                em_bloco_nos = False
                continue
            m = LINHA_ERRO.match(linha)
            if not m:
                continue
            msg = m.group("mensagem")
            dex = DEX.search(msg)
            metodo = METODO.search(msg)
            registros.append(
                {
                    "categoria": categoria(msg),
                    "dex": dex.group("dex") if dex else None,
                    "metodo_ou_recurso": metodo.group("metodo") if metodo else None,
                    "mensagem": msg[:400],
                }
            )
    return registros, resumo_contagem, nos_com_falha


def gerar_markdown(registros, resumo_contagem, origem, nos):
    por_cat = Counter(r["categoria"] for r in registros)
    por_dex = Counter(r["dex"] or "desconhecido" for r in registros)
    por_tipo_no = Counter(n["tipo"] for n in nos)
    linhas = [
        "# Erros do JADX (gerado por scripts/analisar_log_jadx.py)",
        "",
        f"Log analisado: `{origem}`",
        "",
        f"- Contagem informada pelo JADX (\"N errors occurred in following nodes\"): **{resumo_contagem}**",
        f"- Nós com falha listados no resumo final: **{len(nos)}** (classes: {por_tipo_no.get('Class', 0)}, métodos: {por_tipo_no.get('Method', 0)})",
        f"- Linhas `ERROR` individuais no log (exceto o resumo): **{len(registros)}**",
        "- Fonte primária de falha: a lista de nós do resumo final. As linhas `ERROR` individuais trazem causas,",
        "  mas não correspondem 1:1 aos nós listados.",
        "",
        "## Nós com falha (resumo final do JADX)",
        "",
    ]
    for n in nos:
        linhas.append(f"- {n['tipo']}: `{n['nome']}`")
    linhas += ["", "## Linhas de erro individuais por categoria", ""]
    for cat, n in por_cat.most_common():
        linhas.append(f"- `{cat}`: {n}")
    linhas += ["", "## Linhas de erro individuais por DEX de origem", ""]
    for dex, n in por_dex.most_common():
        linhas.append(f"- `{dex}`: {n}")
    linhas += ["", "## Mensagens das linhas de erro individuais", ""]
    for r in registros:
        alvo = r["metodo_ou_recurso"] or "-"
        linhas.append(f"- [{r['categoria']}] dex=`{r['dex'] or '-'}` alvo=`{alvo}` — {r['mensagem'][:200]}")
    return "\n".join(linhas) + "\n"


def main():
    if len(sys.argv) != 3:
        print("uso: analisar_log_jadx.py <log.jadx> <saida_base>", file=sys.stderr)
        return 2
    log, saida = sys.argv[1], sys.argv[2]
    registros, resumo, nos = analisar(log)
    with open(saida + ".json", "w", encoding="utf-8") as f:
        json.dump(
            {"log": log, "contagem_jadx": resumo, "nos_com_falha": nos, "registros": registros},
            f,
            ensure_ascii=False,
            indent=2,
        )
    with open(saida + ".md", "w", encoding="utf-8") as f:
        f.write(gerar_markdown(registros, resumo, log, nos))
    print(f"ok: {len(nos)} nós com falha, {len(registros)} linhas de erro -> {saida}.md")
    return 0


if __name__ == "__main__":
    sys.exit(main())
