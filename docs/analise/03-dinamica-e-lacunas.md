# Análise dinâmica: bloqueio comprovado e preparação segura

## O que aconteceu nesta execução

**O KineMaster não foi instalado nem executado.** A modalidade dinâmica
obrigatória permanece pendente. Não há logcat, tráfego de aplicativo, captura de
processo Android, observação de ciclo de vida ou exportação de projeto para
correlacionar com os resultados estáticos. O diagnóstico do host não é análise
dinâmica do aplicativo; os testes dos scripts tampouco o são.

Evidência: `evidencias/ambiente-dinamico.json`, produzido por
`python3 scripts/diagnosticar_dinamica.py` em Linux x86_64, sem:

- ADB, Android Emulator, QEMU ou Docker detectados no PATH;
- `/dev/kvm` presente/acessível;
- barramento USB exposto;
- dispositivo Android dedicado fornecido à sessão.

O aplicativo contém somente ABI **arm64-v8a**, exige Android API ≥ 26 e usa
serviços gráficos/áudio/ML. Não se podem carregar suas `.so` no processo Linux
x86_64 e chamar isso de execução Android. A ausência de KVM sozinha não torna
emulação por software impossível; o bloqueio efetivo combina ausência de
emulador/imagem/dispositivo e aquisição oficial do SDK/imagens fora dos hosts
permitidos. Não foi tentado transformar o host em dispositivo pessoal ou
inventar compatibilidade ARM por tradução não verificada.

## Preparação disponível, mas ainda não validada num dispositivo

`observar_android.py` exige ADB explicitamente escolhido, hash SHA-256 informado,
serial explícito e confirmação de ambiente descartável. Ele:

1. Verifica o hash do ADB e registra sua identidade;
2. Confere versão/API e ABI antes de instalar;
3. Confere os hashes dos 20 APKs com o inventário;
4. Usa `install-multiple` sem `-r`, para não substituir instalação preexistente;
5. Resolve somente a atividade inicial do pacote e exige confirmação do ActivityManager;
6. Obtém PID, logcat limitado ao processo e metadados do pacote;
7. Tenta observar maps via `run-as`, sem elevar privilégios nem contornar bloqueios;
8. Armazena dados brutos em `dados-dinamicos/`, fora do Git.

O script foi revisado estaticamente e sua sintaxe foi verificada; **não foi
executado com ADB**, portanto não se alega validação de integração nem sucesso
dessas operações. Uma inicialização bem-sucedida futura ainda será apenas um
cenário de boot, não cobertura funcional abrangente.

ADB/Platform Tools oficial e imagem/dispositivo não estão versionados como se
estivessem disponíveis. É necessário obter uma versão oficial autorizada,
registrar hash, licença e dependências no manifesto e revisar seus termos antes
de redistribuição. Não há URL/hash de SDK inventados para aparentar completude.
A instrumentação futura deve permanecer em ambiente dedicado e conta de teste
legitimamente autorizada. Não contornar login, licenciamento, assinatura, DRM,
root detection ou outros mecanismos para obter acesso não autorizado.

## Cenários técnicos pendentes e evidência necessária

| Pergunta | Artefato estático conhecido | Evidência de execução necessária | Estado |
|---|---|---|---|
| O splash inicializa `NexEditor`? | Manifesto, `KineMasterApplication.c`, métodos JNI | start, logcat, PID/maps, sequência de eventos | Não observado |
| O DEX Audience Network é carregado? | `assets/audience_network/classes.dex` | carregador/classe observada e origem DEX | Não observado |
| Qual variante STT é selecionada? | duas `libkmSTTv4_*`, dependência ONNX | maps/carregamento durante cenário STT legítimo | Não observado |
| Como o projeto é persistido? | Room `project_table`, tags Wire | projeto sintético antes/depois, hashes, arquivos e banco de teste | Não observado |
| Como ocorre exportação? | métodos nativos `encodeProject*` | projeto de mídia sintética, logs e saída validada | Não observado |
| Quais recursos são baixados? | carregadores e referências empacotadas | requests observadas e arquivos adquiridos em teste autorizado | Não observado |
| Quais serviços de rede são necessários? | dependências Play/Firebase/SDKs | observação de rede no ambiente isolado, com segredos removidos | Não observado |

Não substituir captura técnica por screenshots. Não fazer interceptação TLS ou
alteração de certificados automaticamente. Escolher observação autorizada que
não exija contornar proteções; registrar a impossibilidade de inspecionar corpos
quando pertinente. Dumps autenticados, tokens, mídia pessoal e banco de contas
não devem entrar no PR.

## Classificação das limitações atuais

- **Infraestrutura/alto impacto:** sem Android compatível; toda correlação
  comportamental permanece desconhecida. Liberação: dispositivo ARM64 dedicado
  ou imagem/emulador legítimo e rede de provisionamento compatível.
- **Aquisição e tamanho/alto impacto:** Ghidra/JDK completo/JADX moderno não
  preparados. Liberação: downloads oficiais com hashes fixados, armazenamento
  externo reproduzível quando exceder o Git; não alterar políticas de rede à força.
- **Ferramenta/alto impacto:** milhares de stubs Java e inferência de tipos
  incorreta. Liberação: versão moderna e revisão com smali/DAD por método.
- **Semântica nativa/alto impacto:** desmontagem não estabelece funções, tipos,
  fluxo indireto nem todos os bindings JNI. Liberação: análise Ghidra e referências
  cruzadas, símbolos/debug recuperáveis e confirmação em runtime.
- **Formato/médio a alto impacto:** tags Wire parciais, caudas NEF, modelos e
  `dic` ainda não completamente interpretados. Liberação: amostras sintéticas,
  parsers compatíveis e hipóteses testadas, sem inventar especificações.
- **Validação/médio a alto impacto:** só um DEX minúsculo foi remontado e uma
  classe Java foi repetida. Não houve build do APK, validação de todos os métodos,
  de todos os recursos nem de assinatura v3/linhagem.
- **Licenciamento/alto impacto para publicação:** o aplicativo não herda MIT;
  não redistribuir extrações, fontes, modelos e mídia sem base jurídica adequada.

As lacunas não são justificativa para declarar conclusão total. O próximo ciclo
pode avançar nas questões estáticas independentes enquanto se prepara o ambiente
dinâmico; `TODO.md` registra tarefas abertas, inclusive revisão de cada falha.
