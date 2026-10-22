#!/usr/bin/env python3
"""Recupera tags Wire observadas em smali, sem inventar um arquivo .proto original."""
import hashlib
import json
from pathlib import Path
import re

RAIZ = Path(__file__).resolve().parents[1]


def campos_anotados(texto):
    atual, linhas = None, []
    for linha in texto.splitlines():
        if linha.startswith('.field '):
            atual = linha
            linhas = []
        elif atual and linha == '.end field':
            bloco = '\n'.join(linhas)
            for anotacao, corpo in re.findall(r'\.annotation runtime (L[^\s]+;)\n(.*?)\.end annotation', bloco, re.S):
                if not anotacao.startswith('Lcom/squareup/wire/'):
                    continue
                tag = re.search(r'\btag\s*=\s*(0x[0-9a-fA-F]+|\d+)', corpo)
                if tag:
                    campo = atual.split()[-1].split(':', 1)
                    yield {'campo': campo[0], 'descritor': campo[1], 'tag': int(tag.group(1), 0),
                           'anotacao_original': anotacao, 'atributos_observados': [l.strip() for l in corpo.splitlines() if l.strip()]}
            atual = None
        elif atual:
            linhas.append(linha)


def main():
    origem = RAIZ / 'decompilado/gerado/smali/com.nexstreaming.app.kinemasterfree__classes6.dex/com/nexstreaming/kinemaster/wire'
    resultado = []
    for arquivo in sorted(origem.glob('KMProto*.smali')):
        texto = arquivo.read_text()
        campos = list(campos_anotados(texto))
        if not campos:
            continue
        classe = re.search(r'^\.class .* (L\S+;)$', texto, re.M).group(1)
        tags = [c['tag'] for c in campos]
        resultado.append({'classe': classe, 'smali': arquivo.relative_to(RAIZ).as_posix(),
                           'sha256_smali': hashlib.sha256(arquivo.read_bytes()).hexdigest(),
                           'campos': campos, 'tags_duplicadas': sorted({t for t in tags if tags.count(t)>1})})
    destino = RAIZ / 'docs/analise/inventarios/formatos/esquema-wire.json'
    destino.write_text(json.dumps({'mensagens_com_tags': len(resultado), 'campos_com_tags': sum(len(i['campos']) for i in resultado),
                                   'mensagens': resultado,
                                   'limites': 'Somente tags e descritores explícitos nas anotações preservadas; nomes/semântica/contêiner de projeto e campos removidos não foram reconstruídos integralmente.'}, ensure_ascii=False, indent=2)+'\n')
    print(f'{len(resultado)} mensagens com tags Wire e {sum(len(i["campos"]) for i in resultado)} campos observados.')


if __name__ == '__main__':
    main()
