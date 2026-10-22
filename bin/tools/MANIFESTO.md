# Manifesto das ferramentas de engenharia reversa

Este manifesto registra, para cada ferramenta usada na análise do KineMaster, a origem, a versão, a licença,
os hashes, as instruções de uso, as dependências, as limitações e a situação de versionamento.
Gerado e mantido manualmente; a verificação dos hashes versionados é feita por `scripts/preparar_ambiente.sh`.

Data de verificação: 2026-10-22 (ambiente: Debian 12, x86_64, Python 3.11.2).

## Estrutura

```
bin/tools/
├── MANIFESTO.md            este arquivo
├── SHA256SUMS.txt          SHA-256 de cada arquivo versionado em android/
├── requirements.txt        dependências Python fixadas (instaladas em KM_WORKDIR/venv)
└── android/
    ├── jadx/               JADX 1.1.0 (distribuição oficial de skylot/jadx, Apache-2.0)
    └── apktool/            Apktool 2.4.1 (apktool_2.4.1.jar, Apache-2.0)
```

`KM_WORKDIR` (padrão `~/kinemaster-work`) guarda artefatos grandes gerados (XAPK extraído, APKs, código
descompilado, logs). Eles ficam **fora do Git** de propósito (centenas de MB). Ver `scripts/ambiente.sh`.

## Restrições de origem de rede (fato observado)

O sandbox permite apenas `github.com`, `codeload.github.com`, `api.github.com`, `registry.npmjs.org`,
`pypi.org` e `files.pythonhosted.org`. **Os downloads de assets de release do GitHub
(`release-assets.githubusercontent.com`) e o Maven Central, Google, Gradle e Adoptium estão bloqueados.**
Por isso, as ferramentas foram obtidas de pacotes npm e PyPI, cujos integrity/hashes foram registrados.

---

## 1. JADX 1.1.0 — descompilador de DEX para Java

| Campo | Valor |
|---|---|
| Finalidade | Descompilar `classes*.dex` para código Java e recursos |
| Versão exata | **1.1.0** (`jadx --version` → `1.1.0`) |
| Origem oficial | https://github.com/skylot/jadx (release v1.1.0) |
| Origem efetiva deste repositório | pacote npm `@mishguru/jadx-node@3.1.0` (tarball `resources/jadx-1.1.0/`) |
| Licença | Apache-2.0 (`android/jadx/LICENSE`, `NOTICE`); wrapper npm ISC (não distribuído aqui) |
| Hash do tarball npm usado | SHA-256 `1521a7a2d64f399cd5a3926a2ae363e4cdc5a81619fbe94a3f3f5c7d3c0263c7`; `integrity` npm `sha512-Q3/hsNEE7uaY6K8oGvHE4ridtx+M7ndBvj98PG6C8l0EKs/DiqATaOFheQ48v07SlhFWZV9/vozQ1XUj7Rs10g==` |
| Hashes dos arquivos versionados | ver `SHA256SUMS.txt` (prefixo `android/jadx/`) |
| Plataforma | Qualquer JVM 11+ (testado com Temurin/OpenJDK 21.0.8); Linux x86_64 |
| Versionado no Git | **Sim**, em `android/jadx/` (16 MB descompactado; ~37 JARs) |
| Execução | `JAVA_HOME=<jdk> bin/tools/android/jadx/bin/jadx -d <saida> <arquivo.apk>` ou `scripts/decompilar_jadx.sh` |
| Dependências | Java 11+ (fornecido por `jdk4py`, ver seção 3) |
| Limitações conhecidas | **Versão antiga (1.1.0, 2020)**. A mais recente (1.5.x) é a preferível e não pôde ser baixada (release bloqueado). Gera muitos avisos (ex.: falhas de reestruturação de `try/catch` e de inferência de tipos em `Bitmap`/`Canvas`); cada `ERROR`/`WARN` precisa ser auditado por classe (ver `docs/analise/inventarios/jadx-*.txt`). |
| Redistribuição | Permitida pela Apache-2.0 com preservação de LICENSE/NOTICE (presentes). |
| Verificação | `scripts/preparar_ambiente.sh` confere hashes e `jadx --version`. |

## 2. Apktool 2.4.1 — decodificador de recursos e smali

| Campo | Valor |
|---|---|
| Finalidade | Decodificar `AndroidManifest.xml`, `resources.arsc`, XML de recursos, `smali` de todos os DEX |
| Versão exata | **2.4.1** (`java -jar apktool_2.4.1.jar --version` → `2.4.1`) |
| Origem oficial | https://github.com/iBotPeaches/Apktool (release v2.4.1) |
| Origem efetiva | pacote npm `apktool-jar@2.4.1` (arquivo `bin/apktool_2.4.1.jar`) |
| Licença | Apache-2.0 (Apktool). Pacote npm wrapper: ISC. |
| Hash do JAR versionado | `bdeb66211d1dc1c71f138003ce35f6d0cd19af6f8de7ffbdd5b118d02d825a52` (SHA-256 — ver `SHA256SUMS.txt`) |
| Hash do tarball npm usado | SHA-256 `65accb583cf50fea0f6990dd92ba6d4d9129945c7c1805a30d3594b75e071dd7`; `integrity` npm `sha512-69T9zeKJ1dVCFJ8cle9S4V6A4QLkfvf3MbJaOiep8FC9NhsW61J65RsthWnee/WbEt06lFyHiSLtjGE2JnYCiw==` |
| Verificação de origem | O asset oficial `apktool_2.4.1.jar` da release v2.4.1 (API do GitHub) tem **17.661.172 bytes**, igual ao JAR versionado. **Não há digest SHA-256 publicado pela API para esse asset e o download direto está bloqueado; portanto a identidade é por tamanho e integridade npm, não por hash oficial.** |
| Plataforma | Qualquer JVM 8+ |
| Versionado no Git | **Sim**, `android/apktool/apktool_2.4.1.jar` (17,7 MB, abaixo do limite de 100 MB do GitHub) |
| Execução | `java -jar bin/tools/android/apktool/apktool_2.4.1.jar d -f -o <saida> <apk>` ou `scripts/decompilar_apktool.sh` |
| Limitações conhecidas | Versão 2.4.1 (2020) é anterior a APKs com `targetSdk` 36; a decodificação de atributos novos pode falhar ou ser incompleta. Usa framework padrão em `~/.local/share/apktool/framework` (gerado na primeira execução). |
| Redistribuição | Apache-2.0 permite redistribuição com LICENSE. |

## 3. Java 21 (runtime) — fornecido pelo pacote PyPI `jdk4py`

| Campo | Valor |
|---|---|
| Finalidade | Executar JADX e Apktool |
| Versão exata | **OpenJDK/Temurin 21.0.8+9 (runtime, JRE)** — `jdk4py==21.0.8.2` |
| Origem | https://pypi.org/project/jdk4py/ (empacota um runtime Temurin) |
| Licença | GPLv2 com Classpath Exception (OpenJDK); wrapper `jdk4py`: Apache-2.0 |
| Localização | `KM_VENV/lib/python3.11/site-packages/jdk4py/java-runtime` (não versionado) |
| Limitações | **O runtime não inclui `jdk.compiler` (sem `javac`)**. Isso é um requisito do provider Android do REA (ver seção 7). Não há JDK completo disponível (Adoptium bloqueado). |
| Versionado no Git | Não (reproduzível por `requirements.txt`). |

## 4. Ferramentas Python (fixadas em `requirements.txt`)

| Ferramenta | Versão | Finalidade | Licença | Versionado? |
|---|---|---|---|---|
| py7zr | 0.22.0 | Extrair o 7z de entrada | LGPL-2.1-or-later | Não (pip) |
| androguard | 4.1.3 | Análise de APK/DEX/manifesto (2º decompilador de verificação cruzada, fase 3) | Apache-2.0 | Não (pip) |
| lief | 0.16.6 | Análise de binários ELF (fase 4) | Apache-2.0 | Não (pip) |
| pyelftools | 0.32 | Parsing ELF (inventário nativo) | Domínio público | Não (pip) |
| capstone | 5.0.6 | Desmontagem ARM64 (fase 4) | BSD | Não (pip) |
| jdk4py | 21.0.8.2 | Runtime Java (seção 3) | ver seção 3 | Não (pip) |

Instalação: `scripts/preparar_ambiente.sh` (cria `KM_VENV` e instala as versões fixadas).

## 5. binutils (sistema) — `readelf`, `objdump`, `nm`, `strings`

| Campo | Valor |
|---|---|
| Versão | GNU Binutils 2.40 (Debian 12) |
| Origem | Pacote de sistema `binutils` (apt). **Não versionado; é pré-requisito do host.** |
| Licença | GPL-3.0 (binutils) |
| Uso | Inventário de seções, símbolos e cabeçalhos ELF (`scripts/inventario_nativo.py` usa pyelftools; `readelf` para verificação cruzada) |

## 6. Ferramentas NÃO disponíveis neste ambiente (registro de lacunas)

| Ferramenta | Situação | Motivo | Caminho recomendado |
|---|---|---|---|
| **Ghidra** (NSA, Apache-2.0) | Não instalado | Release do GitHub (~400 MB) bloqueado; não redistribuível no Git por tamanho | Baixar a release oficial 12.x em https://github.com/NationalSecurityAgency/ghidra/releases, definir `GHIDRA_INSTALL_DIR` e validar o SHA-256 oficial antes do uso. Ver `docs/analise/ferramentas-e-restricoes.md` |
| **JADX 1.5.x+** | Não obtido | Release bloqueado | Mesmo procedimento do JADX; atualizar `bin/tools/android/jadx` quando possível |
| **Android SDK Platform-Tools (ADB), emulador, system images** | Não instalado | `dl.google.com` bloqueado; sem KVM verificado | Ver fase 6 em `TODO.md` |
| **jadx-headless-mcp 0.7.1 (JAR, REA)** | Não obtido | Só existe até 0.7.0 no npm; o JAR não está no pacote | Definir `REA_JADX_MCP_JAR` em ambiente com acesso |
| **Hopper, IDA, Kuna** | Não instalados | Licenças proprietárias / sem acesso | Fora do escopo gratuito |
| **REA/MCP (`rea-agents` 6.7.0, MIT)** | `doctor` executado (somente leitura); saúde: não saudável | Faltam Hopper/Ghidra/JADX MCP/Kuna; e as ferramentas REA **não estão registradas nesta sessão** | Ver `docs/analise/ferramentas-e-restricoes.md` |

## 7. Requisitos do REA para Android (informativo)

Segundo a skill `reverse-engineer-anything` (metadados versão 44): o provider Android exige JADX MCP JAR 0.7.1
(`REA_JADX_MCP_JAR`) e um **JDK completo com `jdk.compiler`** (Java 17+). Este ambiente tem JRE sem `jdk.compiler`.
Ver `docs/analise/ferramentas-e-restricoes.md`.

## 8. Itens que não podem ser redistribuídos

- Os APKs e o XAPK do KineMaster (propriedade de terceiros) **não são versionados como código extraído**;
  os volumes 7z originais já estão em `bin/` por decisão do autor do repositório e não são alterados.
- Nenhum JDK, Ghidra, SDK Android ou imagem de sistema foi versionado.

## 9. Como reproduzir

```bash
bash scripts/preparar_ambiente.sh        # venv + hashes + verificação das ferramentas
python3 scripts/extrair_xapk.py          # reconstitui e extrai o XAPK (usa KM_WORKDIR)
python3 scripts/inventario_apk.py ...    # ver docs/analise/README.md
```
