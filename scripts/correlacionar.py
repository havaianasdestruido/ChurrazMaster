#!/usr/bin/env python3
"""Relaciona métodos native, nomes JNI exportados e dependências ELF sem inferir runtime."""
from collections import Counter
import gzip
import json
from pathlib import Path

RAIZ = Path(__file__).resolve().parents[1]
INV = RAIZ / 'docs/analise/inventarios'


def ler(caminho):
    return [json.loads(l) for l in gzip.decompress(caminho.read_bytes()).decode().splitlines()]


def codificar_jni(texto):
    resultado = ''
    for caractere in texto:
        if caractere.isascii() and caractere.isalnum():
            resultado += caractere
        elif caractere == '/':
            resultado += '_'
        elif caractere in '_;[':
            resultado += {'_': '_1', ';': '_2', '[': '_3'}[caractere]
        else:
            # JNI representa unidades UTF-16, não pontos de código arbitrários.
            bruto = caractere.encode('utf-16-be')
            resultado += ''.join(f'_0{int.from_bytes(bruto[i:i+2], "big"):04x}' for i in range(0, len(bruto), 2))
    return resultado


def main():
    bibliotecas = json.loads((INV / 'nativo/bibliotecas.json').read_text())
    exports = {}
    nomes = {Path(b['arquivo']).name for b in bibliotecas}
    for biblioteca in bibliotecas:
        for simbolo in biblioteca['jni_exports']:
            exports.setdefault(simbolo['nome'], []).append({'biblioteca': Path(biblioteca['arquivo']).name, 'endereco': simbolo['endereco']})
    relacoes, classes = [], Counter()
    for caminho in sorted((INV / 'dex').glob('*.nativos.jsonl.gz')):
        for metodo in ler(caminho):
            curto = 'Java_' + codificar_jni(metodo['classe'][1:-1]) + '_' + codificar_jni(metodo['metodo'])
            parametros = metodo['descritor'].split(')', 1)[0][1:].replace(' ', '')
            longo = curto + '__' + codificar_jni(parametros)
            correspondencias = exports.get(longo, []) or exports.get(curto, [])
            relacoes.append({**metodo, 'dex': caminho.name.split('.nativos')[0],
                             'nome_jni_curto': curto, 'nome_jni_longo': longo,
                             'correspondencias_estaticas': correspondencias,
                             'estado': 'símbolo JNI compatível' if correspondencias else 'sem export compatível; registro dinâmico ou dependência externa não resolvidos'})
    for caminho in (INV / 'dex').glob('*.classes.jsonl.gz'):
        classes.update(i['classe'] for i in ler(caminho))
    dependencias = [{'biblioteca': Path(b['arquivo']).name, 'necessaria': n,
                     'resolucao': 'presente no pacote' if n in nomes else 'externa ao pacote; verificar no Android'}
                    for b in bibliotecas for n in b['dependencias']]
    (INV / 'jni.jsonl.gz').write_bytes(gzip.compress(''.join(json.dumps(i, ensure_ascii=False)+'\n' for i in relacoes).encode(), mtime=0))
    resultado = {'metodos_native': len(relacoes), 'com_export_compativel': sum(bool(i['correspondencias_estaticas']) for i in relacoes),
                 'sem_export_compativel': sum(not i['correspondencias_estaticas'] for i in relacoes),
                 'classes_unicas': len(classes), 'classes_repetidas_entre_dex': {k:v for k,v in classes.items() if v>1},
                 'dependencias_elf': dependencias,
                 'limites': 'Correspondência de nomes não prova biblioteca escolhida pelo carregador; RegisterNatives e dlsym não foram resolvidos.'}
    (INV / 'correlacao.json').write_text(json.dumps(resultado, ensure_ascii=False, indent=2) + '\n')
    print(f'{len(relacoes)} métodos native; {resultado["com_export_compativel"]} com símbolo compatível; {resultado["sem_export_compativel"]} pendentes.')


if __name__ == '__main__':
    main()
