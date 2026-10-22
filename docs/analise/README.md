# Análise do KineMaster — índice

Missão: decompilação rastreável do KineMaster 8.2.14.37398 (conteúdo de `bin/`), com engenharia reversa estática e, quando autorizada e possível, dinâmica.

## Documentos

- `relatorio-parcial.md` — resultados das Fases 0 a 5, com fatos, inferências, hipóteses, métricas com denominador e lacunas.
- `ferramentas-e-restricoes.md` — ferramentas, versões, origens, restrições de rede, redistribuição e bloqueios (inclui a análise dinâmica bloqueada).
- `../../TODO.md` — plano persistente por fases, com situação de cada item.
- `../../bin/tools/MANIFESTO.md` — ficha de cada ferramenta versionada (versão, URL, licença, SHA-256, limitações).

## Inventários (`inventarios/`)

| Arquivo | Conteúdo | Gerado por |
|---|---|---|
| `entrada-sha256.txt` | Hashes dos volumes 7z e do 7z | `extrair_xapk.py` |
| `xapk-sha256.txt` | Hashes dos 22 arquivos do XAPK | `extrair_xapk.py` |
| `apks.{json,md}` | Inventário dos APKs (DEX, assinatura, formatos) | `inventario_apk.py` |
| `manifesto-base.{json,md}` | Manifesto, permissões, componentes, assinatura | `analise_manifesto.py` |
| `apktool-base.txt` | Resumo da decodificação Apktool | `decompilar_apktool.sh` |
| `jadx-base-jadx.txt` | Resumo da passagem JADX padrão | `decompilar_jadx.sh` |
| `jadx-base-erros.{json,md}` | Nós com falha no JADX padrão | `analisar_log_jadx.py` |
| `jadx-base-showbad-erros.{json,md}` | Nós com falha no JADX `--show-bad-code` | `analisar_log_jadx.py` |
| `cobertura-dex-base.{json,md}` | Cobertura DEX × JADX | `cobertura_dex.py` |
| `cobertura-dex-base-ausentes.txt` | Classes ausentes e causa registrada | `cobertura_dex.py` |
| `cobertura-dex-base-sinteticas-vazias.txt` | Stubs R8 sem membros (excluídos do denominador) | `cobertura_dex.py` |
| `nativo-arm64.{json,md}` | Inventário das 22 bibliotecas nativas | `inventario_nativo.py` |
| `jni-relacao.{json,md}` | Métodos `native` × símbolos JNI | `relacionar_jni.py` |
| `ativos-base.{csv,md}` | Inventário de ativos por assinatura | `inventario_ativos.py` |
| `kmassets-base.{csv,md}` | Inventário do `assets/kmassets.zip` (1.832 entradas) | `inventario_kmassets.py` |

Os CSV grandes são versionados por serem a evidência de hash do inventário; os logs brutos do JADX e os artefatos descompactados ficam fora do Git, em `KM_WORKDIR`.

## Como repetir

Ver a seção 8 de `relatorio-parcial.md`. Defina `KM_WORKDIR=/home/user/kinemaster-work` antes de usar os scripts em `scripts/`.

## Convenções

- Idioma: português brasileiro; nomes originais de classes, métodos e APIs são preservados.
- Fato, inferência e hipótese são separados nos relatórios.
- Nenhuma credencial, chave privada ou conteúdo de segredo é reproduzido. A chave em `AppstoreAuthenticationKey.pem` é pública e só é referenciada por hash.
