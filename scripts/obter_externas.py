#!/usr/bin/env python3
"""Obtém uma distribuição oficial grande com SHA-256 fixado, sem instalá-la globalmente."""
import argparse
import hashlib
import json
from pathlib import Path
import urllib.request

RAIZ = Path(__file__).resolve().parents[1]


def main():
    argumentos = argparse.ArgumentParser(description=__doc__)
    argumentos.add_argument('ferramenta', choices=['jadx', 'jdk', 'ghidra'])
    args = argumentos.parse_args()
    item = next(i for i in json.loads((RAIZ / 'bin/tools/externas.json').read_text()) if i['nome'] == args.ferramenta)
    destino = RAIZ / '.trabalho/downloads' / item['name']
    destino.parent.mkdir(parents=True, exist_ok=True)
    if destino.exists():
        raise SystemExit('Destino já existe; verifique seu hash antes de reutilizar ou remover.')
    temporario = destino.with_suffix(destino.suffix + '.parcial')
    calculo = hashlib.sha256()
    try:
        with urllib.request.urlopen(item['browser_download_url'], timeout=60) as entrada, temporario.open('xb') as saida:
            while bloco := entrada.read(1048576):
                calculo.update(bloco)
                saida.write(bloco)
        if calculo.hexdigest() != item['sha256'] or temporario.stat().st_size != item['size']:
            raise ValueError('Hash ou tamanho divergente dos metadados oficiais fixados.')
        temporario.rename(destino)
    except Exception as erro:
        raise SystemExit(f'Não foi possível obter a ferramenta: {erro}. Arquivo parcial, se existir, foi preservado para inspeção.') from erro
    print(f'Distribuição validada em {destino.relative_to(RAIZ)}. Consulte licenças e dependências antes de extrair.')


if __name__ == '__main__':
    main()
