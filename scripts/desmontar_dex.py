#!/usr/bin/env python3
"""Preserva bytecode em smali como alternativa verificável às falhas do JADX."""
import gzip
import hashlib
import json
from pathlib import Path
import subprocess

RAIZ = Path(__file__).resolve().parents[1]


def main():
    java = RAIZ / '.trabalho/ferramentas/java/jdk4py/java-runtime/bin/java'
    bibliotecas = RAIZ / '.trabalho/ferramentas/jadx/package/resources/jadx-1.1.0/lib'
    destino = RAIZ / 'decompilado/gerado/smali'
    destino.mkdir(parents=True, exist_ok=True)
    resultados = []
    for dex in sorted((RAIZ / '.trabalho/extraido/apks').rglob('*.dex')):
        identidade = dex.relative_to(RAIZ / '.trabalho/extraido/apks').as_posix().replace('/', '__')
        saida = destino / identidade
        if saida.exists():
            raise SystemExit(f'Saída existente; não será sobrescrita: {saida}')
        comando = [str(java), '-Xmx1200m', '-cp', str(bibliotecas / '*'), 'org.jf.baksmali.Main',
                   'disassemble', '--output', str(saida), str(dex)]
        processo = subprocess.run(comando, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        indice, classes, metodos, com_codigo = [], 0, 0, 0
        for fonte in sorted(saida.rglob('*.smali')):
            dados = fonte.read_bytes()
            linhas = dados.decode().splitlines()
            classes += sum(l.startswith('.class ') for l in linhas)
            for linha in linhas:
                if linha.startswith('.method '):
                    metodos += 1
                    com_codigo += not any(t in linha.split() for t in ('native', 'abstract'))
            indice.append({'arquivo': fonte.relative_to(saida).as_posix(), 'tamanho': len(dados),
                           'sha256': hashlib.sha256(dados).hexdigest()})
        esperado = json.loads((RAIZ / 'docs/analise/inventarios/dex' / f'{identidade}.json').read_text())
        resultados.append({'dex': identidade, 'ferramenta': 'baksmali 2.3.4', 'codigo_saida': processo.returncode,
                           'diagnostico': processo.stdout.decode(errors='replace'), 'classes': classes,
                           'metodos': metodos, 'metodos_com_codigo': com_codigo,
                           'classes_conferem': classes == esperado['classes_indexadas'],
                           'metodos_conferem': metodos == esperado['metodos_definidos'],
                           'corpos_conferem': com_codigo == esperado['metodos_com_codigo']})
        indice_saida = RAIZ / 'docs/analise/inventarios/dex' / f'{identidade}.smali.jsonl.gz'
        indice_saida.write_bytes(gzip.compress(''.join(json.dumps(i, ensure_ascii=False) + '\n' for i in indice).encode(), mtime=0))
        print(f'{identidade}: {classes} classes; {metodos} métodos; retorno {processo.returncode}.', flush=True)
    (RAIZ / 'docs/analise/inventarios/smali.json').write_text(json.dumps(resultados, ensure_ascii=False, indent=2) + '\n')
    if any(r['codigo_saida'] or not all(r[k] for k in ['classes_conferem', 'metodos_conferem', 'corpos_conferem']) for r in resultados):
        raise SystemExit('Há divergências de desmontagem; consulte smali.json.')


if __name__ == '__main__':
    main()
