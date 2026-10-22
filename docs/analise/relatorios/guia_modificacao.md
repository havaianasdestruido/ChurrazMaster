# Guia de modificação — mapa do que é seguro e útil alterar

**Objetivo deste documento:** servir ao propósito declarado do repositório (facilitar mods e
acrescentar conteúdos/features) usando **apenas** o que foi comprovado na análise estática.

> **Escopo ético e legal.** Este guia trata de *interoperabilidade e personalização do próprio
> pacote instalado*: recursos visuais, efeitos, textos, fluxos de UI e novos conteúdos. Ele **não**
> cobre — e a missão **não** investigou formas de — contornar cobrança, contas, DRM ou as verificações
> de integridade do Play (`com.google.android.play.core.integrity`,
> `com.kinemaster.app.modules.integrity.*`). Alterar esses mecanismos viola termos de uso e, em
> vários países, a lei.

## 1. Antes de editar: o que o pacote é

| Fato observado | Consequência prática |
|---|---|
| 20 APKs divididos (base + ABI + densidade + 17 idiomas) | Um mod precisa ser aplicado ao **split correto**; alterar apenas a base não muda recursos de idioma/densidade |
| Somente `arm64-v8a` | Não há como testar em emulador x86_64 |
| Assinatura **v1 + v2 + v3** | Qualquer alteração invalida a assinatura; é preciso **re-assinar** com chave própria e desinstalar a versão oficial antes |
| `android:extractNativeLibs="false"` | As `.so` ficam comprimidas/mapeadas do APK; não basta copiar arquivos para `/data` |
| Código ofuscado por R8 | Nomes como `a`, `b`, `un1` são **originais** do build; renomeá-los quebra chamadas reflexivas |
| Sem código recompilável | A saída do jadx é **referência de leitura**, não projeto de build |

## 2. Caminhos de modificação de baixo risco (recomendados)

### 2.1 Conteúdo e efeitos embarcados — sem tocar em código

**Fato observado:** `assets/kmassets.zip` (36,2 MB, 2.421 entradas, 19 pacotes) contém os efeitos,
transições, filtros, stickers e títulos. Cada pacote tem `packageinfo.json` com `assetLevel` e
`assetName` localizado.

Para adicionar conteúdo:
1. Extrair `assets/kmassets.zip` (ver `scripts/08_formatos_proprietarios.py`).
2. Duplicar um pacote existente preservando a estrutura
   `<familia>/<tipo>/<variante>.<nome>/nexeffect/…`.
3. Ajustar o `packageinfo.json` (nome localizado, nível do asset).
4. Recompactar como ZIP **sem compressão** de armazenamento (o app lê por `AssetManager`) e
   substituir no APK base.
5. Re-assinar e instalar em aparelho de teste.

**Inferência:** o `assetLevel` controla a elegibilidade do pacote (conteúdo premium x gratuito).
Alterá-lo muda o comportamento de exibição — trate como mudança de produto, não como "desbloqueio".

### 2.2 Parâmetros de efeitos em Lua

**Fato observado:** cada efeito traz um `nexstreaming.lua` com uma tabela global:

```lua
effect = {matrix = 0, left = -1, top = -1, right = 1, bottom = 1, alpha = 1}
function setDefaultValue() ... end
```

**Inferência forte:** esses valores são os parâmetros padrão lidos pelo motor
`libnexeditorsdk.so`. Editar os `.lua` é a forma menos invasiva de criar variações de efeito.
Não há confirmação dinâmica (Fase 6 pendente), portanto valide em dispositivo.

### 2.3 Presets de áudio

`assets/audio_effect/{eq,reverb,voice_changer}/*.json` são presets legíveis (JSON simples,
com faixas de ganho/frequência). Adicionar um arquivo novo é suficiente, em princípio, para criar
um preset — **hipótese** não validada em execução.

### 2.4 Textos e recursos visuais

Recursos estão nos splits: `config.xxhdpi.apk` (imagens) e `config.<idioma>.apk`
(`values-*/strings.xml`, `arrays.xml`, `plurals.xml`). Para mudar texto em pt-BR, o alvo é
`config.pt.apk` → `res/values-pt/strings.xml`.

## 3. Mapa de classes para quem for alterar código

Todas as classes abaixo existem na saída do jadx (fatos observados). Caminhos relativos a
`/tmp/churraz-trabalho/04_jadx_base/sources` (árvore completa, gerada por script).

| Área | Classe / pacote | Para que serve |
|---|---|---|
| Inicialização | `com/kinemaster/app/base/KineMasterApplication` | `Application`; ponto de injeção de inicialização (SDKs, engine, tokens) |
| Entrada de UI | `com/kinemaster/app/screen/splash/SplashActivity` | primeira Activity; encadeia para a home |
| Esquemas externos | `com/kinemaster/app/screen/launch/kmscheme/KMSchemePendingActivity` | trata `Intent` de deep link/`kmscheme` |
| Ponte com o motor nativo | `com/kinemaster/module/nexeditormodule/nexvideoeditor/NexEditor` e `NexEditorUtils` | 370 símbolos JNI em `libnexeditorsdk.so` (mapa em `decompilado/indices/simbolos_jni.csv`) |
| Projeto (arquivo) | `com/nexstreaming/kinemaster/project/util/d` | cria `<UUID>.kine/<título>.kmproject` |
| Projeto (banco) | `com/kinemaster/app/database/project/{ProjectDatabase,ProjectEntity}` | tabela `project_table` (12 bancos, 49 entidades em `esquema_banco_de_dados.md`) |
| Loja de assets | `com/kinemaster/module/network/remote/service/store/**` (várias `@Database`) | catálogo, downloads e cache de efeitos |
| Exportação | `com/nexstreaming/kinemaster/editorwrapper/{k,l}` | empacotamento `.kine`, importação por ZIP |
| Modelos de IA | `com/kinemaster/app/screen/projecteditor/aimodel/AIModelProcessArguments*` | argumentos por tipo (`AIModelType`: `AUTO_CUT`, `AI_STYLE`, `MAGIC_REMOVER`, `STT`…) |
| Assinatura/entitlement | `com/kinemaster/app/modules/integrity/entitlement/**` (160 classes) | estado premium — **fora do escopo de modificação** |

## 4. Fluxo de trabalho sugerido (reproduzível)

```bash
# 1. Decodificar o APK alvo com recursos (sem smali, se a mudança for só de recurso)
java -jar bin/tools/android/apktool/apktool_2.4.1.jar d -f -o trabalho/base_completo \
  /tmp/churraz-trabalho/02_apks/com.nexstreaming.app.kinemasterfree.apk

# 2. Trocar apenas recursos/assets (NÃO altere classes.dex sem necessidade)

# 3. Recompilar e re-assinar (aqui é preciso build-tools/APKTOOL com aapt2 — indisponível neste ambiente)
#    apktool b base_completo -o base_mod.apk
#    apksigner sign --ks minha-chave.jks base_mod.apk

# 4. Instalar junto com os splits originais e testar em dispositivo dedicado
adb install-multiple -r base_mod.apk config.arm64_v8a.apk config.xxhdpi.apk config.pt.apk
```

⚠️ **Este ambiente não consegue recompilar APK**: não há `aapt2`/build-tools (canais bloqueados) e
o Apktool versionado é 2.4.1. A etapa 3 deve ser feita em uma máquina com Android SDK instalado.

## 5. Armadilhas conhecidas (todas observadas na análise)

1. **Renomeações do jadx**: `firebase-auth-api` aparece como `p002firebaseauthapi` e classes
   inválidas ganham prefixos (`AnonymousClass4V`). Ao procurar código, use o mapa do
   `scripts/05_metricas_dex.py`, não o nome do arquivo.
2. **Classes do pacote padrão** estão em `defpackage/` — os nomes curtos (`a0b`, `xr0`) são reais.
3. **Código parcial**: 148 erros de geração e 52 classes não salvas. Antes de "consertar" um método,
   confirme se o problema não é da descompilação.
4. **`assets/audience_network/classes.dex`** é um DEX separado (SDK de anúncio) — não confunda com
   código do editor.
5. **Assinatura e integridade**: o app usa Play Integrity e verificação de recibo. Um APK re-assinado
   pode ser recusado por serviços online — isso é esperado e **não** deve ser contornado.

## 6. Limites deste guia

- Baseado **somente** em análise estática; nada foi executado (Fase 6 bloqueada).
- Nenhuma alteração de código foi compilada ou testada.
- Não há garantia de que os caminhos de arquivo em runtime sejam os mesmos do APK
  (o app usa `getExternalFilesDir`, DataStore e Room).
