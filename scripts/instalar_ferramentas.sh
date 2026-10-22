#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# instalar_ferramentas.sh — prepara o ambiente reproduzível da análise
#
# 1. Instala as bibliotecas Python a partir dos wheels versionados em
#    bin/tools/utilitarios/wheels (com fallback para o PyPI).
# 2. Instala o wheel do JDK (jdk4py) e extrai a árvore em bin/tools/java/jdk/.
# 3. Verifica Java, jadx, Apktool e o servidor MCP do jadx.
# 4. Confere os hashes SHA-256 dos artefatos versionados.
#
# Não instala Ghidra nem Android SDK (indisponíveis neste ambiente; ver
# bin/tools/MANIFESTO.md §4).
# ---------------------------------------------------------------------------
set -euo pipefail
source "$(dirname "$0")/lib/comum.sh"

DIR_WHEELS="$RAIZ_PROJETO/bin/tools/utilitarios/wheels"
DIR_JAVA_WHEEL="$RAIZ_PROJETO/bin/tools/java"
DIR_JDK="$DIR_JAVA_WHEEL/jdk"

info "1/4 — bibliotecas Python"
if compgen -G "$DIR_WHEELS/*.whl" > /dev/null; then
    pip3 install --break-system-packages --no-index --find-links "$DIR_WHEELS" \
        py7zr==1.1.4 androguard==4.1.4 pyelftools==0.33 lief==1.0.0 capstone==5.0.9 \
        || falhar "falha ao instalar wheels locais"
else
    aviso "wheels locais ausentes; instalando do PyPI"
    pip3 install --break-system-packages py7zr==1.1.4 androguard==4.1.4 pyelftools==0.33 \
        lief==1.0.0 capstone==5.0.9 || falhar "falha ao instalar do PyPI"
fi
ok "bibliotecas Python instaladas"

info "2/4 — JDK (wheel jdk4py)"
if [[ ! -x "$DIR_JDK/bin/java" ]]; then
    if compgen -G "$DIR_JAVA_WHEEL/jdk4py-*.whl" > /dev/null; then
        pip3 install --break-system-packages --no-index --find-links "$DIR_JAVA_WHEEL" jdk4py==25.0.2.1 \
            || falhar "falha ao instalar o wheel do jdk4py"
    else
        pip3 install --break-system-packages jdk4py==25.0.2.1 || falhar "falha ao instalar jdk4py do PyPI"
    fi
    ORIGEM_JAVA="$(python3 -c 'import jdk4py, os; print(os.path.join(jdk4py.JAVA_HOME))')"
    mkdir -p "$DIR_JAVA_WHEEL"
    # cópia da árvore para dentro de bin/tools/java/jdk (derivada, ignorada pelo Git)
    cp -a "$ORIGEM_JAVA" "$DIR_JDK"
fi
resolver_java || falhar "JDK indisponível após instalação"
java -version 2>&1 | head -1
ok "JDK pronto em $DIR_JDK (observação: é um JRE, sem javac — ver MANIFESTO §2.1)"

info "3/4 — verificação das ferramentas Java"
"$DIR_JADX/bin/jadx" --version 2>/dev/null | tail -1 || falhar "jadx não executou"
java -jar "$JAR_APKTOOL" --version 2>/dev/null | tail -1 || falhar "Apktool não executou"
ok "jadx e Apktool respondem"

info "4/4 — hashes dos artefatos versionados"
sha256sum "$JAR_APKTOOL" "$JAR_JADX_MCP" \
          "$DIR_JADX/lib/jadx-core-1.1.0.jar" "$DIR_JADX/lib/jadx-cli-1.1.0.jar" \
          "$DIR_JAVA_WHEEL"/jdk4py-*.whl "$DIR_WHEELS"/*.whl

ok "Ambiente pronto. Próximo passo: scripts/00_verificar_integridade.sh"
