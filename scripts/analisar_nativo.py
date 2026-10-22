#!/usr/bin/env python3
"""Analisa ELF/AArch64 sem carregar bibliotecas: símbolos, dependências e desmontagem."""
import gzip
import hashlib
import json
from pathlib import Path
from elftools.elf.elffile import ELFFile
from elftools.elf.sections import SymbolTableSection
from capstone import Cs, CS_ARCH_ARM64, CS_MODE_LITTLE_ENDIAN

RAIZ = Path(__file__).resolve().parents[1]
INV = RAIZ / 'docs/analise/inventarios/nativo'
GERADO = RAIZ / 'decompilado/gerado/nativo'


def salvar(caminho, itens):
    dados = ''.join(json.dumps(i, ensure_ascii=False) + '\n' for i in itens).encode()
    caminho.write_bytes(gzip.compress(dados, mtime=0))


def main():
    INV.mkdir(parents=True, exist_ok=True)
    GERADO.mkdir(parents=True, exist_ok=True)
    resumos = []
    for caminho in sorted((RAIZ / '.trabalho/extraido/apks').rglob('*')):
        if not caminho.is_file():
            continue
        with caminho.open('rb') as arquivo:
            if arquivo.read(4) != b'\x7fELF':
                continue
            arquivo.seek(0)
            elf = ELFFile(arquivo)
            simbolos, dependencias, notas, relocacoes = [], [], [], []
            for secao in elf.iter_sections():
                if isinstance(secao, SymbolTableSection):
                    for simbolo in secao.iter_symbols():
                        if simbolo.name:
                            simbolos.append({'nome': simbolo.name, 'tabela': secao.name,
                                             'tipo': simbolo['st_info']['type'], 'ligacao': simbolo['st_info']['bind'],
                                             'visibilidade': simbolo['st_other']['visibility'], 'secao': simbolo['st_shndx'],
                                             'endereco': simbolo['st_value'], 'tamanho': simbolo['st_size']})
                if secao['sh_type'] == 'SHT_DYNAMIC':
                    for tag in secao.iter_tags():
                        if tag.entry.d_tag == 'DT_NEEDED':
                            dependencias.append(tag.needed)
                if secao['sh_type'] == 'SHT_NOTE':
                    for nota in secao.iter_notes():
                        notas.append({'nome': nota['n_name'], 'tipo': nota['n_type'], 'descricao': str(nota['n_desc'])})
                if secao['sh_type'] in ('SHT_RELA', 'SHT_REL'):
                    relocacoes.append({'secao': secao.name, 'quantidade': secao.num_relocations()})
            imports = [s for s in simbolos if s['secao'] == 'SHN_UNDEF']
            exports = [s for s in simbolos if s['secao'] != 'SHN_UNDEF' and s['ligacao'] in ('STB_GLOBAL', 'STB_WEAK')]
            jni = [s for s in exports if s['nome'].startswith('Java_') or s['nome'] == 'JNI_OnLoad']
            salvar(INV / f'{caminho.name}.simbolos.jsonl.gz', simbolos)
            # Desmonta integralmente seções executáveis AArch64, sem afirmar recuperação de funções.
            instrucoes, falhas, bytes_codigo = 0, [], 0
            motor = Cs(CS_ARCH_ARM64, CS_MODE_LITTLE_ENDIAN)
            if elf['e_machine'] != 'EM_AARCH64':
                falhas.append('Arquitetura não suportada por esta rotina de desmontagem.')
            else:
                with (GERADO / f'{caminho.name}.asm').open('w') as saida:
                    saida.write('; Desmontagem estática linear; dados intercalados podem parecer instruções.\n')
                    for secao in elf.iter_sections():
                        if not secao['sh_flags'] & 4 or not secao['sh_size']:
                            continue
                        dados = secao.data()
                        bytes_codigo += len(dados)
                        saida.write(f'\n; Seção original: {secao.name}\n')
                        # AArch64 usa instruções de 4 bytes; registra lacunas explicitamente.
                        decodificados = 0
                        motor.skipdata = True
                        for endereco, tamanho, mnemonico, operandos in motor.disasm_lite(dados, secao['sh_addr']):
                            saida.write(f'{endereco:016x}: {mnemonico} {operandos}\n')
                            decodificados += tamanho
                            instrucoes += mnemonico != '.byte'
                            if mnemonico == '.byte':
                                falhas.append({'endereco': endereco, 'tamanho': tamanho, 'motivo': 'Dados ou instrução não reconhecida'})
                        if decodificados != len(dados):
                            falhas.append({'secao': secao.name, 'bytes_nao_decodificados': len(dados) - decodificados})
            salvar(INV / f'{caminho.name}.lacunas-desmontagem.jsonl.gz', falhas)
            resumos.append({
                'arquivo': caminho.relative_to(RAIZ).as_posix(), 'sha256': hashlib.sha256(caminho.read_bytes()).hexdigest(),
                'classe_elf': elf.elfclass, 'arquitetura': elf['e_machine'], 'tipo': elf['e_type'],
                'little_endian': elf.little_endian, 'entrada': elf['e_entry'],
                'secoes': [{'nome': s.name, 'tipo': s['sh_type'], 'endereco': s['sh_addr'], 'tamanho': s['sh_size']} for s in elf.iter_sections()],
                'dependencias': dependencias, 'notas': notas, 'relocacoes': relocacoes,
                'simbolos': len(simbolos), 'imports': len(imports), 'exports': len(exports),
                'jni_exports': jni, 'possui_symtab': elf.get_section_by_name('.symtab') is not None,
                'possui_debug_info': elf.get_section_by_name('.debug_info') is not None,
                'bytes_secoes_executaveis': bytes_codigo, 'instrucoes_lineares': instrucoes,
                'lacunas_desmontagem': len(falhas), 'pseudocodigo_automatico': 'não recuperado',
                'limites': 'Desmontagem linear e símbolos não demonstram limites de função nem comportamento em execução.'})
            print(f'ELF analisado estruturalmente: {caminho.name}; {len(exports)} exports; {len(jni)} entradas JNI.', flush=True)
    (INV / 'bibliotecas.json').write_text(json.dumps(resumos, ensure_ascii=False, indent=2) + '\n')


if __name__ == '__main__':
    main()
