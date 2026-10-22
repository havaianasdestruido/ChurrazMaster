#!/usr/bin/env python3
"""Confere a autoconsistência APK v2/RSA-SHA256; não atesta autenticidade do editor."""
import hashlib
import json
from pathlib import Path
import struct
from cryptography import x509
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import padding
from androguard.core.apk import APK
from loguru import logger

logger.disable('androguard')
RAIZ = Path(__file__).resolve().parents[1]


def digest_conteudo(dados):
    eocd = None
    for posicao in range(len(dados) - 22, max(-1, len(dados) - 65558), -1):
        if dados[posicao:posicao+4] == b'PK\x05\x06' and posicao + 22 + struct.unpack_from('<H', dados, posicao+20)[0] == len(dados):
            eocd = posicao
            break
    if eocd is None:
        raise ValueError('EOCD ZIP não encontrado ou truncado.')
    diretorio = struct.unpack_from('<I', dados, eocd + 16)[0]
    if dados[diretorio-16:diretorio] != b'APK Sig Block 42':
        raise ValueError('Bloco de assinatura APK ausente.')
    tamanho = struct.unpack_from('<Q', dados, diretorio-24)[0]
    inicio = diretorio - tamanho - 8
    if inicio < 0 or struct.unpack_from('<Q', dados, inicio)[0] != tamanho:
        raise ValueError('Comprimentos do bloco de assinatura inconsistentes.')
    cauda = bytearray(dados[eocd:])
    struct.pack_into('<I', cauda, 16, inicio)
    blocos = []
    for secao in [dados[:inicio], dados[diretorio:eocd], cauda]:
        for deslocamento in range(0, len(secao), 1024*1024):
            trecho = secao[deslocamento:deslocamento+1024*1024]
            blocos.append(hashlib.sha256(b'\xa5' + struct.pack('<I', len(trecho)) + trecho).digest())
    return hashlib.sha256(b'\x5a' + struct.pack('<I', len(blocos)) + b''.join(blocos)).digest()


def verificar(dados):
    digest = digest_conteudo(dados)
    apk = APK(dados, raw=True, skip_analysis=True)
    apk.parse_v2_signing_block()
    signatarios = []
    # API interna usada conscientemente com Androguard fixado em 4.1.4.
    for signatario in apk._v2_signing_data:
        assinaturas = dict(signatario.signatures)
        digests = dict(signatario.signed_data.digests)
        if set(assinaturas) != {0x0103} or set(digests) != {0x0103}:
            raise ValueError('Algoritmo fora do escopo validado RSA PKCS#1 v1.5/SHA-256.')
        if digests[0x0103] != digest:
            raise ValueError('Digest do conteúdo APK divergente.')
        certificado = x509.load_der_x509_certificate(signatario.signed_data.certificates[0])
        chave = certificado.public_key()
        publica_der = chave.public_bytes(serialization.Encoding.DER, serialization.PublicFormat.SubjectPublicKeyInfo)
        if publica_der != signatario.public_key:
            raise ValueError('Chave pública do certificado difere do signatário v2.')
        chave.verify(assinaturas[0x0103], signatario.signed_data._bytes, padding.PKCS1v15(), hashes.SHA256())
        signatarios.append({'certificado_sha256': certificado.fingerprint(hashes.SHA256()).hex(),
                            'algoritmo': '0x0103 RSA PKCS#1 v1.5/SHA-256', 'bits_chave': chave.key_size,
                            'digest_conteudo_confere': True, 'assinatura_signed_data_confere': True,
                            'chave_publica_confere': True})
    if not signatarios:
        raise ValueError('Nenhum signatário v2 recuperado.')
    return signatarios


def main():
    resultados = []
    for caminho in sorted((RAIZ / '.trabalho/extraido/pacote').glob('*.apk')):
        registro = {'apk': caminho.name, 'sha256': hashlib.sha256(caminho.read_bytes()).hexdigest()}
        try:
            registro.update(signatarios=verificar(caminho.read_bytes()), estado='autoconsistência v2 confirmada')
        except Exception as erro:
            registro.update(estado='falha ou algoritmo não suportado', erro=str(erro), tipo_erro=type(erro).__name__)
        resultados.append(registro)
    base = RAIZ / '.trabalho/extraido/pacote/com.nexstreaming.app.kinemasterfree.apk'
    mutacao = bytearray(base.read_bytes())
    mutacao[32] ^= 1
    detectada = False
    try:
        verificar(mutacao)
    except ValueError as erro:
        detectada = 'Digest do conteúdo APK divergente' in str(erro)
    saida = {'apks': resultados, 'teste_negativo_mutacao_em_memoria_detectada': detectada,
             'limites': 'Verificação independente delimitada a v2 e algoritmo 0x0103. Não valida v3/v3.1, linhagem, source stamp, confiança na CA/editor nem todas as regras de instalação Android. Nenhum original foi modificado.'}
    (RAIZ / 'docs/analise/inventarios/assinaturas-v2.json').write_text(json.dumps(saida, ensure_ascii=False, indent=2)+'\n')
    if not detectada or any(i['estado'] != 'autoconsistência v2 confirmada' for i in resultados):
        raise SystemExit('Verificação v2 incompleta ou falha no teste negativo; consulte o relatório.')
    print(f'{len(resultados)} APKs com autoconsistência v2 confirmada; mutação em memória rejeitada.')


if __name__ == '__main__':
    main()
