#!/usr/bin/env python3
"""Testa a hipótese de registros zlib no prefixo NEF; não especifica o formato inteiro."""
import hashlib
import json
from pathlib import Path
import struct
import zlib

RAIZ = Path(__file__).resolve().parents[1]
MAGIA_REGISTRO = b'\x01\xda\xfe\xef'
LIMITE = 32 * 1024 * 1024


def analisar(dados):
    registros, posicao = [], 0
    while dados[posicao:posicao + 4] == MAGIA_REGISTRO:
        if posicao + 20 > len(dados):
            raise ValueError('Cabeçalho NEF truncado.')
        _, campo_4, campo_8, compactado, expandido = struct.unpack_from('<5I', dados, posicao)
        if compactado > LIMITE or expandido > LIMITE or posicao + 20 + compactado > len(dados):
            raise ValueError('Comprimento NEF inválido ou acima do limite de segurança.')
        trecho = dados[posicao + 20:posicao + 20 + compactado]
        motor = zlib.decompressobj()
        corpo = motor.decompress(trecho, LIMITE + 1)
        if not motor.eof or motor.unused_data or motor.unconsumed_tail or len(corpo) != expandido:
            raise ValueError('Registro zlib inconsistente com os comprimentos declarados.')
        registros.append({'deslocamento': posicao, 'campo_4_desconhecido': campo_4,
                           'campo_8_desconhecido': campo_8, 'tamanho_compactado': compactado,
                           'tamanho_expandido': expandido, 'sha256_expandido': hashlib.sha256(corpo).hexdigest(),
                           'jpeg_por_assinatura': corpo.startswith(b'\xff\xd8\xff'), 'corpo': corpo})
        posicao += 20 + compactado
    return registros, posicao


def main():
    resultados = []
    destino = RAIZ / 'decompilado/gerado/nef'
    destino.mkdir(parents=True, exist_ok=True)
    for caminho in sorted((RAIZ / 'decompilado/gerado/assets-internos').rglob('*.nef')):
        dados = caminho.read_bytes()
        registros, final = analisar(dados)
        for numero, registro in enumerate(registros):
            corpo = registro.pop('corpo')
            (destino / f'{caminho.name}.{numero}.expandido').write_bytes(corpo)
        resultados.append({'arquivo': caminho.relative_to(RAIZ).as_posix(), 'tamanho': len(dados),
                           'sha256': hashlib.sha256(dados).hexdigest(), 'registros_zlib': registros,
                           'deslocamento_cauda_nao_compreendida': final,
                           'tamanho_cauda': len(dados) - final,
                           'sha256_cauda': hashlib.sha256(dados[final:]).hexdigest(),
                           'cabecalho_cauda_hex_24_bytes': dados[final:final + 24].hex(),
                           'limite': 'Campos 4/8 e cauda não interpretados; JPEG identificado por assinatura, não renderizado.'})
    (RAIZ / 'docs/analise/inventarios/formatos/nef.json').write_text(json.dumps(resultados, ensure_ascii=False, indent=2) + '\n')
    print(f'{len(resultados)} NEFs testados; {sum(len(r["registros_zlib"]) for r in resultados)} registros zlib validados.')


if __name__ == '__main__':
    main()
