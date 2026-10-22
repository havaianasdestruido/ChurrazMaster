#!/usr/bin/env python3
"""Registra pré-requisitos do host sem alegar execução do aplicativo."""
import json
import os
from pathlib import Path
import platform
import shutil

RAIZ = Path(__file__).resolve().parents[1]


def main():
    dados = {'sistema': platform.system(), 'arquitetura_host': platform.machine(),
             'kernel': platform.release(), 'kvm_presente': Path('/dev/kvm').exists(),
             'kvm_acessivel': os.access('/dev/kvm', os.R_OK | os.W_OK),
             'usb_presente': Path('/dev/bus/usb').exists(),
             'comandos_detectados': {nome: shutil.which(nome) for nome in ['adb', 'emulator', 'qemu-system-aarch64', 'qemu-system-x86_64', 'docker']},
             'abi_aplicativo': ['arm64-v8a'], 'android_minimo': 26,
             'aplicativo_executado': False,
             'motivo': 'Diagnóstico do host; não instala nem inicia o aplicativo. Sem Android dedicado compatível, a execução permanece pendente.'}
    destino = RAIZ / 'docs/analise/evidencias/ambiente-dinamico.json'
    destino.parent.mkdir(parents=True, exist_ok=True)
    destino.write_text(json.dumps(dados, ensure_ascii=False, indent=2) + '\n')
    print('Diagnóstico gravado; não é evidência de execução Android.')


if __name__ == '__main__':
    main()
