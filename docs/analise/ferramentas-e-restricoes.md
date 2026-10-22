# Ferramentas, versões e restrições

Este documento registra **o que foi usado**, **em qual versão**, **de onde veio** e **o que não pôde ser usado**.
Fatos verificados estão marcados como tal; o que não foi executado não é afirmado como feito.

Ficha completa de cada ferramenta versionada: `bin/tools/MANIFESTO.md`. Hashes: `bin/tools/SHA256SUMS.txt`.

## 1. Skill Reverse Engineer Anything (REA)

- **Fato:** a skill foi lida de `morluto/rea`, commit `5375ff0f`, `version: "44"` (metadados do `SKILL.md`), e copiada para `.agents/skills/reverse-engineer-anything/` (inclui `LICENSE-REA`).
- **Fato:** a skill exige diagnóstico com `npx -y rea-agents@latest doctor --client <cliente> --json` (executado com `rea-agents@6.7.0`; saída em `logs/rea-doctor.json` fora do Git; resultado: não saudável, código de saída 1).
- **Fato:** a skill exige, para análise Android via `inspect_android_*` e `trace_android_references`, o JADX MCP JAR 0.7.1 (`REA_JADX_MCP_JAR`) e JDK 17+ com `jdk.compiler`.
- **Fato:** **nenhuma ferramenta REA/MCP estava registrada nesta sessão.** Portanto, **nenhuma chamada `inspect_android_*`, `trace_android_references` ou `close_binary` foi executada.** Não há evidência de uso de recursos REA/MCP.
- **Fato:** `jadx-headless-mcp` 0.7.1 não está publicado no npm (último publicado: 0.7.0), e o JDK do ambiente não tem `jdk.compiler`.

## 2. Ferramentas usadas na análise

| Ferramenta | Versão | Origem | Situação | Local |
|---|---|---|---|---|
| JADX | 1.1.0 | GitHub `skylot/jadx` (release v1.1.0; SHA-256 do pacote verificado por `bin/tools/SHA256SUMS.txt`) | Versionado em `bin/tools/android/jadx/` (Apache-2.0) | `bin/tools/` |
| Apktool | 2.4.1 | GitHub `iBotPeaches/Apktool` (release v2.4.1) | Versionado em `bin/tools/android/apktool/` (Apache-2.0) | `bin/tools/` |
| py7zr | 0.22.0 | PyPI | Venv fora do Git | `/home/user/kinemaster-work/venv` |
| androguard | 4.1.3 | PyPI | Venv fora do Git (Apache-2.0) | idem |
| lief | 0.16.6 | PyPI | Venv fora do Git (Apache-2.0) | idem |
| pyelftools | 0.32 | PyPI | Venv fora do Git | idem |
| capstone | 5.0.6 | PyPI | Venv fora do Git (BSD) | idem |
| jdk4py | 21.0.8.2 (Temurin 21.0.8) | PyPI | Java usado pelo JADX/Apktool; sem `jdk.compiler` | idem |
| binutils | 2.40 | sistema | `readelf`, `objdump`, `nm`, `strings` | sistema |

Observação de versão: a versão 1.1.0 do JADX é antiga. A 1.5.x (preferível) não pôde ser baixada (ver §4). Isso afeta a qualidade da descompilação e foi medido (ver `docs/analise/relatorio-parcial.md`).

## 3. Ferramentas consideradas e não usadas

| Ferramenta | Motivo |
|---|---|
| Ghidra | Release oficial (~400 MB) inacessível pelo host de download; não instalado. Análise nativa ficou limitada a inventário e a leitura com binutils (§5 do relatório). |
| jadx-mcp (plugin) | Servidor MCP baseado em plugin de JADX GUI; não é ferramenta de linha de comando. Descartado. |
| jadx-headless-mcp 0.7.1 | Exigido pela skill REA; não publicado no npm. |
| angr | Não instalado nesta etapa; avaliação pendente (TODO, Fase 4). |
| Emulador Android / adb | Inexistente no ambiente (sem `/dev/kvm`, sem `adb`, sem `emulator`). Ver §6. |

## 4. Restrições de rede e acesso (verificadas)

Hosts permitidos: `github.com`, `codeload.github.com`, `api.github.com`, `registry.npmjs.org`, `pypi.org`, `files.pythonhosted.org`.

- **Fato:** `release-assets.githubusercontent.com` (assets de release do GitHub), `raw.githubusercontent.com`, Maven, Google (`dl.google.com`), Gradle e Adoptium **não são acessíveis**. Por isso o JADX 1.5.x e o Temurin não foram baixados diretamente.
- **Fato:** a API do GitHub (`api.github.com`) forneceu metadados das releases, mas **não há digest SHA-256 publicado nos assets** do JADX 1.1.0 e do Apktool 2.4.1. A integridade foi, portanto, registrada por SHA-256 calculado localmente, a partir de pacotes obtidos por npm (`@mishguru/jadx-node` 3.1.0, que contém JADX 1.1.0; `apktool-jar` 2.4.1) e comparados com o tamanho do asset oficial (`apktool_2.4.1.jar`: 17.661.172 B). Ver `bin/tools/MANIFESTO.md`.

## 5. Situação de redistribuição

| Item | Situação | Fundamento |
|---|---|---|
| JADX 1.1.0 | Redistribuído em `bin/tools/` | Apache-2.0 (permite redistribuição com aviso de licença) |
| Apktool 2.4.1 | Redistribuído em `bin/tools/` | Apache-2.0 |
| KineMaster (APK/XAPK/7z em `bin/`) | Entrada original, **não modificada**, já versionada pelo autor do repositório | Não é ferramenta; sua redistribuição não é avaliada aqui. Não enviar a serviços externos. |
| Ghidra, Temurin, Android SDK | Não versionados; instruções e validação de hash em `bin/tools/MANIFESTO.md` | Tamanho e licenciamento de terceiros |

Artefatos grandes gerados (APK extraído, decompilados, logs) ficam **fora do Git**, em `/home/user/kinemaster-work/`, por tamanho e para não redistribuir o conteúdo do aplicativo.

## 6. Análise dinâmica: bloqueio verificado

- **Fato:** o ambiente não tem `/dev/kvm`, `adb` nem `emulator`; tem 2 CPUs e ~3 GB de RAM; `dl.google.com` retorna falha de conexão (código HTTP `000`).
- **Consequência:** a Fase 6 (dinâmica) **não foi executada**. Nenhuma observação de runtime é apresentada como resultado.
- **Política:** dinâmica só em ambiente autorizado/controlado, sem contornar DRM, licença ou autenticação do aplicativo.

## 7. Como o ambiente é preparado

Ver `scripts/preparar_ambiente.sh` (verifica hashes de `bin/tools/` e presença de Java, JADX e Apktool) e `scripts/ambiente.sh` (exporta `KM_JADX_HOME`, `KM_APKTOOL_JAR` e `KM_WORKDIR`). Para a área de trabalho externa ao Git, defina `KM_WORKDIR=/home/user/kinemaster-work`.
