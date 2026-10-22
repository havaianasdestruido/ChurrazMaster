#!/usr/bin/env python3
"""Valida hashes e prepara ferramentas locais sem alterar configurações do agente."""
import hashlib
import json
from pathlib import Path
import subprocess
import sys
import tarfile
import zipfile

RAIZ = Path(__file__).resolve().parents[1]


def main():
    for item in json.loads((RAIZ / 'bin/tools/ferramentas.json').read_text()):
        caminho = RAIZ / item['arquivo']
        if hashlib.sha256(caminho.read_bytes()).hexdigest() != item['sha256']:
            raise SystemExit(f'Hash de ferramenta divergente: {caminho.name}')
    destino = RAIZ / '.trabalho/ferramentas'
    destino.mkdir(parents=True, exist_ok=True)
    for origem, subdiretorio in [
        ('bin/tools/utilitarios/7zip-bin-5.2.0.tgz', '7zip'),
        ('bin/tools/android/mishguru-jadx-node-3.1.0.tgz', 'jadx'),
        ('bin/tools/rea/skill-44-5375ff0.tar.gz', 'rea-skill'),
    ]:
        pasta = destino / subdiretorio
        if not pasta.exists():
            pasta.mkdir()
            with tarfile.open(RAIZ / origem) as pacote:
                # Somente arquivos regulares/diretórios de ferramentas com hash fixado.
                for membro in pacote.getmembers():
                    alvo = (pasta / membro.name).resolve()
                    if not alvo.is_relative_to(pasta.resolve()) or not (membro.isfile() or membro.isdir()):
                        raise SystemExit('Entrada insegura no pacote de ferramenta.')
                pacote.extractall(pasta)
    java = destino / 'java'
    if not java.exists():
        java.mkdir()
        with zipfile.ZipFile(next((RAIZ / 'bin/tools/java').glob('jdk4py-*.whl'))) as pacote:
            pacote.extractall(java)
    for arquivo in (java / 'jdk4py/java-runtime/bin').iterdir():
        arquivo.chmod(0o755)
    (destino / '7zip/package/linux/x64/7za').chmod(0o755)
    if not (RAIZ / '.venv/bin/python').exists():
        subprocess.run([sys.executable, '-m', 'venv', str(RAIZ / '.venv')], check=True)
    subprocess.run([str(RAIZ / '.venv/bin/pip'), 'install', '--no-index', '--find-links',
                    str(RAIZ / 'bin/tools/python'), 'androguard==4.1.4', 'pyelftools==0.32', 'capstone==5.0.6'], check=True)
    print('Ferramentas verificadas; ambiente local preparado sem downloads adicionais.')


if __name__ == '__main__':
    main()
