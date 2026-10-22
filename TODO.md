# TODO — Missão de engenharia reversa do KineMaster

Plano de execução persistente. Marque `[x]` somente após verificar o resultado.
Estado atual e evidências: ver `docs/analise/` (a ser criado).

Legenda de situação: `[ ]` pendente · `[x]` concluído e verificado · `[~]` parcial (ver nota)

## Achados iniciais (Fase 0, parcial)

- A entrada em `bin/` **não é um XAPK único**: são 4 volumes de um arquivo **7z multivolume**
  (`KineMaster+-+AI+Video+Editor_8.2.14.37398.GP_APKPure.7z.001` a `.004`, ~96 MB no total).
  O conteúdo interno (XAPK/APK) ainda não foi verificado.
- `bin/readme.txt` diz apenas "binários do KineMaster".
- Não existe `.gitignore` no repositório.
- REA/MCP: a skill `reverse-engineer-anything` (versão de metadados `44`, commit `5375ff0f` em `morluto/rea`)
  foi lida, mas **nenhuma ferramenta REA/MCP aparece na sessão atual**.
- `raw.githubusercontent.com` não está na lista de hosts permitidos; a skill foi obtida via `git clone` sparse de `github.com`.

## Dependências entre fases

Fase 0 → Fase 1 → Fase 2 → (Fase 3 ∥ Fase 4 ∥ Fase 5) → Fase 6 → Fase 7 → Fase 8 → Fase 9

---

## Fase 0 — Preparação e integridade

- [~] Identificar o XAPK de origem em `bin/`. *(Parcial: é um 7z multivolume; conteúdo ainda não verificado.)*
- [x] Registrar SHA-256, tamanho e metadados disponíveis dos 4 volumes e do `readme.txt`.
- [ ] Inspecionar a estrutura atual do repositório. *(Feito parcialmente: sem `.gitignore`, sem `scripts/`, sem `docs/`.)*
- [ ] Definir os diretórios de saída sem sobrescrever os arquivos originais.
- [ ] Registrar as restrições de licenciamento e redistribuição.
- [ ] Definir critérios objetivos de conclusão.

## Fase 1 — Ferramentas e ambiente reproduzível

- [x] Ler e aplicar a skill Reverse Engineer Anything. *(Lida; ver nota de versão acima.)*
- [ ] Verificar a disponibilidade real dos recursos REA/MCP. *(Nenhuma ferramenta REA na sessão; registrar.)*
- [ ] Inventariar as ferramentas necessárias.
- [ ] Baixar e organizar as ferramentas redistribuíveis em `bin/tools/`.
- [ ] Corrigir regras inadequadas do `.gitignore` (criar um `.gitignore` mínimo, sem excluir `bin/tools/`).
- [ ] Criar `bin/tools/MANIFESTO.md`.
- [ ] Registrar versões, licenças, origens e hashes.
- [ ] Criar instruções reproduzíveis para configurar ferramentas não redistribuíveis.
- [ ] Validar que outro agente consegue identificar e preparar o ambiente.

## Fase 2 — Inventário e extração integral do XAPK

- [ ] Reconstituir o 7z a partir dos volumes (concatenação) fora do Git e validar a integridade.
- [ ] Listar e extrair o conteúdo sem alterar os arquivos de origem.
- [ ] Identificar o formato real do conteúdo (XAPK/APK/APKS) e o APK principal.
- [ ] Identificar APKs divididos (split APKs), se existirem.
- [ ] Inventariar arquivos DEX, bibliotecas nativas, recursos, assets e configurações.
- [ ] Identificar certificados, manifesto e informações de assinatura.
- [ ] Identificar componentes ausentes, duplicados ou inconsistentes.
- [ ] Registrar hashes e relações entre os arquivos.
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
- [ ] Atualizar o `TODO.md` com os resultados reais.
- [ ] Definir explicitamente o que foi recuperado, parcialmente recuperado ou permanece desconhecido.

## Fase 9 — Entrega e documentação

- [ ] Documentar a estrutura final do projeto.
- [ ] Documentar como repetir cada etapa.
- [ ] Incluir comandos reproduzíveis e versões das ferramentas.
- [ ] Garantir que todos os documentos criados estejam em português brasileiro.
- [ ] Revisar o estado do Git e verificar arquivos ignorados indevidamente.
- [ ] Preparar um pull request com evidências, limitações e resultados.
- [ ] Não declarar conclusão total enquanto houver lacunas relevantes sem justificativa.
