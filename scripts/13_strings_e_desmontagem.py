#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
13_strings_e_desmontagem.py — Fase 4 (complemento): strings e desmontagem pontual.

Sem descompilador nativo (Ghidra) é possível, ainda assim, produzir evidência útil:
  * strings imprimíveis por biblioteca, classificadas (dependências JNI, formatos de
    arquivo, URLs, mensagens de erro, símbolos de proteção);
  * busca dirigida por nomes de formato proprietário (`.nef`, `.lua`, `.kmproject`);
  * desmontagem (capstone/ARM64) de `JNI_OnLoad` e detecção de chamadas indiretas
    compatíveis com `RegisterNatives`.

O que este script **não** faz: não gera pseudocódigo, não reconstrói assinaturas e não
atribui semântica a funções além do que os símbolos e as strings permitem afirmar.

Uso:
  python3 scripts/13_strings_e_desmontagem.py --dir-nativo /tmp/churraz-trabalho/06_nativo
"""
from __future__ import annotations

import argparse
import json
import os
import re
import sys
from collections import Counter
from datetime import datetime, timezone

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DIR_INVENTARIOS = os.path.join(RAIZ, "docs", "analise", "inventarios")
DIR_RELATORIOS = os.path.join(RAIZ, "docs", "analise", "relatorios")

RE_IMPRIMIVEL = re.compile(rb"[\x20-\x7e]{5,200}")
INTERESSES = {
    "formatos_proprietarios": re.compile(r"\.(nef|lua|kmproject|kine)$|\.nef|\.lua\b", re.I),
    "urls": re.compile(r"^https?://"),
    "mensagens_de_erro": re.compile(r"(fail|error|invalid|cannot|unable|denied)", re.I),
    "protecao": re.compile(r"(protection|license|licence|check4km|integrity|signature|tamper)", re.I),
}


def strings_de(caminho: str) -> list[str]:
    dados = open(caminho, "rb").read()
    return [m.group(0).decode("ascii", "replace") for m in RE_IMPRIMIVEL.finditer(dados)]


def desmontar_jni_onload(caminho: str, maximo: int = 160) -> dict:
    """Localiza JNI_OnLoad e desmonta as primeiras instruções com capstone (ARM64)."""
    from elftools.elf.elffile import ELFFile
    try:
        from capstone import Cs, CS_ARCH_ARM64, CS_MODE_ARM
    except Exception as exc:  # pragma: no cover
        return {"erro": f"capstone indisponível: {exc}"}

    with open(caminho, "rb") as f:
        elf = ELFFile(f)
        dynsym = elf.get_section_by_name(".dynsym")
        if dynsym is None:
            return {"erro": "sem .dynsym"}
        alvo = None
        for simb in dynsym.iter_symbols():
            if simb.name == "JNI_OnLoad" and simb["st_value"]:
                alvo = simb
                break
        if alvo is None:
            return {"presente": False}

        endereco = alvo["st_value"]
        tamanho = alvo["st_size"] or 512
        # converte endereço virtual em offset de arquivo via segmentos PT_LOAD
        deslocamento = None
        for seg in elf.iter_segments():
            if seg["p_type"] == "PT_LOAD" and seg["p_vaddr"] <= endereco < seg["p_vaddr"] + seg["p_filesz"]:
                deslocamento = seg["p_offset"] + (endereco - seg["p_vaddr"])
                break
        if deslocamento is None:
            return {"presente": True, "erro": "endereço não mapeado em PT_LOAD"}
        codigo = open(caminho, "rb").read()[deslocamento:deslocamento + tamanho]

    md = Cs(CS_ARCH_ARM64, CS_MODE_ARM)
    md.detail = True
    instrucoes = []
    chamadas_indiretas = 0
    for insn in md.disasm(codigo, endereco):
        instrucoes.append(f"{insn.address:#x}: {insn.mnemonic}\t{insn.op_str}")
        if insn.mnemonic in ("blr", "br"):
            chamadas_indiretas += 1
        if len(instrucoes) >= maximo:
            break
    return {
        "presente": True,
        "endereco": hex(endereco),
        "instrucoes_desmontadas": len(instrucoes),
        "chamadas_indiretas_blr_br": chamadas_indiretas,
        "amostra": instrucoes[:60],
        "interpretacao": ("Chamadas indiretas são compatíveis com o padrão "
                          "JNIEnv->RegisterNatives, mas **não** provam isso sem análise de fluxo."),
    }


def main() -> int:
    ap = argparse.ArgumentParser(description="Strings e desmontagem das bibliotecas nativas")
    ap.add_argument("--dir-nativo", default="/tmp/churraz-trabalho/06_nativo")
    args = ap.parse_args()

    camera = os.path.join(args.dir_nativo, "lib", "arm64-v8a")
    if not os.path.isdir(camera):
        print(f"[erro] bibliotecas ausentes em {camera}", file=sys.stderr)
        return 2

    resultados = []
    for nome in sorted(os.listdir(camera)):
        if not nome.endswith(".so"):
            continue
        caminho = os.path.join(camera, nome)
        todas = strings_de(caminho)
        classificadas = {chave: sorted({s for s in todas if regex.search(s)})[:40]
                         for chave, regex in INTERESSES.items()}
        item = {
            "biblioteca": nome,
            "tamanho": os.path.getsize(caminho),
            "total_strings": len(todas),
            "classificacao": classificadas,
            "desmontagem_jni_onload": desmontar_jni_onload(caminho),
        }
        resultados.append(item)
        print(f"[info] {nome}: {len(todas)} strings; "
              f"proteção={len(classificadas['protecao'])}; "
              f"formatos={classificadas['formatos_proprietarios'][:3]}")

    dados = {
        "gerado_em": datetime.now(timezone.utc).isoformat(),
        "total_bibliotecas": len(resultados),
        "com_jni_onload_desmontado": [r["biblioteca"] for r in resultados
                                      if r["desmontagem_jni_onload"].get("presente")],
        "bibliotecas": resultados,
        "limitacao": ("Strings e desmontagem parcial não substituem pseudocódigo. "
                      "Nenhuma semântica de função foi inferida além dos símbolos e strings."),
    }
    os.makedirs(DIR_INVENTARIOS, exist_ok=True)
    json.dump(dados, open(os.path.join(DIR_INVENTARIOS, "strings_nativas.json"), "w", encoding="utf-8"),
              ensure_ascii=False, indent=2)

    # --- relatório ---
    md = ["# Strings e desmontagem das bibliotecas nativas\n",
          f"Gerado em `{dados['gerado_em']}` por `scripts/13_strings_e_desmontagem.py`.\n",
          "> Complemento da Fase 4. Sem descompilador nativo, o limite honesto é: **strings**, "
          "**símbolos** e **desmontagem pontual**. Nenhum pseudocódigo foi produzido.\n"]

    md.append("## Quem manipula os formatos proprietários\n")
    md.append("| biblioteca | strings de formato |")
    md.append("|---|---|")
    for r in resultados:
        fmt = r["classificacao"]["formatos_proprietarios"]
        if fmt:
            md.append(f"| `{r['biblioteca']}` | " + ", ".join(f"`{s}`" for s in fmt[:8]) + " |")
    md.append("")
    md.append("**Fato observado:** `libnexeditorsdk.so` é a única biblioteca que referencia `.nef` e `.lua`,")
    md.append("o que a torna a candidata direta a leitora desses formatos. **Inferência**, não prova:")
    md.append("a string pode vir de uma tabela de nomes/erros sem código de parsing associado.\n")

    md.append("## Indícios de proteção/licenciamento no motor\n")
    for r in resultados:
        prot = r["classificacao"]["protecao"]
        if prot:
            md.append(f"### `{r['biblioteca']}`\n")
            md.append("```")
            for s in prot[:20]:
                md.append(s)
            md.append("```\n")
    md.append("**Fato observado:** existem símbolos JNI com nomes autoexplicativos como")
    md.append("`NexEditor_setPacakgeName4Protection` (grafia original, com erro de digitação) e")
    md.append("`NexEditor_check4km`. **Conclusão:** o motor nativo recebe o nome do pacote e possui uma")
    md.append("rotina de verificação. Isso está documentado aqui apenas como característica do")
    md.append("funcionamento; **contornar essa verificação está fora do escopo** desta missão.\n")

    md.append("## Desmontagem de `JNI_OnLoad`\n")
    for r in resultados:
        d = r["desmontagem_jni_onload"]
        if d.get("presente"):
            md.append(f"### `{r['biblioteca']}` — entrada em `{d['endereco']}`\n")
            md.append(f"- Instruções desmontadas: {d.get('instrucoes_desmontadas')}")
            md.append(f"- Chamadas indiretas (`blr`/`br`): {d.get('chamadas_indiretas_blr_br')}")
            md.append(f"- {d.get('interpretacao','')}\n")
            md.append("```asm")
            md.extend(d.get("amostra", [])[:30])
            md.append("```\n")
    if not dados["com_jni_onload_desmontado"]:
        md.append("Nenhum `JNI_OnLoad` pôde ser desmontado.\n")

    md.append("## O que continua desconhecido\n")
    md.append("- Estrutura do `.nef` (não especificada).")
    md.append("- Semântica dos 480 símbolos JNI além do nome.")
    md.append("- Fluxo de verificação de pacote/licença (não investigado por escopo ético).\n")

    os.makedirs(DIR_RELATORIOS, exist_ok=True)
    open(os.path.join(DIR_RELATORIOS, "strings_e_desmontagem_nativa.md"), "w",
         encoding="utf-8").write("\n".join(md))
    print(f"[ok] relatório em {os.path.join(DIR_RELATORIOS, 'strings_e_desmontagem_nativa.md')}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
