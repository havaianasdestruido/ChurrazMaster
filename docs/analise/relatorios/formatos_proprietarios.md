# Formatos proprietários e recursos especiais

Gerado em `2026-10-22T16:02:32.391316+00:00` por `scripts/08_formatos_proprietarios.py`.

## 1. `assets/kmassets.zip` — pacote de efeitos e recursos

- **SHA-256 do zip**: `576813aa41cd1bc8633d100cb5eac7ec1fd0743703528951997fa8948eea21de`
- **Tamanho**: 36175287 bytes
- **Entradas**: 2421
- **Pacotes com `packageinfo.json`**: 19
- **Distribuição por extensão**: `{'(sem extensão)': 817, 'json': 531, 'png': 467, 'xml': 364, 'jpg': 91, 'svg': 58, 'glsl': 57, 'lua': 28, 'nef': 8}`

**Fato observado:** o zip é um contêiner de *asset packs* de efeitos; cada pacote tem um
`packageinfo.json` com `assetLevel`, `assetName` (localizado em vários idiomas) e
metadados de apresentação. Nomes de pacotes encontrados:
`Travel and Activity`, `Fun Transitions`, `Warm`, `Classic Transitions`, `Basic Effects`, `Mono`, `Cold`, `Powerful`, `Vivid`, `Classic Stickers`, `Picture-in-Picture and Multivideo`, `Text Transitions`, `Sentimental`, `More Title Effects`, `Basic Title Effects`, `Basic`, `Presentation`, `Low Saturation`, `3D Transitions`

**Estrutura interna típica** (observada em `3d_transitions`):
```
<familia>/<tipo>/<variante_proporcao>.<nome>/nexeffect/<arquivo>.<...>.nef
```

## 2. Efeitos em Lua (`*.lua`)

**Fato observado:** há scripts Lua dentro de `kmassets.zip`, um por efeito, com uma tabela
global `effect` e funções chamadas pelo motor nativo. Exemplo real extraído de
`basic_effects/filter/blend_08/nexstreaming.lua`:
```lua
effect = {matrix = 0, left = -1, top = -1, right = 1, bottom = 1, alpha = 1}
function setDefaultValue() ... end
```
**Fato observado (reforço):** `libnexeditorsdk.so` contém as strings de caminho padrão de
`LUA_PATH` da versão **5.3**
(`/usr/local/share/lua/5.3/?.lua;…;./?.lua;./?/init.lua`), o que é evidência direta de que o
motor **embute um interpretador Lua 5.3** (ver `strings_e_desmontagem_nativa.md`).
**Inferência:** a tabela `effect` desses scripts é a API de parametrização consumida por esse
interpretador. **Não há** confirmação em execução (Fase 6 bloqueada).

## 3. `.nef` — NexEffect (formato binário proprietário)

- **Arquivos encontrados**: 16
- **Assinaturas iniciais observadas**: `{'1300ffee': 14, '01dafeef': 2}`
- **Marcas ASCII de 4 bytes (até 64 KB)**: `{'DDDB': 96, 'ADDD': 48, 'DEAD': 8, 'WFUA': 4, 'AWUL': 2, 'RQAL': 2, 'DDSA': 2, 'AVMI': 2, 'VMIB': 2, 'UKBA': 2, 'JALA': 2, 'COBK': 2, 'ZDVA': 2, 'HZAS': 2, 'FOTB': 2}`

**Fato observado (amostra):**

| arquivo | bytes | SHA-256 | primeiros bytes (hex) |
|---|---:|---|---|
| `kmassets/kmassets/3d_transitions/transition/1v1.bouncebox/res/transition.bouncy_box.1v1.left.nef` | 16760 | `e8cabab3e10e02ff9a95c282…` | `1300ffee060000004e00000005000000ffffffff0000000044454144fc7f0000` |
| `kmassets/kmassets/3d_transitions/transition/9v16.bouncebox/res/transition.bouncy_box.9v16.up.nef` | 16760 | `fbf53341e1e791f0e4501867…` | `1300ffee060000004e00000005000000ffffffff0000000072006f0067007200` |
| `kmassets/kmassets/3d_transitions/transition/1v1.crack_transition/nexeffect/transition.cracked_glass.1v1.left.nef` | 1874348 | `621e77853c28d2139737c361…` | `1300ffeeb500000080000000ba000000ffffffff000000004445414400000000` |
| `kmassets/kmassets/3d_transitions/transition/9v16.crack_transition/nexeffect/transition.cracked_glass.9v16.up.nef` | 1874348 | `f301295eb700efad392ada46…` | `1300ffeeb500000080000000ba000000ffffffff0000000090ce07d3fc7f0000` |
| `kmassets/kmassets/3d_transitions/transition/4v5.crack_transition/nexeffect/transition.cracked_glass.4v5.left.nef` | 1874620 | `14f3d4990883ecab853da9f4…` | `1300ffeeb500000080000000ba000000ffffffff000000000000000000000000` |
| `kmassets/kmassets/3d_transitions/transition/16v9.crack_transition/nexeffect/crack_transition.nef` | 2549096 | `acdf1d604993de91eebb42d9…` | `01dafeef1d26226b0200000091eb020046030300789c9cbc775c9359f72f8a3a` |
| `kmassets/kmassets/3d_transitions/transition/3v4.crack_transition/nexeffect/transition.cracked_glass.3v4.left.nef` | 1874212 | `8e8143f55a7c8fb93022ecc3…` | `1300ffeeb500000080000000ba000000ffffffff000000000000000000000000` |
| `kmassets/kmassets/3d_transitions/transition/4v3.crack_transition/nexeffect/transition.cracked_glass.4v3.left.nef` | 1874348 | `dbe3d849c0b2afd492c61c77…` | `1300ffeeb500000080000000ba000000ffffffff000000000000000000000000` |

**Hipóteses testáveis (NÃO são especificação):**
1. Os 4 primeiros bytes (`13 00 ff ee`) funcionam como assinatura/mágica; há uma
   variante com `01 da fe ef`.
2. Os campos `u32` seguintes parecem contagens/versões (`6`, `181`) — não confirmado.
3. A marca `DEAD` e sequências de `double` em faixa `0x7ffc…` sugerem tabelas de
   parâmetros numéricos serializadas, possivelmente com *checksum* ou marcador de fim.
4. Para confirmar seria necessário localizar no código nativo o leitor desse formato
   (candidato: `libnexeditorsdk.so`), o que exige descompilador nativo indisponível aqui.

**Limitação declarada:** o formato `.nef` **não** foi especificado. As hipóteses acima são
testáveis, mas não foram validadas; nenhuma estrutura foi inventada.

## 4. Projetos: `.kine` e `.kmproject`

**Fato observado no código** (citações com arquivo e linha):

### `.kine`

- `com/nexstreaming/kinemaster/editorwrapper/k.java:528` — `File file = new File(d2, num.concat(".kine"));`
- `com/nexstreaming/kinemaster/editorwrapper/l.java:41` — `this.j = !pr8.k(str, ".kine", false) ? str.concat(".kine") : str;`
- `com/nexstreaming/kinemaster/project/util/d.java:435` — `File file = new File(wn4.i("Project").getAbsolutePath(), qq2.r(UUID.randomUUID().toString(), ".kine"));`
- `com/nexstreaming/kinemaster/ui/projectexport/ProjectExportViewModel$start$1.java:401` — `str = qq2.r(projectEntity.getTitle(), "_d.kine");`
- `com/nexstreaming/kinemaster/ui/projectexport/ProjectExportViewModel$start$1.java:403` — `str = qq2.r(projectEntity.getTitle(), ".kine");`
- `com/nexstreaming/kinemaster/ui/projectimport/ProjectImportViewModel$importProject$1.java:163` — `str = UUID.randomUUID().toString() + ".kine";`
- `com/kinemaster/app/screen/projecteditor/reverse/b.java:104` — `if (parent != null && pr8.k(parent, ".kine", false)) {`
- `com/kinemaster/app/screen/upload/TemplateUploadSharedViewModel$requestKineFile$2.java:127` — `file3 = new File(d, "cache_template_upload_original_kine_file.kine");`
- `com/kinemaster/app/screen/upload/TemplateUploadSharedViewModel$requestKineFile$2.java:129` — `file3 = new File(d, "cache_template_upload_trimmed_kine_file.kine");`
- `defpackage/wn4.java:76` — `if (!(file == null || (parent = file.getParent()) == null || !pr8.k(parent, ".kine", false))) {`
- `defpackage/hka.java:117` — `if (parent != null && pr8.k(parent, ".kine", false)) {`
- `defpackage/dx2.java:99` — `sb.append(".kine");`

### `.kmproject`

- `com/nexstreaming/kinemaster/editorwrapper/ProjectImporter$clearImportDirectory$2.java:56` — `if (pr8.k(absolutePath, ".kmproject", true) && !file4.isDirectory()) {`
- `com/nexstreaming/kinemaster/editorwrapper/ProjectImporter$unzipProject$2.java:63` — `java.lang.String r2 = ".kmproject"`
- `com/nexstreaming/kinemaster/project/util/ProjectHelper$renameProjectTo$2.java:133` — `if (pr8.k(name, ".kmproject", false)) {`
- `com/nexstreaming/kinemaster/project/util/ProjectHelper$renameProjectTo$2.java:136` — `String Q = hr8.Q(name2, ".kmproject");`
- `com/nexstreaming/kinemaster/project/util/d.java:436` — `String concat = str.concat(".kmproject");`
- `com/nexstreaming/kinemaster/project/util/d.java:501` — `int K = hr8.K(name2, ".kmproject", 0, 6);`
- `com/nexstreaming/kinemaster/project/util/d.java:538` — `if (pr8.k(name3, ".kmproject", false)) {`
- `com/nexstreaming/kinemaster/project/util/d.java:557` — `File file3 = new File(parent, e.b(R, arrayList, ProjectDatabaseHelper$GenerateUniqueType.FILE_NAME).concat(".kmproject"));`
- `com/nexstreaming/kinemaster/project/util/d.java:572` — `if (!pr8.k(name4, ".kmproject", z)) {`
- `com/nexstreaming/kinemaster/project/util/d.java:980` — `if (pr8.k(name, ".kmproject", false)) {`
- `com/kinemaster/app/database/util/e.java:51` — `if (hr8.v(str, '.') && pr8.k(str, ".kmproject", false)) {`
- `com/kinemaster/app/database/util/e.java:57` — `byte[] bytes2 = ".kmproject".getBytes(charset);`

**Fato observado adicional:** em `com/nexstreaming/kinemaster/project/util/d.java` o
projeto é criado como **diretório** `<UUID>.kine` contendo o arquivo `<título>.kmproject`.
**Interpretação:** `.kine` é o diretório de projeto (container) e `.kmproject` é o arquivo
de projeto dentro dele. Isso é consistente com o código, mas a estrutura interna do
`.kmproject` não foi recuperada nesta fase (exigiria gerar/abrir um projeto real).

## 5. Carregamento dinâmico e bibliotecas ausentes

**Fato observado:** o código chama `System.loadLibrary` para nomes que **não existem** como
arquivo no XAPK:

- `OpenCL`
- `OpenCL-car`
- `OpenCL-pixel`
- `geckox_bspatch`
- `stlport_shared`

**Correlação com o código gerenciado:**
- `androidx/datastore/core/MultiProcessCoordinator$lazySharedCounter$1.java:23` — `System.loadLibrary("datastore_shared_counter");`
- `com/google/android/renderscript/Toolkit.java:16` — `System.loadLibrary("renderscript-toolkit");`
- `com/google/firebase/sessions/FirebaseSessionsComponent.java:38` — `System.loadLibrary("datastore_shared_counter");`
- `com/google/mlkit/nl/translate/internal/TranslateJni.java:64` — `System.loadLibrary("translate_jni");`
- `com/kinemaster/module/nexeditormodule/nexvideoeditor/NexEditorUtils.java:157` — `System.loadLibrary("nexeditorsdk");`
- `com/kinemaster/module/nexeditormodule/nexvideoeditor/NexTextEffect.java:283` — `System.loadLibrary("nexeditorsdk");`
- `com/kinemaster/module/nexeditormodule/nexvideoeditor/NexThemeRenderer.java:15` — `System.loadLibrary("stlport_shared");`
- `com/kinemaster/module/nexeditormodule/nexvideoeditor/NexThemeRenderer.java:16` — `System.loadLibrary("nexeditorsdk");`
- `com/kinemaster/module/nexeditormodule/nexvideoeditor/NexEditor.java:1139` — `System.loadLibrary("nexeditorsdk");`
- `com/kinemaster/module/nexeditormodule/nexvideoeditor/NexEditor.java:1236` — `System.loadLibrary("OpenCL-pixel");`
- `com/kinemaster/module/nexeditormodule/nexvideoeditor/NexEditor.java:1241` — `System.loadLibrary("OpenCL-car");`
- `com/kinemaster/module/nexeditormodule/nexvideoeditor/NexEditor.java:1246` — `System.loadLibrary("OpenCL");`

**Fato observado:** `geckox_bspatch` é referenciado por
`com/bykv/vk/openvk/preload/geckox/utils/BsPatch.java` (SDK de anúncios ByteDance/Pangle),
que aplica *patches* binários (bsdiff/bspatch) a recursos baixados em tempo de execução.
`pglarmor` é usado por `com/bytedance/sdk/component/pglcrypt/PglCryptUtils.java`.
**Conclusão de escopo (inferência forte):** esses mecanismos pertencem ao SDK de anúncios,
não ao núcleo de edição do KineMaster; não foi encontrado mecanismo próprio de *hot-patch*
do lado do aplicativo principal.

`OpenCL`, `OpenCL-car`, `OpenCL-pixel` e `stlport_shared` são carregadas
condicionalmente (renderização/modelo de dispositivo); a ausência do arquivo indica
carregamento opcional com tratamento de erro, não corrupção do pacote.

## 6. O que permanece desconhecido

- Estrutura interna completa de `.kmproject` e do banco de dados de projeto (Room).

- Especificação de `.nef` (apenas hipóteses).
- Papel exato dos 28 scripts Lua no pipeline de renderização (sem execução).
- Formato dos modelos `.tflite`/`.bin` de IA e sua ligação com `libonnxruntime.so`.
