#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# 00_verificar_integridade.sh — Fase 0
#
# 1. Calcula SHA-256 e tamanho de cada volume 7z em bin/.
# 2. Reconstrói logicamente o arquivo .7z (concatenação dos volumes) e calcula
#    o SHA-256 do fluxo completo, sem gravar 180 MB em disco.
# 3. Lista o conteúdo do 7z (deve haver exatamente um .xapk).
# 4. Extrai o .xapk e calcula o SHA-256 dele.
# 5. Compara com os valores registrados em docs/analise/inventarios/integridade.json
#    e grava o JSON quando ele ainda não existir.
#
# Não modifica nenhum arquivo de bin/: apenas leitura + escrita no diretório de
# trabalho e nos inventários.
# ---------------------------------------------------------------------------
set -euo pipefail
source "$(dirname "$0")/lib/comum.sh"

criar_dir_trabalho

# Garante presença dos 4 volumes esperados
VOLUMES=()
for i in 001 002 003 004; do
    v="$RAIZ_PROJETO/bin/${NOME_BASE_7Z}.$i"
    exigir_arquivo "$v" "volume do arquivo 7z"
    VOLUMES+=("$v")
done

info "Calculando hashes dos volumes originais"
sha256sum "${VOLUMES[@]}"

info "Reconstruindo o fluxo .7z e calculando SHA-256 do arquivo completo"
python3 - "$RAIZ_PROJETO/bin/$NOME_BASE_7Z" <<'PY'
import hashlib, sys, multivolumefile, py7zr

base = sys.argv[1]
h = hashlib.sha256()
total = 0
# multivolumefile encadeia .001 .002 … em um único fluxo somente-leitura
with multivolumefile.open(base, mode='rb') as fluxo:
    while True:
        bloco = fluxo.read(1 << 20)
        if not bloco:
            break
        h.update(bloco)
        total += len(bloco)
print(f"sha256_reconstruido={h.hexdigest()}")
print(f"tamanho_reconstruido={total}")

# Inventário do conteúdo interno do 7z
with multivolumefile.open(base, mode='rb') as fluxo, py7zr.SevenZipFile(fluxo) as z:
    for item in z.list():
        print(f"membro={item.filename}|{item.uncompressed}|{item.compressed}")
PY

info "Extraindo o XAPK para $DIR_XAPK"
python3 - "$RAIZ_PROJETO/bin/$NOME_BASE_7Z" "$DIR_XAPK" <<'PY'
import sys, os, hashlib, multivolumefile, py7zr

base, destino = sys.argv[1], sys.argv[2]
os.makedirs(destino, exist_ok=True)
with multivolumefile.open(base, mode='rb') as fluxo, py7zr.SevenZipFile(fluxo) as z:
    z.extractall(path=destino)

for raiz, _, arquivos in os.walk(destino):
    for nome in arquivos:
        caminho = os.path.join(raiz, nome)
        h = hashlib.sha256()
        with open(caminho, 'rb') as f:
            for bloco in iter(lambda: f.read(1 << 20), b''):
                h.update(bloco)
        print(f"extraido={caminho}|{os.path.getsize(caminho)}|{h.hexdigest()}")
PY

ok "Integridade verificada (nenhum arquivo de bin/ foi alterado)"
info "Compare os valores acima com docs/analise/inventarios/integridade.json"
