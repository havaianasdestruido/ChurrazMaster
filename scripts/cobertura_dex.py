#!/usr/bin/env python3
"""Contagem independente de classes e métodos dos arquivos DEX de um APK e cobertura do JADX.

Lê os DEX diretamente (formato Android DEX 035/037/038/039/040), sem depender do JADX
nem do androguard, para obter:
  - classes definidas (class_defs) e classes de nível superior;
  - métodos com corpo de código (code_off != 0), isto é, métodos que o decompilador deve produzir;
  - por DEX, quantas classes de nível superior aparecem como arquivo .java no JADX.

Cobertura "por classe de nível superior" = classes de nível superior presentes em sources/ do JADX
divididas pelas classes de nível superior definidas nos DEX. Classes internas (com '$') são
contadas dentro do arquivo da classe externa; por isso são reportadas à parte.

Uso:
    python3 scripts/cobertura_dex.py <apk> <pasta_sources_jadx> <log_jadx_ou_vazio> <saida_base>
    (use "-" no lugar de <log_jadx_ou_vazio> para não cruzar com falhas do JADX)
"""
import json
import os
import re
import struct
import sys
import zipfile
from collections import Counter

ASSINATURAS_VALIDAS = (b"dex\n035\x00", b"dex\n037\x00", b"dex\n038\x00", b"dex\n039\x00", b"dex\n040\x00")


def ler_uleb128(dados, pos):
    resultado, deslocamento = 0, 0
    while True:
        byte = dados[pos]
        pos += 1
        resultado |= (byte & 0x7F) << deslocamento
        if byte & 0x80 == 0:
            return resultado, pos
        deslocamento += 7


def ler_string(dados, off):
    tamanho, pos = ler_uleb128(dados, off)
    # MUTF-8: para nomes de classe/método o subconjunto ASCII/UTF-8 basta para contagem e comparação.
    bruto = dados[pos:]
    fim = bruto.index(b"\x00")
    return bruto[:fim].decode("utf-8", "replace")


def analisar_dex(dados, nome):
    if dados[:8] not in ASSINATURAS_VALIDAS:
        return {"nome": nome, "erro": f"assinatura DEX inválida: {dados[:8]!r}"}
    (
        string_ids_size, string_ids_off,
        type_ids_size, type_ids_off,
        _proto_size, _proto_off,
        _field_size, _field_off,
        method_ids_size, method_ids_off,
        class_defs_size, class_defs_off,
    ) = struct.unpack_from("<12I", dados, 56)
    strings = [
        ler_string(dados, struct.unpack_from("<I", dados, string_ids_off + 4 * i)[0])
        for i in range(string_ids_size)
    ]
    tipos = [strings[struct.unpack_from("<I", dados, type_ids_off + 4 * i)[0]] for i in range(type_ids_size)]
    # method_id: class_idx(u16), proto_idx(u16), name_idx(u32)
    metodos_ids = []
    for i in range(method_ids_size):
        classe_idx, _proto, nome_idx = struct.unpack_from("<HHI", dados, method_ids_off + 8 * i)
        metodos_ids.append((classe_idx, nome_idx))

    classes = []
    membros_por_classe = {}
    metodos_com_codigo = 0
    metodos_declarados = 0
    for i in range(class_defs_size):
        base = class_defs_off + 32 * i
        class_idx, _acc, _sup, _ifs, _src, _ann, class_data_off, _sv = struct.unpack_from("<8I", dados, base)
        nome_classe = tipos[class_idx]  # ex.: Lcom/foo/Bar;
        java = nome_classe[1:-1].replace("/", ".")
        classes.append(java)
        if class_data_off == 0:
            membros_por_classe[java] = {"campos": 0, "metodos": 0, "metodos_com_codigo": 0}
            continue  # classe sem dados (ex.: interface marcador ou stub)
        pos = class_data_off
        campos_estaticos, pos = ler_uleb128(dados, pos)
        campos_instancia, pos = ler_uleb128(dados, pos)
        metodos_diretos, pos = ler_uleb128(dados, pos)
        metodos_virtuais, pos = ler_uleb128(dados, pos)
        membros_por_classe[java] = {
            "campos": campos_estaticos + campos_instancia,
            "metodos": metodos_diretos + metodos_virtuais,
            "metodos_com_codigo": 0,
        }
        for _ in range(campos_estaticos + campos_instancia):
            _, pos = ler_uleb128(dados, pos)  # field_idx_diff
            _, pos = ler_uleb128(dados, pos)  # access_flags
        for _ in range(metodos_diretos + metodos_virtuais):
            _, pos = ler_uleb128(dados, pos)  # method_idx_diff
            _, pos = ler_uleb128(dados, pos)  # access_flags
            code_off, pos = ler_uleb128(dados, pos)
            metodos_declarados += 1
            if code_off != 0:
                metodos_com_codigo += 1
                membros_por_classe[java]["metodos_com_codigo"] += 1
    superiores = sorted({c for c in classes if "$" not in c})
    return {
        "nome": nome,
        "versao_formato": dados[4:7].decode("ascii"),
        "tamanho_bytes": len(dados),
        "class_defs": class_defs_size,
        "classes_definidas_nivel_superior": len(superiores),
        "classes_internas_ou_anonimas": class_defs_size - len(superiores),
        "metodos_declarados": metodos_declarados,
        "metodos_com_codigo": metodos_com_codigo,
        "nomes_nivel_superior": superiores,
        "membros_nivel_superior": {c: membros_por_classe[c] for c in superiores},
        "erro": None,
    }


def classes_no_jadx(pasta_sources):
    """Conjunto de nomes de nível superior presentes como .java em sources/."""
    nomes = set()
    for raiz, _, arquivos in os.walk(pasta_sources):
        for a in arquivos:
            if a.endswith(".java"):
                rel = os.path.relpath(os.path.join(raiz, a), pasta_sources)
                nome = rel[:-5].replace(os.sep, ".")
                # O JADX grava classes do pacote padrão (sem package) em defpackage/.
                if nome.startswith("defpackage."):
                    nome = nome[len("defpackage."):]
                nomes.add(nome)
    return nomes


PADRAO_RENOMEADA = re.compile(r"renamed from:\s*(\S+)\s+reason:")


def aliases_renomeados(pasta_sources):
    """Mapeia nome original (DEX) -> nome gerado pelo JADX, a partir do comentário
    `/* renamed from: <original> reason: ... */` que o próprio JADX grava nos arquivos."""
    mapa = {}
    for raiz, _, arquivos in os.walk(pasta_sources):
        for a in arquivos:
            if not a.endswith(".java"):
                continue
            caminho = os.path.join(raiz, a)
            with open(caminho, encoding="utf-8", errors="replace") as f:
                cabeca = f.read(4096)
            m = PADRAO_RENOMEADA.search(cabeca)
            if m:
                novo = os.path.relpath(caminho, pasta_sources)[:-5].replace(os.sep, ".")
                mapa[m.group(1)] = novo
    return mapa


def falhas_do_log(caminho):
    """Lê os nós com falha do resumo final do JADX (classes e métodos)."""
    if not caminho or caminho == "-" or not os.path.exists(caminho):
        return set(), set()
    classes_falhas, metodos_falhas = set(), set()
    padrao = re.compile(r"^ERROR\s+-\s+(Class|Method):\s+(\S+)")
    padrao_salvar = re.compile(r"Error saving class:\s+(\S+)")
    em_bloco = False
    with open(caminho, encoding="utf-8", errors="replace") as f:
        for linha in f:
            linha = linha.rstrip("\n")
            m_salvar = padrao_salvar.search(linha)
            if m_salvar:
                classes_falhas.add(m_salvar.group(1))
            if "errors occurred in following nodes" in linha:
                em_bloco = True
                continue
            if em_bloco:
                m = padrao.match(linha)
                if m:
                    (classes_falhas if m.group(1) == "Class" else metodos_falhas).add(m.group(2))
                elif linha.startswith("ERROR - finished") or linha.startswith("WARN") or linha.startswith("INFO"):
                    em_bloco = False
    return classes_falhas, metodos_falhas


def main():
    if len(sys.argv) != 5:
        print("uso: cobertura_dex.py <apk> <pasta_sources_jadx> <log_jadx|-> <saida_base>", file=sys.stderr)
        return 2
    apk, sources, log, saida = sys.argv[1:5]
    dexes = []
    with zipfile.ZipFile(apk) as z:
        for item in z.infolist():
            if item.filename.endswith(".dex"):
                dexes.append(analisar_dex(z.read(item.filename), item.filename))
    no_jadx = classes_no_jadx(sources) if os.path.isdir(sources) else set()
    renomeadas = aliases_renomeados(sources) if os.path.isdir(sources) else {}
    falhas_classe, falhas_metodo = falhas_do_log(log)

    todas_superiores = set()
    membros = {}
    for d in dexes:
        if d.get("erro"):
            continue
        todas_superiores.update(d["nomes_nivel_superior"])
        membros.update(d.get("membros_nivel_superior", {}))
    presentes_direto = todas_superiores & no_jadx
    via_alias = {c for c in todas_superiores if c not in no_jadx and c in renomeadas and renomeadas[c] in no_jadx}
    presentes = presentes_direto | via_alias
    faltantes = sorted(todas_superiores - presentes)
    sinteticas_vazias = [c for c in faltantes if membros.get(c, {}).get("campos", 1) == 0 and membros.get(c, {}).get("metodos", 1) == 0]
    ausentes = [c for c in faltantes if c not in set(sinteticas_vazias)]
    ausentes_com_falha = [c for c in ausentes if c in falhas_classe or c.split("$")[0] in falhas_classe]
    ausentes_sem_causa = [c for c in ausentes if c not in set(ausentes_com_falha)]
    # Falhas do JADX mapeadas para a classe de nível superior (remove sufixo interno).
    def classe_de_no(texto):
        """Classe de nível superior de um nó: 'pkg.Classe$Interna.metodo(args):ret' -> 'pkg.Classe'."""
        sem_assinatura = texto.split("(", 1)[0]
        classe = sem_assinatura.rsplit(".", 1)[0] if "." in sem_assinatura and "(" in texto else sem_assinatura
        return classe.split("$")[0]

    superiores_com_falha = {classe_de_no(c) for c in falhas_classe} | {classe_de_no(m) for m in falhas_metodo}
    com_falha_presentes = presentes & superiores_com_falha

    total_metodos_com_codigo = sum(d.get("metodos_com_codigo", 0) for d in dexes)
    resumo = {
        "apk": os.path.basename(apk),
        "dex_encontrados": len(dexes),
        "dex_analisados_com_sucesso": sum(1 for d in dexes if not d.get("erro")),
        "class_defs_total": sum(d.get("class_defs", 0) for d in dexes),
        "classes_nivel_superior_unicas": len(todas_superiores),
        "arquivos_java_jadx_nivel_superior": len(no_jadx),
        "classes_nivel_superior_presentes_no_jadx_direto": len(presentes_direto),
        "classes_nivel_superior_presentes_no_jadx_via_renomeacao": len(via_alias),
        "classes_nivel_superior_presentes_no_jadx": len(presentes),
        "classes_nivel_superior_omitidas_sinteticas_vazias": len(sinteticas_vazias),
        "classes_nivel_superior_ausentes_no_jadx": len(ausentes),
        "ausentes_com_falha_registrada_no_jadx": len(ausentes_com_falha),
        "ausentes_sem_causa_registrada": len(ausentes_sem_causa),
        "ausentes_sem_causa_registrada_exemplos": ausentes_sem_causa[:50],
        "cobertura_nivel_superior_percentual": round(100 * len(presentes) / len(todas_superiores), 4) if todas_superiores else None,
        "cobertura_nivel_superior_excluindo_sinteticas_vazias_percentual": round(100 * len(presentes) / (len(todas_superiores) - len(sinteticas_vazias)), 4) if len(todas_superiores) > len(sinteticas_vazias) else None,
        "classes_superiores_com_falha_registrada_no_jadx": len(com_falha_presentes),
        "nos_com_falha_no_resumo_jadx": {"classes": len(falhas_classe), "metodos": len(falhas_metodo)},
        "metodos_declarados_total": sum(d.get("metodos_declarados", 0) for d in dexes),
        "metodos_com_codigo_total": total_metodos_com_codigo,
        "metodos_com_falha_registrada_no_jadx": len(falhas_metodo),
        "ausentes_exemplos": ausentes[:50],
        "sinteticas_vazias_exemplos": sinteticas_vazias[:50],
        "por_dex": [{k: v for k, v in d.items() if k != "nomes_nivel_superior"} for d in dexes],
    }
    with open(saida + ".json", "w", encoding="utf-8") as f:
        json.dump(resumo, f, ensure_ascii=False, indent=2)
    with open(saida + "-ausentes.txt", "w", encoding="utf-8") as f:
        f.write("# Classes de nível superior presentes nos DEX e ausentes em sources/ do JADX (não sintéticas vazias).\n")
        f.write("# Coluna 2: 'falha_registrada' se o log do JADX registra erro para a classe; 'sem_causa_registrada' caso contrário.\n")
        for c in ausentes:
            f.write(f"{c}\t{'falha_registrada' if c in ausentes_com_falha else 'sem_causa_registrada'}\n")
    with open(saida + "-sinteticas-vazias.txt", "w", encoding="utf-8") as f:
        f.write("\n".join(sinteticas_vazias) + ("\n" if sinteticas_vazias else ""))
    linhas = [
        "# Cobertura DEX x JADX (gerado por scripts/cobertura_dex.py)",
        "",
        f"- APK: `{resumo['apk']}`",
        f"- DEX encontrados: **{resumo['dex_encontrados']}**; analisados sem erro: **{resumo['dex_analisados_com_sucesso']}**",
        f"- class_defs (todas, incluindo internas): **{resumo['class_defs_total']}**",
        f"- Classes de nível superior únicas nos DEX: **{resumo['classes_nivel_superior_unicas']}**",
        f"- Arquivos .java de nível superior gerados pelo JADX: **{resumo['arquivos_java_jadx_nivel_superior']}**",
        f"- Presentes no JADX pelo nome original: **{resumo['classes_nivel_superior_presentes_no_jadx_direto']}**; presentes por renomeação registrada pelo JADX (`renamed from`): **{resumo['classes_nivel_superior_presentes_no_jadx_via_renomeacao']}**",
        f"- Omitidas por serem sintéticas vazias (sem métodos nem campos no DEX): **{resumo['classes_nivel_superior_omitidas_sinteticas_vazias']}** (lista em `{os.path.basename(saida)}-sinteticas-vazias.txt`)",
        f"- Ausentes no JADX: **{resumo['classes_nivel_superior_ausentes_no_jadx']}** — com falha registrada pelo JADX (ex.: `Error saving class`): **{resumo['ausentes_com_falha_registrada_no_jadx']}**; sem causa registrada: **{resumo['ausentes_sem_causa_registrada']}** (lista completa em `{os.path.basename(saida)}-ausentes.txt`)",
        f"- Cobertura por nome (todas as classes de nível superior): {resumo['cobertura_nivel_superior_percentual']}%; excluindo sintéticas vazias: {resumo['cobertura_nivel_superior_excluindo_sinteticas_vazias_percentual']}%",
        f"- Classes de nível superior presentes **com** falha registrada (classe ou método): **{resumo['classes_superiores_com_falha_registrada_no_jadx']}**",
        f"- Nós com falha no resumo do JADX: classes **{len(falhas_classe)}**, métodos **{len(falhas_metodo)}**",
        f"- Métodos declarados: **{resumo['metodos_declarados_total']}**; métodos com corpo de código: **{total_metodos_com_codigo}**",
        "",
        "## Por DEX",
        "",
        "| DEX | Formato | class_defs | Nível superior | Métodos com código |",
        "|---|---|---:|---:|---:|",
    ]
    for d in dexes:
        if d.get("erro"):
            linhas.append(f"| {d['nome']} | ERRO: {d['erro']} | - | - | - |")
        else:
            linhas.append(
                f"| {d['nome']} | {d['versao_formato']} | {d['class_defs']} | {d['classes_definidas_nivel_superior']} | {d['metodos_com_codigo']} |"
            )
    with open(saida + ".md", "w", encoding="utf-8") as f:
        f.write("\n".join(linhas) + "\n")
    print(
        "ok: DEX={d} classes_superiores={s} presentes_no_jadx={p} ausentes={a} metodos_com_codigo={m}".format(
            d=len(dexes), s=len(todas_superiores), p=len(presentes), a=len(ausentes), m=total_metodos_com_codigo
        )
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
