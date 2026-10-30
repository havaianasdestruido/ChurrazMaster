# Manifesto das ferramentas de engenharia reversa

## O que está realmente versionado

`ferramentas.json` contém **44 arquivos de distribuição**, cada um com tamanho,
SHA-256 calculado, versão, origem, licença, plataforma e dependências. Não há LFS
nem regra que ignore `bin/tools/`. Os arquivos originais de terceiros conservam
seus textos e licenças; não são material autoral traduzido desta investigação.
`DEPENDENCIAS.md` detalha os módulos Python transitivos e suas licenças.

| Ferramenta | Versão efetivamente executada | Finalidade e execução | Origem e licença |
|---|---|---|---|
| p7zip, via 7zip-bin | 16.02; pacote npm 5.2.0 | Verificação CRC e extração multivolume: `python3 scripts/extrair.py` | [7zip-bin](https://github.com/develar/7zip-bin), [p7zip](https://github.com/p7zip-project/p7zip). MIT no empacotamento **não substitui** LGPL-2.1+ e restrição unRAR dos binários. Textos adicionais neste diretório. |
| JADX | 1.1.0, redistribuído por @mishguru/jadx-node 3.1.0 | Java aproximado por DEX e multidex: `.venv/bin/python scripts/decompilar.py` | [JADX](https://github.com/skylot/jadx); Apache-2.0 e dependências em LICENSE/NOTICE do arquivo. Pacote npm de terceiro, não release oficial recém-baixada. |
| baksmali/dexlib2 | 2.3.4, JARs incluídos na distribuição acima | Bytecode legível: `.venv/bin/python scripts/desmontar_dex.py` | [smali](https://github.com/JesusFreke/smali), BSD-3-Clause; JARs e avisos preservados. |
| Temurin OpenJDK, runtime reduzido via jdk4py | runtime 21.0.8+9; jdk4py 21.0.8.2 | JVM local, `java -version`; preparação abaixo | [jdk4py](https://github.com/activeviam/jdk4py), MIT no empacotamento; runtime GPL-2.0 com Classpath Exception e avisos em `java-runtime/legal/`. **Não é um JDK completo**, não contém `javac` nem `jdk.compiler`. |
| Androguard | 4.1.4 | APK, AXML, ARSC e índice DEX: `scripts/analisar_android.py` | [Androguard](https://github.com/androguard/androguard), Apache-2.0; distribuições PyPI com hashes confrontados com o índice. |
| pyelftools | 0.32 | ELF, símbolos, relocação, DT_NEEDED: `scripts/analisar_nativo.py` | [pyelftools](https://github.com/eliben/pyelftools), domínio público/BSD conforme licença incorporada. |
| Capstone | 5.0.6 | Desmontagem linear AArch64 na mesma rotina | [Capstone](https://github.com/capstone-engine/capstone), BSD-3-Clause e avisos incorporados. Não é descompilador. |
| Skill Reverse Engineer Anything | 44, commit `5375ff0f9aadb4fd0d46333815aa0431e3df1010` | Método de trabalho; arquivo oficial com referências em `rea/skill-44-5375ff0.tar.gz` | [REA](https://github.com/morluto/rea), MIT. Orientação local em pt-BR em `.agents/skills/`. |
| REA CLI | 6.7.0 | Somente diagnóstico `doctor --json`, retorno 1 registrado | [REA](https://github.com/morluto/rea), MIT. Pacote npm preservado; dependências npm não são vendorizadas como se fossem um servidor MCP pronto. |

## Preparação local e execução

Requer host **Linux x86_64 com glibc ≥ 2.34**, Python 3.11 com `venv`, `pip` e
suporte a `lzma`, e Bash. Outras plataformas contidas nos arquivos npm não foram
validadas. O conjunto de wheels foi resolvido para CPython 3.11/Linux x86_64;
não promete instalação offline em ARM, Windows ou macOS.

```bash
python3 scripts/preparar.py
# Não usa rede: verifica hashes, descompacta ferramentas e instala wheels locais.
.trabalho/ferramentas/java/jdk4py/java-runtime/bin/java -version
JAVA_HOME="$PWD/.trabalho/ferramentas/java/jdk4py/java-runtime" \
  bash .trabalho/ferramentas/jadx/package/resources/jadx-1.1.0/bin/jadx --version
```

Não é necessário instalar Node.js nem executar wrappers npm para extrair e analisar.
O script não roda scripts de instalação dos pacotes npm. Para repetir **apenas**
o diagnóstico REA, com Node.js 22 e rede npm autorizada:

```bash
npx -y rea-agents@6.7.0 doctor --json
```

Esse último comando pode obter dependências transitivas npm e gravar cache.
Seu resultado histórico está em `docs/analise/evidencias/rea-diagnostico.json.gz`.
O diagnóstico observou provedores ausentes e nenhuma inscrição MCP; não equivale
a uma sessão REA conectada. A CLI 6.7.0 espera skill 42, enquanto a versão oficial
consultada é 44. Foram lidos os guias pertinentes e usados scripts especializados,
não ferramentas REA Android/nativas nem Evidence IDs fictícios. Nenhuma configuração
preexistente do agente foi sobrescrita. Arena não foi presumido ser Codex.

## Ferramentas grandes, restritas ou indisponíveis

`externas.json` fixa URL, tamanho e SHA-256 **informados pela API oficial GitHub**
para JADX 1.5.6, JDK Temurin 21.0.13+9 completo e Ghidra 12.1. Esses hashes não são
apresentados como resultado de downloads concluídos. Para tentar obtê-los em host
com acesso ao domínio de assets:

```bash
python3 scripts/obter_externas.py jadx
python3 scripts/obter_externas.py jdk
python3 scripts/obter_externas.py ghidra
```

JDK completo (207.858.352 bytes) e Ghidra (567.828.511 bytes) excedem o limite de
arquivo comum do GitHub; a distribuição adicional JADX (72.646.741 bytes) somada
aos demais arquivos ultrapassaria o orçamento de patch desta sessão. A solução é
obtenção local, com hash e licença fixados, em `.trabalho/downloads/`, explicitamente
não versionada. Não se utiliza LFS. Extração/instalação dessas versões e Ghidra
headless ainda **não foram executadas**. Ghidra requer JDK completo compatível;
o runtime reduzido já incluído não satisfaz essa dependência. A integração REA
Android exige `jadx-headless-mcp` 0.7.1 e JDK completo, ambos ausentes.

Downloads tentados de 7-Zip 26.05 e JADX 1.5.6 falharam por redirecionamento para
`release-assets.githubusercontent.com`, fora da lista de hosts permitidos.
`raw.githubusercontent.com` também estava bloqueado; a skill foi obtida via Git.
Não persistir em repetir a mesma URL sem mudança de rede. Apktool não foi
instalado: Androguard já decodificou os XML/ARSC identificados; futura reconstrução
pode justificar seu acréscimo. ADB/SDK, imagens Android e Ghidra não são declarados
disponíveis. Não há hash fictício para Platform Tools: sua aquisição oficial e
revisão de termos continuam pendentes, sem copiar binários de origem duvidosa.

## Licenças, fonte correspondente e limitações

- Distribuições são preservadas sem alteração, com LICENSE/NOTICE e arquivos
  `legal/` incorporados. Os hashes protegem integridade do download, não a confiança
  absoluta no distribuidor npm ou PyPI.
- Fontes do p7zip 16.02: o próprio distribuidor inclui as receitas
  `linux/x64/{build,do-build}.sh`, apontando para
  `https://downloads.sourceforge.net/project/p7zip/p7zip/16.02/p7zip_16.02_src_all.tar.bz2`.
  **Não executar essas receitas**: alteram pacotes globais. LGPL exige preservar
  as condições de obtenção/modificação do fonte correspondente; não confundir a
  licença MIT do wrapper com uma relicença do binário. Este projeto não usa o
  descompressor RAR; sua restrição original é preservada por prudência.
- Fontes do runtime: [Temurin 21.0.8+9](https://github.com/adoptium/temurin21-binaries/releases/tag/jdk-21.0.8%2B9),
  [OpenJDK correspondente](https://github.com/adoptium/jdk21u/tree/jdk-21.0.8%2B9).
  Preservar avisos, Classpath Exception e acesso ao fonte correspondente em
  qualquer redistribuição posterior. Não atribuir a licença MIT do jdk4py à JVM.
- Os textos auxiliares LGPL/unRAR foram obtidos do espelho p7zip no commit
  `15d1b6b2800ee5b084d7a53f80c2d9df06eca1ee`; não se afirma que esse espelho é o
  código-fonte exato do binário 16.02. A receita acima identifica a fonte daquela versão.
- JADX 1.1.0 é antigo: houve falhas reais de recuperação e de `RenameVisitor`.
  Índices por DEX isolado mantêm a evidência; a passagem agrupada usa os ajustes
  documentados. Código Java incompleto não é declarado compilável.
- baksmali 2.3.4 com JCommander 1.78 lança exceção ao receber `--jobs 2` neste
  conjunto. A rotina corrigida omite a opção; o diagnóstico inicial foi preservado.
- p7zip 16.02 é antigo; só foi usado offline em entrada fixada por hash. Atualizar
  para a versão oficial atual quando a rede permitir. Não usar em arquivos não confiáveis.
- Python/venv, Bash, glibc, Node.js (somente diagnóstico) são pré-requisitos do
  sistema operacional, não ferramentas RE essenciais deixadas só globalmente.
  Seus executáveis não foram copiados indiscriminadamente para o Git.
