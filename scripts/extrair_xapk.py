#!/usr/bin/env python3
"""Reconstitui e extrai o XAPK do KineMaster a partir dos volumes 7z em bin/.

Etapas (todas não destrutivas para os arquivos de origem em bin/):
  1. Verifica o SHA-256 de cada volume contra docs/analise/inventarios/entrada-sha256.txt.
  2. Concatena os volumes (.7z.001 ... .7z.00N) em uma cópia de trabalho.
  3. Lista e extrai o 7z com py7zr (sem senha; o script aborta se houver criptografia).
  4. Testa a integridade do XAPK (ZIP) e extrai o conteúdo em diretório separado.
  5. Grava o SHA-256 de todos os arquivos extraídos em um manifesto.

Os artefatos grandes ficam fora do Git, em KM_WORKDIR (padrão: ~/kinemaster-work).

Uso:
    python3 scripts/extrair_xapk.py            # usa KM_WORKDIR padrão
    KM_WORKDIR=/outro/caminho python3 scripts/extrair_xapk.py
"""
import hashlib
import os
import shutil
import sys
import zipfile

try:
    import py7zr
except ImportError:  # pragma: no cover - mensagem orientativa
    print("erro: py7zr não instalado. Execute scripts/preparar_ambiente.sh", file=sys.stderr)
    sys.exit(1)

RAIZ = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
BIN = os.path.join(RAIZ, "bin")
REGISTRO_VOLUMES = os.path.join(RAIZ, "docs", "analise", "inventarios", "entrada-sha256.txt")
WORKDIR = os.environ.get("KM_WORKDIR", os.path.expanduser("~/kinemaster-work"))
PASTA_VOLUMES = os.path.join(WORKDIR, "volumes")
PASTA_XAPK = os.path.join(WORKDIR, "extraido", "xapk")
PASTA_7Z = os.path.join(WORKDIR, "extraido", "7z")
MANIFESTO_XAPK = os.path.join(RAIZ, "docs", "analise", "inventarios", "xapk-sha256.txt")


def sha256(caminho):
    h = hashlib.sha256()
    with open(caminho, "rb") as f:
        for bloco in iter(lambda: f.read(1 << 20), b""):
            h.update(bloco)
    return h.hexdigest()


def volumes_em_bin():
    """Retorna os volumes 7z (ordenados) presentes em bin/."""
    nomes = sorted(n for n in os.listdir(BIN) if ".7z.0" in n)
    if not nomes:
        raise SystemExit("erro: nenhum volume .7z.NNN encontrado em bin/")
    return nomes


def verificar_volumes(nomes):
    """Compara os hashes dos volumes com o registro versionado."""
    if not os.path.exists(REGISTRO_VOLUMES):
        raise SystemExit(f"erro: registro ausente: {REGISTRO_VOLUMES}")
    esperado = {}
    with open(REGISTRO_VOLUMES, encoding="utf-8") as f:
        for linha in f:
            partes = linha.split()
            if len(partes) == 2 and not linha.startswith("#"):
                esperado[partes[1]] = partes[0]
    for nome in nomes:
        real = sha256(os.path.join(BIN, nome))
        if esperado.get(nome) != real:
            raise SystemExit(f"erro: SHA-256 divergente em {nome}: esperado {esperado.get(nome)}, obtido {real}")
    print(f"ok: {len(nomes)} volume(s) com SHA-256 conforme o registro")


def reconstituir(nomes):
    os.makedirs(PASTA_VOLUMES, exist_ok=True)
    destino = os.path.join(PASTA_VOLUMES, "kinemaster-8.2.14.7z")
    with open(destino, "wb") as saida:
        for nome in nomes:
            with open(os.path.join(BIN, nome), "rb") as entrada:
                shutil.copyfileobj(entrada, saida, 1 << 20)
    print(f"ok: volumes concatenados em {destino} ({os.path.getsize(destino)} bytes)")
    return destino


def extrair_7z(caminho_7z):
    with py7zr.SevenZipFile(caminho_7z, "r") as z:
        if z.needs_password():
            raise SystemExit("erro: o 7z exige senha; não será tentada nenhuma senha.")
        nomes = z.getnames()
    print(f"ok: 7z sem criptografia, entradas: {nomes}")
    shutil.rmtree(PASTA_7Z, ignore_errors=True)
    os.makedirs(PASTA_7Z, exist_ok=True)
    with py7zr.SevenZipFile(caminho_7z, "r") as z:
        z.extractall(path=PASTA_7Z)
    xapks = [n for n in nomes if n.lower().endswith(".xapk")]
    if len(xapks) != 1:
        raise SystemExit(f"erro: esperado exatamente um .xapk no 7z, encontrado: {xapks}")
    return os.path.join(PASTA_7Z, xapks[0])


def extrair_xapk(caminho_xapk):
    if not zipfile.is_zipfile(caminho_xapk):
        raise SystemExit("erro: o conteúdo extraído do 7z não é um ZIP/XAPK válido")
    with zipfile.ZipFile(caminho_xapk) as z:
        falha = z.testzip()
        if falha is not None:
            raise SystemExit(f"erro: falha de CRC em {falha}")
    shutil.rmtree(PASTA_XAPK, ignore_errors=True)
    os.makedirs(PASTA_XAPK, exist_ok=True)
    with zipfile.ZipFile(caminho_xapk) as z:
        z.extractall(PASTA_XAPK)
    print(f"ok: XAPK extraído em {PASTA_XAPK}")


def gravar_manifesto():
    registros = []
    for raiz, _, arquivos in os.walk(PASTA_XAPK):
        for a in arquivos:
            completo = os.path.join(raiz, a)
            rel = os.path.relpath(completo, PASTA_XAPK)
            registros.append(f"{sha256(completo)}  {rel}")
    registros.sort(key=lambda s: s.split("  ", 1)[1])
    cabecalho = [
        "# SHA-256 dos arquivos extraídos do XAPK (gerado por scripts/extrair_xapk.py)",
        "# Formato: <sha256>  <caminho relativo>",
    ]
    with open(MANIFESTO_XAPK, "w", encoding="utf-8") as f:
        f.write("\n".join(cabecalho + registros) + "\n")
    print(f"ok: manifesto gravado em {MANIFESTO_XAPK} ({len(registros)} arquivos)")


def main():
    nomes = volumes_em_bin()
    verificar_volumes(nomes)
    caminho_7z = reconstituir(nomes)
    caminho_xapk = extrair_7z(caminho_7z)
    extrair_xapk(caminho_xapk)
    gravar_manifesto()
    return 0


if __name__ == "__main__":
    sys.exit(main())
