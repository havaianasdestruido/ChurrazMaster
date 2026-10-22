#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# 01_extrair.sh — Fase 2: extração dos APKs e dos DEX para o diretório de trabalho
#
# Pré-requisito: scripts/00_verificar_integridade.sh (XAPK já extraído em $DIR_XAPK)
# Resultado:
#   $DIR_APKS/<nome>.apk   — os 20 APKs do XAPK, sem alteração
#   $DIR_DEX/<nome>.dex    — os 6 DEX do APK base + o DEX embutido em assets/
#
# Nada é escrito em bin/: os originais permanecem intactos.
# ---------------------------------------------------------------------------
set -euo pipefail
source "$(dirname "$0")/lib/comum.sh"

criar_dir_trabalho
exigir_arquivo "$DIR_XAPK/$NOME_XAPK" "XAPK extraído"

info "Extraindo os APKs do XAPK"
python3 - "$DIR_XAPK/$NOME_XAPK" "$DIR_APKS" <<'PY'
import hashlib, os, sys, zipfile

xapk, destino = sys.argv[1], sys.argv[2]
os.makedirs(destino, exist_ok=True)
with zipfile.ZipFile(xapk) as z:
    for item in z.infolist():
        if not item.filename.endswith(".apk"):
            continue
        dados = z.read(item.filename)
        caminho = os.path.join(destino, item.filename)
        with open(caminho, "wb") as f:
            f.write(dados)
        print(f"apk={item.filename}|{len(dados)}|{hashlib.sha256(dados).hexdigest()}")
PY

info "Extraindo os arquivos DEX do APK base (inclui o DEX de assets/)"
python3 - "$DIR_APKS/$NOME_APK_BASE" "$DIR_DEX" <<'PY'
import hashlib, os, sys, zipfile

apk, destino = sys.argv[1], sys.argv[2]
os.makedirs(destino, exist_ok=True)
with zipfile.ZipFile(apk) as z:
    for item in z.infolist():
        if not item.filename.endswith(".dex"):
            continue
        dados = z.read(item.filename)
        # 'assets/audience_network/classes.dex' -> 'assets_audience_network_classes.dex'
        nome = item.filename.replace("/", "_")
        with open(os.path.join(destino, nome), "wb") as f:
            f.write(dados)
        print(f"dex={item.filename}|{nome}|{len(dados)}|{hashlib.sha256(dados).hexdigest()}")
PY

ok "Extração concluída em $DIR_APKS e $DIR_DEX"
