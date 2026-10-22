#!/usr/bin/env bash
# Decodifica um APK com Apktool (bin/tools/android/apktool): manifesto, recursos XML,
# tabela de recursos (resources.arsc), smali de todos os DEX e assets/libs.
#
# Uso:  bash scripts/decompilar_apktool.sh <arquivo.apk> <nome_da_saida>
# Saída: $KM_WORKDIR/decompilado/apktool-<nome_da_saida>/
#        docs/analise/inventarios/apktool-<nome_da_saida>.txt (resumo versionado)
set -euo pipefail

cd "$(dirname "$0")/.."
# shellcheck source=scripts/ambiente.sh
source scripts/ambiente.sh

[ "$#" -eq 2 ] || { echo "uso: $0 <arquivo.apk> <nome_da_saida>" >&2; exit 2; }
APK="$1"
NOME="$2"
[ -f "$APK" ] || { echo "ERRO: APK não encontrado: $APK" >&2; exit 1; }

SAIDA="$KM_WORKDIR/decompilado/apktool-$NOME"
LOG="$KM_WORKDIR/logs/apktool-$NOME.log"
RESUMO="docs/analise/inventarios/apktool-$NOME.txt"
mkdir -p "$(dirname "$LOG")" "$(dirname "$SAIDA")"
rm -rf "$SAIDA"

set +e
java -Xmx1500m -jar "$KM_APKTOOL_JAR" d -f -o "$SAIDA" "$APK" > "$LOG" 2>&1
CODIGO=$?
set -e

SMALI=$(find "$SAIDA" -name '*.smali' 2>/dev/null | wc -l)
XML_RES=$(find "$SAIDA/res" -name '*.xml' 2>/dev/null | wc -l)
ERROS=$(grep -cE '(^|[[:space:]])(E|ERROR|Exception)[: ]' "$LOG" || true)

{
    echo "# Resumo da decodificação Apktool (gerado por scripts/decompilar_apktool.sh)"
    echo "apk: $(basename "$APK")"
    echo "sha256: $(sha256sum "$APK" | cut -d' ' -f1)"
    echo "apktool: $(java -jar "$KM_APKTOOL_JAR" --version 2>/dev/null | tail -1)"
    echo "codigo_saida_apktool: $CODIGO"
    echo "arquivos_smali: $SMALI"
    echo "arquivos_xml_de_recursos: $XML_RES"
    echo "linhas_de_erro_no_log: $ERROS"
    echo "manifesto_decodificado: $( [ -f "$SAIDA/AndroidManifest.xml" ] && echo sim || echo nao )"
} > "$RESUMO"

cat "$RESUMO"
[ "$CODIGO" -eq 0 ] && [ "$SMALI" -gt 0 ] || { echo "ERRO: decodificação incompleta (ver $LOG)" >&2; exit 1; }
