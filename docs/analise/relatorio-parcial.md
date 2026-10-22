# Relatório parcial — Fases 0 a 5 (KineMaster 8.2.14.37398)

Status: **parcial**. Nenhuma fase está concluída de forma total. Lacunas explícitas na seção 7.

Convenções:
- **Fato**: observado diretamente e reproduzível pelos scripts indicados.
- **Inferência**: conclusão lógica a partir de fatos, ainda não verificada diretamente.
- **Hipótese**: possibilidade não confirmada, com teste sugerido.
- Percentuais sempre com denominador explícito.

## 1. Entrada e integridade

- **Fato:** `bin/` contém um **7z multivolume** de 4 volumes (`.001` a `.004`), não um XAPK diretamente. Concatenados, formam `kinemaster-8.2.14.7z` (96.560.538 B; SHA-256 `b24aa365…25dc6`), fora do Git.
- **Fato:** o 7z contém um XAPK de 180.092.744 B (SHA-256 `07025005…092720`). `unzip -t` sem erros.
- **Fato:** hashes de todos os volumes, do 7z e de cada arquivo extraído: `docs/analise/inventarios/entrada-sha256.txt` e `xapk-sha256.txt` (22 arquivos no XAPK).
- **Fato:** o original em `bin/` não foi modificado (conferido por hash).

## 2. Estrutura do XAPK e do APK base

- **Fato:** `manifest.json` com `xapk_version` 2; `minSdk` 26; `targetSdk` 36.
- **Fato:** 1 APK base (`com.nexstreaming.app.kinemasterfree.apk`) + 19 splits de configuração; bibliotecas nativas apenas em `arm64_v8a` (22 `.so`).
- **Fato:** APK base com 7 DEX: `classes.dex` a `classes6.dex` (formato 038) e `assets/audience_network/classes.dex` (formato 035). Inventário: `apks.md`.
- **Fato (manifesto):** `versionName` 8.2.14.37398.GP; `versionCode` 37398; `compileSdkVersion` 37; `allowBackup=false`; `extractNativeLibs=false`; `largeHeap=true`; `requestLegacyExternalStorage=true`. Atividades 55 (4 exportadas efetivas); serviços 19 (3); receptores 8 (4); provedores 8 (0); permissões declaradas 32. Detalhes: `manifesto-base.md`.
- **Fato (assinatura):** esquemas v2 e v3 presentes; v1 ausente. Certificado `O=NexStreaming,L=Seoul,C=KR`, SHA-256 `b2b90f2f…c6988`.
- **Limitação:** o XML decodificado pelo Apktool 2.4.1 não traz `<uses-sdk>` nem `versionName`; por isso SDK e versão foram lidos com androguard.

## 3. Decompilação

### 3.1 Apktool 2.4.1 (smali e recursos)

- **Fato:** `scripts/decompilar_apktool.sh base` concluído com código 0; **52.903** arquivos `.smali` (igual ao total de `class_defs` medido nos DEX); **2.263** XML de recursos; 0 linhas de erro no log. Resumo: `inventarios/apktool-base.txt`.

### 3.2 JADX 1.1.0 (Java)

Passagem padrão (`scripts/decompilar_jadx.sh`):
- **Fato:** **45.235** arquivos `.java` (nenhum vazio); 2.911 recursos. Log: `/home/user/kinemaster-work/logs/base-jadx.log` (fora do Git).
- **Fato:** o resumo final do JADX lista **112 nós** com falha (17 classes e 95 métodos). Além disso, o log contém **12** linhas `Error saving class`, que **não entram** nesse resumo. Somadas, são **124 nós** (29 classes e 95 métodos). Análise: `inventarios/jadx-base-erros.md`.
- **Fato:** `FontBrowserViewModel` (`com.kinemaster.app.screen.projecteditor.options.text.font`) não tem arquivo gerado. O log registra `Error saving class` com causa `Code generation error after restart`. Tentativa isolada com `--single-class` falhou da mesma forma (`logs/single-FontBrowserViewModel.log`).

Passagem com `--show-bad-code`:
- **Fato:** **45.195** `.java`; resumo com **148** nós; mais **52** `Error saving class` (total 200 nós com falha). Análise: `inventarios/jadx-base-showbad-erros.md`.
- **Fato:** a passagem `--show-bad-code` **não é um superconjunto** da padrão. Das 45.235 de `base-jadx`, **40** arquivos não existem em `base-jadx-showbad`; as 40 correspondem exatamente a classes com `Error saving class` nessa passagem. Ou seja, `--show-bad-code` perde 40 classes que a passagem padrão escreveu. **Não usar** a passagem `show-bad` como substituto sem essa ressalva.

### 3.3 Cobertura DEX × JADX

Script: `scripts/cobertura_dex.py`. Resultados em `inventarios/cobertura-dex-base.md`.

- **Fato:** class_defs = **52.903**; classes de nível superior únicas = **41.297**.
- **Fato:** presentes no JADX = **40.773** de 41.297 (98,7311%); pelo nome original 36.738, por renomeação registrada pelo JADX (`renamed from`) 4.035.
- **Fato:** sintéticas vazias (0 métodos e 0 campos no DEX) = **523**, excluídas do denominador de cobertura substantiva. Cobertura excluindo essas: **40.773 de 40.774 (99,9975%)**.
- **Fato:** ausentes = **1** (`FontBrowserViewModel`), com falha registrada pelo JADX (`Error saving class`). Nenhuma ausência sem causa registrada.
- **Fato:** métodos declarados = **258.710**; com corpo de código = **243.263**.
- **Fato:** classes de nível superior presentes com alguma falha registrada (classe ou método) = **88**.

Erros de mapeamento corrigidos durante o trabalho (não reverter): classes do pacote padrão ficam em `defpackage/`; nomes inválidos são renomeados pelo JADX; stubs sintéticos R8 são omitidos.

## 4. Ativos, formatos e recursos (Fase 5, parcial)

### 4.1 `assets/kmassets.zip` (ZIP aninhado)

- **Fato:** SHA-256 `576813aa…21de`; 36.175.287 B; `testzip` OK.
- **Fato:** **1.832** entradas de arquivo; 42.006.134 B descompactados; **60** categorias (dois níveis após `kmassets/`). Ex.: `more_title_effects/effect` (273), `basic_title_effects/effect` (168), `basic_effects/filter` (143), `text_transitions/transition` (105).
- **Fato:** extensões mais comuns: `json` 531, `png` 467, `xml` 364, sem extensão 228 (todos PNG pela assinatura), `jpg` 91, `svg` 58, `glsl` 57, `lua` 28, `nef` 8.
- **Fato:** 512 `_info.json` (todos JSON válido); chaves mais frequentes `filename`, `id`, `type`, `kmcategory`, `label`, `hidden`, `icon`, `ratios`, `ratioGroupId`.
- **Fato:** 328 `kedl.xml` (todos XML válido), com raiz `<effect>`; filhos diretos mais comuns `<animatedvalue>`, `<texture>`, `<userfield>`, `<projection>`.
- **Inferência:** pacotes de efeito/transição são descritos por `_info.json` (metadados e localização) e `kedl.xml` (grafo de texturas e valores animados), com código GLSL e Lua para comportamento. O significado exato de "KEDL" não foi confirmado.
- **Hipótese (testável):** `kedl.xml` é interpretado pelo `libnexeditorsdk.so`. Teste sugerido: localizar as strings de tag (`animatedvalue`, `texture`) na biblioteca nativa.
- Inventário: `kmassets-base.md` e `kmassets-base.csv`. Script: `scripts/inventario_kmassets.py`.

### 4.2 Outros ativos (`assets/`, `res/`, raiz)

- **Fato:** 2.673 entradas inventariadas (excluindo `.dex` e `META-INF/`); identificação por assinatura, não por extensão: `inventarios/ativos-base.md`.
- **Fato:** `assets/km_segmentation_352x352_fp16.tflite` (7.132.104 B) tem identificador `TFL3` (FlatBuffer TFLite). **Inferência:** modelo de segmentação; não decodificado.
- **Fato:** `assets/kmVAD_v512_fp16.bin` (885.098 B) não tem assinatura reconhecida. **Hipótese:** pesos de detecção de atividade de voz; não verificado.
- **Fato:** `assets/audio_effect/**/*.json` (59 arquivos, de 351 B a 2.736 B): parâmetros de efeitos de áudio (equalizador, reverb, troca de voz). Estrutura não documentada aqui; ver a próxima etapa.
- **Fato:** `assets/dexopt/baseline.prof` (11.632 B) e `baseline.profm`: perfis de compilação ART.
- **Fato:** `assets/PublicSuffixDatabase.list`: lista pública de sufixos (dado aberto).
- **Fato:** `assets/AppstoreAuthenticationKey.pem` (451 B) é **chave pública**, com SHA-256 `2c3db33a…144e31`. Conteúdo não reproduzido neste repositório.
- **Fato:** `assets/dic` (4.016 B), `assets/DCI/dci.json` (160.548 B), `assets/DCI/dci_audio` (MP4), `assets/hotkey/*.json` e `assets/shortKey.json` existem; **não interpretados**.
- **Fato:** `logs/proto/...` e `developers/mobile/...`, `firebase/...` contêm `.proto` de bibliotecas de terceiros (Firebase, Google). Não são formatos do KineMaster.

### 4.3 O que não foi feito nesta etapa

- Não há especificação dos formatos proprietários (`kedl.xml`, `.nef`, `dic`, `kmVAD`, `dci`). Nada disso foi inventado.
- O `.nef` de `kmassets` não tem assinatura identificada pelo inventário (formato desconhecido).

## 5. Nativo (`arm64_v8a`, Fase 4 parcial)

- **Fato:** 22 `.so`, 0 erros de leitura (`inventarios/nativo-arm64.md`).
- **Fato:** `libnexeditorsdk.so` tem 370 símbolos `Java_*`; `libkmSTTv4_armv80/82.so` têm 1.209 exportações; `libonnxruntime.so` tem 203 importações; 8 bibliotecas sem `JNI_OnLoad`; símbolos de depuração (`.symtab`) só em `librenderscript-toolkit.so`.
- **Fato (relação JNI):** dos **549** métodos `native` declarados no Java (JADX), **386** foram verificados por símbolo JNI exportado (`scripts/relacionar_jni.py`; `inventarios/jni-relacao.md`).
- **Fato:** **156** métodos `native` têm o nome presente como string em alguma `.so`, sem símbolo exportado. **Fato:** nenhuma `.so` do APK importa `RegisterNatives` como símbolo dinâmico.
- **Inferência:** os 156 não aparecem como símbolo exportado; uma explicação plausível é o registro manual via tabela de funções do `JNIEnv` (que não gera import de `RegisterNatives`). **Hipótese não verificada.** Teste sugerido: disassembly das `.so` procurando chamadas indiretas por índice da tabela JNIEnv.
- **Fato:** **7** métodos `native` têm o nome ausente de todas as bibliotecas do APK (ex.: `NexTextEffect.createPolygonContext`, `deletePolygonContext`, `setPolygonContext`; `com.google.android.gms.tasks.NativeOnCompleteListener.nativeOnComplete`). **Hipótese:** código de módulo não incluído no XAPK ou carregado em execução. Não confirmado.
- **Fato:** `entitlementClearSubscriptionN` e `entitlementEnrollStateN` aparecem em `libnexeditorsdk.so`.
- **Fato:** `loadLibrary` com literal estático: 11 (ex.: `airbridgelib`, `geckox_bspatch`, `pglarmor`, `renderscript-toolkit`). Há também carregamentos com nome em variável (ex.: `com/apm/insight/a.java`), que exigem análise.
- **Não feito:** descompilação de código nativo (pseudocódigo) com Ghidra ou angr. Ghidra indisponível; angr não avaliado nesta etapa.

## 6. Dinâmica (Fase 6): não executada

Bloqueio verificado (ver `ferramentas-e-restricoes.md` §6): sem KVM, adb ou emulador; download do Android SDK bloqueado. Nenhum resultado de execução é apresentado.

## 7. Lacunas

| Lacuna | Causa | Impacto | Próximo passo |
|---|---|---|---|
| `FontBrowserViewModel` não gerada | `Code generation error after restart` do JADX 1.1.0 | 1 classe de 41.297 | Usar smali como fonte de verdade ou JADX 1.5.x |
| 124 nós com falha no JADX padrão (29 classes, 95 métodos) | Limitações do JADX 1.1.0 | 88 classes de nível superior afetadas (ver cobertura) | Auditar por classe; comparar com JADX 1.5.x quando disponível |
| 40 classes ausentes na passagem `show-bad` | `Error saving class` nessa passagem | `show-bad` não é superconjunto | Não usar como substituto |
| 156 + 7 métodos nativos sem símbolo | Registro dinâmico ou código não incluído (hipótese) | Relação JNI incompleta | Disassembly e análise de tabela JNIEnv |
| Nativo sem pseudocódigo | Ghidra indisponível; angr não avaliado | Comportamento nativo não recuperado | Avaliar angr/capstone em `libbuffer_pgl.so` |
| Formatos proprietários | Sem especificação; amostras insuficientes | Formatos não documentados | Análise comparativa de amostras (`kmassets`, `audio_effect`) |
| Dinâmica | Sem emulador | Carregamento em runtime não observado | Ambiente autorizado com KVM e Android |
| REA/MCP | Não registrado na sessão | Nenhum uso REA | Reconectar ferramentas REA e repetir |

## 8. Reprodução

1. `export KM_WORKDIR=/home/user/kinemaster-work` e `bash scripts/preparar_ambiente.sh`.
2. Concatenar e extrair o 7z (ver `scripts/extrair_xapk.py`).
3. `bash scripts/decompilar_jadx.sh <base.apk> base-jadx` e `bash scripts/decompilar_apktool.sh <base.apk> base`.
4. `python scripts/analisar_log_jadx.py <log> <saída>`, `scripts/cobertura_dex.py`, `scripts/analise_manifesto.py`, `scripts/inventario_ativos.py`, `scripts/inventario_kmassets.py`, `scripts/relacionar_jni.py`.

Caminhos de saída: `docs/analise/inventarios/`. Índice: `docs/analise/README.md`.
