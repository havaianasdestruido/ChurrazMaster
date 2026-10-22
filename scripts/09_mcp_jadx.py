#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
09_mcp_jadx.py — cliente MCP mínimo (stdio) para o jadx-headless-mcp do repositório.

Por que este script existe:
  O jadx-headless-mcp é o servidor MCP que a skill REA usa como provedor Android
  (variável REA_JADX_MCP_JAR). Neste ambiente não há cliente MCP registrado, então
  falamos JSON-RPC 2.0 diretamente pelo stdin/stdout do processo Java.

Operações:
  --listar                 lista as ferramentas expostas pelo servidor (tools/list)
  --chamar NOME --args JSON executa uma ferramenta
  --lote ARQUIVO.json      executa várias chamadas descritas em um JSON

Observação: o transporte stdio do MCP usa mensagens JSON delimitadas por linha.

Uso:
  python3 scripts/09_mcp_jadx.py --apk /tmp/.../base.apk --listar
"""
from __future__ import annotations

import argparse
import json
import os
import subprocess
import sys
import time

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAR = os.path.join(RAIZ, "bin", "tools", "android", "jadx-mcp", "jadx-mcp-0.7.0.jar")


def resolver_java() -> str:
    """Retorna o executável java (JAVA_HOME ou jdk4py)."""
    java_home = os.environ.get("JAVA_HOME")
    if not java_home:
        try:
            import jdk4py
            java_home = os.path.join(jdk4py.JAVA_HOME)
        except Exception:
            java_home = None
    if java_home and os.path.exists(os.path.join(java_home, "bin", "java")):
        return os.path.join(java_home, "bin", "java")
    from shutil import which
    caminho = which("java")
    if not caminho:
        raise SystemExit("[erro] Java não encontrado. Execute scripts/instalar_ferramentas.sh")
    return caminho


class ClienteMCP:
    """Cliente JSON-RPC sobre stdio, suficiente para tools/list e tools/call."""

    def __init__(self, apk: str, heap: str = "1500m") -> None:
        self.java = resolver_java()
        self.processo = subprocess.Popen(
            [self.java, f"-Xmx{heap}", "-jar", JAR, "--file", apk, "--transport", "STDIO"],
            stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
            text=True, bufsize=1,
        )
        self.proximo_id = 1

    def _enviar(self, mensagem: dict) -> None:
        assert self.processo.stdin
        self.processo.stdin.write(json.dumps(mensagem) + "\n")
        self.processo.stdin.flush()

    def _ler_resposta(self, id_esperado: int, limite_s: float) -> dict:
        """Lê linhas até encontrar a resposta com o id esperado."""
        assert self.processo.stdout
        prazo = time.time() + limite_s
        while time.time() < prazo:
            linha = self.processo.stdout.readline()
            if not linha:
                break
            linha = linha.strip()
            if not linha.startswith("{"):
                continue
            try:
                msg = json.loads(linha)
            except json.JSONDecodeError:
                continue
            if msg.get("id") == id_esperado:
                return msg
        raise TimeoutError(f"sem resposta para id={id_esperado} em {limite_s}s")

    def inicializar(self) -> dict:
        self._enviar({
            "jsonrpc": "2.0", "id": self.proximo_id, "method": "initialize",
            "params": {"protocolVersion": "2024-11-05", "capabilities": {},
                       "clientInfo": {"name": "churrazmaster", "version": "1.0"}},
        })
        resposta = self._ler_resposta(self.proximo_id, 120)
        self.proximo_id += 1
        self._enviar({"jsonrpc": "2.0", "method": "notifications/initialized"})
        return resposta

    def listar(self) -> dict:
        self._enviar({"jsonrpc": "2.0", "id": self.proximo_id, "method": "tools/list", "params": {}})
        resposta = self._ler_resposta(self.proximo_id, 120)
        self.proximo_id += 1
        return resposta

    def chamar(self, nome: str, argumentos: dict, limite_s: float = 600) -> dict:
        self._enviar({"jsonrpc": "2.0", "id": self.proximo_id, "method": "tools/call",
                      "params": {"name": nome, "arguments": argumentos}})
        resposta = self._ler_resposta(self.proximo_id, limite_s)
        self.proximo_id += 1
        return resposta

    def fechar(self) -> None:
        try:
            self.processo.terminate()
            self.processo.wait(timeout=15)
        except Exception:
            self.processo.kill()


def main() -> int:
    ap = argparse.ArgumentParser(description="Cliente MCP mínimo para jadx-headless-mcp")
    ap.add_argument("--apk", required=True, help="APK/DEX analisado")
    ap.add_argument("--listar", action="store_true", help="lista ferramentas (tools/list)")
    ap.add_argument("--chamar", help="nome da ferramenta a executar")
    ap.add_argument("--args", default="{}", help="argumentos JSON da ferramenta")
    ap.add_argument("--saida", help="grava a resposta JSON neste arquivo")
    args = ap.parse_args()

    if not os.path.exists(JAR):
        print(f"[erro] jar do jadx-headless-mcp ausente: {JAR}", file=sys.stderr)
        return 2

    cliente = ClienteMCP(args.apk)
    try:
        inicializacao = cliente.inicializar()
        print("[info] servidor iniciado:", json.dumps(inicializacao.get("result", {}).get("serverInfo", {}),
                                                      ensure_ascii=False))
        if args.listar:
            resposta = cliente.listar()
            ferramentas = resposta.get("result", {}).get("tools", [])
            print(f"[ok] {len(ferramentas)} ferramentas expostas:")
            for f in ferramentas:
                print(f"  - {f['name']}: {(f.get('description') or '')[:110]}")
            if args.saida:
                os.makedirs(os.path.dirname(args.saida), exist_ok=True)
                json.dump(ferramentas, open(args.saida, "w", encoding="utf-8"), ensure_ascii=False, indent=2)
        if args.chamar:
            resposta = cliente.chamar(args.chamar, json.loads(args.args))
            texto = json.dumps(resposta, ensure_ascii=False, indent=2)
            if args.saida:
                os.makedirs(os.path.dirname(args.saida), exist_ok=True)
                open(args.saida, "w", encoding="utf-8").write(texto)
                print(f"[ok] resposta gravada em {args.saida}")
            else:
                print(texto[:8000])
    finally:
        cliente.fechar()
    return 0


if __name__ == "__main__":
    sys.exit(main())
