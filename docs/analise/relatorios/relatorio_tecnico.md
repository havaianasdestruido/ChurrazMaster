# Relatório técnico — engenharia reversa do KineMaster 8.2.14.37398

**Alvo:** KineMaster — AI Video Editor, `com.nexstreaming.app.kinemasterfree`,
versão `8.2.14.37398.GP` (versionCode `37398`), minSdk 26, targetSdk 36, compileSdk 37.
**Origem do material:** `bin/` (4 volumes 7z fornecidos no repositório; origem declarada no nome
do arquivo: APKPure, variante `GP`).
**Data da análise:** 2026-10-22. **Branch:** `arena/ba1624f4-churrazmaster`.

Convenções deste relatório:

- **Fato observado** — verificado por ferramenta/inspeção direta, reproduzível por script.
- **Inferência** — conclusão que usa fatos observados + conhecimento técnico; não executada.
- **Hipótese** — proposta testável, ainda não confirmada.

---

## 1. Cadeia de integridade (Fase 0–2)

| Etapa | Objeto | Tamanho (bytes) | SHA-256 |
|---|---|---:|---|
| 1 | `…7z.001` | 25.165.824 | `41805c506119b0c1245c74c2ed8830614f6785e6e08e8def6137afc1add02ec0` |
| 2 | `…7z.002` | 25.165.824 | `6a4a76bc976967064a5320f4a47e9d17b678ed362e8fec4e2b689413cf659992` |
| 3 | `…7z.003` | 25.165.824 | `2ed8ba377aaf6e5f683ed8c43e9ec0fdce6f35b8e7da70adb26f138bfafb36c9` |
| 4 | `…7z.004` | 21.063.066 | `641f72ac4297804b192b068c4a06e6db5fa059cdf08e7e318c871745004903c1` |
| 5 | 7z reconstruído (concatenação lógica) | 96.560.538 | `b24aa3653a673de2ed5f85f039360f436c337afb6754f4a024c1a765ab025dc6` |
| 6 | XAPK interno (`….xapk`) | 180.092.744 | `07025005a02fd1874cf03f422e86182dcd053b950ee97f15f6fd3a0e0b092720` |

**Fato observado:** o material entregue **não é um XAPK**, e sim um **arquivo 7z multi-volume**
(assinatura `37 7a bc af 27 1c`, versão 0.4) contendo **um único membro**, que é o XAPK.
Nenhum arquivo de `bin/` foi modificado; a extração ocorre em diretório de trabalho.

**Fato observado:** o XAPK é `xapk_version: 2` com **20 APKs** (1 base + 1 ABI + 1 densidade + 17 idiomas),
3.141 entradas no total, 190.460.217 bytes descomprimidos (soma dos membros ZIP).

Detalhes completos: `docs/analise/inventarios/integridade.json` e `inventario_xapk.md`.

## 2. Estrutura do pacote

| Componente | Conteúdo relevante |
|---|---|
| **Base** `com.nexstreaming.app.kinemasterfree.apk` (109.972.018 B) | 6 DEX (`classes.dex` … `classes6.dex`), 2.841 entradas, `assets/` com 65 MB (efeitos, fontes, modelos de IA, `kmassets.zip`), DEX extra em `assets/audience_network/` |
| **ABI** `config.arm64_v8a.apk` (62.769.226 B) | 22 bibliotecas `.so`, **somente `arm64-v8a`** (AArch64) |
| **Densidade** `config.xxhdpi.apk` (2.820.048 B) | 72 PNG + 29 XML + `resources.arsc` |
| **Idiomas** (17 splits) | `values-<locale>/` com `strings.xml`, `arrays.xml`, `plurals.xml` |

**Assinatura (fato observado):** esquemas **v1 + v2 + v3** presentes; certificado
`NexStreaming` (KR/Seoul), SHA-256 `b2b90f2f5a3679186fb82a6fb8c07f5e6ab4b55ea01b5625b28a9b5549fc6988`,
número de série `1363565999`. Nenhuma inconsistência de assinatura entre splits foi observada.

**Componentes declarados (fato observado, `decompilado/indices/componentes.md`):**
32 permissões, 1 permissão própria (`DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`),
55 atividades, 19 serviços, 8 receptores, 8 provedores, 52 `meta-data`, 9 `uses-feature`,
3 `uses-native-library`, 3 `uses-library`.

## 3. Código gerenciado (Fase 3)

### 3.1 DEX

| DEX | Classes | Métodos | Campos | Strings | Bytes |
|---|---:|---:|---:|---:|---:|
| `classes.dex` | 4.421 | 22.126 | — | 27.023 | 4.147.876 |
| `classes2.dex` | 1 | 1 | — | 21 | 1.364 |
| `classes3.dex` | 9.752 | 53.944 | — | 50.699 | 9.328.836 |
| `classes4.dex` | 12.854 | 58.654 | — | 42.910 | 9.061.372 |
| `classes5.dex` | 13.493 | 51.963 | — | 49.961 | 9.420.292 |
| `classes6.dex` | 8.777 | 45.297 | — | 43.971 | 8.280.692 |
| `assets/audience_network/classes.dex` | 3.605 | 26.725 | — | 32.685 | 5.455.144 |
| **Total** | **52.903** | **258.710** | **167.260** | **247.270** | **45.695.576** |

Todos os DEX são versão `038`, exceto o de `assets/` que é `035` (mais antigo — coerente com SDK de anúncio empacotado à parte).

### 3.2 Descompilação

| Execução | Ferramenta | Resultado |
|---|---|---|
| APK base (6 DEX) | jadx 1.1.0 | **45.195** arquivos `.java`, 2.911 recursos decodificados, 385 MB |
| DEX de `assets/` | jadx 1.1.0 | **3.538** arquivos `.java`, 25 MB |
| APK base (referência) | jadx-core do `jadx-headless-mcp` 0.7.0 | **43.251** classes, `errors=0, warnings=0` |

**Erros registrados pelo jadx 1.1.0:** 148 no cômputo final do APK base, **52 classes que falharam ao salvar**
(`ERROR - Error saving class`) e **119 ocorrências** de `JADX ERROR` embutidas nos arquivos gerados
(ex.: `JadxOverflowException: Regions count limit`). Isso é **falha parcial de método**, não falha de arquivo:
o código dessas classes existe, mas pode estar incompleto ou em forma de *smali fallback*.

### 3.3 Cobertura de classes (métrica com denominador verificável)

| Métrica | Valor |
|---|---|
| Classes declaradas nos DEX | 52.903 |
| Classes com arquivo `.java` correspondente | 52.105 |
| **Cobertura** | **98,49 %** |
| Classes sem `.java` | 798 |

Classificação das 798 lacunas (fato observado, `metricas_dex.json → classificacao_lacunas`):

| Categoria | Quantidade | Significado |
|---|---:|---|
| `sem_membros` | 523 | classes sem métodos e sem campos no DEX (marcadores/sintéticas) |
| `anonima` | 222 | classes anônimas (`Foo$1$2`) absorvidas no arquivo da classe externa |
| `classe_interna` | 50 | classes internas nomeadas absorvidas |
| `classe_toplevel_com_codigo` | **3** | perda real de código de nível superior |

**Portanto: a perda efetiva de código de nível superior é de 3 classes em 52.903 (0,006 %).**
A reconciliação `.java` ↔ classe do DEX precisou tratar três renomeações do jadx (todas detectadas empiricamente):
pacote com caractere inválido (`firebase-auth-api` → `p002firebaseauthapi`), classe com nome inválido
(`…X.4V` → `AnonymousClass4V`) e pacote padrão → `defpackage/`. O script `05_metricas_dex.py` reconstrói
esse mapa pelos comentários `/* renamed from: … */` gravados pelo próprio jadx.

### 3.4 Arquitetura do aplicativo (fato observado)

Entrypoint: `com.kinemaster.app.base.KineMasterApplication`
(`decompilado/selecionado/java/.../KineMasterApplication.java`).
Subpacotes do próprio app: `com.kinemaster.app.{base,database,mediastore,modules,repository,screen,util,widget,ai,dci,errorcode}`,
`com.kinemaster.module.{network,nexeditormodule,nextask}`, `com.kinemaster.shortkey.model`,
`com.nexstreaming.kinemaster.{project,editorwrapper,notification,ui}`.
Total de classes do próprio aplicativo (excluindo bibliotecas): **4.930** (`decompilado/indices/classes_kinemaster.txt`).

Persistência: 5 `@Database` Room — `ProjectDatabase`, `TemplateDatabase`, `InstalledAssetsDatabase`,
`FontDatabase`, além do `WorkDatabase` do WorkManager.

Modelo de exportação/importação de projeto: diretório `<UUID>.kine` contendo `<título>.kmproject`
(`com/nexstreaming/kinemaster/project/util/d.java`), com importação por ZIP contendo `contents/`.

### 3.5 Bibliotecas e SDKs identificados (fato observado, por pacote)

Publicidade/monetização: Google Mobile Ads (`com.google.android.gms.internal.ads`, 4.179 classes),
Meta Audience Network (`com.facebook.ads.redexgen.X`, 3.510), InMobi (`com.inmobi.media`, 1.449),
Fyber/Inneractive, Pangle/ByteDance (`com.bykv.*`, `com.bytedance.sdk.*`),
Billing (`com.android.billingclient`).
Plataforma Google: Firebase (auth, firestore, messaging, crashlytics, sessions, remote config),
Play Core (assetpacks, appupdate, integrity, review), ML Kit (translate, language-id, common, vision),
reCAPTCHA, Tink (criptografia), Protobuf.
Infraestrutura: AndroidX completo, Kotlin/Coroutines, Room, Paging, WorkManager, OkHttp/Retrofit,
Picasso, Airbridge (`co.ab180.airbridge`), DataStore.

### 3.6 Recursos de IA observados

**Fato observado:** existe o enum `AIModelType` com os valores efetivamente usados no código:
`AI_STYLE`, `AI_VOICE`, `AUTO_CUT`, `BOX_TRACKING`, `MAGIC_REMOVER`, `MUSIC_MATCH`,
`NOISE_REDUCTION`, `STT`, `SUPER_RESOLUTION`, `TTS`, `VOCAL_SEPARATOR`.
Modelos embarcados em `assets/`: `km_segmentation_352x352_fp16.tflite` (7,1 MB),
`kmVAD_v512_fp16.bin` (0,9 MB, detecção de atividade de voz), além dos runtimes
`libtensorflowlite_jni.so`, `libtensorflowlite_gpu_jni.so`, `libonnxruntime.so`,
`libtranslate_jni.so`, `libkmSTTv4_armv80.so` / `_armv82.so` (reconhecimento de fala).

## 4. Código nativo (Fase 4)

22 bibliotecas ELF, todas `AArch64`, total **62.598.024 bytes**. Inventário completo com hashes,
`DT_NEEDED`, símbolos e seções: `docs/analise/inventarios/nativo.json`.

| Biblioteca | Bytes | Exportados | Símbolos JNI | Papel observado |
|---|---:|---:|---:|---|
| `libnexeditorsdk.so` | 9.691.912 | 372 | **370** | núcleo do editor (JNI_OnLoad presente); depende de `libonnxruntime.so`, `libtensorflowlite*_jni.so`, `libGLESv3.so`, `libEGL.so` |
| `libonnxruntime.so` | 17.571.152 | 5 | 0 | runtime ONNX (inferência) |
| `libtensorflowlite_jni.so` | 4.152.032 | 277 | 56 | TensorFlow Lite (clang 19, LLD 19) |
| `libtranslate_jni.so` | 16.361.048 | 8 | 3 | tradução (ML Kit) |
| `libkmSTTv4_armv80/82.so` | 2.784.416 / 2.783.712 | 1.209 cada | 0 | reconhecimento de fala (duas variantes de CPU) |
| `librenderscript-toolkit.so` | 3.894.848 | 864 | 22 | RenderScript Toolkit |
| `libtt_ugen_layout.so` | 440.672 | 708 | 0 | layout do Pangle |
| `libpglarmor.so` | 68.776 | 1 | 1 | `PglCryptUtils.bc` (ByteDance; clang 14) |
| `libapminsighta/b.so` | 102.256 / 124.352 | 1 / 71 | 0 | APM (monitoramento) |
| `libairbridgelib.so` | 12.816 | 19 | 1 | assinatura Airbridge |
| `libnms.so`, `libnexcralbody_mc_jb.so`, `libnexralbody_audio.so`, `libwrapnex*.so`, `libbuffer_pgl.so`, `libfile_lock_pgl.so`, `libdatastore_shared_counter.so` | — | — | — | componentes internos do editor/DataStore |

**Fato observado:** nenhuma biblioteca possui `.symtab` de depuração e **nenhum protetor conhecido**
(UPX, Jiagu, Bangcle, Legu…) foi detectado por assinatura. Entretanto `libnexeditorsdk.so`
**não possui seção `.comment`** e exporta 372 símbolos — o que é compatível com *build* otimizado (LTO/strip),
não necessariamente com ofuscação.

**Fato observado:** bibliotecas carregadas por `System.loadLibrary` que **não existem no APK**:
`OpenCL`, `OpenCL-car`, `OpenCL-pixel`, `geckox_bspatch`, `stlport_shared`.
- `geckox_bspatch` pertence ao SDK ByteDance (aplicação de *patch* bsdiff em recursos baixados).
- `pglarmor` e `OpenCL*` são carregadas condicionalmente, com tratamento de erro (não é pacote corrompido).

**Limitação:** não há descompilador nativo disponível (Ghidra inacessível — ver `MANIFESTO.md` §4.2).
**Nenhum pseudocódigo nativo foi produzido**; a análise é estrutural e de símbolos.
O mapa completo símbolo JNI → classe Java está em `decompilado/indices/simbolos_jni.csv` (480 símbolos).

### 4.1 Strings e desmontagem (complemento)

Relatório dedicado: `docs/analise/relatorios/strings_e_desmontagem_nativa.md`.

**Fatos observados:**
- `libnexeditorsdk.so` é a única biblioteca que referencia `.nef` e `.lua`; contém ainda os caminhos
  padrão de `LUA_PATH` do **Lua 5.3**, evidência de interpretador Lua embutido.
- A biblioteca contém o módulo de proteção `nexProtection.cpp`, com mensagens como
  `Manufacture lock is pass! : pstSDKInfo->pPKGName[...]` e os símbolos JNI
  `NexEditor_setPacakgeName4Protection` (grafia original) e `NexEditor_check4km`.
  **Consequência documentada:** o motor nativo recebe o nome do pacote e executa verificação de
  licenciamento/privilégio. Contornar isso está **fora do escopo** desta missão.
- `JNI_OnLoad` foi desmontado (capstone/ARM64) em 5 bibliotecas; em `libapminsighta.so`
  são 56 instruções com 3 chamadas indiretas — compatível com `RegisterNatives`, mas
  **não comprovado** sem análise de fluxo.

## 5. Recursos e formatos proprietários (Fase 5)

Relatório dedicado: `docs/analise/relatorios/formatos_proprietarios.md`. Resumo:

- **`assets/kmassets.zip`** (36.175.287 B, SHA-256 `576813aa41cd1bc8633d100cb5eac7ec1fd0743703528951997fa8948eea21de`): 2.421 entradas, 19 pacotes de assets,
  organizado em *asset packs* (transições 3D, filtros, stickers, títulos, efeitos) com
  `packageinfo.json` localizado (inclui `pt`).
- **Efeitos em Lua:** 28 scripts com tabela global `effect` (`matrix`, `left`, `top`, `right`, `bottom`, `alpha`)
  e função `setDefaultValue()` — API de parametrização de efeitos consumida pelo motor nativo.
- **`.nef` (NexEffect):** 16 arquivos no pacote; assinatura `13 00 ff ee` (14) e `01 da fe ef` (2);
  marcas ASCII `DEAD`, `KM_T`, `SIDE`; **formato não especificado** — apenas hipóteses testáveis documentadas.
- **`.kine` / `.kmproject`:** `.kine` é o **diretório** do projeto e `.kmproject` o arquivo de projeto dentro dele;
  importação/exportação por ZIP com `contents/`. Estrutura interna do `.kmproject`: **não recuperada**.
- **Modelos de IA embarcados:** `.tflite` e `.bin` (ver §3.6).

### 5.1 Esquema dos bancos Room (reconstruído estaticamente)

Relatório dedicado: `docs/analise/relatorios/esquema_banco_de_dados.md` (dados em `esquema_room.json`).

**Fato observado:** existem **12** classes `@Database`, **49** entidades `@Entity` e **517** campos
declarados, todos legíveis nas anotações preservadas no bytecode:

| Banco (`@Database`) | Versão | Entidades |
|---|---:|---:|
| `ProjectDatabase` | 3 | 1 (`project_table`) |
| `TemplateDatabase` | 9 | 15 (+1 *view*) |
| `InstalledAssetsDatabase` | 8 | 6 |
| `FontDatabase` | 6 | 7 |
| `StoreDatabase` | 8 | 6 |
| `HotFeaturedDatabase` / `NewFeaturedDatabase` | 4 / 4 | 1 / 1 |
| `SaveAsVideoDatabase`, `LimitDatabase`, `SearchRecentDatabase`, `AudioSearchKeywordDatabase` | 3, 3, 1, 2 | 1, 2, 1, 1 |

**Limitação:** o DDL (`CREATE TABLE`) e as consultas `@Query` **não** estão no código Java decompilado —
o Room os gera em classes delegadas que foram ofuscadas/renomeadas pelo R8. Os nomes de tabela/coluna
vêm das anotações `@Entity(tableName=…)` e `@ColumnInfo(name=…)`; quando ausentes, valem os nomes de
classe/campo (regra do Room) e o relatório marca isso como **inferido**.

### 5.2 Superfície de rede (estática)

Relatório dedicado: `docs/analise/relatorios/superficie_de_rede.md`.

**Fato observado:** **173** URLs distintas no código, das quais **32** apontam para domínios do
fabricante e **25** são variantes de teste/homologação (pares `test-api-*` / `api-*`).
Hosts de produção identificados: `api-account`, `api-asset`, `api-asset-item-info`, `api-assetstore`,
`api-auth`, `api-dci`, `api-notice`, `api-project-feed`, `redirect`, `notice`, `product-sku`
(`kinemasters.com` / `kinemaster.com`), além de `tts-t.kinemix.com` (TTS).
**Fato observado:** `res/xml/network_security_config.xml` permite **tráfego em claro**
(`cleartextTrafficPermitted="true"`), inclusive para `cdn-project-feed.kinemasters.com` e `127.0.0.1`,
e confia em certificados de usuário apenas em `debug-overrides`.
**Nenhuma requisição foi executada**; a lista não prova que um endpoint esteja ativo.

## 6. Análise dinâmica (Fase 6) — **NÃO EXECUTADA**

Ver `docs/analise/relatorios/analise_dinamica.md`. Motivo objetivo: ausência de `/dev/kvm`,
de SDK Android/`adb`, de emulador e de dispositivo; RAM de 3,8 GiB; APK **AArch64-only**.
Nenhuma afirmação deste relatório depende de execução do aplicativo.

## 7. Ferramentas e reprodutibilidade

- Manifesto completo (versão, licença, origem, SHA-256, situação no Git): `bin/tools/MANIFESTO.md`.
- Guia passo a passo: `docs/analise/relatorios/guia_reproducao.md`.
- Guia de modificação (o que é seguro e útil alterar): `docs/analise/relatorios/guia_modificacao.md`.
- Scripts: `scripts/00` … `scripts/10` (todos com comentários e mensagens em português).

## 8. O que foi recuperado, parcialmente recuperado e desconhecido

**Recuperado (fato):**
- Estrutura completa do pacote, hashes e assinatura.
- 98,49 % das classes dos DEX em Java (48.733 arquivos gerados), com perda real de 3 classes.
- Manifesto completo decodificado, recursos decodificados dos 20 APKs.
- Inventário nativo completo (22 ELF) com símbolos JNI e dependências.
- Assets de efeitos, scripts Lua, pacotes e modelos de IA.

**Parcialmente recuperado:**
- Corpo de métodos nas 52 classes que falharam ao salvar e nos 119 pontos com `JADX ERROR`
  (código presente, qualidade reduzida; **não** equivalente a código-fonte original).
- Nomes: o app é ofuscado por R8; nomes de classes/métodos curtos (`a`, `b`, `zzafc`) são os nomes
  reais do DEX, e não apelidos criados pela ferramenta.
- Semântica de `.nef`, `.kmproject` e dos bancos Room (estruturas parciais).

**Desconhecido nesta fase:**
- Comportamento em execução (nenhuma execução realizada).
- Especificação de formatos proprietários binários.
- Conteúdo de recursos baixados em runtime (efeitos/asset store) e endpoints de rede não exercitados.
- Pseudocódigo nativo.
