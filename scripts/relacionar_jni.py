#!/usr/bin/env python3
"""Relaciona métodos `native` do código Java (JADX) com funções JNI exportadas pelas bibliotecas .so.

Método:
  1. Procura em sources/ do JADX declarações `native` e chamadas `System.loadLibrary("x")`/`System.load(...)`.
  2. Para cada método nativo (classe FQCN + nome), calcula o nome JNI esperado pela convenção
     oficial de mangling (Java_<classe mangled>_<método mangled>; ver JNI spec, "Resolving Native Method Names").
  3. Verifica se o símbolo existe na lista de exportações de cada biblioteca (inventario_nativo.json).

Limitações: métodos `native` com sobrecarga usam o nome longo (com assinatura), que não é verificado aqui;
o script reporta esses casos como "sobrecarga_nao_verificada". Não executa nada.

Uso:
    python3 scripts/relacionar_jni.py <pasta_sources_jadx> <inventario_nativo.json> <saida_base>
"""
import json
import os
import re
import sys
from bisect import bisect_right
from collections import defaultdict

# Declaração real de método nativo: modificadores + `native` no início da linha (evita falsos positivos
# como identificadores ou strings contendo a palavra "native").
DECL_NATIVE = re.compile(
    r"^\s*(?:(?:public|private|protected|static|final|synchronized|abstract|strictfp)\s+)*native\s+"
    r"[\w.$<>\[\]?,\s]+?\s+([A-Za-z_$][\w$]*)\s*\(",
    re.M,
)
# Chamada de carregamento: literal entre aspas (estático) ou variável/expressão (dinâmico).
LOAD_LIB = re.compile(r"System\.(loadLibrary|load)\(\s*(\"[^\"]*\"|[^)]*)\)")
PACOTE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.M)
CLASSE = re.compile(r"\b(?:class|interface|enum)\s+([A-Za-z_$][\w$]*)")


def mangle(texto):
    """Mangling JNI: '_' -> '_1', ';' -> '_2', '[' -> '_3', '/' e '.' -> '_', demais não ASCII -> _0xxxx."""
    saida = []
    for ch in texto:
        if ch == "_":
            saida.append("_1")
        elif ch == ";":
            saida.append("_2")
        elif ch == "[":
            saida.append("_3")
        elif ch in "./":
            saida.append("_")
        elif ch.isascii() and (ch.isalnum()):
            saida.append(ch)
        else:
            saida.append("_0%04x" % ord(ch))
    return "".join(saida)


def classes_envolventes(codigo):
    """Percorre o código ignorando strings/comentários e devolve, para cada posição de
    declaração `class|interface|enum`, o nome da classe que a contém (pilha de escopos por chaves)."""
    declaracoes = {}  # posição do '{' -> nome da classe
    pilha = []  # (nome, profundidade)
    profundidade = 0
    pendente = None
    i, n = 0, len(codigo)
    marcas = []  # (posicao_inicio_declaracao, nome_envolvente)
    nome_pendente_pos = None
    padrao_decl = re.compile(r"\b(?:class|interface|enum)\s+([A-Za-z_$][\w$]*)")
    proximas = {}
    for m in padrao_decl.finditer(codigo):
        proximas[m.start()] = m.group(1)
    while i < n:
        c = codigo[i]
        if c == "/" and i + 1 < n and codigo[i + 1] == "/":
            fim_linha = codigo.find("\n", i)
            i = n if fim_linha < 0 else fim_linha
            continue
        if c == "/" and i + 1 < n and codigo[i + 1] == "*":
            fim_bloco = codigo.find("*/", i + 2)
            i = n if fim_bloco < 0 else fim_bloco + 2
            continue
        if c == '"':
            i += 1
            while i < n and codigo[i] != '"':
                i += 2 if codigo[i] == "\\" else 1
            i += 1
            continue
        if c == "'":
            i += 1
            while i < n and codigo[i] != "'":
                i += 2 if codigo[i] == "\\" else 1
            i += 1
            continue
        if i in proximas:
            pendente = proximas[i]
        if c == "{":
            profundidade += 1
            if pendente is not None:
                pilha.append((pendente, profundidade))
                pendente = None
        elif c == "}":
            if pilha and pilha[-1][1] == profundidade:
                pilha.pop()
            profundidade -= 1
        marcas.append((i, pilha[-1][0] if pilha else None))
        i += 1
    return marcas


def varrer_fontes(pasta):
    """Retorna (lista de métodos nativos com FQCN, mapa arquivo->bibliotecas carregadas)."""
    nativos = []  # (fqcn, metodo, arquivo)
    carregamentos = defaultdict(set)
    for raiz, _, arquivos in os.walk(pasta):
        for a in arquivos:
            if not a.endswith(".java"):
                continue
            caminho = os.path.join(raiz, a)
            with open(caminho, encoding="utf-8", errors="replace") as f:
                codigo = f.read()
            if "native" not in codigo and "loadLibrary" not in codigo and "System.load" not in codigo:
                continue
            rel = os.path.relpath(caminho, pasta)
            m_pacote = PACOTE.search(codigo)
            pacote = m_pacote.group(1) if m_pacote else ""
            classe_topo = os.path.basename(rel)[:-5]
            for m in LOAD_LIB.finditer(codigo):
                argumento = m.group(2).strip()
                if argumento.startswith('"') and argumento.endswith('"'):
                    carregamentos[rel].add("literal:" + argumento.strip('"'))
                else:
                    carregamentos[rel].add("dinamico:" + argumento)
            declaracoes = list(DECL_NATIVE.finditer(codigo))
            if not declaracoes:
                continue
            envolventes = dict(classes_envolventes(codigo)) if False else None
            escopo = {}
            for pos, nome in classes_envolventes(codigo):
                escopo[pos] = nome
            # Para cada declaração nativa, procura o escopo na posição do início do match.
            ultimo = None
            posicoes_ordenadas = sorted(escopo)
            for m in declaracoes:
                alvo = m.start(1)
                # escopo mais próximo anterior à posição do nome do método
                indice = bisect_right(posicoes_ordenadas, alvo) - 1
                nome_envolvente = escopo[posicoes_ordenadas[indice]] if indice >= 0 else None
                classe = nome_envolvente or classe_topo
                fqcn = ".".join(p for p in [pacote, classe_topo] if p)
                if classe != classe_topo:
                    fqcn = fqcn + "$" + classe
                nativos.append((fqcn, m.group(1), rel))
    return nativos, carregamentos


def strings_das_bibliotecas(pasta_so):
    """Conjunto de strings terminadas em NUL presentes em cada .so (ex.: tabelas JNINativeMethod)."""
    mapa = {}
    if not pasta_so or not os.path.isdir(pasta_so):
        return mapa
    for raiz, _, arquivos in os.walk(pasta_so):
        for a in arquivos:
            if a.endswith(".so"):
                with open(os.path.join(raiz, a), "rb") as f:
                    dados = f.read()
                mapa[a] = dados
    return mapa


def main():
    if len(sys.argv) not in (4, 5):
        print("uso: relacionar_jni.py <pasta_sources_jadx> <inventario_nativo.json> <saida_base> [pasta_so]", file=sys.stderr)
        return 2
    pasta, inventario, saida = sys.argv[1:4]
    pasta_so = sys.argv[4] if len(sys.argv) == 5 else None
    bibliotecas_bytes = strings_das_bibliotecas(pasta_so)
    with open(inventario, encoding="utf-8") as f:
        inv = json.load(f)
    exportacoes = {}
    for lib in inv["bibliotecas"]:
        nome = os.path.basename(lib["arquivo"])
        exportacoes[nome] = set(lib.get("funcoes_jni_exportadas", []))
        if lib.get("exporta_JNI_OnLoad"):
            exportacoes[nome].add("JNI_OnLoad")
    todas_exportadas = set().union(*exportacoes.values()) if exportacoes else set()

    nativos, carregamentos = varrer_fontes(pasta)
    registros = []
    for fqcn, metodo, arquivo in nativos:
        esperado = "Java_" + mangle(fqcn) + "_" + mangle(metodo)
        lib_encontrada = [n for n, ex in exportacoes.items() if esperado in ex]
        # Sobrecarga: o símbolo longo inclui "__" + assinatura; detecta prefixo curto.
        prefixo = [s for s in todas_exportadas if s.startswith(esperado + "__")]
        registro_string = None
        if lib_encontrada:
            status = "verificado_por_simbolo_curto"
        elif prefixo:
            status = "sobrecarga_nao_verificada"
        else:
            # Sem símbolo exportado: procura o nome do método como string NUL-terminada nas bibliotecas.
            # Presença do nome é INDÍCIO (ex.: tabela de registro dinâmico via RegisterNatives), não prova.
            # Ausência em todas as bibliotecas do APK indica implementação nativa fora deste pacote
            # (ou código morto); isso precisa de verificação adicional.
            for nome_lib, dados in bibliotecas_bytes.items():
                alvo = b"\x00" + metodo.encode("utf-8") + b"\x00"
                pos = dados.find(alvo)
                if pos >= 0:
                    proximo = dados[pos + len(alvo): pos + len(alvo) + 160].split(b"\x00", 1)[0]
                    assinatura = proximo.decode("ascii", "replace") if proximo.startswith(b"(") and b")" in proximo else None
                    registro_string = {"biblioteca": nome_lib, "assinatura_vizinha": assinatura}
                    break
            if registro_string:
                status = "nome_presente_como_string_sem_simbolo_exportado"
            else:
                status = "nome_ausente_das_bibliotecas_do_apk"
        registros.append(
            {
                "fqcn": fqcn,
                "metodo": metodo,
                "arquivo_jadx": arquivo,
                "simbolo_jni_esperado": esperado,
                "biblioteca": lib_encontrada[0] if lib_encontrada else (registro_string or {}).get("biblioteca"),
                "assinatura_vizinha_na_string_table": (registro_string or {}).get("assinatura_vizinha"),
                "status": status,
            }
        )
    contagem = defaultdict(int)
    for r in registros:
        contagem[r["status"]] += 1
    resumo = {
        "metodos_nativos_declarados_no_jadx": len(registros),
        "por_status": dict(contagem),
        "jni_exportados_total": len(todas_exportadas),
        "jni_exportados_por_biblioteca": {k: len(v) for k, v in exportacoes.items()},
        "carregamentos_loadLibrary": {k: sorted(v) for k, v in carregamentos.items()},
        "registros": registros,
    }
    with open(saida + ".json", "w", encoding="utf-8") as f:
        json.dump(resumo, f, ensure_ascii=False, indent=2)
    linhas = [
        "# Relação entre métodos `native` (Java) e funções JNI exportadas (gerado por scripts/relacionar_jni.py)",
        "",
        f"- Métodos `native` declarados no código gerado pelo JADX: **{len(registros)}**",
    ]
    for k, v in sorted(contagem.items()):
        linhas.append(f"- `{k}`: **{v}**")
    linhas += [
        f"- Símbolos `Java_*`/JNI exportados por todas as bibliotecas: **{len(todas_exportadas)}**",
        "",
        "## Carregamentos de bibliotecas (`System.loadLibrary`/`System.load`) no código Java",
        "",
    ]
    for arq, libs in sorted(carregamentos.items()):
        linhas.append(f"- `{arq}` → {', '.join(sorted(libs))}")
    linhas += ["", "## Métodos nativos verificados por símbolo", ""]
    for r in registros:
        if r["status"] == "verificado_por_simbolo_curto":
            linhas.append(f"- `{r['fqcn']}.{r['metodo']}` → `{r['simbolo_jni_esperado']}` em `{r['biblioteca']}`")
    with open(saida + ".md", "w", encoding="utf-8") as f:
        f.write("\n".join(linhas) + "\n")
    print(f"ok: {len(registros)} métodos nativos; status: {dict(contagem)} -> {saida}.md")
    return 0


if __name__ == "__main__":
    sys.exit(main())
