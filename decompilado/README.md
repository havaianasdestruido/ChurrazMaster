# `decompilado/` — artefatos recuperados versionados

Este diretório contém **apenas** artefatos pequenos e verificáveis, escolhidos para permitir
que um futuro agente entenda o aplicativo sem precisar descompactar e descompilar tudo de novo.

## O que está aqui

| Caminho | Conteúdo |
|---|---|
| `AndroidManifest.xml` | manifesto decodificado do APK base (Apktool 2.4.1) |
| `apktool.yml` | metadados do pacote base (versão, `doNotCompress`, splits) |
| `selecionado/java/` | 14 classes de interesse direto para mods, copiadas da saída do jadx **sem edição** |
| `indices/componentes.md` | atividades, serviços, receptores, provedores, permissões e `meta-data` |
| `indices/simbolos_jni.csv` | 480 símbolos JNI → classe Java inferida, por biblioteca |
| `indices/classes_kinemaster.txt` | 4.930 classes do próprio aplicativo (sem bibliotecas) |
| `indices/arvore_de_pacotes.txt` | árvore de 1.755 pacotes com contagem de classes (jadx-headless-mcp) |

## O que **não** está aqui (e por quê)

As árvores completas são **geradas por script** e ficam no diretório de trabalho
(`/tmp/churraz-trabalho`, configurável por `DIR_TRABALHO`):

- `04_jadx_base/sources` — 45.195 arquivos `.java`, ~385 MB
- `04_jadx_audience/sources` — 3.538 arquivos `.java`, ~25 MB
- `05_apktool/` — 20 árvores de recursos decodificados, ~201 MB
- `06_nativo/lib/arm64-v8a` — 22 bibliotecas `.so`, ~62 MB

Motivo: o conjunto passa de 600 MB e dezenas de milhares de arquivos, o que inviabiliza o
versionamento (limites práticos do Git/GitHub e do próprio agente). A reprodução é determinística
pelos scripts `03`, `04` e `07`, com as versões de ferramenta fixadas em `bin/tools/MANIFESTO.md`.

## Aviso sobre qualidade

O código em `selecionado/java/` é **saída de descompilador**, não código-fonte original.
Espera-se encontrar, nessa ordem de frequência: nomes ofuscados pelo R8, variáveis genéricas
(`r0`, `un1`), `JADX WARNING`/`JADX ERROR` em pontos de fluxo complexo e construções rearranjadas.
Onde o jadx falhou, o arquivo contém comentários explícitos — eles **não devem ser removidos**,
pois registram a perda de informação.
