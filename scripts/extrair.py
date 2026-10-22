#!/usr/bin/env python3
"""Preserva os volumes, valida o 7z e extrai XAPK/APKs com inventários SHA-256."""
import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
import subprocess
import zipfile

RAIZ = Path(__file__).resolve().parents[1]


def resumo(caminho):
    calculo = hashlib.sha256()
    with caminho.open('rb') as arquivo:
        for bloco in iter(lambda: arquivo.read(1048576), b''):
            calculo.update(bloco)
    return {'caminho': caminho.relative_to(RAIZ).as_posix(),
            'tamanho': caminho.stat().st_size, 'sha256': calculo.hexdigest()}


def salvar(caminho, dados):
    caminho.parent.mkdir(parents=True, exist_ok=True)
    caminho.write_text(json.dumps(dados, ensure_ascii=False, indent=2) + '\n')


def extrair_zip(origem, destino, inventario, pai):
    """Recusa duplicatas, caminhos absolutos, travessias e ligações simbólicas."""
    destino.mkdir(parents=True, exist_ok=False)
    vistos = set()
    with zipfile.ZipFile(origem) as pacote:
        for item in pacote.infolist():
            nome = PurePosixPath(item.filename)
            if (nome.is_absolute() or '..' in nome.parts or '\\' in item.filename
                    or item.filename in vistos or (item.external_attr >> 16) & 0o170000 == 0o120000):
                raise ValueError(f'Entrada de arquivo insegura ou duplicada: {item.filename}')
            vistos.add(item.filename)
            saida = destino.joinpath(*nome.parts)
            if item.is_dir():
                saida.mkdir(parents=True, exist_ok=True)
                continue
            saida.parent.mkdir(parents=True, exist_ok=True)
            with pacote.open(item) as entrada, saida.open('xb') as arquivo:
                while bloco := entrada.read(1048576):
                    arquivo.write(bloco)
            registro = resumo(saida)
            registro.update(pai=pai, membro=item.filename, crc32=f'{item.CRC:08x}',
                            tamanho_compactado=item.compress_size)
            inventario.append(registro)


def main():
    argumentos = argparse.ArgumentParser(description=__doc__)
    argumentos.add_argument('--saida', default='.trabalho/extraido')
    argumentos.add_argument('--inventarios', default='docs/analise/inventarios')
    args = argumentos.parse_args()
    saida = (RAIZ / args.saida).resolve()
    if not saida.is_relative_to(RAIZ) or saida.exists():
        raise SystemExit('A saída deve ser um diretório novo dentro do repositório.')
    volumes = sorted((RAIZ / 'bin').glob('*.7z.[0-9][0-9][0-9]'))
    if not volumes or len(list((RAIZ / 'bin').glob('*.7z.001'))) != 1:
        raise SystemExit('Esperada uma única série de volumes 7z em bin/.')
    primeiro = next((RAIZ / 'bin').glob('*.7z.001'))
    serie = primeiro.name[:-3]
    if [p.name for p in volumes] != [f'{serie}{i:03}' for i in range(1, len(volumes)+1)]:
        raise SystemExit('Volumes ausentes ou séries distintas; operação interrompida.')
    inv = RAIZ / args.inventarios
    originais = [resumo(p) for p in volumes]
    linha_base = inv / 'originais.json'
    if linha_base.exists() and json.loads(linha_base.read_text()) != originais:
        raise SystemExit('Integridade divergente da linha de base; originais não serão usados.')
    salvar(linha_base, originais)
    saida.mkdir(parents=True)
    sete = RAIZ / '.trabalho/ferramentas/7zip/package/linux/x64/7za'
    subprocess.run([str(sete), 't', str(primeiro)], check=True)
    subprocess.run([str(sete), 'x', '-y', f'-o{saida / "xapk"}', str(primeiro)], check=True)
    xapks = list((saida / 'xapk').glob('*.xapk'))
    if len(xapks) != 1:
        raise SystemExit('Quantidade inesperada de XAPKs; inspecione a extração.')
    xapk = xapks[0]
    salvar(inv / 'xapk.json', resumo(xapk))
    inventario = []
    extrair_zip(xapk, saida / 'pacote', inventario, xapk.name)
    apks = sorted((saida / 'pacote').glob('*.apk'))
    for apk in apks:
        extrair_zip(apk, saida / 'apks' / apk.stem, inventario, apk.name)
    inv.mkdir(parents=True, exist_ok=True)
    with (inv / 'arquivos.jsonl').open('w') as arquivo:
        for item in inventario:
            arquivo.write(json.dumps(item, ensure_ascii=False) + '\n')
    salvar(inv / 'extracao.json', {
        'apks': len(apks), 'membros_inventariados': len(inventario),
        'bytes_extraidos': sum(i['tamanho'] for i in inventario),
        'dex': sum(i['membro'].endswith('.dex') for i in inventario),
        'bibliotecas_so': sum(i['membro'].endswith('.so') for i in inventario),
        'originais_inalterados': [resumo(p) for p in volumes] == originais})
    print(f'Extração verificada: {len(apks)} APKs; {len(inventario)} membros com hash.')


if __name__ == '__main__':
    main()
