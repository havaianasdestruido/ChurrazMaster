#!/usr/bin/env bash
# Descompila os DEX de um APK com JADX (versão em bin/tools/android/jadx) e grava
# um resumo verificável. NÃO considera sucesso apenas o código de saída do JADX:
# conta os .java gerados, arquivos vazios e erros/avisos reportados no log.
#
# Uso:  bash scripts/decompilar_jadx.sh <arquivo.apk> <nome_da_saida>
# Saída: $KM_WORKDIR/decompilado/<nome_da_saida>/  (sources/, resources/)
#        $KM_WORKDIR/logs/<nome_da_saida>.log       (log completo do JADX)
#        docs/analise/inventarios/jadx-<nome_da_saida>.txt (resumo versionado)
set -euo pipefail

cd "$(dirname "$0")/.."
# shellcheck source=scripts/ambiente.sh
source scripts/ambiente.sh

[ "$#" -eq 2 ] || { echo "uso: $0 <arquivo.apk> <nome_da_saida>" >&2; exit 2; }
APK="$1"
NOME="$2"
[ -f "$APK" ] || { echo "ERRO: APK não encontrado: $APK" >&2; exit 1; }

SAIDA="$KM_WORKDIR/decompilado/$NOME"
LOG="$KM_WORKDIR/logs/$NOME.log"
RESUMO="docs/analise/inventarios/jadx-$NOME.txt"
mkdir -p "$(dirname "$LOG")" "$(dirname "$SAIDA")"

# O JADX grava em SAIDA; limpa execuções anteriores desta mesma saída.
rm -rf "$SAIDA"

export JADX_OPTS="${JADX_OPTS:--Xmx2g}"
set +e
"$KM_JADX_HOME/bin/jadx" -j "${KM_JADX_THREADS:-2}" --log-level INFO -d "$SAIDA" "$APK" > "$LOG" 2>&1
CODIGO=$?
set -e

JAVA_FILES=$(find "$SAIDA/sources" -name '*.java' 2>/dev/null | wc -l)
JAVA_VAZIOS=$(find "$SAIDA/sources" -name '*.java' -size 0 2>/dev/null | wc -l)
RECURSOS=$(find "$SAIDA/resources" -type f 2>/dev/null | wc -l)
AVISOS=$(grep -cE '(^|[[:space:]])WARN[[:space:]]' "$LOG" || true)
ERROS=$(grep -cE '(^|[[:space:]])ERROR[[:space:]]' "$LOG" || true)
RESUMO_FINAL=$(grep -E 'finished with errors|finished' "$LOG" | tail -1 || true)

{
    echo "# Resumo da descompilação JADX (gerado por scripts/decompilar_jadx.sh)"
    echo "apk: $(basename "$APK")"
    echo "sha256: $(sha256sum "$APK" | cut -d' ' -f1)"
    echo "jadx: $("$KM_JADX_HOME/bin/jadx" --version 2>/dev/null | tail -1)"
    echo "codigo_saida_jadx: $CODIGO"
    echo "arquivos_java_gerados: $JAVA_FILES"
    echo "arquivos_java_vazios: $JAVA_VAZIOS"
    echo "arquivos_de_recursos: $RECURSOS"
    echo "linhas_WARN_no_log: $AVISOS"
    echo "linhas_ERROR_no_log: $ERROS"
    echo "mensagem_final_jadx: $RESUMO_FINAL"
} > "$RESUMO"

cat "$RESUMO"
# Falha explícita se o JADX não gerou nada útil, mesmo com código 0.
[ "$JAVA_FILES" -gt 0 ] || { echo "ERRO: nenhum .java gerado" >&2; exit 1; }
