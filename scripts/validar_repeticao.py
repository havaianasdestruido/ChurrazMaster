#!/usr/bin/env python3
"""Compara extrações repetidas e a remontagem delimitada de classes2.dex."""
import hashlib
import json
from pathlib import Path
from loguru import logger
from androguard.core.dex import DEX

RAIZ = Path(__file__).resolve().parents[1]
logger.disable('androguard')


def normalizar_inventario(caminho):
    return sorted((i['pai'], i['membro'], i['tamanho'], i['sha256'], i['crc32'])
                  for i in (json.loads(l) for l in caminho.read_text().splitlines()))


def assinatura_dex(caminho):
    dex = DEX(caminho.read_bytes())
    return {'classes': sorted(dex.get_classes_names()),
            'metodos': sorted((m.get_class_name(), m.get_name(), m.get_descriptor(),
                               [(i.get_name(), i.get_output()) for i in m.get_instructions()])
                              for m in dex.get_encoded_methods())}


def main():
    original = RAIZ / 'docs/analise/inventarios/arquivos.jsonl'
    repetido = RAIZ / '.trabalho/inventarios-repeticao/arquivos.jsonl'
    dex = RAIZ / '.trabalho/extraido/apks/com.nexstreaming.app.kinemasterfree/classes2.dex'
    remontado = RAIZ / '.trabalho/classes2-remontado.dex'
    fontes1 = RAIZ / '.trabalho/jadx-teste-multidex'
    fontes2 = RAIZ / '.trabalho/jadx-teste-repetido'

    def hashes_fontes(pasta):
        return {p.relative_to(pasta).as_posix(): hashlib.sha256(p.read_bytes()).hexdigest() for p in pasta.rglob('*.java')}

    primeira, segunda = hashes_fontes(fontes1), hashes_fontes(fontes2)
    resultado = {'extracoes_conferem': normalizar_inventario(original) == normalizar_inventario(repetido),
                 'membros_comparados': len(normalizar_inventario(original)),
                 'remontagem': {'arquivo': 'classes2.dex', 'ferramenta': 'smali 2.3.4',
                               'sha256_original': hashlib.sha256(dex.read_bytes()).hexdigest(),
                               'sha256_remontado': hashlib.sha256(remontado.read_bytes()).hexdigest(),
                               'tamanho_original': dex.stat().st_size, 'tamanho_remontado': remontado.stat().st_size,
                               'classes_metodos_instrucoes_simbolicas_conferem': assinatura_dex(dex) == assinatura_dex(remontado),
                               'limite': 'Um DEX de uma classe/um método, sem equivalência binária nem reconstrução do APK.'},
                 'jadx_repetido': {'classes_alvo': ['com.kinemaster.app.database.project.ProjectDatabase_Impl'],
                                   'fontes_primeira': len(primeira), 'fontes_segunda': len(segunda),
                                   'hashes_conferem': bool(primeira) and primeira == segunda,
                                   'limite': 'Ensaio delimitado de uma classe; não valida determinismo de toda a saída JADX.'}}
    (RAIZ / 'docs/analise/evidencias/repetibilidade.json').write_text(json.dumps(resultado, ensure_ascii=False, indent=2)+'\n')
    if not resultado['extracoes_conferem'] or not resultado['remontagem']['classes_metodos_instrucoes_simbolicas_conferem'] or not resultado['jadx_repetido']['hashes_conferem']:
        raise SystemExit('A validação encontrou divergências; consulte repetibilidade.json.')
    print('Extração repetida, ensaio JADX e remontagem delimitada conferem; não constituem reconstrução integral.')


if __name__ == '__main__':
    main()
