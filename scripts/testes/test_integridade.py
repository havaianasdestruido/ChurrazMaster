"""Testes locais para extração segura, JNI e hipóteses de formato."""
import hashlib
import json
from pathlib import Path
import struct
import sys
import tempfile
import unittest
import warnings
import zipfile
import zlib

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from extrair import RAIZ, extrair_zip
from correlacionar import codificar_jni
from investigar_nef import analisar, MAGIA_REGISTRO
from recuperar_esquema import campos_anotados


class TestesDeIntegridade(unittest.TestCase):
    def pacote(self, nomes):
        pasta = RAIZ / '.trabalho/testes'
        pasta.mkdir(parents=True, exist_ok=True)
        temporario = tempfile.TemporaryDirectory(dir=pasta)
        raiz = Path(temporario.name)
        entrada = raiz / 'entrada.zip'
        with warnings.catch_warnings():
            warnings.simplefilter('ignore', UserWarning)
            with zipfile.ZipFile(entrada, 'w') as arquivo:
                for nome in nomes:
                    arquivo.writestr(nome, b'conteudo de teste')
        return temporario, raiz, entrada

    def test_extracao_registra_hash_e_relacao(self):
        temporario, raiz, entrada = self.pacote(['pasta/arquivo.txt'])
        with temporario:
            inventario = []
            extrair_zip(entrada, raiz / 'saida', inventario, 'origem.zip')
            self.assertEqual(len(inventario), 1)
            self.assertEqual(inventario[0]['pai'], 'origem.zip')
            self.assertEqual(inventario[0]['sha256'], hashlib.sha256(b'conteudo de teste').hexdigest())

    def test_rejeita_travessia_absolutos_e_barras_invertidas(self):
        for nome in ['../fora', '/absoluto', 'pasta\\fora']:
            temporario, raiz, entrada = self.pacote([nome])
            with temporario, self.assertRaises(ValueError):
                extrair_zip(entrada, raiz / 'saida', [], 'origem.zip')

    def test_rejeita_duplicatas(self):
        temporario, raiz, entrada = self.pacote(['repetido', 'repetido'])
        with temporario, self.assertRaises(ValueError):
            extrair_zip(entrada, raiz / 'saida', [], 'origem.zip')

    def test_rejeita_ligacao_simbolica(self):
        item = zipfile.ZipInfo('ligacao')
        item.create_system = 3
        item.external_attr = 0o120777 << 16
        temporario, raiz, entrada = self.pacote([item])
        with temporario, self.assertRaises(ValueError):
            extrair_zip(entrada, raiz / 'saida', [], 'origem.zip')

    def test_jni_preserva_descritores_e_escapes(self):
        self.assertEqual(codificar_jni('a_b/C$d;[I'), 'a_1b_C_00024d_2_3I')
        self.assertEqual(codificar_jni('😀'), '_0d83d_0de00')

    def test_nef_valida_comprimentos_e_preserva_cauda(self):
        corpo = b'\xff\xd8\xffteste'
        bloco = zlib.compress(corpo)
        dados = MAGIA_REGISTRO + struct.pack('<4I', 123, 2, len(bloco), len(corpo)) + bloco
        registros, final = analisar(dados + b'cauda desconhecida')
        self.assertEqual(final, len(dados))
        self.assertEqual(registros[0]['corpo'], corpo)
        self.assertTrue(registros[0]['jpeg_por_assinatura'])

    def test_nef_recusa_tamanho_falso(self):
        dados = MAGIA_REGISTRO + struct.pack('<4I', 0, 0, 999, 999)
        with self.assertRaises(ValueError):
            analisar(dados)

    def test_nef_desconhecido_nao_inventa_registro(self):
        self.assertEqual(analisar(b'\x13\x00\xff\xeedesconhecido'), ([], 0))

    def test_tags_wire_exigem_anotacao_e_tag_explicitas(self):
        exemplo = '.field public final nome:Ljava/lang/String;\n    .annotation runtime Lcom/squareup/wire/k;\n        tag = 0x85\n    .end annotation\n.end field\n'
        resultado = list(campos_anotados(exemplo))
        self.assertEqual(resultado[0]['tag'], 133)
        self.assertEqual(resultado[0]['campo'], 'nome')
        self.assertEqual(list(campos_anotados('.field public final vazio:I\n')), [])

    def test_ferramentas_conferem_com_manifesto(self):
        for item in json.loads((RAIZ / 'bin/tools/ferramentas.json').read_text()):
            arquivo = RAIZ / item['arquivo']
            self.assertEqual(arquivo.stat().st_size, item['tamanho'])
            self.assertEqual(hashlib.sha256(arquivo.read_bytes()).hexdigest(), item['sha256'])


if __name__ == '__main__':
    unittest.main()
