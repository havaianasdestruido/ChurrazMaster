# Plano persistente de engenharia reversa do KineMaster

Estado: investigação em andamento; nenhuma conclusão integral declarada.

## Critérios e dependências

Cada caixa exige evidência reproduzível vinculada em `docs/analise/`. Fases 0–2 sustentam 3–5; a fase 6 exige ambiente Android autorizado e compatível; 7 depende dos resultados estáticos e dinâmicos; 8–9 auditam todas as anteriores. Conclusão integral exige denominadores verificáveis, tratamento de cada falha, correlação estática/dinâmica e declaração explícita de perdas. Inventário não equivale a análise, e pseudocódigo não equivale ao fonte original.

## Fase 0 — Preparação e integridade

- [x] Identificar o XAPK de origem em "bin/".
- [x] Registrar SHA-256, tamanho e metadados disponíveis.
- [x] Inspecionar a estrutura atual do repositório.
- [x] Definir os diretórios de saída sem sobrescrever os arquivos originais.
- [x] Registrar as restrições de licenciamento e redistribuição.
- [x] Definir critérios objetivos de conclusão.

## Fase 1 — Ferramentas e ambiente reproduzível

- [x] Ler e aplicar a skill Reverse Engineer Anything.
- [x] Verificar a disponibilidade real dos recursos REA/MCP.
- [x] Inventariar as ferramentas necessárias.
- [ ] Baixar e organizar as ferramentas redistribuíveis em "bin/tools/".
- [x] Corrigir regras inadequadas do ".gitignore".
- [x] Criar "bin/tools/MANIFESTO.md".
- [x] Registrar versões, licenças, origens e hashes.
- [ ] Criar instruções reproduzíveis para configurar ferramentas não redistribuíveis.
- [x] Validar que outro agente consegue identificar e preparar o ambiente.

## Fase 2 — Inventário e extração integral do XAPK

- [x] Identificar todos os componentes do pacote.
- [x] Extrair o conteúdo sem alterar os arquivos de origem.
- [x] Identificar APK principal e APKs divididos.
- [x] Inventariar arquivos DEX, bibliotecas nativas, recursos, assets e configurações.
- [x] Identificar certificados, manifesto e informações de assinatura.
- [x] Identificar componentes ausentes, duplicados ou inconsistentes.
- [x] Registrar hashes e relações entre os arquivos.
- [x] Produzir um relatório de cobertura da extração.

## Fase 3 — Análise estática do Android

- [x] Decodificar os manifestos e recursos XML.
- [x] Recuperar tabelas de recursos e strings.
- [ ] Decompilar todos os arquivos DEX identificados.
- [ ] Examinar classes, métodos, campos, pacotes e referências.
- [ ] Identificar bibliotecas, SDKs e dependências.
- [x] Mapear permissões, atividades, serviços, receptores e provedores.
- [x] Identificar reflexão, carregamento dinâmico e código ofuscado.
- [x] Registrar erros de descompilação por arquivo e por classe.
- [ ] Investigar todos os componentes não processados com sucesso.

## Fase 4 — Bibliotecas nativas e código de baixo nível

- [x] Inventariar todos os arquivos ELF e bibliotecas nativas.
- [x] Identificar ABI, arquitetura, símbolos, imports e exports.
- [ ] Identificar dependências JNI e pontos de entrada nativos.
- [ ] Examinar bibliotecas sem símbolos de depuração.
- [ ] Usar Ghidra ou outro descompilador compatível quando necessário.
- [ ] Recuperar pseudocódigo, funções, referências e estruturas possíveis.
- [ ] Identificar bibliotecas empacotadas, criptografadas ou carregadas dinamicamente.
- [x] Documentar funções e estruturas ainda não recuperáveis.
- [ ] Relacionar os achados nativos às classes Java/Kotlin.

## Fase 5 — Formatos proprietários e recursos especiais

- [ ] Identificar formatos de projeto e arquivos internos do KineMaster.
- [ ] Investigar bancos de dados, configurações, templates e recursos de mídia.
- [x] Identificar compactação, serialização e possíveis formatos proprietários.
- [ ] Investigar carregadores de módulos e recursos baixados em execução.
- [x] Recuperar estruturas e metadados quando houver evidências suficientes.
- [x] Documentar formatos desconhecidos com amostras, hashes e hipóteses testáveis.
- [x] Não inventar especificações para formatos não compreendidos.

## Fase 6 — Análise dinâmica autorizada

- [ ] Preparar um emulador ou dispositivo de teste compatível.
- [ ] Documentar versão do Android, arquitetura e configuração do ambiente.
- [ ] Instalar o aplicativo de forma controlada.
- [ ] Capturar inicialização, logs e falhas.
- [ ] Observar carregamento de classes e bibliotecas.
- [ ] Examinar comportamento de componentes e fluxos de execução.
- [ ] Correlacionar observações de runtime com os resultados estáticos.
- [ ] Registrar dependências de rede, armazenamento e serviços externos.
- [ ] Investigar código carregado apenas em execução.
- [x] Documentar comportamentos não reproduzidos e condições de teste.

## Fase 7 — Reconstrução e validação

- [x] Organizar o código decompilado em uma estrutura compreensível.
- [x] Preservar os nomes e os metadados originais recuperáveis.
- [ ] Verificar referências cruzadas e dependências entre módulos.
- [ ] Comparar a reconstrução com os artefatos originais.
- [x] Repetir extrações e descompilações para validar a reprodutibilidade.
- [ ] Testar a compilação quando tecnicamente possível.
- [x] Separar código recompilável de pseudocódigo e aproximações.
- [x] Registrar diferenças, perdas e limitações irrecuperáveis.

## Fase 8 — Cobertura, lacunas e auditoria

- [x] Consolidar a cobertura por APK, DEX, classe, método, biblioteca e recurso.
- [x] Verificar arquivos ignorados, falhas silenciosas e saídas vazias.
- [x] Revisar todos os itens pendentes das fases anteriores.
- [ ] Produzir um relatório final de evidências.
- [x] Classificar as limitações por causa e impacto.
- [x] Atualizar o "TODO.md" com os resultados reais.
- [x] Definir explicitamente o que foi recuperado, parcialmente recuperado ou permanece desconhecido.

## Fase 9 — Entrega e documentação

- [x] Documentar a estrutura final do projeto.
- [x] Documentar como repetir cada etapa.
- [x] Incluir comandos reproduzíveis e versões das ferramentas.
- [x] Garantir que todos os documentos criados estejam em português brasileiro.
- [x] Revisar o estado do Git e verificar arquivos ignorados indevidamente.
- [x] Preparar um pull request com evidências, limitações e resultados.
- [x] Não declarar conclusão total enquanto houver lacunas relevantes sem justificativa.

## Registro incremental

- 2026-10-22: repositório inicialmente limpo; quatro volumes `.7z.001` a `.004` em `bin/`, sem XAPK diretamente visível. Não assumir identidade pelo nome externo. Skill oficial versão 44 lida a partir do clone oficial; registrar proveniência e limitações na fase 1.

- 2026-10-22: extração validada (20 APKs, 7 DEX e 22 bibliotecas); ferramentas oficiais de release bloqueadas por redirecionamento de rede. Alternativas locais efetivamente executadas; decompilação, bytecode e ELF em andamento. Evidência: `docs/analise/00-integridade.md` e inventários.

## Evidências e critérios aplicados nesta etapa

- Fase 0: critérios satisfeitos em `docs/analise/00-integridade.md` e inventários de originais/XAPK. Os quatro volumes foram preservados.
- Fase 1: 44 distribuições com hash/licença/origem em `bin/tools/`; instalação Python offline em ambiente novo validada. O conjunto atual está pronto, mas Ghidra/JDK completo/JADX moderno/ADB não estão. Por isso a tarefa ampla de obter todas as ferramentas continua aberta. REA MCP não foi utilizado; CLI somente diagnóstica.
- Fase 2: extração primária integral e inventariada, 20 APKs/3.163 membros; ZIP de assets acrescenta 1.832 arquivos regulares em camada separada. APKs declarados/versões/certificados conferem; nove classes repetidas estão identificadas, sem presumir conflito em runtime.
- Fase 3: 20 manifestos, 2.294 XML adicionais e 19 ARSC processados; 7/7 DEX indexados e desmontados, 52.903 classes e 258.710 métodos. Decompilação Java foi tentada em todos, porém 6.873 stubs persistem na passagem do APK. A tarefa de decompilar todos permanece aberta, não equivalente a processamento semântico bem-sucedido.
- Fase 4: 22 ELF examinados estruturalmente e desmontados; leitura manual de acessores DataStore; 386/549 métodos native com export compatível. Não há decompilação ampla, nem resolução dos 163 restantes; tarefas correspondentes continuam abertas.
- Fase 5: 589 campos com tags Wire em 42 mensagens; dois registros zlib de um NEF recuperados. Formatos completos, contêiner de projeto, modelos e caudas permanecem parciais. A tarefa de recuperar metadados está marcada só no escopo comprovado, não como conclusão da fase.
- Fase 6: **não executada**. Diagnóstico mostra ausência de Android ARM64/ADB/emulador/KVM/USB. Script de observação preparado, sem teste de integração real. Documentação de bloqueio não marca execução como feita.
- Fase 7: extração inteira repetida com 3.163 hashes iguais; JADX repetido em uma classe; um DEX de uma classe/um método remontado com instruções simbólicas iguais e hash binário diferente. Repetibilidade marcada nesse escopo delimitado; build do APK e equivalência ampla continuam abertos.
- Fase 8: cobertura atual consolidada e lacunas classificadas em `docs/analise/RELATORIO.md` e `inventarios/cobertura.json`. O relatório final integral continua pendente. Não há porcentagem inventada de Java/funções nativas recuperados.
- Fase 9: documentação e comandos entregues em português; textos originais de ferramentas/diagnósticos/identificadores são preservados como artefatos de terceiros, conforme manifesto. PR deve permanecer em rascunho, sem merge automático.

## Subtarefas adicionais verificadas

- [x] Verificar autoconsistência v2 nos 20 APKs, com teste negativo de mutação em memória.
- [ ] Validar integralmente v3, linhagem, source stamp e regras Android com ferramenta independente.
- [x] Tratar incompatibilidade `--jobs` do baksmali/JCommander e preservar falha inicial.
- [x] Repetir JADX com hierarquia multidex e ajuste de nomes, preservando a passagem falha inicial.
- [x] Detectar sobreposição de Audience Network na passagem do APK e proibir soma indevida.
- [x] Produzir segunda opinião DAD em dois métodos; registrar tipos incorretos remanescentes.
- [x] Testar extração segura, JNI, parser NEF e tags Wire: 10 testes aprovados.
- [x] Validar preparação offline em ambiente Python novo, sintaxe e dependências.
- [ ] Preparar e executar Ghidra em host com JDK completo e downloads oficiais permitidos.
- [ ] Investigar cada assinatura não recuperada, métodos não emitidos, aliases e erros de tipos.
- [ ] Recuperar enums/defaults Wire e validar projeto sintético com arquivos gerados legitimamente.
- [ ] Investigar os sete NEF de magic `13 00 ff ee` e a cauda do oitavo.
- [ ] Interpretar modelo TFLite, estrutura VAD e `dic` com parsers apropriados.
- [ ] Prover Android ARM64/API ≥ 26 dedicado e executar cenários sem contornar mecanismos de acesso.
- [ ] Revisar obrigações de fonte correspondente/licenças antes de redistribuições adicionais das ferramentas e do aplicativo.

## Próxima tarefa executável

Priorizar revisão estática por assinatura e preparação das ferramentas modernas,
sem repetir a mesma aquisição bloqueada. Para cumprir a modalidade dinâmica será
necessário disponibilizar dispositivo/emulador Android dedicado compatível e
obter ADB/SDK oficiais; a ausência atual não é tratada como trabalho concluído.

- 2026-10-22: análise estática, bytecode, ELF, assinaturas v2 e formatos efetivamente executados; testes offline e repetibilidade verificados. Ver `docs/analise/RELATORIO.md`. Correção de retomada do JADX testada com fontes ausentes e índices preexistentes. Dinâmica continua bloqueada; PR será entregue em rascunho.

- 2026-10-22: criado o único PR em rascunho da missão: https://github.com/havaianasdestruido/ChurrazMaster/pull/3 . Sem merge; fases e lacunas permanecem abertas conforme evidências.
