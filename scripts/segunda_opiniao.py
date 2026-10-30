#!/usr/bin/env python3
"""Usa o decompilador DAD em métodos delimitados, sem substituir o bytecode original."""
import hashlib
import json
from pathlib import Path
from loguru import logger
from androguard.core.dex import DEX
from androguard.core.analysis.analysis import Analysis
from androguard.decompiler.decompile import DvMethod

RAIZ = Path(__file__).resolve().parents[1]
logger.disable('androguard')


def main():
    caminho = RAIZ / '.trabalho/extraido/apks/com.nexstreaming.app.kinemasterfree/classes.dex'
    dex = DEX(caminho.read_bytes())
    analise = Analysis(dex)
    alvos = [('Lcom/kinemaster/app/base/KineMasterApplication;', 'c', '()V'),
             ('Lcom/kinemaster/app/database/project/ProjectDatabase_Impl;', 'clearAllTables', '()V')]
    registros = []
    destino = RAIZ / 'decompilado/gerado/dad'
    destino.mkdir(parents=True, exist_ok=True)
    for classe, nome, descritor in alvos:
        registro = {'classe': classe, 'metodo': nome, 'descritor': descritor, 'ferramenta': 'DAD do Androguard 4.1.4',
                    'entrada_sha256': hashlib.sha256(caminho.read_bytes()).hexdigest()}
        try:
            candidatos = [m for m in dex.get_encoded_methods() if m.get_class_name() == classe and m.get_name() == nome and m.get_descriptor().replace(' ', '') == descritor]
            if len(candidatos) != 1:
                raise ValueError('Método não identificado univocamente.')
            decompilador = DvMethod(analise.get_method(candidatos[0]))
            decompilador.process()
            fonte = decompilador.get_source()
            if not fonte or not fonte.strip():
                raise ValueError('Fonte vazia não é recuperação.')
            arquivo = destino / (classe[1:-1].replace('/', '_') + '.' + nome + '.java')
            arquivo.write_text(fonte)
            registro.update(estado='texto aproximado produzido, sem validação semântica completa',
                             arquivo=arquivo.relative_to(RAIZ).as_posix(), tamanho=len(fonte.encode()),
                             sha256=hashlib.sha256(fonte.encode()).hexdigest())
        except Exception as erro:
            registro.update(estado='falha', erro=str(erro), tipo_erro=type(erro).__name__)
        registros.append(registro)
        print(f'{classe}->{nome}{descritor}: {registro["estado"]}', flush=True)
    (RAIZ / 'docs/analise/inventarios/segunda-opiniao.json').write_text(json.dumps(registros, ensure_ascii=False, indent=2) + '\n')


if __name__ == '__main__':
    main()
