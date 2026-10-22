#!/usr/bin/env python3
"""Instala e observa apenas o pacote em Android descartável explicitamente autorizado."""
import argparse
import hashlib
import json
from pathlib import Path
import subprocess
import time

RAIZ = Path(__file__).resolve().parents[1]
PACOTE = 'com.nexstreaming.app.kinemasterfree'


def main():
    argumentos = argparse.ArgumentParser(description=__doc__)
    argumentos.add_argument('--adb', type=Path, required=True, help='ADB oficial previamente obtido, com licença preservada.')
    argumentos.add_argument('--sha256-adb', required=True)
    argumentos.add_argument('--serial', required=True, help='Dispositivo dedicado autorizado; nunca selecionar automaticamente.')
    argumentos.add_argument('--confirmar-ambiente-descartavel', action='store_true')
    args = argumentos.parse_args()
    if not args.confirmar_ambiente_descartavel:
        raise SystemExit('É necessário confirmar que o dispositivo é descartável e autorizado.')
    if hashlib.sha256(args.adb.read_bytes()).hexdigest() != args.sha256_adb:
        raise SystemExit('Hash do ADB divergente.')
    destino = RAIZ / 'dados-dinamicos' / time.strftime('%Y%m%d-%H%M%S', time.gmtime())
    destino.mkdir(parents=True, exist_ok=False)
    comandos = []

    def executar(*partes, obrigatorio=True):
        processo = subprocess.run([str(args.adb.resolve()), '-s', args.serial, *partes],
                                  capture_output=True, text=True, timeout=180)
        # O serial não é gravado no índice. Saídas brutas ficam excluídas do Git.
        comandos.append({'argumentos': list(partes), 'codigo_saida': processo.returncode})
        (destino / f'{len(comandos):02}.txt').write_text(processo.stdout + processo.stderr)
        if obrigatorio and processo.returncode:
            raise RuntimeError(f'Comando Android falhou; consulte {destino.name}/{len(comandos):02}.txt')
        return processo.stdout.strip()

    resumo = {'pacote': PACOTE, 'adb_sha256': args.sha256_adb, 'execucao_confirmada': False}
    try:
        resumo['android_sdk'] = executar('shell', 'getprop', 'ro.build.version.sdk')
        resumo['android_versao'] = executar('shell', 'getprop', 'ro.build.version.release')
        resumo['abis'] = executar('shell', 'getprop', 'ro.product.cpu.abilist')
        if int(resumo['android_sdk']) < 26 or 'arm64-v8a' not in resumo['abis'].split(','):
            raise RuntimeError('Android incompatível com SDK mínimo ou ABI arm64-v8a.')
        apks = sorted((RAIZ / '.trabalho/extraido/pacote').glob('*.apk'))
        if len(apks) != 20:
            raise RuntimeError('Esperados 20 APKs verificados pela extração.')
        inventario = [json.loads(l) for l in (RAIZ / 'docs/analise/inventarios/arquivos.jsonl').read_text().splitlines()]
        hashes_esperados = {i['membro']: i['sha256'] for i in inventario if i['membro'].endswith('.apk')}
        for apk in apks:
            if hashlib.sha256(apk.read_bytes()).hexdigest() != hashes_esperados.get(apk.name):
                raise RuntimeError(f'Integridade do APK divergente: {apk.name}')
        # Sem -r: não sobrescrever uma instalação preexistente e seus dados.
        instalacao = executar('install-multiple', *[str(p) for p in apks])
        if 'Success' not in instalacao:
            raise RuntimeError('O ADB não confirmou instalação bem-sucedida.')
        componente = executar('shell', 'cmd', 'package', 'resolve-activity', '--brief', PACOTE).splitlines()[-1]
        if not componente.startswith(PACOTE + '/'):
            raise RuntimeError('Atividade inicial não resolvida para o pacote esperado.')
        resumo['atividade'] = componente
        inicio = executar('shell', 'am', 'start', '-W', '-n', componente)
        if 'Status: ok' not in inicio:
            raise RuntimeError('Inicialização não confirmada pelo ActivityManager.')
        pid = executar('shell', 'pidof', PACOTE)
        if not pid or not all(p.isdigit() for p in pid.split()):
            raise RuntimeError('Processo não encontrado após inicialização.')
        resumo['execucao_confirmada'] = True
        executar('shell', 'logcat', '-d', '--pid=' + pid.split()[0], '-t', '2000', obrigatorio=False)
        executar('shell', 'dumpsys', 'package', PACOTE, obrigatorio=False)
        executar('shell', 'run-as', PACOTE, 'cat', f'/proc/{pid.split()[0]}/maps', obrigatorio=False)
        resumo['limites'] = 'Somente inicialização; sem login, edição, exportação, tráfego de rede ou cobertura funcional abrangente. run-as pode ser negado.'
    except Exception as erro:
        resumo['erro'] = str(erro)
        raise
    finally:
        resumo['comandos'] = comandos
        (destino / 'resumo.json').write_text(json.dumps(resumo, ensure_ascii=False, indent=2) + '\n')
        print(f'Evidências locais em {destino.relative_to(RAIZ)}; revisar e anonimizar antes de qualquer publicação.')


if __name__ == '__main__':
    main()
