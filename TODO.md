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
- [ ] Criar "bin/tools/MANIFESTO.md".
- [ ] Registrar versões, licenças, origens e hashes.
- [ ] Criar instruções reproduzíveis para configurar ferramentas não redistribuíveis.
- [ ] Validar que outro agente consegue identificar e preparar o ambiente.

## Fase 2 — Inventário e extração integral do XAPK

- [x] Identificar todos os componentes do pacote.
- [x] Extrair o conteúdo sem alterar os arquivos de origem.
- [x] Identificar APK principal e APKs divididos.
- [x] Inventariar arquivos DEX, bibliotecas nativas, recursos, assets e configurações.
- [ ] Identificar certificados, manifesto e informações de assinatura.
- [ ] Identificar componentes ausentes, duplicados ou inconsistentes.
- [x] Registrar hashes e relações entre os arquivos.
- [ ] Produzir um relatório de cobertura da extração.

## Fase 3 — Análise estática do Android

- [ ] Decodificar os manifestos e recursos XML.
- [ ] Recuperar tabelas de recursos e strings.
- [ ] Decompilar todos os arquivos DEX identificados.
- [ ] Examinar classes, métodos, campos, pacotes e referências.
- [ ] Identificar bibliotecas, SDKs e dependências.
- [ ] Mapear permissões, atividades, serviços, receptores e provedores.
- [ ] Identificar reflexão, carregamento dinâmico e código ofuscado.
- [ ] Registrar erros de descompilação por arquivo e por classe.
- [ ] Investigar todos os componentes não processados com sucesso.

## Fase 4 — Bibliotecas nativas e código de baixo nível

- [ ] Inventariar todos os arquivos ELF e bibliotecas nativas.
- [ ] Identificar ABI, arquitetura, símbolos, imports e exports.
- [ ] Identificar dependências JNI e pontos de entrada nativos.
- [ ] Examinar bibliotecas sem símbolos de depuração.
- [ ] Usar Ghidra ou outro descompilador compatível quando necessário.
- [ ] Recuperar pseudocódigo, funções, referências e estruturas possíveis.
- [ ] Identificar bibliotecas empacotadas, criptografadas ou carregadas dinamicamente.
- [ ] Documentar funções e estruturas ainda não recuperáveis.
- [ ] Relacionar os achados nativos às classes Java/Kotlin.

## Fase 5 — Formatos proprietários e recursos especiais

- [ ] Identificar formatos de projeto e arquivos internos do KineMaster.
- [ ] Investigar bancos de dados, configurações, templates e recursos de mídia.
- [ ] Identificar compactação, serialização e possíveis formatos proprietários.
- [ ] Investigar carregadores de módulos e recursos baixados em execução.
- [ ] Recuperar estruturas e metadados quando houver evidências suficientes.
- [ ] Documentar formatos desconhecidos com amostras, hashes e hipóteses testáveis.
- [ ] Não inventar especificações para formatos não compreendidos.

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
- [ ] Documentar comportamentos não reproduzidos e condições de teste.

## Fase 7 — Reconstrução e validação

- [ ] Organizar o código decompilado em uma estrutura compreensível.
- [ ] Preservar os nomes e os metadados originais recuperáveis.
- [ ] Verificar referências cruzadas e dependências entre módulos.
- [ ] Comparar a reconstrução com os artefatos originais.
- [ ] Repetir extrações e descompilações para validar a reprodutibilidade.
- [ ] Testar a compilação quando tecnicamente possível.
- [ ] Separar código recompilável de pseudocódigo e aproximações.
- [ ] Registrar diferenças, perdas e limitações irrecuperáveis.

## Fase 8 — Cobertura, lacunas e auditoria

- [ ] Consolidar a cobertura por APK, DEX, classe, método, biblioteca e recurso.
- [ ] Verificar arquivos ignorados, falhas silenciosas e saídas vazias.
- [ ] Revisar todos os itens pendentes das fases anteriores.
- [ ] Produzir um relatório final de evidências.
- [ ] Classificar as limitações por causa e impacto.
- [ ] Atualizar o "TODO.md" com os resultados reais.
- [ ] Definir explicitamente o que foi recuperado, parcialmente recuperado ou permanece desconhecido.

## Fase 9 — Entrega e documentação

- [ ] Documentar a estrutura final do projeto.
- [ ] Documentar como repetir cada etapa.
- [ ] Incluir comandos reproduzíveis e versões das ferramentas.
- [ ] Garantir que todos os documentos criados estejam em português brasileiro.
- [ ] Revisar o estado do Git e verificar arquivos ignorados indevidamente.
- [ ] Preparar um pull request com evidências, limitações e resultados.
- [ ] Não declarar conclusão total enquanto houver lacunas relevantes sem justificativa.

## Registro incremental

- 2026-10-22: repositório inicialmente limpo; quatro volumes `.7z.001` a `.004` em `bin/`, sem XAPK diretamente visível. Não assumir identidade pelo nome externo. Skill oficial versão 44 lida a partir do clone oficial; registrar proveniência e limitações na fase 1.

- 2026-10-22: extração validada (20 APKs, 7 DEX e 22 bibliotecas); ferramentas oficiais de release bloqueadas por redirecionamento de rede. Alternativas locais efetivamente executadas; decompilação, bytecode e ELF em andamento. Evidência: `docs/analise/00-integridade.md` e inventários.
