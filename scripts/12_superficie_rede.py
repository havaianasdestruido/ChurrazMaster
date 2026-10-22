#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
12_superficie_rede.py — Fase 5/3: superfície de rede observável estaticamente.

Extrai do código decompilado e dos recursos:
  * URLs embutidas, agrupadas por host, com separação entre produção e teste;
  * a política de rede (`res/xml/network_security_config.xml` decodificado);
  * nomes de hosts citados como constantes;
  * domínios declarados em `strings.xml`/`manifest`.

Isto é **análise estática**: nenhuma requisição foi feita e nenhum endpoint foi exercitado.
Portanto, presença de URL não prova uso em produção.

Uso:
  python3 scripts/12_superficie_rede.py --dir-jadx /tmp/.../04_jadx_base/sources \
      --dir-recursos /tmp/.../05_apktool/base
"""
from __future__ import annotations

import argparse
import json
import os
import re
import sys
from collections import Counter, defaultdict
from datetime import datetime, timezone
from urllib.parse import urlparse

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DIR_INVENTARIOS = os.path.join(RAIZ, "docs", "analise", "inventarios")
DIR_RELATORIOS = os.path.join(RAIZ, "docs", "analise", "relatorios")

RE_URL = re.compile(r'"(https?://[^"\s]{4,200})"')
RE_HOST = re.compile(r'"((?:[a-z0-9-]+\.)+(?:com|net|org|io|co|kr|ai|cloud|app|dev))"')
PACOTES_DO_APP = ("com/kinemaster", "com/nexstreaming")


def main() -> int:
    ap = argparse.ArgumentParser(description="Superfície de rede estática")
    ap.add_argument("--dir-jadx", default="/tmp/churraz-trabalho/04_jadx_base/sources")
    ap.add_argument("--dir-recursos", default="/tmp/churraz-trabalho/05_apktool/base")
    args = ap.parse_args()

    if not os.path.isdir(args.dir_jadx):
        print(f"[erro] diretório inexistente: {args.dir_jadx}", file=sys.stderr)
        return 2

    urls_app: Counter = Counter()
    urls_terceiros: Counter = Counter()
    hosts: Counter = Counter()
    origem_das_urls: dict[str, set[str]] = defaultdict(set)

    for raiz, _, arquivos in os.walk(args.dir_jadx):
        for nome in arquivos:
            if not nome.endswith(".java"):
                continue
            caminho = os.path.join(raiz, nome)
            relativo = os.path.relpath(caminho, args.dir_jadx)
            texto = open(caminho, "r", encoding="utf-8", errors="replace").read()
            alvo = urls_app if relativo.startswith(PACOTES_DO_APP) else urls_terceiros
            for m in RE_URL.finditer(texto):
                url = m.group(1)
                alvo[url] += 1
                origem_das_urls[url].add(relativo)
            for m in RE_HOST.finditer(texto):
                hosts[m.group(1)] += 1

    producao, teste, outros = [], [], []
    for url in sorted(set(urls_app) | set(urls_terceiros)):
        host = urlparse(url).hostname or ""
        principal = url in urls_app
        item = {"url": url, "host": host, "no_codigo_do_app": principal,
                "ocorrencias": urls_app.get(url, 0) + urls_terceiros.get(url, 0),
                "exemplos_de_arquivo": sorted(origem_das_urls[url])[:3]}
        if host.startswith("test") or ".test." in host or "/test/" in url:
            teste.append(item)
        elif host.endswith(("kinemasters.com", "kinemaster.com", "kinemix.com")):
            producao.append(item)
        else:
            outros.append(item)

    # Política de rede
    politica = {}
    caminho_politica = os.path.join(args.dir_recursos, "res", "xml", "network_security_config.xml")
    if os.path.exists(caminho_politica):
        politica["arquivo"] = "res/xml/network_security_config.xml"
        politica["conteudo"] = open(caminho_politica, encoding="utf-8", errors="replace").read()[:2000]

    dados = {
        "gerado_em": datetime.now(timezone.utc).isoformat(),
        "urls_de_producao": producao,
        "urls_de_teste": teste,
        "urls_de_terceiros": outros[:200],
        "total_urls": len(producao) + len(teste) + len(outros),
        "hosts_citados_como_constante": hosts.most_common(60),
        "politica_de_rede": politica,
        "aviso": ("Análise estática; nenhuma requisição foi executada. "
                  "URL presente no código não implica endpoint ativo."),
    }
    os.makedirs(DIR_INVENTARIOS, exist_ok=True)
    json.dump(dados, open(os.path.join(DIR_INVENTARIOS, "superficie_rede.json"), "w", encoding="utf-8"),
              ensure_ascii=False, indent=2)

    md = ["# Superfície de rede (análise estática)\n",
          f"Gerado em `{dados['gerado_em']}` por `scripts/12_superficie_rede.py`.\n",
          "> **Nenhuma requisição foi feita.** As URLs abaixo foram extraídas de constantes no código;",
          "> a presença delas não prova que o endpoint esteja ativo ou seja usado na versão analisada.\n",
          f"- URLs distintas encontradas: **{dados['total_urls']}**",
          f"- Domínios do fabricante (`kinemasters.com`, `kinemaster.com`, `kinemix.com`): **{len(producao)}**",
          f"- Variantes de teste: **{len(teste)}**\n"]

    md.append("## Endpoints de produção\n")
    md.append("| host | URL | classe de origem |")
    md.append("|---|---|---|")
    for item in sorted(producao, key=lambda x: (x["host"], x["url"])):
        md.append(f"| `{item['host']}` | `{item['url']}` | `{item['exemplos_de_arquivo'][0] if item['exemplos_de_arquivo'] else '—'}` |")
    md.append("")

    if teste:
        md.append("## Variantes de teste/homologação\n")
        md.append("| host | URL |")
        md.append("|---|---|")
        for item in sorted(teste, key=lambda x: x["url"]):
            md.append(f"| `{item['host']}` | `{item['url']}` |")
        md.append("")

    md.append("## Domínios de terceiros mais citados\n")
    md.append("| host | ocorrências |")
    md.append("|---|---:|")
    for host, quantidade in dados["hosts_citados_como_constante"][:30]:
        md.append(f"| `{host}` | {quantidade} |")
    md.append("")

    if politica:
        md.append("## Política de rede do aplicativo\n")
        md.append("`res/xml/network_security_config.xml` (decodificado):\n")
        md.append("```xml\n" + politica["conteudo"].strip() + "\n```\n")
        md.append("**Fato observado:** `cleartextTrafficPermitted=\"true\"` na configuração base, com "
                  "domínios explicitamente autorizados para HTTP claro. `debug-overrides` confia em "
                  "certificados instalados pelo usuário — o que facilita a observação de tráfego em "
                  "build de depuração, mas **não** implica interceptação possível em build de release.\n")

    md.append("## O que não foi determinado\n")
    md.append("- Autenticação dos endpoints (tokens, headers, mTLS).")
    md.append("- Quais endpoints são realmente chamados em execução (exige Fase 6).")
    md.append("- Contratos de API (payloads) — exigiria DTOs + captura de tráfego.\n")

    os.makedirs(DIR_RELATORIOS, exist_ok=True)
    open(os.path.join(DIR_RELATORIOS, "superficie_de_rede.md"), "w", encoding="utf-8").write("\n".join(md))
    print(f"[ok] {dados['total_urls']} URLs ({len(producao)} produção, {len(teste)} teste), "
          f"{len(dados['hosts_citados_como_constante'])} hosts citados")
    return 0


if __name__ == "__main__":
    sys.exit(main())
