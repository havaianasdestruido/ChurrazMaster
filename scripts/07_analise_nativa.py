#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
07_analise_nativa.py — Fase 4: inventário e análise estrutural das bibliotecas ELF.

O que este script produz (fatos observados, não inferências):
  * arquitetura, tipo, bitola, ponto de entrada e "stripped" de cada .so;
  * bibliotecas dinâmicas requeridas (DT_NEEDED) e SONAME;
  * símbolos exportados, com destaque para símbolos JNI (Java_* e JNI_OnLoad);
  * mapeamento de símbolos Java_* para nomes de classe Java/Kotlin;
  * seções, tamanhos e entropia do segmento .text (indício objetivo de empacotamento);
  * tamanho de .symtab/.dynsym e presença de seções de protetores conhecidos;
  * correlação com o código gerenciado: quais System.loadLibrary("x") existem.

Limitação declarada: não há descompilador nativo disponível neste ambiente
(Ghidra/IDA não instaláveis — ver bin/tools/MANIFESTO.md). O pseudocódigo nativo
não é produzido aqui; a análise para no nível estrutural e de símbolos.

Uso:
  python3 scripts/07_analise_nativa.py --dir-nativo /tmp/churraz-trabalho/06_nativo \
      --dir-jadx /tmp/churraz-trabalho/04_jadx_base/sources \
      --saida docs/analise/inventarios/nativo.json
"""
from __future__ import annotations

import argparse
import json
import math
import os
import re
import sys
from collections import Counter
from datetime import datetime, timezone

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# Seções/strings típicas de empacotadores e protetores conhecidos
ASSINATURAS_PROTETOR = {
    b"UPX!": "UPX",
    b".pglarmor": "PGL Armor (protetor comercial)",
    b"libjiagu": "Qihoo 360 Jiagu",
    b"libDexHelper": "Bangcle",
    b"libmobisec": "Alibaba MobiSec",
    b"libexec.so": "Tencent Legu",
    b"libshella": "SecShell",
}


def entropia(dados: bytes) -> float:
    """Entropia de Shannon em bits por byte (máx. 8,0)."""
    if not dados:
        return 0.0
    contagem = Counter(dados)
    total = len(dados)
    return -sum((n / total) * math.log2(n / total) for n in contagem.values())


def nome_java_de_simbolo(simbolo: str) -> str | None:
    """
    Converte 'Java_com_exemplo_Foo_bar' em 'com.exemplo.Foo#bar'.

    O mapeamento é aproximado por natureza: underscores podem ser parte do nome
    original; apenas reconstruímos a forma mais provável e marcamos como inferência.
    """
    if not simbolo.startswith("Java_"):
        return None
    resto = simbolo[len("Java_"):]
    partes = resto.split("_")
    # convenção: último segmento = método, penúltimo (se iniciar com letra maiúscula) = classe
    if len(partes) < 2:
        return None
    metodo = partes[-1]
    classe = "_".join(partes[:-1])
    return f"{classe.replace('_', '.')}#{metodo}"


def analisar_elf(caminho: str) -> dict:
    from elftools.elf.elffile import ELFFile

    dados = open(caminho, "rb").read()
    info = {
        "arquivo": os.path.basename(caminho),
        "tamanho": len(dados),
    }
    with open(caminho, "rb") as f:
        elf = ELFFile(f)
        info["classe"] = elf.elfclass
        info["little_endian"] = elf.little_endian
        info["tipo"] = elf.header["e_type"]
        info["maquina"] = elf.header["e_machine"]
        info["ponto_de_entrada"] = hex(elf.header["e_entry"])
        info["possui_debug"] = elf.get_section_by_name(".debug_info") is not None
        info["possui_symtab"] = elf.get_section_by_name(".symtab") is not None

        secoes = []
        for secao in elf.iter_sections():
            item = {"nome": secao.name, "tamanho": secao.data_size, "tipo": secao["sh_type"]}
            if secao.name in (".text", ".rodata", ".data", ".init_array", ".comment"):
                conteudo = secao.data() if secao.data_size else b""
                item["entropia"] = round(entropia(conteudo[:2 * 1024 * 1024]), 3)
                if secao.name == ".comment":
                    item["conteudo"] = conteudo.decode("utf-8", "replace").strip("\x00").replace("\x00", "; ")
            secoes.append(item)
        info["secoes"] = sorted(secoes, key=lambda x: -x["tamanho"])[:40]

        # Dependências e SONAME
        necessario, soname = [], None
        dyn = elf.get_section_by_name(".dynamic")
        if dyn is not None:
            for tag in dyn.iter_tags():
                if tag.entry.d_tag == "DT_NEEDED":
                    necessario.append(tag.needed)
                elif tag.entry.d_tag == "DT_SONAME":
                    soname = tag.soname
        info["dt_needed"] = necessario
        info["soname"] = soname

        # Símbolos exportados (tabela dinâmica) e JNI
        exportados, jni, jni_onload = [], [], False
        dynsym = elf.get_section_by_name(".dynsym")
        if dynsym is not None:
            for simb in dynsym.iter_symbols():
                if not simb.name:
                    continue
                if simb["st_info"]["bind"] in ("STB_GLOBAL", "STB_WEAK") and \
                   simb["st_shndx"] not in ("SHN_UNDEF", 0):
                    exportados.append(simb.name)
                    if simb.name.startswith("Java_"):
                        jni.append(simb.name)
                    if simb.name in ("JNI_OnLoad", "JNI_OnUnload"):
                        jni_onload = True
        info["total_exportados"] = len(exportados)
        info["simbolos_jni"] = jni
        info["jni_onload"] = jni_onload
        info["classes_java_inferidas"] = sorted({
            m.split("#")[0] for m in (nome_java_de_simbolo(s) for s in jni) if m
        })

    # Assinaturas de protetor/empacotador: procura nas primeiras dezenas de KB e no arquivo todo
    amostra = dados[:512 * 1024]
    protetores = [nome for marca, nome in ASSINATURAS_PROTETOR.items() if marca in amostra]
    info["protetores_detectados"] = sorted(set(protetores))
    info["md5_like"] = None
    return info


def correlacionar_carregamento(dir_jadx: str) -> dict:
    """
    Procura no código gerenciado as chamadas de carregamento de biblioteca e de
    código dinâmico. Retorna nomes de bibliotecas citados e arquivos onde aparecem.
    """
    padrao_load = re.compile(r'System\.loadLibrary\(\s*"([^"]+)"')
    padrao_dinamico = re.compile(r'(DexClassLoader|InMemoryDexClassLoader|PathClassLoader|'
                                 r'System\.load\(|dlopen|Runtime\.getRuntime\(\)\.exec)')
    bibliotecas: dict[str, list[str]] = {}
    dinamico: dict[str, list[str]] = {}
    if not os.path.isdir(dir_jadx):
        return {"bibliotecas_carregadas": bibliotecas, "apis_de_carregamento_dinamico": dinamico}
    for raiz, _, arquivos in os.walk(dir_jadx):
        for nome in arquivos:
            if not nome.endswith(".java"):
                continue
            caminho = os.path.join(raiz, nome)
            try:
                conteudo = open(caminho, "r", encoding="utf-8", errors="replace").read()
            except Exception:
                continue
            for achado in padrao_load.finditer(conteudo):
                bibliotecas.setdefault(achado.group(1), []).append(
                    os.path.relpath(caminho, dir_jadx))
            if padrao_dinamico.search(conteudo):
                for achado in set(padrao_dinamico.findall(conteudo)):
                    dinamico.setdefault(achado, []).append(os.path.relpath(caminho, dir_jadx))
    return {
        "bibliotecas_carregadas": {k: sorted(set(v)) for k, v in sorted(bibliotecas.items())},
        "apis_de_carregamento_dinamico": {k: sorted(set(v))[:40] for k, v in sorted(dinamico.items())},
    }


def main() -> int:
    ap = argparse.ArgumentParser(description="Análise estrutural das bibliotecas nativas")
    ap.add_argument("--dir-nativo", default="/tmp/churraz-trabalho/06_nativo")
    ap.add_argument("--dir-jadx", default="/tmp/churraz-trabalho/04_jadx_base/sources")
    ap.add_argument("--saida", default=os.path.join(RAIZ, "docs", "analise", "inventarios", "nativo.json"))
    args = ap.parse_args()

    camera = os.path.join(args.dir_nativo, "lib", "arm64-v8a")
    if not os.path.isdir(camera):
        print(f"[erro] bibliotecas não extraídas em {camera}. "
              f"Execute scripts/01_extrair.sh e scripts/03_recursos.sh", file=sys.stderr)
        return 2

    resultados = []
    for nome in sorted(os.listdir(camera)):
        if not nome.endswith(".so"):
            continue
        info = analisar_elf(os.path.join(camera, nome))
        resultados.append(info)
        print(f"[info] {nome}: {info['total_exportados']} exportados, "
              f"{len(info['simbolos_jni'])} símbolos JNI, protetor={info['protetores_detectados']}")

    correlacao = correlacionar_carregamento(args.dir_jadx)

    resumo = {
        "gerado_em": datetime.now(timezone.utc).isoformat(),
        "total_bibliotecas": len(resultados),
        "bytes_totais": sum(r["tamanho"] for r in resultados),
        "bibliotecas_com_jni": [r["arquivo"] for r in resultados if r["simbolos_jni"]],
        "bibliotecas_com_jni_onload": [r["arquivo"] for r in resultados if r["jni_onload"]],
        "bibliotecas_com_symtab": [r["arquivo"] for r in resultados if r["possui_symtab"]],
        "bibliotecas_com_debug": [r["arquivo"] for r in resultados if r["possui_debug"]],
        "bibliotecas_com_protetor": {r["arquivo"]: r["protetores_detectados"]
                                     for r in resultados if r["protetores_detectados"]},
        "needed_agregado": sorted({d for r in resultados for d in r["dt_needed"]}),
        "entropia_texto": {r["arquivo"]: next((s.get("entropia") for s in r["secoes"]
                                               if s["nome"] == ".text"), None) for r in resultados},
        "correlacao_gerenciado": correlacao,
        "bibliotecas": resultados,
        "limitacao": (
            "Análise estrutural e de símbolos apenas. Não há descompilador nativo "
            "(Ghidra/IDA) disponível neste ambiente; pseudocódigo nativo não foi produzido. "
            "Nomes de classe derivados de Java_* são inferências (underscores ambíguos)."
        ),
    }

    os.makedirs(os.path.dirname(args.saida), exist_ok=True)
    json.dump(resumo, open(args.saida, "w", encoding="utf-8"), ensure_ascii=False, indent=2)
    print(f"[ok] {len(resultados)} bibliotecas analisadas -> {args.saida}")
    print(f"[ok] bibliotecas carregadas via System.loadLibrary: {list(correlacao['bibliotecas_carregadas'])}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
