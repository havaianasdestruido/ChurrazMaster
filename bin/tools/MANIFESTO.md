# MANIFESTO DAS FERRAMENTAS — engenharia reversa do KineMaster

Este manifesto registra **todas** as ferramentas usadas ou avaliadas na missão, com versão,
origem oficial, licença, hash SHA-256 e situação de redistribuição no Git.

Estado do ambiente de referência: Linux x86_64, Debian 12, Python 3.11.2, Node.js 22.22.3,
2 vCPU, 3,8 GiB de RAM, **sem `/dev/kvm`** (ou seja, sem virtualização acelerada).

> Regra do projeto: nenhuma ferramenta é declarada disponível sem verificação real de execução.
> A coluna **Verificada em execução** indica o que foi efetivamente executado neste ambiente.

## 1. Resumo da disponibilidade

| # | Ferramenta | Versão | Verificada em execução | Situação |
|---|---|---|---|---|
| 1 | JDK/JRE Temurin (via wheel `jdk4py`) | 25.0.2.1 | Sim (`java -version`) | **Versionada** (wheel) |
| 2 | jadx (distribuição completa) | 1.1.0 | Sim (descompilou 45.195 arquivos `.java`) | **Versionada** |
| 3 | jadx-headless-mcp (jar MCP, embute jadx-core) | 0.7.0 | Sim (21 ferramentas MCP, 43.251 classes) | **Versionada** |
| 4 | Apktool | 2.4.1 | Sim (decodificou 20 APKs) | **Versionada** |
| 5 | py7zr | 1.1.4 | Sim (abriu 7z multi-volume) | **Versionada** (wheel) |
| 6 | androguard | 4.1.4 | Sim (certificados, DEX) | **Versionada** (wheel) |
| 7 | pyelftools | 0.33 | Sim (22 ELF) | **Versionada** (wheel) |
| 8 | LIEF | 1.0.0 | Instalada; não utilizada na análise final | Versionada (wheel) |
| 9 | Capstone | 5.0.9 | Instalada; não utilizada na análise final | Versionada (wheel) |
| 10 | REA (skill + CLI `rea-agents`) | skill v44 / CLI 6.7.0 | Sim (`doctor --json`) | Não versionada (npm) |
| 11 | Ghidra | 12.2 PUBLIC | **Não** — download bloqueado | Não versionada (ver §4) |
| 12 | Android SDK Platform-Tools (ADB) | — | **Não** — indisponível | Ausente (ver §4) |

## 2. Ferramentas versionadas em `bin/tools/`

### 2.1 JDK/JRE — Temurin 25.0.2.1 (wheel `jdk4py`)

- **Finalidade:** executar jadx e Apktool (ambos são aplicações Java).
- **Versão exata:** 25.0.2.1 (Temurin-25.0.2+10-LTS; "OpenJDK Runtime Environment").
- **Origem oficial:** https://pypi.org/project/jdk4py/ (projeto que empacota o Temurin).
  Binário upstream: https://github.com/adoptium/temurin25-binaries (ou temurin21).
- **Licença:** GPLv2 com Classpath Exception (GPL-2.0-with-classpath-exception).
- **Arquivo versionado:** `bin/tools/java/jdk4py-25.0.2.1-py3-none-manylinux_2_17_x86_64.whl` (35.411.575 bytes)
- **SHA-256:** `383d71d8639d072752d2eaafc84944e0d8de7f1c45d30c28f6dfef8a4a65543c`
- **Plataforma:** Linux x86_64 (manylinux 2.17).
- **Execução:** `python3 scripts/instalar_ferramentas.sh` instala o wheel e extrai a árvore em
  `bin/tools/java/jdk/` (diretório derivado, ignorado pelo Git). `JAVA_HOME` é resolvido
  automaticamente pelos scripts via `scripts/lib/comum.sh`.
- **Dependências:** nenhuma em tempo de execução.
- **Limitação conhecida:** o wheel entrega **apenas o JRE** (`bin/java`, `keytool`, `jstack`…),
  **sem `javac` e sem o módulo `jdk.compiler`**. Consequência verificada:
  `java Arquivo.java` (source-file launcher) falha com
  `InternalError: Module jdk.compiler not in boot Layer`. Portanto **não foi possível compilar
  drivers Java próprios**; a análise usa as CLIs já empacotadas (jadx, Apktool) e o servidor MCP.
- **Redistribuição:** o wheel é redistribuível (GPL+CE). A árvore extraída **não** é versionada
  por ser derivada e volumosa; ela é reproduzível a partir do wheel versionado.

### 2.2 jadx 1.1.0 (distribuição completa)

- **Finalidade:** descompilar DEX → Java; decodificar recursos XML do APK.
- **Versão exata:** 1.1.0 (`bin/jadx --version` → `1.1.0`).
- **Origem oficial:** https://github.com/skylot/jadx/releases/tag/v1.1.0
  (o download direto de *release assets* do GitHub está **bloqueado** neste ambiente;
  a distribuição foi obtida do pacote npm `@mishguru/jadx-node@3.1.0`, que empacota a
  distribuição oficial 1.1.0 sem modificações — ver §4.1).
- **Licença:** Apache-2.0 (arquivos `LICENSE` e `NOTICE` preservados em `bin/tools/android/jadx/`).
- **Arquivos versionados:** `bin/tools/android/jadx/` (44 arquivos, 16 MB), incluindo
  `lib/jadx-core-1.1.0.jar`, `lib/jadx-cli-1.1.0.jar`, `lib/baksmali-2.3.4.jar`, `lib/dexlib2-2.3.4.jar`,
  `lib/dx-1.16.jar`, `lib/android-29-clst.jar`, `lib/android-29-res.jar`.
- **SHA-256 (jars principais, calculados localmente):**
  - `lib/jadx-core-1.1.0.jar`: `00ddc42d8967b85507c7fd391a013017ccbc5c5ba5facf9cdcca59b8199b9bbb`
  - `lib/jadx-cli-1.1.0.jar`: `312b0e7fec9246f243c5f20338c27e139e4d11d8ed6a37da0485d85d7b70de6a`
  - `lib/baksmali-2.3.4.jar`: `880bbdb3921db64239b4dc7ebacfc228de990ace8d7947f29bfc6e7cfcba504a`
  - `lib/dexlib2-2.3.4.jar`: `b7b45e790da22950b16177a668193d026b8f611c46f7b941340a61cc8cbe8b3b`
  - Inventário completo (44 arquivos): `docs/analise/inventarios/ferramentas_hashes.json`
- **Plataforma:** qualquer JVM (testado com Temurin 25).
- **Execução:** `bin/tools/android/jadx/bin/jadx -d <saída> <entrada.apk|dex>` (JAVA_HOME necessário).
- **Limitações observadas (importantes):**
  - Renomeia pacotes com caracteres inválidos (`firebase-auth-api` → `p002firebaseauthapi`) e
    classes com nome inválido (`…X.4V` → `AnonymousClass4V`), registrando
    `/* renamed from: ... reason: ... */` — o script `scripts/05_metricas_dex.py` reconstrói esse mapa.
  - Move classes do pacote padrão para `defpackage/`.
  - Registrou **148 erros de geração de código** (`JADX ERROR`, `JadxOverflowException`,
    `RegionMakerVisitor`) nos 6 DEX principais e **7** no DEX de `assets/`.
- **Redistribuição:** Apache-2.0 permite redistribuição com avisos preservados. **Versionada.**

### 2.3 jadx-headless-mcp 0.7.0 (jar executável, embute jadx-core)

- **Finalidade:** servidor MCP de análise Android (o mesmo tipo de provedor que a skill REA usa em
  `REA_JADX_MCP_JAR`). Expõe 21 ferramentas: `list_classes`, `get_class_source`, `get_smali_of_class`,
  `get_xrefs_to_class/method/field`, `search_code`, `get_package_tree`, `get_manifest`, entre outras.
- **Versão exata:** 0.7.0 (arquivo `bin/tools/android/jadx-mcp/jadx-mcp-0.7.0.jar`).
- **Origem oficial:** https://github.com/1013503897/jadx-headless-mcp (empacotado no npm
  `jadx-mcp@1.4.1`). A skill REA referencia a versão **0.7.1**; a versão disponível e verificada
  aqui é **0.7.0** — divergência registrada.
- **Licença:** Apache-2.0 (declarada no pacote npm).
- **Tamanho / SHA-256:** 43.756.715 bytes — `8f246e83f37a18259ca60a5fab6acc23af972445c02afde8ca867317a57b08f6`
- **Plataforma:** JVM; testado com Temurin 25 e `-Xmx1500m`.
- **Execução:** `java -jar bin/tools/android/jadx-mcp/jadx-mcp-0.7.0.jar --file <apk> --transport STDIO`
  ou, pelo cliente do projeto, `python3 scripts/09_mcp_jadx.py --apk <apk> --listar`.
- **Resultado observado:** `get_info` → `classes=43251, errors=0, warnings=0`; árvore de pacotes com
  1.755 pacotes. Renomeia `firebase-auth-api` → `p002firebaseauthapi`.
- **Limitação:** não possui CLI de exportação em massa; cada classe exige uma chamada MCP.
- **Redistribuição:** **Versionada** (Apache-2.0).

### 2.4 Apktool 2.4.1

- **Finalidade:** decodificar `AndroidManifest.xml` binário, `resources.arsc` e XMLs de recurso.
- **Versão exata:** 2.4.1 (`java -jar … --version` → `2.4.1`).
- **Origem oficial:** https://github.com/iBotPeaches/Apktool/releases/tag/v2.4.1
  (obtido pelo pacote npm `apktool-jar@2.4.1`, que empacota o jar oficial).
- **Licença:** Apache-2.0 (jar). O invólucro npm declara ISC.
- **Tamanho / SHA-256:** 17.661.172 bytes — `bdeb66211d1dc1c71f138003ce35f6d0cd19af6f8de7ffbdd5b118d02d825a52`
- **Plataforma:** JVM (funcionou com Temurin 25, embora 2.4.1 não tenha sido escrita para Java 25).
- **Execução:** `java -jar bin/tools/android/apktool/apktool_2.4.1.jar d -f -s -o <saída> <apk>`
- **Resultado observado:** decodificou com sucesso os 20 APKs, incluindo `compileSdkVersion=37`,
  `targetSdkVersion=36` e `resources.arsc` moderno.
- **Limitação conhecida:** sendo de 2020, pode não reproduzir recursos de versões muito recentes
  do AAPT2; ainda assim, nesta amostra **não houve falha de decodificação**.
- **Redistribuição:** **Versionada** (Apache-2.0).

### 2.5 Bibliotecas Python (wheels versionados em `bin/tools/utilitarios/wheels/`)

| Biblioteca | Versão | Licença | Wheel (bytes) | SHA-256 |
|---|---|---|---|---|
| py7zr | 1.1.4 | LGPL-2.1 | 75.484 | `f6f868a457a6bd485b78d26e619045718bcd0b7720a6bf97d5fe86a1523423e8` |
| androguard | 4.1.4 | Apache-2.0 | 1.026.935 | `6265a6d4007401cf5a62a98fa99a1c2994644d4311da031a5398efe3bcaa63b0` |
| pyelftools | 0.33 | Domínio público (PD) | 201.178 | `f215ad5f47d3f1373a21496a6c9e0707c622840d0622f23ff7ce08678b020036` |
| LIEF | 1.0.0 | Apache-2.0 | 3.670.339 | `96277e45d58de45a33249079dac43e50db1c96da162fbe1ad02fbaa82e3bd06f` |
| Capstone | 5.0.9 | BSD-3-Clause | 1.485.316 | `273fd8d747d2e35c88f91450be51a603ecfaafb00d96d9f315dcb8689c86193e` |

- **Instalação reproduzível:** `python3 scripts/instalar_ferramentas.sh` (usa `--no-index --find-links`
  quando os wheels estão presentes; caso contrário, recorre ao PyPI).
- **Dependências transitivas** (apkInspector, cryptography, lxml, loguru, sqlalchemy, networkx etc.)
  **não** estão versionadas; são obtidas do PyPI na instalação. Isso é uma limitação declarada.
- **Redistribuição:** todas permitem redistribuição; **versionadas**.

### 2.6 REA — skill Reverse Engineer Anything

- **Versão da skill:** `44` (metadados do `SKILL.md`), commit
  `5375ff0f9aadb4fd0d46333815aa0431e3df1010` do repositório `morluto/rea`.
- **CLI:** `rea-agents@6.7.0` (executado via `npx -y rea-agents@latest`); identidade reportada:
  SDK do servidor 2.3.1, 207 ferramentas MCP declaradas no catálogo.
- **Licença:** conforme o repositório `morluto/rea` (não versionada aqui).
- **Situação real:** a skill **não registra** o servidor MCP nem instala motores. Neste ambiente:
  `healthy=false`; `GHIDRA_INSTALL_DIR` ausente; `REA_JADX_MCP_JAR` ausente; IDA e Kuna ausentes.
  Nenhuma ferramenta MCP do REA está disponível na sessão do agente.
- **Uso efetivo:** a skill foi usada como **metodologia** (evidência antes de conclusão,
  separação fato/inferência/hipótese, cobertura declarada) e o caminho Android dela
  (`inspect_android_package` → `jadx-headless-mcp`) foi reproduzido manualmente pelo cliente
  `scripts/09_mcp_jadx.py`, já que o MCP do REA não está registrado.

## 3. Ferramentas avaliadas e **não** utilizadas

| Ferramenta | Motivo |
|---|---|
| `baksmali`/`smali` 2.3.4 (embutidos no jadx 1.1.0) | Disponíveis e versionados; usados apenas se necessário para casos pontuais |
| dex2jar | Não obtido por canal permitido; jadx cobre a necessidade |
| aapt2 / build-tools | Sem canal permitido (Google Maven/CDN bloqueado); Apktool supre o decode |
| `apkInspector` 1.3.7 | Instalado como dependência do androguard; não necessário |

## 4. Ferramentas ausentes e o motivo técnico

### 4.1 Bloqueio de rede para *release assets* do GitHub

Downloads de `release-assets.githubusercontent.com` falham neste sandbox:

```
curl: (35) OpenSSL SSL_connect: SSL_ERROR_SYSCALL in connection to release-assets.githubusercontent.com:443
```

Hosts permitidos: `github.com`, `codeload.github.com`, `api.github.com`, `registry.npmjs.org`,
`pypi.org`, `files.pythonhosted.org`. Consequência direta: **não** foi possível baixar
jadx-1.5.6, apktool 3.0.3, Temurin JDK completo nem Ghidra pelos canais oficiais.
A alternativa usada — pacotes npm/PyPI que **empacotam** esses artefatos oficiais — está
registrada acima, com hash local.

### 4.2 Ghidra 12.2 PUBLIC — indisponível

- **Necessidade:** descompilação de pseudocódigo nativo (libnexeditorsdk.so tem 9,7 MB e 370 símbolos JNI).
- **Situação:** o asset `ghidra_12.2_PUBLIC_20261016.zip` (543,3 MB, SHA-256 oficial obtido da API do
  GitHub) não pôde ser baixado pelo bloqueio descrito em §4.1; também não está disponível em
  npm/PyPI. Exigiria JDK 21+.
- **Recomendação reproduzível:** em um host com rede liberada:
  ```bash
  curl -L -o ghidra_12.2_PUBLIC_20261016.zip \
    https://github.com/NationalSecurityAgency/ghidra/releases/download/Ghidra_12.2_build/ghidra_12.2_PUBLIC_20261016.zip
  sha256sum ghidra_12.2_PUBLIC_20261016.zip   # comparar com o digest publicado na API do GitHub
  unzip -q ghidra_12.2_PUBLIC_20261016.zip -d bin/tools/native/
  export GHIDRA_INSTALL_DIR="$PWD/bin/tools/native/ghidra_12.2_PUBLIC"
  ```
  (diretório ignorado pelo Git por exceder o limite de 100 MB por arquivo do GitHub).
- **Impacto declarado:** a análise nativa ficou no nível **estrutural e de símbolos**
  (ELF, seções, `DT_NEEDED`, `JNI_OnLoad`, símbolos `Java_*`, entropia, protetores).
  **Nenhum pseudocódigo nativo foi recuperado.**

### 4.3 Android SDK Platform-Tools (ADB) e emulador — indisponíveis

- Não há SDK Android, `adb`, `emulator` nem `/dev/kvm` neste ambiente (2 vCPU, 3,8 GiB RAM).
- O APK é **AArch64 apenas** (`lib/arm64-v8a`), o que impede execução nativa direta aqui.
- Consequência: a **Fase 6 (dinâmica) não foi executada**; ver
  `docs/analise/relatorios/limitacoes.md` para o procedimento reprodutível em dispositivo real.

## 5. Como validar este manifesto

```bash
# 1. hashes dos artefatos versionados
sha256sum bin/tools/android/apktool/apktool_2.4.1.jar \
          bin/tools/android/jadx-mcp/jadx-mcp-0.7.0.jar \
          bin/tools/java/*.whl bin/tools/utilitarios/wheels/*.whl

# 2. preparar ambiente
python3 scripts/instalar_ferramentas.sh
source scripts/lib/comum.sh && resolver_java && java -version

# 3. smoke tests
bin/tools/android/jadx/bin/jadx --version
java -jar bin/tools/android/apktool/apktool_2.4.1.jar --version
python3 scripts/09_mcp_jadx.py --apk <apk> --listar | head
```
