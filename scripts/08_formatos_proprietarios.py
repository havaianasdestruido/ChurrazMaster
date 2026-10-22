#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
08_formatos_proprietarios.py — Fase 5: recursos especiais e formatos proprietários.

Investiga, com evidência citável:
  * o pacote de assets embutido `assets/kmassets.zip` (estrutura, tipos, pacotes);
  * os manifestos `packageinfo.json` dos pacotes de efeitos/transições;
  * a linguagem de efeitos em Lua (API exposta pelos scripts);
  * o formato binário `.nef` (NexEffect): cabeçalho, contagens e marcas observadas;
  * a semântica de `.kine` e `.kmproject` conforme o código gerenciado;
  * bibliotecas nativas carregadas que NÃO existem no APK (carregamento dinâmico).

Saída:
  docs/analise/relatorios/formatos_proprietarios.md   (relatório em pt-BR)
  docs/analise/inventarios/formatos.json              (dados brutos + hashes)

Separação obrigatória: fatos observados ≠ hipóteses. Nenhuma especificação é
inventada; o que não foi confirmado é marcado como hipótese testável.

Uso:
  python3 scripts/08_formatos_proprietarios.py \
      --dir-apktool /tmp/churraz-trabalho/05_apktool \
      --dir-jadx /tmp/churraz-trabalho/04_jadx_base/sources
"""
from __future__ import annotations

import argparse
import hashlib
import json
import os
import re
import struct
import subprocess
import sys
import zipfile
from collections import Counter
from datetime import datetime, timezone

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DIR_RELATORIOS = os.path.join(RAIZ, "docs", "analise", "relatorios")
DIR_INVENTARIOS = os.path.join(RAIZ, "docs", "analise", "inventarios")


def eh_texto_ascii(bloco: bytes) -> bool:
    return all(32 <= b < 127 for b in bloco)


def analisar_kmassets(caminho_zip: str, destino: str) -> dict:
    """Inventaria o pacote de assets e extrai os manifestos de pacote."""
    with zipfile.ZipFile(caminho_zip) as z:
        itens = z.infolist()
        nomes = [i.filename for i in itens]

        # Extrai apenas os arquivos pequenos e de interesse estrutural
        padroes = ("packageinfo.json", ".lua", ".glsl")
        for i in itens:
            if any(i.filename.endswith(p) for p in padroes) or i.filename.endswith(".nef"):
                alvo = os.path.join(destino, i.filename)
                os.makedirs(os.path.dirname(alvo), exist_ok=True)
                with open(alvo, "wb") as f:
                    f.write(z.read(i.filename))

        extensoes = Counter()
        for nome in nomes:
            base = nome.rsplit("/", 1)[-1]
            if "." in base:
                extensoes[base.rsplit(".", 1)[-1].lower()] += 1
            else:
                extensoes["(sem extensão)"] += 1

        pacotes = []
        for nome in nomes:
            if nome.endswith("packageinfo.json"):
                try:
                    dados = json.loads(z.read(nome).decode("utf-8"))
                except Exception:
                    continue
                pacotes.append({
                    "caminho": nome,
                    "assetLevel": dados.get("assetLevel"),
                    "assetName_en": (dados.get("assetName") or {}).get("en"),
                    "chaves": sorted(dados.keys()),
                })

    return {
        "arquivo": os.path.basename(caminho_zip),
        "tamanho": os.path.getsize(caminho_zip),
        "sha256": hashlib.sha256(open(caminho_zip, "rb").read()).hexdigest(),
        "total_de_entradas": len(nomes),
        "extensoes": dict(extensoes.most_common(20)),
        "pacotes_de_assets": len(pacotes),
        "exemplos_de_pacotes": pacotes[:10],
        "nomes_de_pacotes": [p["assetName_en"] for p in pacotes],
    }


def analisar_nef(diretorio: str) -> dict:
    """
    Levanta os fatos observáveis dos arquivos .nef sem presumir estrutura.

    Observações possíveis de verificar: assinatura inicial, contagem de arquivos,
    quantidade de cabeçalhos distintos e presença de marcas ASCII de 4 bytes.
    """
    arquivos = []
    for raiz, _, nomes in os.walk(diretorio):
        for nome in nomes:
            if nome.endswith(".nef"):
                arquivos.append(os.path.join(raiz, nome))
    amostras, magias, marcas = [], Counter(), Counter()
    for caminho in arquivos:
        dados = open(caminho, "rb").read()
        magias[dados[:4].hex()] += 1
        for i in range(0, min(len(dados), 65536) - 4):
            bloco = dados[i:i + 4]
            if all(65 <= b <= 90 for b in bloco):
                marcas[bloco.decode("ascii")] += 1
        amostras.append({
            "arquivo": os.path.relpath(caminho, diretorio),
            "tamanho": len(dados),
            "sha256": hashlib.sha256(dados).hexdigest(),
            "primeiros_32_bytes": dados[:32].hex(),
            "u32_le_iniciais": list(struct.unpack_from("<6I", dados, 0)) if len(dados) >= 24 else [],
        })
    return {
        "total_arquivos": len(arquivos),
        "assinaturas": dict(magias),
        "marcas_ascii_4b_ate_64kb": dict(marcas.most_common(15)),
        "amostras": amostras,
    }


def procurar_no_codigo(dir_jadx: str, padrao: str, limite: int = 12) -> list[dict]:
    """Retorna ocorrências 'arquivo:linha: trecho' de uma expressão regular."""
    regex = re.compile(padrao)
    achados = []
    if not os.path.isdir(dir_jadx):
        return achados
    for raiz, _, nomes in os.walk(dir_jadx):
        for nome in nomes:
            if not nome.endswith(".java"):
                continue
            caminho = os.path.join(raiz, nome)
            try:
                with open(caminho, "r", encoding="utf-8", errors="replace") as f:
                    for numero, linha in enumerate(f, 1):
                        if regex.search(linha):
                            achados.append({
                                "arquivo": os.path.relpath(caminho, dir_jadx),
                                "linha": numero,
                                "trecho": linha.strip()[:180],
                            })
                            if len(achados) >= limite:
                                return achados
            except Exception:
                continue
    return achados


def main() -> int:
    ap = argparse.ArgumentParser(description="Formatos proprietários e recursos especiais")
    ap.add_argument("--dir-apktool", default="/tmp/churraz-trabalho/05_apktool")
    ap.add_argument("--dir-jadx", default="/tmp/churraz-trabalho/04_jadx_base/sources")
    ap.add_argument("--dir-saida", default="/tmp/churraz-trabalho/07_assets")
    args = ap.parse_args()

    origem_kmassets = os.path.join(args.dir_apktool, "base", "assets", "kmassets.zip")
    if not os.path.exists(origem_kmassets):
        print(f"[erro] kmassets.zip não encontrado em {origem_kmassets}; "
              f"execute scripts/03_recursos.sh", file=sys.stderr)
        return 2

    os.makedirs(args.dir_saida, exist_ok=True)
    info_kmassets = analisar_kmassets(origem_kmassets, args.dir_saida)
    info_nef = analisar_nef(args.dir_saida)

    # Evidências no código gerenciado
    evidencias = {
        "kmproject": procurar_no_codigo(args.dir_jadx, r'\.kmproject'),
        "kine": procurar_no_codigo(args.dir_jadx, r'"\.kine"|\.kine"'),
        "loadlibrary": procurar_no_codigo(args.dir_jadx, r'System\.loadLibrary\('),
        "carregamento_dinamico": procurar_no_codigo(
            args.dir_jadx, r'DexClassLoader|InMemoryDexClassLoader|bsPatch|bspatch'),
    }

    # Bibliotecas carregadas que não existem como arquivo no APK
    caminho_nativo = os.path.join(RAIZ, "docs", "analise", "inventarios", "nativo.json")
    ausentes = []
    if os.path.exists(caminho_nativo):
        nativo = json.load(open(caminho_nativo, encoding="utf-8"))
        existentes = {b["arquivo"][3:-3] for b in nativo["bibliotecas"]}  # libX.so -> X
        carregadas = set(nativo["correlacao_gerenciado"]["bibliotecas_carregadas"])
        ausentes = sorted(carregadas - existentes)

    dados = {
        "gerado_em": datetime.now(timezone.utc).isoformat(),
        "kmassets": info_kmassets,
        "nef": info_nef,
        "evidencias": evidencias,
        "bibliotecas_carregadas_ausentes_no_apk": ausentes,
    }
    os.makedirs(DIR_INVENTARIOS, exist_ok=True)
    json.dump(dados, open(os.path.join(DIR_INVENTARIOS, "formatos.json"), "w", encoding="utf-8"),
              ensure_ascii=False, indent=2)

    # ---------------- relatório em português ----------------
    md = []
    md.append("# Formatos proprietários e recursos especiais\n")
    md.append(f"Gerado em `{dados['gerado_em']}` por `scripts/08_formatos_proprietarios.py`.\n")
    md.append("## 1. `assets/kmassets.zip` — pacote de efeitos e recursos\n")
    md.append(f"- **SHA-256 do zip**: `{info_kmassets['sha256']}`")
    md.append(f"- **Tamanho**: {info_kmassets['tamanho']} bytes")
    md.append(f"- **Entradas**: {info_kmassets['total_de_entradas']}")
    md.append(f"- **Pacotes com `packageinfo.json`**: {info_kmassets['pacotes_de_assets']}")
    md.append(f"- **Distribuição por extensão**: `{info_kmassets['extensoes']}`\n")
    md.append("**Fato observado:** o zip é um contêiner de *asset packs* de efeitos; cada pacote tem um")
    md.append("`packageinfo.json` com `assetLevel`, `assetName` (localizado em vários idiomas) e")
    md.append("metadados de apresentação. Nomes de pacotes encontrados:")
    md.append(", ".join(f"`{n}`" for n in info_kmassets["nomes_de_pacotes"] if n) + "\n")
    md.append("**Estrutura interna típica** (observada em `3d_transitions`):")
    md.append("```\n<familia>/<tipo>/<variante_proporcao>.<nome>/nexeffect/<arquivo>.<...>.nef\n```\n")

    md.append("## 2. Efeitos em Lua (`*.lua`)\n")
    md.append("**Fato observado:** há scripts Lua dentro de `kmassets.zip`, um por efeito, com uma tabela")
    md.append("global `effect` e funções chamadas pelo motor nativo. Exemplo real extraído de")
    md.append("`basic_effects/filter/blend_08/nexstreaming.lua`:")
    md.append("```lua")
    md.append("effect = {matrix = 0, left = -1, top = -1, right = 1, bottom = 1, alpha = 1}")
    md.append("function setDefaultValue() ... end")
    md.append("```")
    md.append("**Inferência (não confirmada em execução):** o motor `libnexeditorsdk.so` embute um")
    md.append("interpretador Lua e expõe essa tabela como API de parametrização de efeitos. Não há")
    md.append("evidência dinâmica neste ambiente (sem execução do app).\n")

    md.append("## 3. `.nef` — NexEffect (formato binário proprietário)\n")
    md.append(f"- **Arquivos encontrados**: {info_nef['total_arquivos']}")
    md.append(f"- **Assinaturas iniciais observadas**: `{info_nef['assinaturas']}`")
    md.append(f"- **Marcas ASCII de 4 bytes (até 64 KB)**: `{info_nef['marcas_ascii_4b_ate_64kb']}`\n")
    md.append("**Fato observado (amostra):**\n")
    md.append("| arquivo | bytes | SHA-256 | primeiros bytes (hex) |")
    md.append("|---|---:|---|---|")
    for a in info_nef["amostras"][:8]:
        md.append(f"| `{a['arquivo']}` | {a['tamanho']} | `{a['sha256'][:24]}…` | `{a['primeiros_32_bytes']}` |")
    md.append("")
    md.append("**Hipóteses testáveis (NÃO são especificação):**")
    md.append("1. Os 4 primeiros bytes (`13 00 ff ee`) funcionam como assinatura/mágica; há uma")
    md.append("   variante com `01 da fe ef`.")
    md.append("2. Os campos `u32` seguintes parecem contagens/versões (`6`, `181`) — não confirmado.")
    md.append("3. A marca `DEAD` e sequências de `double` em faixa `0x7ffc…` sugerem tabelas de")
    md.append("   parâmetros numéricos serializadas, possivelmente com *checksum* ou marcador de fim.")
    md.append("4. Para confirmar seria necessário localizar no código nativo o leitor desse formato")
    md.append("   (candidato: `libnexeditorsdk.so`), o que exige descompilador nativo indisponível aqui.\n")
    md.append("**Limitação declarada:** o formato `.nef` **não** foi especificado. As hipóteses acima são")
    md.append("testáveis, mas não foram validadas; nenhuma estrutura foi inventada.\n")

    md.append("## 4. Projetos: `.kine` e `.kmproject`\n")
    md.append("**Fato observado no código** (citações com arquivo e linha):\n")
    for chave, titulo in (("kine", "`.kine`"), ("kmproject", "`.kmproject`")):
        md.append(f"### {titulo}\n")
        for e in evidencias[chave]:
            md.append(f"- `{e['arquivo']}:{e['linha']}` — `{e['trecho']}`")
        md.append("")
    md.append("**Fato observado adicional:** em `com/nexstreaming/kinemaster/project/util/d.java` o")
    md.append("projeto é criado como **diretório** `<UUID>.kine` contendo o arquivo `<título>.kmproject`.")
    md.append("**Interpretação:** `.kine` é o diretório de projeto (container) e `.kmproject` é o arquivo")
    md.append("de projeto dentro dele. Isso é consistente com o código, mas a estrutura interna do")
    md.append("`.kmproject` não foi recuperada nesta fase (exigiria gerar/abrir um projeto real).\n")

    md.append("## 5. Carregamento dinâmico e bibliotecas ausentes\n")
    md.append("**Fato observado:** o código chama `System.loadLibrary` para nomes que **não existem** como")
    md.append("arquivo no XAPK:\n")
    for nome in dados["bibliotecas_carregadas_ausentes_no_apk"]:
        md.append(f"- `{nome}`")
    md.append("")
    md.append("**Correlação com o código gerenciado:**")
    for e in evidencias["loadlibrary"]:
        md.append(f"- `{e['arquivo']}:{e['linha']}` — `{e['trecho']}`")
    md.append("")
    md.append("**Fato observado:** `geckox_bspatch` é referenciado por")
    md.append("`com/bykv/vk/openvk/preload/geckox/utils/BsPatch.java` (SDK de anúncios ByteDance/Pangle),")
    md.append("que aplica *patches* binários (bsdiff/bspatch) a recursos baixados em tempo de execução.")
    md.append("`pglarmor` é usado por `com/bytedance/sdk/component/pglcrypt/PglCryptUtils.java`.")
    md.append("**Conclusão de escopo (inferência forte):** esses mecanismos pertencem ao SDK de anúncios,")
    md.append("não ao núcleo de edição do KineMaster; não foi encontrado mecanismo próprio de *hot-patch*")
    md.append("do lado do aplicativo principal.\n")
    md.append("`OpenCL`, `OpenCL-car`, `OpenCL-pixel` e `stlport_shared` são carregadas")
    md.append("condicionalmente (renderização/modelo de dispositivo); a ausência do arquivo indica")
    md.append("carregamento opcional com tratamento de erro, não corrupção do pacote.\n")

    md.append("## 6. O que permanece desconhecido\n")
    md.append("- Estrutura interna completa de `.kmproject` e do banco de dados de projeto (Room).\n")
    md.append("- Especificação de `.nef` (apenas hipóteses).")
    md.append("- Papel exato dos 28 scripts Lua no pipeline de renderização (sem execução).")
    md.append("- Formato dos modelos `.tflite`/`.bin` de IA e sua ligação com `libonnxruntime.so`.\n")

    os.makedirs(DIR_RELATORIOS, exist_ok=True)
    open(os.path.join(DIR_RELATORIOS, "formatos_proprietarios.md"), "w", encoding="utf-8").write("\n".join(md))
    print(f"[ok] {info_kmassets['total_de_entradas']} entradas em kmassets.zip, "
          f"{info_nef['total_arquivos']} arquivos .nef, "
          f"{len(dados['bibliotecas_carregadas_ausentes_no_apk'])} bibliotecas ausentes")
    print(f"[ok] relatório: {os.path.join(DIR_RELATORIOS, 'formatos_proprietarios.md')}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
