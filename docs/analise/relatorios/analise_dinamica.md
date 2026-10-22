# Análise dinâmica — estado, bloqueio e procedimento reprodutível

> **Status: NÃO EXECUTADA.** Nenhuma observação de runtime do KineMaster foi produzida nesta missão.
> Este documento registra o motivo objetivo do bloqueio e descreve, de forma reproduzível, como
> executar a Fase 6 em um ambiente adequado e autorizado.

## 1. Por que não foi executada

| Requisito | Situação verificada neste ambiente |
|---|---|
| Virtualização acelerada (`/dev/kvm`) | **ausente** (`ls /dev/kvm` → não existe) |
| Android SDK / Platform-Tools (`adb`) | **ausente** (nenhum binário no PATH) |
| Emulador AVD | **ausente** |
| Dispositivo físico conectado | **ausente** |
| Arquitetura do alvo | **AArch64 apenas** (`lib/arm64-v8a`) — não executável em x86_64 sem tradução |
| Recursos de máquina | 2 vCPU, 3,8 GiB de RAM, disco 20 GB |
| `frida`/`objection` | não instalados; sem alvo para instrumentar |

Executar um Android completo (mesmo ARM emulado por QEMU-TCG) nesse perfil é inviável na prática:
sem KVM, o *boot* e a renderização de vídeo levariam horas e tendem a falhar por falta de memória.
O aplicativo também exige **serviços externos** (Firebase, Play Billing, verificação de integridade
do Play) e possui detecção de ambiente; a tentativa de contornar autenticação/licenciamento está
**fora do escopo autorizado** desta missão.

Consequência assumida: **nenhuma** conclusão do relatório técnico depende de execução. Toda afirmação
estática é marcada como fato observado; qualquer comportamento é inferência ou hipótese.

## 2. Ambiente recomendado para executar a Fase 6

- Dispositivo **ARM64** dedicado (não pessoal), com dados de teste, Android 10–15, depuração USB.
  Alternativa: emulador `arm64-v8a` em host com KVM (Linux) ou Apple Silicon.
- Ferramentas: `adb`, `frida-server` (mesma arquitetura), `frida-tools`, `objection` (opcional),
  `mitmproxy` (para tráfego, apenas do que for necessário), `logcat`.
- Conta de teste legítima; **não** usar credenciais pessoais.

## 3. Instalação controlada (comandos)

```bash
# 1. Instalar os splits na ordem correta (base + ABI + densidade + idioma desejado)
adb install-multiple -r \
  com.nexstreaming.app.kinemasterfree.apk \
  config.arm64_v8a.apk config.xxhdpi.apk config.pt.apk

# 2. Confirmar identidade realmente instalada
adb shell dumpsys package com.nexstreaming.app.kinemasterfree | \
  grep -E "versionName|versionCode|primaryCpuAbi"

# 3. Coletar logs de inicialização (limpar antes)
adb logcat -c
adb shell am start -n com.nexstreaming.app.kinemasterfree/com.kinemaster.app.screen.splash.SplashActivity
adb logcat -v threadtime > inicializacao.log
```

## 4. Observações planejadas (com o que registrar)

| # | Observação | Como obter | Correlação estática |
|---|---|---|---|
| D1 | Bibliotecas carregadas na inicialização | `adb shell cat /proc/<pid>/maps | grep '\.so'` | `nativo.json` (22 ELF) |
| D2 | Classes carregadas | `frida -U -f … -l hook_classloader.js` ou `dexdump` | `metricas_dex.json` (52.903 classes) |
| D3 | Chamadas JNI efetivas | hook em `RegisterNatives`/`JNI_OnLoad` | `simbolos_jni.csv` (480 símbolos) |
| D4 | Criação/abertura de projeto | `adb shell ls -R /sdcard/Android/data/…/` | `.kine`/`.kmproject` (Fase 5) |
| D5 | Banco Room em uso | copiar `databases/*.db` e abrir com `sqlite3` | `ProjectDatabase`, `TemplateDatabase`, `InstalledAssetsDatabase`, `FontDatabase` |
| D6 | Modelos de IA efetivamente carregados | `strace`/`frida` em `open`/`mmap` | `km_segmentation_352x352_fp16.tflite`, `kmVAD_v512_fp16.bin` |
| D7 | Endpoints de rede | `mitmproxy` com CA instalada (somente o necessário) | `com.kinemaster.module.network.communication.*` |
| D8 | Carregamento condicional de `OpenCL*`/`geckox_bspatch` | `logcat` de `System.loadLibrary` malsucedido | Fase 5 §5 |

## 5. Cuidados e limites éticos

- Executar apenas em ambiente controlado, com dados de teste, e apenas para compreender o
  funcionamento do aplicativo.
- **Não** contornar autenticação, DRM, verificação de licença ou mecanismos de integridade.
- Não publicar capturas que contenham dados pessoais, tokens ou credenciais.
- Registrar cada observação com: artefato de origem, comando exato, saída bruta e horário.

## 6. Critério para considerar a Fase 6 concluída

A fase só poderá ser marcada como concluída quando existirem, no repositório:
(1) log de inicialização com falhas classificadas; (2) lista de bibliotecas efetivamente mapeadas,
comparada com o inventário estático; (3) amostra de banco de dados de projeto copiada e analisada;
(4) pelo menos um fluxo de funcionalidade correlacionado com classe/método identificado
estaticamente; (5) declaração explícita do que não foi reproduzido.
