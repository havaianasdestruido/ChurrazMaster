#!/usr/bin/env python3
"""Inspeciona assets e ZIPs internos sem executar Lua, shaders ou modelos."""
from collections import Counter
import gzip
import hashlib
import json
from pathlib import Path
import zipfile
from lxml import etree
from extrair import extrair_zip

RAIZ = Path(__file__).resolve().parents[1]
INV = RAIZ / 'docs/analise/inventarios/formatos'


def identificar(dados):
    for prefixo, nome in [(b'\x89PNG\r\n\x1a\n', 'PNG'), (b'\xff\xd8\xff', 'JPEG'),
                           (b'PK\x03\x04', 'ZIP'), (b'\x7fELF', 'ELF'),
                           (b'dex\n', 'DEX'), (b'SQLite format 3\x00', 'SQLite'),
                           (b'-----BEGIN PUBLIC KEY', 'chave pública PEM'), (b'\x1bLua', 'bytecode Lua')]:
        if dados.startswith(prefixo):
            return nome
    if dados[4:8] == b'TFL3':
        return 'FlatBuffer com identificador TFL3'
    if dados[4:8] == b'ftyp':
        return 'ISO Base Media File Format'
    if dados.startswith(b'lmgg'):
        return 'cabeçalho lmgg; hipótese de família GGML a testar'
    try:
        texto = dados.decode('utf-8-sig')
        if '\x00' not in texto:
            return 'texto UTF-8'
    except UnicodeDecodeError:
        pass
    return 'binário não classificado'


def main():
    INV.mkdir(parents=True, exist_ok=True)
    base = RAIZ / '.trabalho/extraido/apks/com.nexstreaming.app.kinemasterfree/assets'
    pasta = RAIZ / 'decompilado/gerado/assets-internos'
    membros = []
    for pacote in sorted(base.rglob('*.zip')):
        destino = pasta / pacote.stem
        if destino.exists():
            raise SystemExit(f'Saída existente; não sobrescrita: {destino}')
        extrair_zip(pacote, destino, membros, pacote.relative_to(base).as_posix())
    (INV / 'membros-internos.jsonl.gz').write_bytes(gzip.compress(''.join(json.dumps(i, ensure_ascii=False) + '\n' for i in membros).encode(), mtime=0))
    registros, json_chaves, xml_tags, falhas = [], Counter(), Counter(), []
    for caminho in sorted(list(base.rglob('*')) + list(pasta.rglob('*'))):
        if not caminho.is_file():
            continue
        dados = caminho.read_bytes()
        item = {'arquivo': caminho.relative_to(RAIZ).as_posix(), 'tamanho': len(dados),
                'sha256': hashlib.sha256(dados).hexdigest(), 'formato': identificar(dados)}
        # Cabeçalhos curtos apenas de binários desconhecidos; não copiar chaves/dados completos.
        if item['formato'] in ('binário não classificado', 'cabeçalho lmgg; hipótese de família GGML a testar'):
            item['cabecalho_hex_16_bytes'] = dados[:16].hex()
        if caminho.suffix == '.json':
            try:
                valor = json.loads(dados)
                item['json_tipo_raiz'] = type(valor).__name__
                if isinstance(valor, dict):
                    item['json_chaves_raiz'] = sorted(valor)
                    json_chaves.update(valor.keys())
            except Exception as erro:
                falhas.append({'arquivo': item['arquivo'], 'etapa': 'JSON', 'erro': str(erro)})
        if caminho.suffix in ('.xml', '.nef') or dados.lstrip().startswith(b'<effect'):
            try:
                raiz = etree.fromstring(dados, parser=etree.XMLParser(resolve_entities=False, no_network=True))
                item['xml_raiz'] = raiz.tag
                item['xml_atributos_raiz'] = sorted(raiz.attrib)
                xml_tags.update(e.tag for e in raiz.iter() if isinstance(e.tag, str))
            except Exception as erro:
                falhas.append({'arquivo': item['arquivo'], 'etapa': 'XML/NEF', 'erro': str(erro)})
        registros.append(item)
    (INV / 'assets.jsonl.gz').write_bytes(gzip.compress(''.join(json.dumps(i, ensure_ascii=False) + '\n' for i in registros).encode(), mtime=0))
    resumo = {'arquivos_inspecionados': len(registros), 'membros_internos_extraidos': len(membros),
              'formatos': dict(Counter(i['formato'] for i in registros)), 'extensoes': dict(Counter(Path(i['arquivo']).suffix for i in registros)),
              'chaves_json_observadas': dict(json_chaves), 'elementos_xml_observados': dict(xml_tags),
              'falhas_de_parse': falhas,
              'limites': 'Identificadores e gramáticas observadas não são especificações completas; Lua/modelos não foram executados.'}
    (INV / 'resumo.json').write_text(json.dumps(resumo, ensure_ascii=False, indent=2) + '\n')
    print(f'{len(membros)} membros internos extraídos; {len(registros)} assets inspecionados; {len(falhas)} falhas explícitas.')


if __name__ == '__main__':
    main()
