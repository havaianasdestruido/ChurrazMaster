#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# 04_decompilar_jadx.sh — Fase 3: descompilação do código gerenciado
#
# Duas execuções independentes:
#   1. APK base (classes.dex … classes6.dex) -> $DIR_JADX_OUT
#   2. DEX embutido em assets/audience_network/classes.dex -> $DIR_TRABALHO/04_jadx_audience
#      (esse DEX não é carregado pelo ClassLoader do aplicativo no boot; é
#      descompilado em árvore separada para não misturar procedências)
#
# Opções: --show-bad-code preserva código parcial em vez de descartar a classe,
# o que aumenta a recuperação e deixa explícitos os pontos problemáticos.
# ---------------------------------------------------------------------------
set -euo pipefail
source "$(dirname "$0")/lib/comum.sh"

resolver_java || falhar "JDK necessário para o jadx"
exigir_arquivo "$DIR_JADX/bin/jadx" "script do jadx"

export JAVA_OPTS="${JAVA_OPTS:--Xmx2400m}"

info "1/2 — descompilando o APK base (isto pode levar vários minutos)"
"$DIR_JADX/bin/jadx" -d "$DIR_JADX_OUT" --show-bad-code --threads-count 2 \
    "$DIR_APKS/$NOME_APK_BASE" > "$DIR_TRABALHO/04_jadx_base.log" 2>&1 || \
    aviso "jadx terminou com código de erro; consulte $DIR_TRABALHO/04_jadx_base.log"

info "2/2 — descompilando o DEX embutido em assets/"
if [[ -f "$DIR_DEX/assets_audience_network_classes.dex" ]]; then
    export JAVA_OPTS="-Xmx1500m"
    "$DIR_JADX/bin/jadx" -d "$DIR_TRABALHO/04_jadx_audience" --show-bad-code \
        "$DIR_DEX/assets_audience_network_classes.dex" > "$DIR_TRABALHO/04_jadx_audience.log" 2>&1 || \
        aviso "jadx (audience) terminou com erro; consulte o log"
else
    aviso "DEX de assets/ ausente; etapa ignorada"
fi

# Métricas objetivas do resultado
arquivos=$(find "$DIR_JADX_OUT/sources" -name "*.java" 2>/dev/null | wc -l)
erros=$(grep -c "^ERROR - Error saving class" "$DIR_TRABALHO/04_jadx_base.log" 2>/dev/null || true)
info "arquivos .java gerados: $arquivos"
info "classes que falharam ao salvar: ${erros:-0}"
info "erros de geração de código (JADX ERROR): $(grep -c 'JADX ERROR' "$DIR_JADX_OUT" -r 2>/dev/null | head -1 || echo 0)"

ok "Descompilação concluída. Próximo passo: scripts/05_metricas_dex.py"
