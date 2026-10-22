#!/usr/bin/env python3
"""Executa JADX por DEX, preservando erros, tempo, hashes e índices de saída."""
import argparse
from collections import Counter
import hashlib
import json
import os
from pathlib import Path
import subprocess
import time

RAIZ = Path(__file__).resolve().parents[1]


def main():
    argumentos = argparse.ArgumentParser(description=__doc__)
    argumentos.add_argument('--por-dex', action='store_true', help='Repete a passagem inicial isolada, com suas limitações de hierarquia.')
    args = argumentos.parse_args()
    modo = 'jadx' if args.por_dex else 'jadx-agrupado'
    destino = RAIZ / 'decompilado/gerado' / modo
    destino.mkdir(parents=True, exist_ok=True)
    relatorios = RAIZ / 'docs/analise/inventarios' / modo
    relatorios.mkdir(parents=True, exist_ok=True)
    ambiente = os.environ.copy()
    ambiente['JAVA_HOME'] = str(RAIZ / '.trabalho/ferramentas/java/jdk4py/java-runtime')
    ambiente['JAVA_OPTS'] = '-Xmx1800m'
    programa = RAIZ / '.trabalho/ferramentas/jadx/package/resources/jadx-1.1.0/bin/jadx'
    entradas = sorted((RAIZ / '.trabalho/extraido/apks').rglob('*.dex')) if args.por_dex else [
        RAIZ / '.trabalho/extraido/pacote/com.nexstreaming.app.kinemasterfree.apk',
        RAIZ / '.trabalho/extraido/apks/com.nexstreaming.app.kinemasterfree/assets/audience_network/classes.dex',
    ]
    for dex in entradas:
        identidade = dex.relative_to(RAIZ / '.trabalho/extraido').as_posix().replace('/', '__') if not args.por_dex else dex.relative_to(RAIZ / '.trabalho/extraido/apks').as_posix().replace('/', '__')
        saida = destino / identidade
        registro = relatorios / f'{identidade}.json'
        if registro.exists():
            print(f'Resultado existente, não sobrescrito: {identidade}', flush=True)
            continue
        if saida.exists():
            raise SystemExit(f'Saída sem relatório; inspecione antes de retomar: {saida}')
        log = RAIZ / '.trabalho' / f'{modo}-{identidade}.log'
        comando = ['bash', str(programa), '-j', '2', '-r', '--no-inline-anonymous',
                   '--rename-flags', 'none' if args.por_dex else 'all', '--fs-case-sensitive',
                   '--log-level', 'WARN', '-d', str(saida), str(dex)]
        inicio = time.monotonic()
        print(f'Iniciando: {identidade}', flush=True)
        with log.open('w') as arquivo:
            processo = subprocess.run(comando, env=ambiente, stdout=arquivo, stderr=subprocess.STDOUT)
        fontes = list(saida.rglob('*.java'))
        problemas = []
        indice = []
        for fonte in sorted(fontes):
            dados = fonte.read_bytes()
            texto = dados.decode('utf-8', errors='replace')
            indice.append({'arquivo': fonte.relative_to(saida).as_posix(), 'tamanho': len(dados),
                           'sha256': hashlib.sha256(dados).hexdigest()})
            for numero, linha in enumerate(texto.splitlines(), 1):
                if any(m in linha for m in ('JADX ERROR', 'JADX WARNING', 'Method not decompiled',
                                            'Code decompiled incorrectly', 'UnsupportedOperationException("Method')):
                    problemas.append({'arquivo': fonte.relative_to(saida).as_posix(),
                                      'linha': numero, 'diagnostico': linha.strip()})
        texto_log = log.read_text(errors='replace')
        resumo = {
            'dex': dex.relative_to(RAIZ).as_posix(), 'sha256': hashlib.sha256(dex.read_bytes()).hexdigest(),
            'ferramenta': 'JADX 1.1.0', 'codigo_saida': processo.returncode,
            'duracao_segundos': round(time.monotonic() - inicio, 2),
            'arquivos_java': len(fontes), 'arquivos_vazios': sum(f.stat().st_size == 0 for f in fontes),
            'marcadores_de_problema': len(problemas),
            'log_sha256': hashlib.sha256(log.read_bytes()).hexdigest(),
            'diagnosticos_total': sum(l.startswith(('ERROR', 'WARN')) for l in texto_log.splitlines()),
            'diagnosticos_distintos': len(set(l for l in texto_log.splitlines() if l.startswith(('ERROR', 'WARN')))),
            'erros_distintos': list(dict.fromkeys(l for l in texto_log.splitlines() if l.startswith('ERROR')))[:30],
            'limitacao': 'Contagem de arquivos não equivale a classes/métodos recuperados; fontes podem ser incompletas.',
        }
        registro.write_text(json.dumps(resumo, ensure_ascii=False, indent=2) + '\n')
        import gzip
        diagnosticos = Counter(l for l in texto_log.splitlines() if l.startswith(('ERROR', 'WARN')))
        for nome, itens in [('fontes', indice), ('problemas', problemas),
                            ('diagnosticos', [{'diagnostico': l, 'ocorrencias': n} for l, n in sorted(diagnosticos.items())])]:
            dados = ''.join(json.dumps(i, ensure_ascii=False) + '\n' for i in itens).encode()
            (relatorios / f'{identidade}.{nome}.jsonl.gz').write_bytes(gzip.compress(dados, mtime=0))
        print(f'Finalizado: {identidade}; retorno {processo.returncode}; {len(fontes)} fontes; {len(problemas)} marcadores.', flush=True)


if __name__ == '__main__':
    main()
