# Strings e desmontagem das bibliotecas nativas

Gerado em `2026-10-22T16:07:06.875899+00:00` por `scripts/13_strings_e_desmontagem.py`.

> Complemento da Fase 4. Sem descompilador nativo, o limite honesto é: **strings**, **símbolos** e **desmontagem pontual**. Nenhum pseudocódigo foi produzido.

## Quem manipula os formatos proprietários

| biblioteca | strings de formato |
|---|---|
| `libnexeditorsdk.so` | `/usr/local/share/lua/5.3/?.lua;/usr/local/share/lua/5.3/?/init.lua;/usr/local/lib/lua/5.3/?.lua;/usr/local/lib/lua/5.3/?/init.lua;./?.lua;./?/init.lua` |

**Fato observado:** `libnexeditorsdk.so` é a única biblioteca que referencia `.nef` e `.lua`,
o que a torna a candidata direta a leitora desses formatos. **Inferência**, não prova:
a string pode vir de uma tabela de nomes/erros sem código de parsing associado.

## Indícios de proteção/licenciamento no motor

### `libairbridgelib.so`

```
Java_co_ab180_airbridge_internal_signature_NativeLib_generate
co/ab180/airbridge/internal/signature/Hmac
co/ab180/airbridge/internal/signature/Totp
signature buffer overflow
```

### `libnexeditorsdk.so`

```
Digital Signature
E-mail Protection
ICC or token signature
Java_com_kinemaster_module_nexeditormodule_nexvideoeditor_NexEditor_check4km
Java_com_kinemaster_module_nexeditormodule_nexvideoeditor_NexEditor_setPacakgeName4Protection
LICENSEKEY
[Landroid/content/pm/Signature;
[nexProtection.cpp %d] Current Time(%ld sec) : %d/%d/%d  %d:%d:%d
[nexProtection.cpp %d] End Time(%ld sec) : %d/%d/%d
[nexProtection.cpp %d] Manufacture lock is pass! : pstSDKInfo->pPKGName[%d](%s), package name(%s)
[nexProtection.cpp %d] No Time Lock
[nexProtection.cpp %d] Package lock is pass! : pstSDKInfo->pPKGName[%d](%s), package name(%s)
[nexProtection.cpp %d] Start Time(%ld sec) : %d/%d/%d
[nexProtection.cpp %d] This SDK is expired at %d/%d/%d
[nexProtection.cpp %d] This SDK is not validate. It's available from %d/%d/%d
[nexProtection.cpp %d] createEditor failed for not available manufacture
[nexProtection.cpp %d] createEditor failed for not available package_name. (%s)
[nexProtection.cpp %d] createEditor failed for timeout
[nexProtection.cpp %d] not available manufacturer: pstSDKInfo->pProjectID(%s), pManufacturertoupper(%s)
[nexProtection.cpp %d] not available pacakge: pstSDKInfo->pPKGName[%d](%s), package name(%s)
```

### `libtensorflowlite_jni.so`

```
<placeholder signature>
Internal error: Can not find java/lang/String class to get SignatureDef keys.
Java_org_tensorflow_lite_NativeInterpreterWrapper_getSignatureKeys
Java_org_tensorflow_lite_NativeSignatureRunnerWrapper_nativeAllocateTensors
Java_org_tensorflow_lite_NativeSignatureRunnerWrapper_nativeGetInputIndex
Java_org_tensorflow_lite_NativeSignatureRunnerWrapper_nativeGetOutputIndex
Java_org_tensorflow_lite_NativeSignatureRunnerWrapper_nativeGetSignatureRunner
Java_org_tensorflow_lite_NativeSignatureRunnerWrapper_nativeGetSubgraphIndex
Java_org_tensorflow_lite_NativeSignatureRunnerWrapper_nativeInputNames
Java_org_tensorflow_lite_NativeSignatureRunnerWrapper_nativeInvoke
Java_org_tensorflow_lite_NativeSignatureRunnerWrapper_nativeOutputNames
Java_org_tensorflow_lite_NativeSignatureRunnerWrapper_nativeResizeInput
Java_org_tensorflow_lite_TensorImpl_createSignatureInputTensor
Java_org_tensorflow_lite_TensorImpl_createSignatureOutputTensor
Java_org_tensorflow_lite_TensorImpl_shapeSignature
Missing exported method name for SignatureDef
N6tflite27SignatureRunnerTensorHandleE
NULL SignatureDef inputs for exported method %s
NULL SignatureDef outputs for exported method %s
TfLiteInterpreterGetSignatureCount
```

### `libtranslate_jni.so`

```
# for the Hjal project in 2003. Distributed using the CC BY license.
Different signature: actual "
Missing exported method name for SignatureDef
NULL SignatureDef inputs for exported method %s
NULL SignatureDef outputs for exported method %s
Not enough bytes to check signature
The interpreter does not have a signature definition named: 
licence
licenced
licences
license
licensed
licenses
protection
signature=%02x%02x%02x%02x%02x%02x 
```

**Fato observado:** existem símbolos JNI com nomes autoexplicativos como
`NexEditor_setPacakgeName4Protection` (grafia original, com erro de digitação) e
`NexEditor_check4km`. **Conclusão:** o motor nativo recebe o nome do pacote e possui uma
rotina de verificação. Isso está documentado aqui apenas como característica do
funcionamento; **contornar essa verificação está fora do escopo** desta missão.

## Desmontagem de `JNI_OnLoad`

### `libapminsighta.so` — entrada em `0x8bb0`

- Instruções desmontadas: 56
- Chamadas indiretas (`blr`/`br`): 3
- Chamadas indiretas são compatíveis com o padrão JNIEnv->RegisterNatives, mas **não** provam isso sem análise de fluxo.

```asm
0x8bb0: stp	x29, x30, [sp, #-0x40]!
0x8bb4: str	x28, [sp, #0x10]
0x8bb8: stp	x22, x21, [sp, #0x20]
0x8bbc: stp	x20, x19, [sp, #0x30]
0x8bc0: mov	x29, sp
0x8bc4: sub	sp, sp, #0x360
0x8bc8: mrs	x22, tpidr_el0
0x8bcc: mov	w2, #6
0x8bd0: ldr	x8, [x22, #0x28]
0x8bd4: add	x1, sp, #8
0x8bd8: movk	w2, #1, lsl #16
0x8bdc: mov	w19, #6
0x8be0: mov	x20, x0
0x8be4: movk	w19, #1, lsl #16
0x8be8: stur	x8, [x29, #-8]
0x8bec: ldr	x8, [x0]
0x8bf0: str	xzr, [sp, #8]
0x8bf4: ldr	x8, [x8, #0x30]
0x8bf8: blr	x8
0x8bfc: cbz	w0, #0x8c08
0x8c00: mov	w19, #-1
0x8c04: b	#0x8c60
0x8c08: ldr	x0, [sp, #8]
0x8c0c: nop	
0x8c10: adr	x1, #0x390d
0x8c14: ldr	x8, [x0]
0x8c18: ldr	x8, [x8, #0x30]
0x8c1c: blr	x8
0x8c20: mov	x21, x0
0x8c24: nop	
```

### `libnexeditorsdk.so` — entrada em `0x2dabb0`

- Instruções desmontadas: 160
- Chamadas indiretas (`blr`/`br`): 15
- Chamadas indiretas são compatíveis com o padrão JNIEnv->RegisterNatives, mas **não** provam isso sem análise de fluxo.

```asm
0x2dabb0: sub	sp, sp, #0x80
0x2dabb4: stp	x29, x30, [sp, #0x30]
0x2dabb8: stp	x26, x25, [sp, #0x40]
0x2dabbc: stp	x24, x23, [sp, #0x50]
0x2dabc0: stp	x22, x21, [sp, #0x60]
0x2dabc4: stp	x20, x19, [sp, #0x70]
0x2dabc8: add	x29, sp, #0x30
0x2dabcc: mrs	x24, tpidr_el0
0x2dabd0: adrp	x9, #0x945000
0x2dabd4: mov	x20, x0
0x2dabd8: ldr	x8, [x24, #0x28]
0x2dabdc: mov	w19, #6
0x2dabe0: movk	w19, #1, lsl #16
0x2dabe4: stur	x8, [x29, #-8]
0x2dabe8: str	x0, [x9, #0xb48]
0x2dabec: nop	
0x2dabf0: adr	x0, #0x2daf2c
0x2dabf4: bl	#0x2ddde0
0x2dabf8: ldr	x8, [x20]
0x2dabfc: mov	w2, #6
0x2dac00: sub	x1, x29, #0x10
0x2dac04: mov	x0, x20
0x2dac08: movk	w2, #1, lsl #16
0x2dac0c: stur	xzr, [x29, #-0x10]
0x2dac10: ldr	x8, [x8, #0x30]
0x2dac14: blr	x8
0x2dac18: cbz	w0, #0x2dac34
0x2dac1c: mov	w19, #-1
0x2dac20: ldr	x8, [x24, #0x28]
0x2dac24: ldur	x9, [x29, #-8]
```

### `libnms.so` — entrada em `0x157a4`

- Instruções desmontadas: 49
- Chamadas indiretas (`blr`/`br`): 0
- Chamadas indiretas são compatíveis com o padrão JNIEnv->RegisterNatives, mas **não** provam isso sem análise de fluxo.

```asm
0x157a4: stp	x29, x30, [sp, #-0x30]!
0x157a8: str	x21, [sp, #0x10]
0x157ac: mov	x29, sp
0x157b0: stp	x20, x19, [sp, #0x20]
0x157b4: sub	sp, sp, #0x10
0x157b8: mrs	x21, tpidr_el0
0x157bc: adrp	x20, #0x51000
0x157c0: ldr	x8, [x21, #0x28]
0x157c4: add	x20, x20, #0x4c0
0x157c8: mov	x19, x0
0x157cc: stur	x8, [x29, #-8]
0x157d0: ldar	w8, [x20]
0x157d4: cbnz	w8, #0x157fc
0x157d8: adrp	x0, #0xb000
0x157dc: adrp	x1, #0x51000
0x157e0: adrp	x2, #0xb000
0x157e4: add	x0, x0, #0x594
0x157e8: add	x1, x1, #0x4b8
0x157ec: add	x2, x2, #0x59c
0x157f0: bl	#0x15304
0x157f4: mov	w8, #1
0x157f8: stlr	w8, [x20]
0x157fc: mov	x8, sp
0x15800: sub	x20, x8, #0x40
0x15804: mov	sp, x20
0x15808: movi	v0.2d, #0000000000000000
0x1580c: adrp	x0, #0x51000
0x15810: add	x0, x0, #0x4b8
0x15814: mov	x1, xzr
0x15818: mov	w2, #0xa
```

### `libtranslate_jni.so` — entrada em `0x6e9fec`

- Instruções desmontadas: 15
- Chamadas indiretas (`blr`/`br`): 1
- Chamadas indiretas são compatíveis com o padrão JNIEnv->RegisterNatives, mas **não** provam isso sem análise de fluxo.

```asm
0x6e9fec: sub	sp, sp, #0x20
0x6e9ff0: stp	x30, x19, [sp, #0x10]
0x6e9ff4: ldr	x8, [x0]
0x6e9ff8: mov	w2, #6
0x6e9ffc: add	x1, sp, #8
0x6ea000: movk	w2, #1, lsl #16
0x6ea004: mov	w19, #6
0x6ea008: ldr	x8, [x8, #0x30]
0x6ea00c: movk	w19, #1, lsl #16
0x6ea010: blr	x8
0x6ea014: cmp	w0, #0
0x6ea018: csinv	w0, w19, wzr, eq
0x6ea01c: ldp	x30, x19, [sp, #0x10]
0x6ea020: add	sp, sp, #0x20
0x6ea024: ret	
```

### `libtt_ugen_layout.so` — entrada em `0x4505c`

- Instruções desmontadas: 24
- Chamadas indiretas (`blr`/`br`): 0
- Chamadas indiretas são compatíveis com o padrão JNIEnv->RegisterNatives, mas **não** provam isso sem análise de fluxo.

```asm
0x4505c: sub	sp, sp, #0x30
0x45060: stp	x29, x30, [sp, #0x10]
0x45064: stp	x20, x19, [sp, #0x20]
0x45068: add	x29, sp, #0x10
0x4506c: mrs	x20, tpidr_el0
0x45070: mov	x1, x0
0x45074: ldr	x8, [x20, #0x28]
0x45078: mov	x0, sp
0x4507c: str	x8, [sp, #8]
0x45080: str	xzr, [sp]
0x45084: bl	#0x65d00
0x45088: mov	w19, w0
0x4508c: ldr	x0, [sp]
0x45090: bl	#0x65430
0x45094: ldr	x8, [x20, #0x28]
0x45098: ldr	x9, [sp, #8]
0x4509c: cmp	x8, x9
0x450a0: b.ne	#0x450b8
0x450a4: mov	w0, w19
0x450a8: ldp	x20, x19, [sp, #0x20]
0x450ac: ldp	x29, x30, [sp, #0x10]
0x450b0: add	sp, sp, #0x30
0x450b4: ret	
0x450b8: bl	#0x64cb0
```

## O que continua desconhecido

- Estrutura do `.nef` (não especificada).
- Semântica dos 480 símbolos JNI além do nome.
- Fluxo de verificação de pacote/licença (não investigado por escopo ético).
