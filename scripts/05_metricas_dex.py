#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
05_metricas_dex.py — Fase 3: métricas verificáveis de DEX e cobertura da descompilação.

Para cada arquivo .dex encontrado:
  * lê o cabeçalho DEX (versão, contagens de ids) — fato observado direto do binário;
  * enumera classes e métodos com androguard;
  * verifica, classe a classe, se existe um .java correspondente na saída do jadx.

A cobertura publicada é sempre "classes com arquivo .java presente / classes
declaradas no DEX". Classes internas (nome com '$') são contabilizadas à parte,
porque o jadx as escreve dentro do arquivo da classe externa.

Uso:
  python3 scripts/05_metricas_dex.py \
      --dir-dex /tmp/churraz-trabalho/03_dex \
      --dir-jadx /tmp/churraz-trabalho/04_jadx_base/sources \
      --saida docs/analise/inventarios/metricas_dex.json
"""
from __future__ import annotations

import argparse
import json
import itertools
import os
import re
import struct
import sys
from collections import Counter
from datetime import datetime, timezone

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def silenciar_logs() -> None:
    """O androguard emite logs muito verbosos; removemos os handlers."""
    try:
        from loguru import logger
        logger.remove()
    except Exception:
        pass


def cabecalho_dex(dados: bytes) -> dict:
    """Lê o cabeçalho DEX conforme a especificação oficial (offset 56 em diante)."""
    campos = struct.unpack_from("<20I", dados, 32)
    nomes = ["file_size", "header_size", "endian_tag", "link_size", "link_off", "map_off",
             "string_ids_size", "string_ids_off", "type_ids_size", "type_ids_off",
             "proto_ids_size", "proto_ids_off", "field_ids_size", "field_ids_off",
             "method_ids_size", "method_ids_off", "class_defs_size", "class_defs_off",
             "data_size", "data_off"]
    return {"versao_dex": dados[4:7].decode("ascii", "replace"),
            "checksum_adler32": f"0x{struct.unpack_from('<I', dados, 8)[0]:08x}",
            **dict(zip(nomes, campos))}


def candidatos_java(raiz_saida: str, nome_classe: str) -> list[str]:
    """
    Caminhos onde o jadx pode ter gravado a classe.

    O jadx 1.1.0 move classes do pacote padrão (sem pacote) para 'defpackage/',
    comportamento observado na saída real deste APK. Classes com pacote ficam no
    caminho normal. Testamos os dois para não subestimar a cobertura.
    """
    relativo = os.path.join(*nome_classe.split("/")) + ".java"
    # Classes do pacote padrão (sem '/') vão para defpackage/ no jadx 1.1.0.
    simple = nome_classe.rsplit("/", 1)[-1]
    candidatos = [os.path.join(raiz_saida, relativo)]
    if "/" not in nome_classe:
        candidatos.append(os.path.join(raiz_saida, "defpackage", simple + ".java"))
    else:
        candidatos.append(os.path.join(raiz_saida, "defpackage", nome_classe + ".java"))
    return candidatos


def existe_java(raiz_saida: str, nome_classe: str, aliases: dict[str, str] | None = None) -> bool:
    """Verifica presença exata, em defpackage/ ou por alias de renomeação do jadx."""
    if any(os.path.exists(p) for p in candidatos_java(raiz_saida, nome_classe)):
        return True
    if aliases and nome_classe in aliases:
        return os.path.exists(aliases[nome_classe])
    return False


PADRAO_RENOMEACAO = re.compile(r"/\*\s*renamed from:\s*(\S+)\s+reason:\s*([^*]+?)\s*\*/")
RAZOES_DE_CLASSE = ("default package", "invalid class name", "invalid package",
                    "case insensitive filesystem", "collision with other inner class")


def indexar_aliases(raiz_saida: str) -> dict[str, str]:
    """
    Constrói o mapa nome-original -> arquivo a partir dos comentários
    '/* renamed from: X reason: ... */' que o jadx grava antes da declaração.

    Isso recupera classes cujo nome não é um identificador Java válido
    (ex.: 'com.facebook.ads.redexgen.X.4V', 'com.google.android.gms.internal.firebase-auth-api.zzafc')
    e que, por isso, recebem nome de arquivo diferente do nome presente no DEX.
    """
    aliases: dict[str, str] = {}
    for raiz, _, arquivos in os.walk(raiz_saida):
        for nome in arquivos:
            if not nome.endswith(".java"):
                continue
            caminho = os.path.join(raiz, nome)
            try:
                with open(caminho, "r", encoding="utf-8", errors="replace") as f:
                    cabeca = "".join(itertools.islice(f, 120))
            except Exception:
                continue
            for achado in PADRAO_RENOMEACAO.finditer(cabeca):
                original, razao = achado.group(1), achado.group(2)
                if any(r in razao for r in RAZOES_DE_CLASSE):
                    aliases[original.replace(".", "/")] = caminho
                    break
    return aliases


def main() -> int:
    ap = argparse.ArgumentParser(description="Métricas de DEX e cobertura do jadx")
    ap.add_argument("--dir-dex", default="/tmp/churraz-trabalho/03_dex")
    ap.add_argument("--dir-jadx", default="/tmp/churraz-trabalho/04_jadx_base/sources")
    ap.add_argument("--dir-jadx-extra", default="/tmp/churraz-trabalho/04_jadx_audience/sources",
                    help="árvore do jadx para DEX embutidos em assets/ (descompilados à parte)")
    ap.add_argument("--saida", default=os.path.join(RAIZ, "docs", "analise", "inventarios", "metricas_dex.json"))
    args = ap.parse_args()

    silenciar_logs()
    from androguard.core.dex import DEX

    if not os.path.isdir(args.dir_dex):
        print(f"[erro] diretório de DEX inexistente: {args.dir_dex}", file=sys.stderr)
        return 2
    if not os.path.isdir(args.dir_jadx):
        print(f"[erro] saída do jadx inexistente: {args.dir_jadx} (execute scripts/04_decompilar_jadx.sh)",
              file=sys.stderr)
        return 2

    indices = {}
    resultados = []
    pacotes_globais: Counter = Counter()
    total_classes = total_metodos = total_campos = total_strings = 0
    cobertas = internas = ausentes = 0

    for nome_arquivo in sorted(os.listdir(args.dir_dex)):
        if not nome_arquivo.endswith(".dex"):
            continue
        caminho = os.path.join(args.dir_dex, nome_arquivo)
        dados = open(caminho, "rb").read()
        cab = cabecalho_dex(dados)
        dex = DEX(dados)
        # O DEX embutido em assets/ (Audience Network) foi descompilado em árvore separada.
        raiz_jadx = args.dir_jadx
        if nome_arquivo.startswith("assets_") and args.dir_jadx_extra:
            raiz_jadx = args.dir_jadx_extra
        if raiz_jadx not in indices:
            indices[raiz_jadx] = indexar_aliases(raiz_jadx)
        aliases = indices[raiz_jadx]

        classes = dex.get_classes()
        metodos = sum(len(c.get_methods()) for c in classes)
        campos = sum(len(c.get_fields()) for c in classes)

        encontradas, faltantes, internas_dex = 0, [], 0
        detalhe_faltantes: list[dict] = []
        for c in classes:
            nome = c.get_name()          # ex.: Lcom/exemplo/Foo$Bar;
            limpo = nome[1:-1] if nome.startswith("L") and nome.endswith(";") else nome
            pacotes_globais[limpo.rsplit("/", 1)[0].replace("/", ".")] += 1
            if "$" in limpo:
                internas_dex += 1
            # jadx 1.1.0 grava classes internas em arquivo próprio (Foo$Bar.java);
            # quando ausente, aceitamos o arquivo da classe externa como aproximação.
            if existe_java(raiz_jadx, limpo, aliases) or existe_java(raiz_jadx, limpo.split("$")[0], aliases):
                encontradas += 1
            else:
                faltantes.append(limpo)
                detalhe_faltantes.append({
                    "classe": limpo,
                    "metodos": len(c.get_methods()),
                    "campos": len(c.get_fields()),
                    "interna_em_item": "$" in limpo,
                    "anonima": "$" in limpo and limpo.rsplit("$", 1)[-1].isdigit(),
                    "sem_membros": len(c.get_methods()) == 0 and len(c.get_fields()) == 0,
                })

        item = {
            "arquivo": nome_arquivo,
            "tamanho": len(dados),
            "cabecalho": cab,
            "classes": len(classes),
            "classes_internas": internas_dex,
            "metodos": metodos,
            "campos": campos,
            "strings": cab["string_ids_size"],
            "classes_com_java": encontradas,
            "classes_sem_java": len(faltantes),
            "exemplos_sem_java": faltantes[:25],
            "lacunas_detalhadas": detalhe_faltantes,
        }
        resultados.append(item)

        total_classes += len(classes)
        total_metodos += metodos
        total_campos += campos
        total_strings += cab["string_ids_size"]
        cobertas += encontradas
        internas += internas_dex
        ausentes += len(faltantes)

        print(f"[info] {nome_arquivo}: {len(classes)} classes, {metodos} métodos, "
              f"{encontradas} com .java, {len(faltantes)} sem .java")

    resumo = {
        "gerado_em": datetime.now(timezone.utc).isoformat(),
        "total_dex": len(resultados),
        "total_classes": total_classes,
        "total_classes_internas": internas,
        "total_metodos": total_metodos,
        "total_campos": total_campos,
        "total_strings": total_strings,
        "classes_com_java": cobertas,
        "classes_sem_java": ausentes,
        "cobertura_classes_pct": round(100.0 * cobertas / total_classes, 2) if total_classes else None,
        "top_pacotes": pacotes_globais.most_common(40),
        "classificacao_lacunas": dict(Counter(
            ("sem_membros" if x["sem_membros"] else
             "anonima" if x["anonima"] else
             "classe_interna" if x["interna_em_item"] else
             "classe_toplevel_com_codigo")
            for item in resultados for x in item["lacunas_detalhadas"]
        )),
        "por_dex": resultados,
        "observacao": (
            "Cobertura = classes declaradas no DEX que possuem arquivo .java na saída do jadx. "
            "Classes internas contam como cobertas quando a classe externa foi descompilada, "
            "pois o jadx as emite no mesmo arquivo. Presença de arquivo não garante corpo "
            "de método correto: erros de descompilação são contados à parte (ver log do jadx)."
        ),
    }

    os.makedirs(os.path.dirname(args.saida), exist_ok=True)
    with open(args.saida, "w", encoding="utf-8") as f:
        json.dump(resumo, f, ensure_ascii=False, indent=2)

    print(f"[ok] resumo: {total_classes} classes, {cobertas} com .java "
          f"({resumo['cobertura_classes_pct']}%), {ausentes} sem .java")
    print(f"[ok] gravado em {args.saida}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
