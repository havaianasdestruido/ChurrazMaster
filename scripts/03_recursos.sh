#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# 03_recursos.sh — Fase 3: decodificação de manifestos e recursos com Apktool
#
# Para cada APK do XAPK (base, ABI, densidade e idiomas) gera:
#   $DIR_APKTOOL/<apk>/AndroidManifest.xml  — manifesto decodificado
#   $DIR_APKTOOL/<apk>/res/...              — recursos decodificados
#   $DIR_APKTOOL/<apk>/apktool.yml          — metadados do pacote
#
# Usa -s (--no-src): os DEX são mantidos intactos; a descompilação de código é
# responsabilidade do jadx (scripts/04_decompilar_jadx.sh).
# ---------------------------------------------------------------------------
set -euo pipefail
source "$(dirname "$0")/lib/comum.sh"

resolver_java || falhar "JDK necessário para o Apktool"
exigir_arquivo "$JAR_APKTOOL" "jar do Apktool"

mapfile -t APKS < <(find "$DIR_APKS" -maxdepth 1 -name "*.apk" | sort)
[[ ${#APKS[@]} -gt 0 ]] || falhar "nenhum APK em $DIR_APKS (execute scripts/01_extrair.sh)"

total=0; falhas=0; falhos=()
for apk in "${APKS[@]}"; do
    nome="$(basename "$apk" .apk)"
    saida="$DIR_APKTOOL/$nome"
    mkdir -p "$saida"
    if java -jar "$JAR_APKTOOL" d -f -s -o "$saida" "$apk" > "$saida.log" 2>&1; then
        total=$((total + 1))
    else
        falhas=$((falhas + 1)); falhos+=("$nome")
        aviso "falha ao decodificar $nome (ver $saida.log)"
    fi
done

info "APKs decodificados: $total de ${#APKS[@]}"
if [[ $falhas -gt 0 ]]; then
    erro "falhas: $falhas -> ${falhos[*]}"
    exit 1
fi

# Extrai as bibliotecas nativas para uma árvore dedicada à Fase 4
mkdir -p "$DIR_NATIVO"
if [[ -d "$DIR_APKTOOL/config.arm64_v8a/lib" ]]; then
    cp -a "$DIR_APKTOOL/config.arm64_v8a/lib" "$DIR_NATIVO/"
    ok "bibliotecas nativas em $DIR_NATIVO/lib"
fi

ok "Recursos decodificados em $DIR_APKTOOL"
