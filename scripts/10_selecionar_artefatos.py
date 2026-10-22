#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
10_selecionar_artefatos.py — Fase 7: seleciona artefatos recuperados para o Git.

As árvores completas de descompilação (≈ 610 MB, dezenas de milhares de arquivos)
não são versionadas: ficam no diretório de trabalho e são reproduzíveis por script.
Para o repositório vão:

  decompilado/AndroidManifest.xml          manifesto decodificado (base)
  decompilado/apktool.yml                  metadados do APK base
  decompilado/selecionado/java/...         classes de interesse para mods
  decompilado/indices/componentes.md       atividades/serviços/receptores/provedores
  decompilado/indices/simbolos_jni.csv     mapa símbolo JNI -> classe Java
  decompilado/indices/arvore_de_pacotes.txt árvore de pacotes (saída do jadx-mcp)
  decompilado/indices/classes_kinemaster.txt classes do próprio app (sem bibliotecas)

Uso:
  python3 scripts/10_selecionar_artefatos.py --dir-trabalho /tmp/churraz-trabalho
"""
from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import sys
import xml.etree.ElementTree as ET

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DIR_DESTINO = os.path.join(RAIZ, "decompilado")

# Classes de interesse direto para o objetivo do projeto (mods e novas features)
CLASSES_DE_INTERESSE = [
    "com/kinemaster/app/base/KineMasterApplication.java",
    "com/kinemaster/app/database/project/ProjectDatabase.java",
    "com/kinemaster/app/database/home/TemplateDatabase.java",
    "com/kinemaster/app/database/installedassets/InstalledAssetsDatabase.java",
    "com/nexstreaming/kinemaster/project/util/d.java",
    "com/nexstreaming/kinemaster/editorwrapper/k.java",
    "com/nexstreaming/kinemaster/editorwrapper/l.java",
    "com/kinemaster/module/nexeditormodule/nexvideoeditor/NexEditor.java",
    "com/kinemaster/module/nexeditormodule/nexvideoeditor/NexEditorUtils.java",
    "com/kinemaster/module/nexeditormodule/nexvideoeditor/NexThemeRenderer.java",
    "com/kinemaster/app/screen/projecteditor/main/ProjectEditorActivity.java",
    "com/kinemaster/app/screen/launch/kmscheme/KMSchemePendingActivity.java",
    "com/kinemaster/app/screen/splash/SplashActivity.java",
    "com/bykv/vk/openvk/preload/geckox/utils/BsPatch.java",
    "com/bytedance/sdk/component/pglcrypt/PglCryptUtils.java",
]


def copiar_selecionadas(dir_jadx: str, destino: str) -> list[str]:
    copiadas, ausentes = [], []
    for relativo in CLASSES_DE_INTERESSE:
        origem = os.path.join(dir_jadx, relativo)
        if os.path.exists(origem):
            alvo = os.path.join(destino, "selecionado", "java", relativo)
            os.makedirs(os.path.dirname(alvo), exist_ok=True)
            shutil.copy2(origem, alvo)
            copiadas.append(relativo)
        else:
            ausentes.append(relativo)
    return copiadas, ausentes


def gerar_indice_componentes(manifesto: str, destino: str) -> dict:
    """Extrai componentes declarados do manifesto decodificado."""
    ns = "{http://schemas.android.com/apk/res/android}"
    arvore = ET.parse(manifesto)
    raiz = arvore.getroot()
    linhas = ["# Componentes declarados no AndroidManifest.xml do APK base\n"]
    linhas.append(f"- **package**: `{raiz.get('package')}`")
    linhas.append(f"- **versionCode**: `{raiz.get(ns + 'versionCode')}`")
    linhas.append(f"- **versionName**: `{raiz.get(ns + 'versionName')}`\n")
    resumo = {}
    for tag in ("uses-permission", "permission", "activity", "service", "receiver",
                "provider", "meta-data", "uses-feature", "uses-native-library", "uses-library"):
        itens = [e for e in raiz.iter() if e.tag == tag]
        resumo[tag] = len(itens)
        linhas.append(f"## {tag} ({len(itens)})\n")
        for e in itens:
            nome = e.get(ns + "name") or e.get(ns + "glEsVersion") or "(sem nome)"
            extras = []
            for attr in ("exported", "authorities", "resource", "value", "glEsVersion"):
                valor = e.get(ns + attr)
                if valor:
                    extras.append(f"{attr}={valor}")
            linhas.append(f"- `{nome}`" + (f" ({', '.join(extras)})" if extras else ""))
        linhas.append("")
    os.makedirs(destino, exist_ok=True)
    open(os.path.join(destino, "componentes.md"), "w", encoding="utf-8").write("\n".join(linhas))
    return resumo


def gerar_indice_jni(nativo_json: str, destino: str) -> int:
    dados = json.load(open(nativo_json, encoding="utf-8"))
    linhas = ["biblioteca,simbolo_jni,classe_java_inferida"]
    total = 0
    for lib in dados["bibliotecas"]:
        for simbolo in lib["simbolos_jni"]:
            resto = simbolo[len("Java_"):]
            partes = resto.split("_")
            classe = "_".join(partes[:-1]).replace("_", ".") if len(partes) > 1 else ""
            metodo = partes[-1] if partes else ""
            linhas.append(f"{lib['arquivo']},{simbolo},{classe}#{metodo}")
            total += 1
    os.makedirs(destino, exist_ok=True)
    open(os.path.join(destino, "simbolos_jni.csv"), "w", encoding="utf-8").write("\n".join(linhas))
    return total


def gerar_indice_classes(dir_jadx: str, destino: str) -> int:
    """Lista classes do aplicativo (com.kinemaster, com.nexstreaming), separadas das bibliotecas."""
    padrao = re.compile(r"^(com/kinemaster|com/nexstreaming)/")
    linhas = []
    for raiz, _, nomes in os.walk(dir_jadx):
        for nome in nomes:
            if not nome.endswith(".java"):
                continue
            relativo = os.path.relpath(os.path.join(raiz, nome), dir_jadx)
            if padrao.match(relativo):
                linhas.append(relativo[:-5].replace("/", "."))
    linhas.sort()
    os.makedirs(destino, exist_ok=True)
    open(os.path.join(destino, "classes_kinemaster.txt"), "w", encoding="utf-8").write("\n".join(linhas))
    return len(linhas)


def main() -> int:
    ap = argparse.ArgumentParser(description="Seleciona artefatos recuperados para o Git")
    ap.add_argument("--dir-trabalho", default=os.environ.get("DIR_TRABALHO", "/tmp/churraz-trabalho"))
    args = ap.parse_args()

    dir_jadx = os.path.join(args.dir_trabalho, "04_jadx_base", "sources")
    manifesto = os.path.join(args.dir_trabalho, "05_apktool", "base", "AndroidManifest.xml")
    if not os.path.isdir(dir_jadx):
        print("[erro] saída do jadx ausente; execute scripts/04_decompilar_jadx.sh", file=sys.stderr)
        return 2
    if not os.path.exists(manifesto):
        print("[erro] manifesto decodificado ausente; execute scripts/03_recursos.sh", file=sys.stderr)
        return 2

    os.makedirs(DIR_DESTINO, exist_ok=True)
    shutil.copy2(manifesto, os.path.join(DIR_DESTINO, "AndroidManifest.xml"))
    yml = os.path.join(args.dir_trabalho, "05_apktool", "base", "apktool.yml")
    if os.path.exists(yml):
        shutil.copy2(yml, os.path.join(DIR_DESTINO, "apktool.yml"))

    copiadas, ausentes = copiar_selecionadas(dir_jadx, DIR_DESTINO)
    resumo = gerar_indice_componentes(manifesto, os.path.join(DIR_DESTINO, "indices"))
    total_jni = gerar_indice_jni(os.path.join(RAIZ, "docs", "analise", "inventarios", "nativo.json"),
                                 os.path.join(DIR_DESTINO, "indices"))
    total_classes = gerar_indice_classes(dir_jadx, os.path.join(DIR_DESTINO, "indices"))

    # árvore de pacotes (se o cliente MCP já gravou a resposta)
    arvore_pacotes = os.path.join(args.dir_trabalho, "arvore_de_pacotes.txt")
    if os.path.exists(arvore_pacotes):
        shutil.copy2(arvore_pacotes, os.path.join(DIR_DESTINO, "indices", "arvore_de_pacotes.txt"))

    print(f"[ok] {len(copiadas)} classes copiadas; ausentes: {ausentes}")
    print(f"[ok] componentes: {resumo}")
    print(f"[ok] {total_jni} símbolos JNI indexados; {total_classes} classes do app listadas")
    return 0


if __name__ == "__main__":
    sys.exit(main())
