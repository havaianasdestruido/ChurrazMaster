#!/usr/bin/env python3
"""Confere a PLT AArch64 usada no exame manual do contador compartilhado."""
import hashlib
import json
from pathlib import Path
from elftools.elf.elffile import ELFFile
from capstone import Cs, CS_ARCH_ARM64, CS_MODE_LITTLE_ENDIAN

RAIZ = Path(__file__).resolve().parents[1]


def main():
    caminho = RAIZ / '.trabalho/extraido/apks/config.arm64_v8a/lib/arm64-v8a/libdatastore_shared_counter.so'
    with caminho.open('rb') as arquivo:
        elf = ELFFile(arquivo)
        relocacoes = elf.get_section_by_name('.rela.plt')
        simbolos = elf.get_section(relocacoes['sh_link'])
        plt = elf.get_section_by_name('.plt')
        if elf['e_machine'] != 'EM_AARCH64' or plt['sh_size'] != 32 + 16 * relocacoes.num_relocations():
            raise SystemExit('Layout da PLT fora da hipótese examinada; não atribuir nomes automaticamente.')
        entradas = [{'endereco_stub_plt': plt['sh_addr'] + 32 + 16*i,
                     'simbolo': simbolos.get_symbol(r['r_info_sym']).name,
                     'endereco_got': r['r_offset']} for i, r in enumerate(relocacoes.iter_relocations())]
        motor = Cs(CS_ARCH_ARM64, CS_MODE_LITTLE_ENDIAN)
        desmontagem = [{'endereco': a, 'mnemonico': m, 'operandos': o} for a, _, m, o in motor.disasm_lite(plt.data(), plt['sh_addr'])]
        resultado = {'biblioteca': caminho.name, 'sha256': hashlib.sha256(caminho.read_bytes()).hexdigest(),
                     'premissa': 'PLT AArch64: cabeçalho de 32 bytes e entradas de 16 bytes; conferir GOT nos operandos com .rela.plt.',
                     'plt_tamanho_confere': True, 'entradas': entradas, 'desmontagem_plt': desmontagem,
                     'limites': 'Relação estática de stubs e símbolos; resolução final pelo linker Android não observada.'}
    (RAIZ / 'docs/analise/evidencias/plt-contador.json').write_text(json.dumps(resultado, ensure_ascii=False, indent=2)+'\n')
    print('PLT do contador documentada; carregamento dinâmico não observado.')


if __name__ == '__main__':
    main()
