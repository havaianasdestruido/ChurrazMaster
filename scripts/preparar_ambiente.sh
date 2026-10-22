#!/usr/bin/env bash
# Prepara o ambiente reproduzível de análise do KineMaster.
#
# 1. Cria o venv Python em KM_VENV e instala as versões fixadas de bin/tools/requirements.txt.
# 2. Verifica os hashes das ferramentas versionadas em bin/tools/ (bin/tools/SHA256SUMS.txt).
# 3. Verifica Java, JADX, Apktool e binutils (readelf/objdump/nm).
#
# Uso:  bash scripts/preparar_ambiente.sh
# Falhas são reportadas com erro explícito e código de saída diferente de zero.
set -euo pipefail

cd "$(dirname "$0")/.."
# shellcheck source=scripts/ambiente.sh
source scripts/ambiente.sh

erro() { echo "ERRO: $*" >&2; exit 1; }
ok()   { echo "OK:   $*"; }

command -v python3 >/dev/null 2>&1 || erro "python3 não encontrado no PATH"
mkdir -p "$KM_WORKDIR"

# 1. Ambiente Python fixado
if [ ! -x "$KM_PYTHON" ]; then
    python3 -m venv "$KM_VENV" || erro "falha ao criar o venv em $KM_VENV"
fi
"$KM_PYTHON" -m pip install -q -r bin/tools/requirements.txt || erro "falha ao instalar bin/tools/requirements.txt"
ok "dependências Python instaladas em $KM_VENV"

# 2. Hashes das ferramentas versionadas
( cd bin/tools && sha256sum -c --quiet SHA256SUMS.txt ) || erro "hash divergente em bin/tools/ (ver SHA256SUMS.txt)"
ok "hashes de bin/tools/ conferem com bin/tools/SHA256SUMS.txt ($(wc -l < bin/tools/SHA256SUMS.txt) arquivos)"

# 3. Ferramentas de execução
[ -n "${JAVA_HOME:-}" ] || erro "runtime Java do jdk4py não encontrado em $KM_VENV (rode novamente este script)"
JAVA_VERSAO="$("$JAVA_HOME/bin/java" -version 2>&1 | head -1)"
ok "Java: $JAVA_VERSAO"

JADX_VERSAO="$("$KM_JADX_HOME/bin/jadx" --version 2>/dev/null | tail -1)"
[ -n "$JADX_VERSAO" ] || erro "JADX não executou"
ok "JADX: $JADX_VERSAO"

APKTOOL_VERSAO="$(java -jar "$KM_APKTOOL_JAR" --version 2>/dev/null | tail -1)"
[ -n "$APKTOOL_VERSAO" ] || erro "Apktool não executou"
ok "Apktool: $APKTOOL_VERSAO"

for b in readelf objdump nm strings; do
    command -v "$b" >/dev/null 2>&1 || erro "binutils: '$b' ausente (instale o pacote binutils do sistema)"
done
ok "binutils disponível ($(readelf --version | head -1))"

echo "Ambiente pronto. KM_WORKDIR=$KM_WORKDIR"
