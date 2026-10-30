#!/usr/bin/env python3
"""Registra presença de namespaces e versões declaradas nos metadados empacotados."""
from collections import Counter
import gzip
import hashlib
import json
from pathlib import Path

RAIZ = Path(__file__).resolve().parents[1]
INV = RAIZ / 'docs/analise/inventarios'


def main():
    classes = []
    for caminho in (INV / 'dex').glob('*.classes.jsonl.gz'):
        classes.extend(json.loads(l)['classe'] for l in gzip.decompress(caminho.read_bytes()).decode().splitlines())
    prefixos = ['Landroidx/', 'Lkotlin/', 'Lkotlinx/', 'Lcom/kinemaster/', 'Lcom/nexstreaming/',
                'Lcom/nextreaming/', 'Lcom/squareup/wire/', 'Lokhttp3/', 'Lokio/', 'Lcom/facebook/ads/',
                'Lcom/google/android/gms/', 'Lcom/google/firebase/', 'Lcom/google/protobuf/',
                'Lcom/bytedance/', 'Lcom/applovin/', 'Lcom/ironsource/', 'Lcom/mbridge/',
                'Lco/ab180/', 'Lio/grpc/']
    namespaces = {p: sum(c.startswith(p) for c in classes) for p in prefixos}
    propriedades = []
    base = RAIZ / '.trabalho/extraido/apks/com.nexstreaming.app.kinemasterfree'
    for caminho in sorted(base.glob('*.properties')):
        itens = {}
        for linha in caminho.read_text(errors='replace').splitlines():
            if '=' not in linha:
                continue
            chave, valor = linha.split('=', 1)
            if chave in ('version', 'client') or chave.endswith('_client'):
                itens[chave] = valor
        propriedades.append({'arquivo': caminho.name, 'sha256': hashlib.sha256(caminho.read_bytes()).hexdigest(),
                             'declaracoes_de_versao': itens})
    familias_chamadas = Counter()
    for caminho in (INV / 'dex').glob('*.chamadas.jsonl.gz'):
        for linha in gzip.decompress(caminho.read_bytes()).decode().splitlines():
            chamada = json.loads(linha)['destino']
            for termo in ['Ljava/lang/reflect/', 'Ljava/lang/Class;->forName', 'Ljava/lang/System;->load',
                          'Ljava/lang/Runtime;->load', 'Ldalvik/system/', 'Landroid/database/sqlite/',
                          'Lcom/kinemaster/module/nexeditormodule/nexvideoeditor/NexEditor;']:
                if termo in chamada:
                    familias_chamadas[termo] += 1
    resultado = {'ocorrencias_de_classes_por_namespace': namespaces,
                 'metadados_de_bibliotecas': propriedades,
                 'chamadas_estaticas_selecionadas_por_familia': dict(familias_chamadas),
                 'limites': 'Namespaces demonstram presença estática, não execução ou autoria integral. Versões são declarações empacotadas, sem conferência com o código upstream. Métodos nativos e reflexão impedem completude de referências.'}
    (INV / 'dependencias-aplicativo.json').write_text(json.dumps(resultado, ensure_ascii=False, indent=2)+'\n')
    print(f'{len(propriedades)} metadados de bibliotecas catalogados; referências estáticas selecionadas contabilizadas.')


if __name__ == '__main__':
    main()
