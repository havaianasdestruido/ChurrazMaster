#!/usr/bin/env bash
# Variáveis de ambiente compartilhadas pelos scripts de engenharia reversa.
# Uso: source scripts/ambiente.sh   (a partir da raiz do repositório)
#
# Cada variável pode ser sobrescrita antes do source. Nenhum caminho absoluto
# da máquina do agente é versionado.

RAIZ_REPO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
export RAIZ_REPO

# Diretório de trabalho para artefatos grandes (fora do Git).
export KM_WORKDIR="${KM_WORKDIR:-$HOME/kinemaster-work}"
export KM_VENV="${KM_VENV:-$KM_WORKDIR/venv}"

# Ferramentas versionadas em bin/tools/.
export KM_JADX_HOME="$RAIZ_REPO/bin/tools/android/jadx"
export KM_APKTOOL_JAR="$RAIZ_REPO/bin/tools/android/apktool/apktool_2.4.1.jar"

# Java: usa o runtime Java 21 fornecido pelo pacote Python jdk4py (ver bin/tools/MANIFESTO.md).
if [ -x "$KM_VENV/lib/python3.11/site-packages/jdk4py/java-runtime/bin/java" ]; then
    export JAVA_HOME="$KM_VENV/lib/python3.11/site-packages/jdk4py/java-runtime"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

export KM_PYTHON="$KM_VENV/bin/python"
