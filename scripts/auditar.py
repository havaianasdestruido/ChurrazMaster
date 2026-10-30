#!/usr/bin/env python3
"""Consolida denominadores, falhas e lacunas sem confundir saídas com recuperação integral."""
from collections import Counter
import gzip
import hashlib
import json
from pathlib import Path
import re

RAIZ = Path(__file__).resolve().parents[1]
INV = RAIZ / 'docs/analise/inventarios'


def ler(pasta):
    return [json.loads(p.read_text()) for p in sorted(pasta.glob('*.json'))]


def auditar_jadx(modo):
    resultados = []
    for relatorio in sorted((INV / modo).glob('*.json')):
        resumo = json.loads(relatorio.read_text())
        pasta = RAIZ / 'decompilado/gerado' / modo / relatorio.stem
        marcadores, falhas, arquivos_afetados = Counter(), [], set()
        fontes = sorted(pasta.rglob('*.java'))
        vazios = []
        for arquivo in fontes:
            dados = arquivo.read_bytes()
            if not dados:
                vazios.append(arquivo.relative_to(pasta).as_posix())
            for numero, linha in enumerate(dados.decode(errors='replace').splitlines(), 1):
                for marcador in ['JADX ERROR', 'JADX WARNING', 'Code decompiled incorrectly', 'Method not decompiled:']:
                    if marcador in linha:
                        marcadores[marcador] += 1
                        arquivos_afetados.add(arquivo.relative_to(pasta).as_posix())
                achado = re.search(r'throw new UnsupportedOperationException\("Method not decompiled: ([^"]+)', linha)
                if achado:
                    falhas.append({'arquivo': arquivo.relative_to(pasta).as_posix(), 'linha': numero, 'metodo_reportado': achado.group(1)})
        (INV / modo / f'{relatorio.stem}.metodos-nao-recuperados.jsonl.gz').write_bytes(gzip.compress(''.join(json.dumps(i, ensure_ascii=False)+'\n' for i in falhas).encode(), mtime=0))
        resultados.append({'entrada': resumo['dex'], 'codigo_saida': resumo['codigo_saida'],
                           'fontes_java': len(fontes), 'fontes_vazias': vazios,
                           'fontes_com_marcadores': len(arquivos_afetados), 'marcadores': dict(marcadores),
                           'stubs_de_metodo': len(falhas), 'stubs_distintos_reportados': len(set(i['metodo_reportado'] for i in falhas)),
                           'contagem_corresponde_ao_indice': len(fontes) == resumo['arquivos_java'],
                           'limitacao': 'Não somar passagens sobrepostas nem subtrair stubs do total DEX para inventar métodos recuperados.'})
    return resultados


def main():
    arquivos = [json.loads(l) for l in (INV / 'arquivos.jsonl').read_text().splitlines()]
    android = json.loads((INV / 'android.json').read_text())
    dex = ler(INV / 'dex')
    smali = json.loads((INV / 'smali.json').read_text())
    nativo = json.loads((INV / 'nativo/bibliotecas.json').read_text())
    correlacao = json.loads((INV / 'correlacao.json').read_text())
    origem = json.loads((INV / 'originais.json').read_text())
    integridade = all(hashlib.sha256((RAIZ/i['caminho']).read_bytes()).hexdigest() == i['sha256'] for i in origem)
    linhas_base = next(a for a in android if a['split'] is None)
    manifesto_xapk = json.loads((RAIZ / '.trabalho/extraido/pacote/manifest.json').read_text())
    declarados = {a['file'] for a in manifesto_xapk['split_apks']}
    presentes = {a['apk'] for a in android}
    versoes = {a['codigo_versao'] for a in android}
    certificados = {tuple(a['assinatura']['certificados_sha256']) for a in android}
    formato = json.loads((INV / 'formatos/resumo.json').read_text())
    resultado = {
        'originais_inalterados': integridade, 'membros_primarios_inventariados': len(arquivos),
        'apks': {'encontrados': len(presentes), 'manifestos_decodificados': len(android),
                 'ausentes_em_relacao_ao_xapk': sorted(declarados-presentes),
                 'nao_declarados_no_xapk': sorted(presentes-declarados),
                 'codigos_versao_distintos': sorted(versoes), 'conjuntos_certificados_distintos': len(certificados),
                 'assinaturas_integralmente_validadas_todos_esquemas': 0,
                 'assinaturas_v2_autoconsistentes': sum(i['estado'] == 'autoconsistência v2 confirmada' for i in json.loads((INV / 'assinaturas-v2.json').read_text())['apks'])},
        'android': {'permissoes_base': len(linhas_base['permissoes']),
                    'componentes_base': dict(Counter(i['tipo'] for i in linhas_base['componentes'])),
                    'xml_adicionais_encontrados': sum(len(a['xml']) for a in android),
                    'xml_adicionais_decodificados_ou_validados': sum(i['estado'] != 'falha' for a in android for i in a['xml']),
                    'xml_com_falha': [i for a in android for i in a['xml'] if i['estado'] == 'falha'],
                    'tabelas_recursos_encontradas': sum(a['recursos']['presente'] for a in android),
                    'tabelas_com_erro': sum('erro' in a['recursos'] for a in android)},
        'dex': {'encontrados': sum(i['membro'].endswith('.dex') for i in arquivos), 'indexados': len(dex),
                'classes_definidas_ocorrencias': sum(i['classes_indexadas'] for i in dex),
                'classes_unicas': correlacao['classes_unicas'], 'classes_duplicadas': correlacao['classes_repetidas_entre_dex'],
                'metodos_definidos': sum(i['metodos_definidos'] for i in dex),
                'metodos_com_codigo': sum(i['metodos_com_codigo'] for i in dex),
                'falhas_leitura_instrucoes': sum(i['falhas_instrucoes'] for i in dex),
                'checksums_internos_validos': sum(i['adler32_valido'] and i['sha1_interno_valido'] for i in dex)},
        'smali': {'dex_processados': len(smali), 'classes': sum(i['classes'] for i in smali),
                  'metodos': sum(i['metodos'] for i in smali),
                  'todos_totais_conferem': all(not i['codigo_saida'] and i['classes_conferem'] and i['metodos_conferem'] and i['corpos_conferem'] for i in smali)},
        'jadx_isolado': auditar_jadx('jadx'), 'jadx_agrupado': auditar_jadx('jadx-agrupado'),
        'nativo': {'bibliotecas_encontradas': sum(i['membro'].endswith('.so') for i in arquivos),
                   'elf_analisados_estruturalmente': len(nativo), 'com_debug_info': sum(i['possui_debug_info'] for i in nativo),
                   'com_symtab': sum(i['possui_symtab'] for i in nativo),
                   'instrucoes_lineares': sum(i['instrucoes_lineares'] for i in nativo),
                   'lacunas_desmontagem': sum(i['lacunas_desmontagem'] for i in nativo),
                   'metodos_native': correlacao['metodos_native'], 'metodos_native_com_export_compativel': correlacao['com_export_compativel'],
                   'metodos_native_sem_export_compativel': correlacao['sem_export_compativel'],
                   'pseudocodigo_automatico': 0, 'funcoes_totais': 'denominador não estabelecido'},
        'formatos': {'membros_internos_extraidos': formato['membros_internos_extraidos'],
                     'assets_inspecionados': formato['arquivos_inspecionados'], 'hipotese_xml_nef_rejeitada_em': len(formato['falhas_de_parse'])},
        'dinamica': {'instalacoes_executadas': 0, 'componentes_observados': 0,
                     'motivo': 'Sem Android dedicado compatível disponível; diagnóstico não equivale a runtime.'},
        'limite_geral': 'Não há denominador confiável para métodos Java semanticamente recuperados ou funções nativas. Missão incompleta.'}
    (INV / 'cobertura.json').write_text(json.dumps(resultado, ensure_ascii=False, indent=2)+'\n')
    if not integridade or not resultado['smali']['todos_totais_conferem']:
        raise SystemExit('Auditoria identificou divergência crítica de integridade ou bytecode.')
    print('Auditoria registrada; falhas de decompilação permanecem explícitas, sem declarar recuperação integral.')


if __name__ == '__main__':
    main()
