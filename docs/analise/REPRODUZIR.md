# Como repetir a investigação

## Pré-requisitos e preservação

Execute na raiz de um checkout com os quatro volumes originais em `bin/`.
Ambiente validado: Debian 12/Linux x86_64, glibc 2.36, CPython 3.11.2, Bash.
O conjunto de wheels exige glibc ≥ 2.34. `venv` e `pip` devem estar disponíveis.
A preparação principal é offline; não exige Java global, Node.js nem REA MCP.
Consulte `bin/tools/MANIFESTO.md` para versões, licenças e exceções de tamanho.

Os scripts de extração recusam saída existente. Não sobrescreva o XAPK ou os
volumes. Em checkout já investigado, arquive apenas os diretórios gerados ou use
outro checkout; não apague arquivos de colaboradores. Índices versionados podem
ser regenerados pelos scripts. Saídas enormes não entram no Git.

## Sequência principal, em diretórios de saída novos

```bash
python3 scripts/preparar.py
python3 scripts/extrair.py
.venv/bin/python scripts/analisar_android.py
# A passagem isolada reproduz o diagnóstico inicial, inclusive falhas conhecidas.
.venv/bin/python scripts/decompilar.py --por-dex
# Passagem corretiva multidex; ainda contém falhas por método.
.venv/bin/python scripts/decompilar.py
.venv/bin/python scripts/desmontar_dex.py
.venv/bin/python scripts/analisar_nativo.py
.venv/bin/python scripts/investigar_contador.py
.venv/bin/python scripts/analisar_formatos.py
.venv/bin/python scripts/investigar_nef.py
.venv/bin/python scripts/recuperar_esquema.py
.venv/bin/python scripts/correlacionar.py
.venv/bin/python scripts/catalogar_dependencias.py
.venv/bin/python scripts/segunda_opiniao.py
.venv/bin/python scripts/verificar_assinaturas.py
python3 scripts/diagnosticar_dinamica.py
.venv/bin/python scripts/auditar.py
.venv/bin/python -m unittest discover -s scripts/testes -v
.venv/bin/python -m compileall -q scripts
.venv/bin/pip check
```

A execução foi feita com cerca de 4 GB de RAM e duas CPUs. Há alguns gigabytes
de saídas regeneráveis: smali, Java de múltiplas passagens e desmontagem nativa.
Não estimar cobertura com base no término do processo: examine `cobertura.json`,
`*.problemas.jsonl.gz`, `*.diagnosticos.jsonl.gz` e `*.metodos-nao-recuperados.jsonl.gz`.
O retorno zero do JADX 1.1.0 não significa ausência de erros.
O script não pula uma passagem apenas porque o relatório já está no Git:
se os fontes locais estiverem ausentes, ele os regenera; se estiverem presentes,
confere hash da entrada e de todos os fontes antes de reutilizá-los. Essa retomada
foi testada removendo logicamente a saída Audience Network (arquivada em
`.trabalho/`), regenerando-a com o relatório preexistente e conferindo o restante.

`analisar_android.py --somente apks` ou `--somente dex` refina uma modalidade
sem repetir a outra. O catálogo de chamadas usa os nomes efetivamente encontrados,
inclusive o namespace atual de `NexEditor`, e não somente o nome antigo.

## Ensaios de repetibilidade realizados

### Extração integral repetida

```bash
python3 scripts/extrair.py \
  --saida .trabalho/repeticao \
  --inventarios .trabalho/inventarios-repeticao
```

O comparador ignora apenas a mudança do caminho de saída: exige iguais pai,
nome do membro, tamanho, CRC e SHA-256 para todos os 3.163 registros.

### Remontagem delimitada de bytecode

```bash
JAVA_LOCAL="$PWD/.trabalho/ferramentas/java/jdk4py/java-runtime/bin/java"
BIBLIOTECAS="$PWD/.trabalho/ferramentas/jadx/package/resources/jadx-1.1.0/lib"
"$JAVA_LOCAL" -cp "$BIBLIOTECAS/*" org.jf.smali.Main assemble \
  --output .trabalho/classes2-remontado.dex \
  decompilado/gerado/smali/com.nexstreaming.app.kinemasterfree__classes2.dex
```

Não usar `--jobs 2` nesta combinação baksmali/JCommander: a incompatibilidade
observada e sua correção estão no manifesto. O ensaio de `classes2.dex` cobre
uma classe/um método. O DEX refeito tem 976 bytes contra 1.364 originais e hash
diferente; classes, assinaturas e instruções simbólicas conferem. Não demonstra
build Android nem equivalência binária de todos os DEX.

### Decompilação repetida de uma classe

```bash
for SAIDA in .trabalho/jadx-teste-multidex .trabalho/jadx-teste-repetido; do
  JAVA_HOME="$PWD/.trabalho/ferramentas/java/jdk4py/java-runtime" \
  JAVA_OPTS='-Xmx1600m' \
    bash .trabalho/ferramentas/jadx/package/resources/jadx-1.1.0/bin/jadx \
    -j 2 -r --single-class com.kinemaster.app.database.project.ProjectDatabase_Impl \
    -d "$SAIDA" .trabalho/extraido/pacote/com.nexstreaming.app.kinemasterfree.apk
done
.venv/bin/python scripts/validar_repeticao.py
```

Os dois ensaios emitiram um arquivo com o mesmo SHA-256. Isso valida somente
a classe selecionada, não determinismo de todas as passagens paralelas do JADX.

## Preparação offline do zero testada

Foi criada uma nova `.venv` após arquivar o ambiente anterior em `.trabalho/`.
`preparar.py` instalou os wheels locais com `--no-index`. Os dez testes e
`pip check` passaram. As ferramentas Java e p7zip são extraídas dos arquivos
com hash fixado para `.trabalho/ferramentas/`, não instaladas globalmente.

## Inspeção dos índices compactados

```bash
.venv/bin/python - <<'PY'
import gzip, json
from pathlib import Path
arquivo = Path('docs/analise/inventarios/jni.jsonl.gz')
registros = [json.loads(linha) for linha in gzip.decompress(arquivo.read_bytes()).decode().splitlines()]
print('Métodos nativos:', len(registros))
print('Sem export compatível:', sum(not r['correspondencias_estaticas'] for r in registros))
PY
```

## Próximo ambiente dinâmico autorizado

Somente após obter ADB oficial, registrar versão/hash/licença e fornecer Android
ARM64 descartável/API ≥ 26, executar com valores **reais**, não os campos ilustrativos:

```text
.venv/bin/python scripts/observar_android.py --adb CAMINHO_REAL \
  --sha256-adb HASH_REAL --serial SERIAL_REAL --confirmar-ambiente-descartavel
```

Essa etapa não foi realizada. O script de observação não é demonstração de runtime.
Não publique dados brutos em `dados-dinamicos/`. Planeje separadamente edição,
exportação, STT, carregamento de classes/bibliotecas e serviços de rede legítimos.
Ferramentas grandes podem ser obtidas com `scripts/obter_externas.py`, conforme
manifesto; a aquisição continua dependente de rede e suas licenças.
