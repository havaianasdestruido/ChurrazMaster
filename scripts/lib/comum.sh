#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# comum.sh — funções e variáveis compartilhadas pelos scripts do projeto
#
# Uso:  source "$(dirname "$0")/lib/comum.sh"
#
# Variáveis de ambiente reconhecidas:
#   DIR_TRABALHO  diretório de artefatos intermediários (padrão: /tmp/churraz-trabalho)
#   JAVA_HOME     JDK/JRE usado por jadx e apktool (padrão: extraído do wheel jdk4py)
# ---------------------------------------------------------------------------

# Raiz do repositório (dois níveis acima de scripts/lib)
RAIZ_PROJETO="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

# Diretório de trabalho: intermediários grandes, NÃO versionados.
# O padrão fica fora do repositório para não inflar o Git nem os snapshots do agente.
DIR_TRABALHO="${DIR_TRABALHO:-/tmp/churraz-trabalho}"

# Nome base dos volumes 7z originais em bin/
NOME_BASE_7Z="KineMaster+-+AI+Video+Editor_8.2.14.37398.GP_APKPure.7z"
NOME_XAPK="KineMaster+-+AI+Video+Editor_8.2.14.37398.GP_APKPure.xapk"
NOME_APK_BASE="com.nexstreaming.app.kinemasterfree.apk"

# Ferramentas versionadas no próprio repositório
DIR_JADX="$RAIZ_PROJETO/bin/tools/android/jadx"
JAR_APKTOOL="$RAIZ_PROJETO/bin/tools/android/apktool/apktool_2.4.1.jar"
JAR_JADX_MCP="$RAIZ_PROJETO/bin/tools/android/jadx-mcp/jadx-mcp-0.7.0.jar"

# Subdiretórios do diretório de trabalho (numeração = ordem de execução)
DIR_XAPK="$DIR_TRABALHO/01_xapk"
DIR_APKS="$DIR_TRABALHO/02_apks"
DIR_DEX="$DIR_TRABALHO/03_dex"
DIR_JADX_OUT="$DIR_TRABALHO/04_jadx_base"
DIR_APKTOOL="$DIR_TRABALHO/05_apktool"
DIR_NATIVO="$DIR_TRABALHO/06_nativo"

export RAIZ_PROJETO DIR_TRABALHO

# --- Saída padronizada ------------------------------------------------------
info()  { printf '[info]  %s\n' "$*"; }
ok()    { printf '[ok]    %s\n' "$*"; }
aviso() { printf '[aviso] %s\n' "$*" >&2; }
erro()  { printf '[erro]  %s\n' "$*" >&2; }

# Encerra com mensagem em português e código de saída próprio do projeto
falhar() {
    erro "$*"
    exit 1
}

# --- Verificações -----------------------------------------------------------
exigir_arquivo() {
    local caminho="$1" descricao="${2:-arquivo necessário}"
    [[ -f "$caminho" ]] || falhar "$descricao não encontrado: $caminho"
}

exigir_comando() {
    local cmd="$1" dica="${2:-}"
    command -v "$cmd" >/dev/null 2>&1 || falhar "comando '$cmd' indisponível. $dica"
}

# Resolve o JAVA_HOME a partir do wheel jdk4py instalado, quando não definido.
resolver_java() {
    if [[ -z "${JAVA_HOME:-}" ]] && command -v python3 >/dev/null 2>&1; then
        JAVA_HOME="$(python3 -c 'import jdk4py, os; print(os.path.join(jdk4py.JAVA_HOME))' 2>/dev/null || true)"
    fi
    if [[ -n "${JAVA_HOME:-}" && -x "$JAVA_HOME/bin/java" ]]; then
        export JAVA_HOME
        export PATH="$JAVA_HOME/bin:$PATH"
        return 0
    fi
    aviso "JAVA_HOME não resolvido; execute scripts/instalar_ferramentas.sh"
    return 1
}

criar_dir_trabalho() {
    mkdir -p "$DIR_TRABALHO" "$DIR_XAPK" "$DIR_APKS" "$DIR_DEX" \
             "$DIR_JADX_OUT" "$DIR_APKTOOL" "$DIR_NATIVO"
}
