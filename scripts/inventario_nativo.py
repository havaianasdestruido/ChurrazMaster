#!/usr/bin/env python3
"""Inventário estático de bibliotecas nativas ELF (.so) extraídas do XAPK.

Para cada biblioteca registra: SHA-256, tamanho, entropia de Shannon, ABI/máquina,
SONAME, dependências NEEDED, símbolos exportados e importados, funções JNI
(`Java_*`), presença de `JNI_OnLoad`, presença de `.symtab` (símbolos de depuração)
e seções. Não executa as bibliotecas.

Uso:
    python3 scripts/inventario_nativo.py <diretorio_com_so> <saida_base>
"""
import hashlib
import json
import math
import os
import sys
from collections import Counter

from elftools.elf.elffile import ELFFile
from elftools.common.exceptions import ELFError


def sha256(caminho):
    h = hashlib.sha256()
    with open(caminho, "rb") as f:
        for bloco in iter(lambda: f.read(1 << 20), b""):
            h.update(bloco)
    return h.hexdigest()


def entropia(dados):
    """Entropia de Shannon em bits por byte (0 a 8)."""
    contagem = Counter(dados)
    total = len(dados)
    return round(-sum((c / total) * math.log2(c / total) for c in contagem.values()), 4)


def analisar(caminho):
    info = {
        "arquivo": os.path.relpath(caminho),
        "tamanho_bytes": os.path.getsize(caminho),
        "sha256": sha256(caminho),
        "erro": None,
    }
    with open(caminho, "rb") as f:
        bruto = f.read()
    info["entropia_shannon"] = entropia(bruto)
    try:
        with open(caminho, "rb") as f:
            elf = ELFFile(f)
            info["classe"] = elf.elfclass
            info["endianness"] = elf.little_endian and "little" or "big"
            info["maquina"] = elf.header["e_machine"]
            info["tipo"] = elf.header["e_type"]
            info["secoes"] = [s.name for s in elf.iter_sections()]
            info["tem_symtab_depuracao"] = elf.get_section_by_name(".symtab") is not None
            info["tem_debug_info"] = elf.has_dwarf_info()
            dinamica = elf.get_section_by_name(".dynamic")
            needed, soname = [], None
            if dinamica is not None:
                for tag in dinamica.iter_tags():
                    if tag.entry.d_tag == "DT_NEEDED":
                        needed.append(tag.needed)
                    elif tag.entry.d_tag == "DT_SONAME":
                        soname = tag.soname
            info["soname"] = soname
            info["needed"] = needed
            exportados, importados = [], []
            simbolos = elf.get_section_by_name(".dynsym")
            if simbolos is not None:
                for sim in simbolos.iter_symbols():
                    nome = sim.name
                    if not nome:
                        continue
                    if sim["st_shndx"] == "SHN_UNDEF":
                        importados.append(nome)
                    else:
                        exportados.append(nome)
            info["simbolos_dinamicos_exportados"] = len(exportados)
            info["simbolos_dinamicos_importados"] = len(importados)
            info["funcoes_jni_exportadas"] = sorted(n for n in exportados if n.startswith("Java_"))
            info["exporta_JNI_OnLoad"] = "JNI_OnLoad" in exportados
            info["exporta_JNI_OnUnload"] = "JNI_OnUnload" in exportados
            info["importa_libc_essenciais"] = sorted(
                n for n in importados if n in ("dlopen", "dlsym", "mmap", "mprotect", "ptrace", "__android_log_print")
            )
    except (ELFError, ValueError, OSError) as exc:
        info["erro"] = f"falha ao analisar ELF: {exc}"
    return info


def gerar_markdown(relatorio):
    linhas = [
        "# Inventário das bibliotecas nativas (gerado por `scripts/inventario_nativo.py`)",
        "",
        "Observações estáticas via `pyelftools`. Nenhuma biblioteca foi executada.",
        "",
        "| Biblioteca | Tamanho | Entropia | Export. | Import. | JNI `Java_*` | JNI_OnLoad | Símbolos de depuração | NEEDED |",
        "|---|---:|---:|---:|---:|---:|:-:|:-:|---|",
    ]
    for lib in relatorio["bibliotecas"]:
        if lib["erro"]:
            linhas.append(f"| {os.path.basename(lib['arquivo'])} | {lib['tamanho_bytes']} | - | - | - | - | - | - | ERRO: {lib['erro']} |")
            continue
        linhas.append(
            "| {n} | {t} | {e} | {ex} | {im} | {jni} | {jl} | {dbg} | {need} |".format(
                n=os.path.basename(lib["arquivo"]),
                t=lib["tamanho_bytes"],
                e=lib["entropia_shannon"],
                ex=lib["simbolos_dinamicos_exportados"],
                im=lib["simbolos_dinamicos_importados"],
                jni=len(lib["funcoes_jni_exportadas"]),
                jl="sim" if lib["exporta_JNI_OnLoad"] else "não",
                dbg="sim" if lib["tem_symtab_depuracao"] else "não",
                need=", ".join(lib["needed"]) or "-",
            )
        )
    linhas.append("")
    linhas.append(f"Total de bibliotecas: {len(relatorio['bibliotecas'])}; com erro de análise: {sum(1 for l in relatorio['bibliotecas'] if l['erro'])}.")
    return "\n".join(linhas) + "\n"


def main():
    if len(sys.argv) != 3:
        print("uso: inventario_nativo.py <diretorio_com_so> <saida_base>", file=sys.stderr)
        return 2
    diretorio, saida = sys.argv[1], sys.argv[2]
    arquivos = []
    for raiz, _, nomes in os.walk(diretorio):
        for n in nomes:
            if n.endswith(".so"):
                arquivos.append(os.path.join(raiz, n))
    arquivos.sort()
    if not arquivos:
        print(f"erro: nenhuma biblioteca .so em {diretorio}", file=sys.stderr)
        return 1
    relatorio = {"bibliotecas": [analisar(a) for a in arquivos]}
    with open(saida + ".json", "w", encoding="utf-8") as f:
        json.dump(relatorio, f, ensure_ascii=False, indent=2)
    with open(saida + ".md", "w", encoding="utf-8") as f:
        f.write(gerar_markdown(relatorio))
    print(f"ok: {len(arquivos)} biblioteca(s) analisadas -> {saida}.json e {saida}.md")
    return 0


if __name__ == "__main__":
    sys.exit(main())
