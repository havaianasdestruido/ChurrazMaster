# Limitações da análise — classificação por causa e impacto

Este documento cumpre a Fase 8: separar **o que foi recuperado**, **o que foi recuperado
parcialmente** e **o que permanece desconhecido**, com a causa de cada limitação.

Legenda de impacto: **Alto** (bloqueia a compreensão de um subsistema central),
**Médio** (reduz a qualidade/documentação de um subsistema), **Baixo** (afeta conforto ou detalhe).

## 1. Limitações do ambiente (causa externa)

| # | Limitação | Causa verificada | Impacto | Alternativa adotada |
|---|---|---|---|---|
| A1 | Sem descompilador nativo (Ghidra/IDA) | `release-assets.githubusercontent.com` bloqueado (`SSL_ERROR_SYSCALL`); Ghidra só existe como release de 543 MB | **Alto** para Fase 4 | Análise estrutural ELF + símbolos JNI + correlação com Java |
| A2 | Sem análise dinâmica | ausência de `/dev/kvm`, `adb`, emulador e dispositivo; APK **AArch64-only** | **Alto** para Fase 6 | Procedimento reprodutível documentado para execução em dispositivo real |
| A3 | Sem JDK completo (apenas JRE) | wheel `jdk4py` não inclui `javac`/`jdk.compiler` | Médio | Uso das CLIs prontas (jadx, Apktool) e do servidor MCP; nenhum driver Java próprio |
| A4 | Sem build-tools/aapt2 | canais do Google (Maven/CDN) bloqueados | Baixo | Apktool 2.4.1 decodificou 100 % dos APKs sem erro |
| A5 | jadx 1.1.0 (2020) em vez de 1.5.x | release assets bloqueados; distribuição obtida via npm | Médio | Segunda opinião com o jadx-core do `jadx-headless-mcp` (43.251 classes, 0 erros) |
| A6 | Servidor MCP do REA não registrado | `doctor` reporta `healthy=false`, sem `GHIDRA_INSTALL_DIR`/`REA_JADX_MCP_JAR` | Médio | Skill usada como metodologia; caminho Android reproduzido com cliente MCP próprio (`scripts/09_mcp_jadx.py`) |
| A7 | Dependências transitivas Python não versionadas | volume/limite de repositório | Baixo | Wheels principais versionados + `pip` com versões fixas |

## 2. Limitações da ferramenta (causa: jadx 1.1.0)

| # | Limitação | Evidência | Impacto |
|---|---|---|---|
| B1 | 52 classes não puderam ser salvas | `ERROR - Error saving class` no log | Médio — 3 delas são de nível superior com código |
| B2 | 119 pontos com `JADX ERROR` dentro dos arquivos | `JadxOverflowException: Regions count limit reached` | Médio — corpo de método pode estar incompleto ou em *fallback* |
| B3 | Renomeação de pacotes/classes inválidos | `firebase-auth-api` → `p002firebaseauthapi`; `X.4V` → `AnonymousClass4V` | Baixo (mapeável) — resolvido por `05_metricas_dex.py` |
| B4 | Classes do pacote padrão movidas | 13.340 arquivos em `defpackage/` | Baixo |
| B5 | Classes internas em arquivo próprio | `Foo$Bar.java` | Baixo |

Observação metodológica importante: **presença de arquivo `.java` não significa corpo de método correto.**
As métricas distinguem "classe presente" (98,49 %) de "classe com corpo íntegro" (não medida com
precisão — apenas os pontos B1/B2 são conhecidos).

## 3. Limitações intrínsecas do alvo

| # | Limitação | Causa | Impacto |
|---|---|---|---|
| C1 | Nomes de classe/método curtos (`a`, `b`, `zzafc`) | ofuscação R8 no build de release | **Alto** para leitura; os nomes são reais, não apelidos da ferramenta |
| C2 | Nenhum símbolo de depuração nos 22 ELF | strip de release | Alto |
| C3 | Ausência de `strings.xml` legível no APK base | strings em `resources.arsc`, distribuídas por splits de idioma | Baixo — os splits foram decodificados |
| C4 | Recursos carregados em runtime (asset store) | servidos pela rede, não embarcados | Médio — não inventariados |

## 4. O que permanece desconhecido (declarado)

1. **Comportamento em execução** — nenhuma observação dinâmica foi produzida.
2. **Pseudocódigo nativo** — nenhuma função nativa foi descompilada.
3. **Especificação de `.nef`** — apenas hipóteses em `formatos_proprietarios.md`.
4. **Estrutura interna de `.kmproject`** e esquema completo dos bancos Room de projeto.
5. **Protocolo de rede do app** (endpoints, autenticação) — não exercitado.
6. **Mecanismo de entitlement/assinatura** — classes identificadas
   (`com.kinemaster.app.modules.integrity.entitlement`, 160 classes), sem análise de fluxo completa.

## 5. Itens que **não** são limitações (para evitar interpretação errada)

- Não há protetor comercial nos `.so` (nenhuma assinatura UPX/Jiagu/Bangcle/Legu detectada).
- Não há APK ausente: 20 de 20 splits presentes e coerentes com `manifest.json`.
- Não há duplicidade significativa de DEX: 9 classes repetidas, todas de anotações
  (`javax.annotation.*`, `kotlin.Metadata`) entre o `classes.dex` e o DEX de `assets/`.
- A ausência de `geckox_bspatch`, `OpenCL*` e `stlport_shared` **não** é corrupção: são
  carregamentos condicionais de SDKs externos.
