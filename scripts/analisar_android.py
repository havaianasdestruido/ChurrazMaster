#!/usr/bin/env python3
"""Decodifica declarações/recursos e indexa classes, métodos e chamadas selecionadas."""
import argparse
import gc
import gzip
import hashlib
import json
from pathlib import Path
import struct
import zlib
from loguru import logger
from lxml import etree
from androguard.core.apk import APK
from androguard.core.axml import AXMLPrinter
from androguard.core.dex import DEX

logger.disable('androguard')
RAIZ = Path(__file__).resolve().parents[1]
INV = RAIZ / 'docs/analise/inventarios'
GERADO = RAIZ / 'decompilado/gerado/android'
ANDROID = '{http://schemas.android.com/apk/res/android}'


def salvar(caminho, dados):
    caminho.parent.mkdir(parents=True, exist_ok=True)
    caminho.write_text(json.dumps(dados, ensure_ascii=False, indent=2) + '\n')


def compactar(caminho, itens):
    dados = ''.join(json.dumps(i, ensure_ascii=False) + '\n' for i in itens).encode()
    caminho.parent.mkdir(parents=True, exist_ok=True)
    caminho.write_bytes(gzip.compress(dados, mtime=0))


def analisar_apks():
    resumos = []
    for caminho in sorted((RAIZ / '.trabalho/extraido/pacote').glob('*.apk')):
        print(f'Decodificando APK: {caminho.name}', flush=True)
        pasta = GERADO / caminho.stem
        pasta.mkdir(parents=True, exist_ok=True)
        apk = APK(str(caminho))
        manifesto = apk.get_android_manifest_xml()
        (pasta / 'AndroidManifest.xml').write_bytes(etree.tostring(manifesto, pretty_print=True, encoding='utf-8', xml_declaration=True))
        componentes = []
        for tipo in ['activity', 'activity-alias', 'service', 'receiver', 'provider']:
            for elemento in manifesto.iter(tipo):
                componentes.append({'tipo': tipo, 'atributos': {k.replace(ANDROID, 'android:'): v for k, v in elemento.attrib.items()},
                                    'acoes': [a.get(ANDROID + 'name') for a in elemento.iter('action')]})
        xml = []
        for membro in apk.get_files():
            if not membro.endswith('.xml') or membro == 'AndroidManifest.xml':
                continue
            try:
                dados = apk.get_file(membro)
                if dados.startswith(b'\x03\x00\x08\x00'):
                    impressor = AXMLPrinter(dados)
                    if not impressor.is_valid():
                        raise ValueError('XML binário inválido')
                    dados = impressor.get_xml(pretty=True)
                    modo = 'XML binário decodificado'
                else:
                    etree.fromstring(dados)
                    modo = 'XML textual validado'
                alvo = pasta / 'xml' / membro
                alvo.parent.mkdir(parents=True, exist_ok=True)
                alvo.write_bytes(dados)
                xml.append({'arquivo': membro, 'estado': modo, 'tamanho_decodificado': len(dados),
                            'sha256_decodificado': hashlib.sha256(dados).hexdigest()})
            except Exception as erro:
                xml.append({'arquivo': membro, 'estado': 'falha', 'erro': str(erro)})
        recursos = {'presente': False}
        try:
            tabela = apk.get_android_resources()
            if tabela is not None:
                dados = tabela.get_strings_resources()
                (pasta / 'strings-todos-idiomas.xml').write_bytes(dados)
                pacotes = tabela.get_packages_names()
                recursos = {'presente': True, 'pacotes': pacotes, 'bytes_strings_decodificadas': len(dados)}
                for pacote in pacotes:
                    (pasta / f'{pacote}-public.xml').write_bytes(tabela.get_public_resources(pacote))
        except Exception as erro:
            recursos = {'presente': True, 'erro': str(erro)}
        certificados = [hashlib.sha256(c.dump()).hexdigest() for c in apk.get_certificates()]
        resumos.append({
            'apk': caminho.name, 'sha256': hashlib.sha256(caminho.read_bytes()).hexdigest(),
            'pacote': apk.get_package(), 'versao': apk.get_androidversion_name(), 'codigo_versao': apk.get_androidversion_code(),
            'sdk_minimo': apk.get_min_sdk_version(), 'sdk_alvo': apk.get_target_sdk_version(),
            'split': manifesto.get('split'), 'atributos_manifesto': dict(manifesto.attrib),
            'permissoes': apk.get_permissions(), 'componentes': componentes,
            'assinatura': {'v1_presente': apk.is_signed_v1(), 'v2_presente': apk.is_signed_v2(),
                           'v3_presente': apk.is_signed_v3(), 'v31_presente': apk.is_signed_v31(),
                           'certificados_sha256': certificados,
                           'verificacao_criptografica_integral': 'não realizada; presença e certificado não provam autenticidade'},
            'xml': xml, 'recursos': recursos})
        del apk
        gc.collect()
    salvar(INV / 'android.json', resumos)


def analisar_dex(caminho):
    identidade = caminho.relative_to(RAIZ / '.trabalho/extraido/apks').as_posix().replace('/', '__')
    print(f'Indexando DEX: {identidade}', flush=True)
    dados = caminho.read_bytes()
    resumo = {'arquivo': caminho.relative_to(RAIZ).as_posix(), 'sha256': hashlib.sha256(dados).hexdigest(),
              'magic': dados[:8].decode('ascii', errors='replace'), 'tamanho': len(dados),
              'adler32_valido': struct.unpack_from('<I', dados, 8)[0] == zlib.adler32(dados[12:]),
              'sha1_interno_valido': dados[12:32] == hashlib.sha1(dados[32:]).digest()}
    for chave, posicao in [('strings_referenciadas', 56), ('tipos_referenciados', 64), ('metodos_referenciados', 88), ('classes_definidas_cabecalho', 96)]:
        resumo[chave] = struct.unpack_from('<I', dados, posicao)[0]
    dex = DEX(dados)
    classes, metodos, nativos, chamadas, falhas = [], [], [], [], []
    termos = ('Ljava/lang/reflect/', 'Ljava/lang/Class;->forName', 'Ljava/lang/System;->load',
              'Ljava/lang/Runtime;->load', 'Ldalvik/system/', 'Landroid/database/sqlite/',
              'Lcom/nexstreaming/kminternal/nexvideoeditor/NexEditor;',
              'Lcom/kinemaster/module/nexeditormodule/nexvideoeditor/NexEditor;')
    for classe in dex.get_classes():
        membros = classe.get_methods()
        classes.append({'classe': classe.get_name(), 'superclasse': classe.get_superclassname(),
                        'interfaces': classe.get_interfaces(), 'acesso': classe.get_access_flags_string(),
                        'campos': len(classe.get_fields()), 'metodos_definidos': len(membros)})
        for metodo in membros:
            item = {'classe': metodo.get_class_name(), 'metodo': metodo.get_name(),
                    'descritor': metodo.get_descriptor(), 'acesso': metodo.get_access_flags_string(),
                    'codigo_off': metodo.get_code_off()}
            metodos.append(item)
            if metodo.get_access_flags() & 0x100:
                nativos.append(item)
            try:
                for deslocamento, instrucao in metodo.get_instructions_idx():
                    if not instrucao.get_name().startswith('invoke'):
                        continue
                    texto = instrucao.get_output()
                    if any(t in texto for t in termos):
                        chamadas.append({**item, 'deslocamento': deslocamento, 'instrucao': instrucao.get_name(), 'destino': texto})
            except Exception as erro:
                falhas.append({**item, 'erro': str(erro)})
    for nome, itens in [('classes', classes), ('metodos', metodos), ('nativos', nativos), ('chamadas', chamadas), ('falhas', falhas)]:
        compactar(INV / 'dex' / f'{identidade}.{nome}.jsonl.gz', itens)
    resumo.update(classes_indexadas=len(classes), metodos_definidos=len(metodos),
                  metodos_com_codigo=sum(bool(m['codigo_off']) for m in metodos),
                  metodos_nativos=len(nativos), chamadas_selecionadas=len(chamadas), falhas_instrucoes=len(falhas))
    salvar(INV / 'dex' / f'{identidade}.json', resumo)


def main():
    argumentos = argparse.ArgumentParser(description=__doc__)
    argumentos.add_argument('--somente', choices=['apks', 'dex'])
    args = argumentos.parse_args()
    if args.somente != 'dex':
        analisar_apks()
    if args.somente != 'apks':
        for caminho in sorted((RAIZ / '.trabalho/extraido/apks').rglob('*.dex')):
            analisar_dex(caminho)
            gc.collect()
    print('Análise Android encerrada; consulte falhas e limites nos índices.', flush=True)


if __name__ == '__main__':
    main()
