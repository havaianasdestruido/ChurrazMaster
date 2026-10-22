# TODO — Missão de engenharia reversa do KineMaster

Plano de execução persistente. Marque `[x]` somente após verificar o resultado com evidência.
Estado atual e evidências: `docs/analise/relatorio-parcial.md` (índice em `docs/analise/README.md`).

Legenda: `[ ]` pendente · `[x]` concluído e verificado · `[~]` parcial (ver nota) · `[!]` bloqueado (ver nota)

## Situação geral

- **Missão não concluída.** Fases 0 a 5 em andamento (parciais); Fase 6 bloqueada; Fases 7 a 9 pendentes.
- Lacunas relevantes listadas em `docs/analise/relatorio-parcial.md`, seção 7.
- REA/MCP: a skill `reverse-engineer-anything` (metadados `version: "44"`, commit `5375ff0f` em `morluto/rea`) foi lida e copiada. **Nenhuma ferramenta REA/MCP foi registrada nesta sessão**; nenhuma chamada REA foi executada.
- Entrada em `bin/`: 4 volumes de um **7z multivolume** (não um XAPK direto), não modificada.

## Dependências entre fases

Fase 0 → Fase 1 → Fase 2 → (Fase 3 ∥ Fase 4 ∥ Fase 5) → Fase 6 → Fase 7 → Fase 8 → Fase 9

---

## Fase 0 — Preparação e integridade

- [x] Identificar o XAPK de origem em `bin/`. *(É um 7z multivolume; o XAPK está dentro dele — ver Fase 2.)*
- [x] Registrar SHA-256 e tamanho dos 4 volumes e do `readme.txt` (`docs/analise/inventarios/entrada-sha256.txt`).
- [x] Inspecionar a estrutura do repositório. *(Sem `.gitignore` no início; corrigido na Fase 1.)*
- [x] Definir diretórios de saída sem sobrescrever originais. *(Versionado: `docs/`, `scripts/`, `bin/tools/`. Não versionado: `/home/user/kinemaster-work/`.)*
- [x] Registrar restrições de licenciamento e redistribuição (`docs/analise/ferramentas-e-restricoes.md` §5).
- [x] Definir critérios objetivos de conclusão. *(Critérios: cobertura de classes com denominador explícito; zero falha sem causa registrada; todo item `[ ]` tratado ou justificado.)*

## Fase 1 — Ferramentas e ambiente reproduzível

- [x] Ler e aplicar a skill Reverse Engineer Anything. *(Lida; versão registrada.)*
- [x] Verificar a disponibilidade dos recursos REA/MCP. *(`doctor` executado com `rea-agents@6.7.0`, código 1, não saudável; ferramentas REA não registradas na sessão.)*
- [x] Inventariar as ferramentas necessárias. *(`docs/analise/ferramentas-e-restricoes.md`.)*
- [x] Baixar e organizar ferramentas redistribuíveis em `bin/tools/`. *(JADX 1.1.0 e Apktool 2.4.1.)*
- [x] Corrigir `.gitignore` sem excluir `bin/tools/`. *(Commit `94da8f9`.)*
- [x] Criar `bin/tools/MANIFESTO.md`.
- [x] Registrar versões, licenças, origens e hashes. *(`bin/tools/SHA256SUMS.txt`. Limitação: sem digest oficial nos assets do GitHub.)*
- [x] Criar instruções reproduzíveis para ferramentas não redistribuíveis. *(Ghidra, Temurin e Android SDK no `MANIFESTO.md`; `preparar_ambiente.sh` valida hashes.)*
- [ ] Validar que outro agente consegue identificar e preparar o ambiente. *(Não validado em sessão independente.)*

## Fase 2 — Inventário e extração integral do XAPK

- [x] Reconstituir o 7z a partir dos volumes e validar a integridade. *(`/home/user/kinemaster-work/volumes/`; assinatura 7z confirmada.)*
- [x] Listar e extrair o conteúdo sem alterar os originais. *(`scripts/extrair_xapk.py`; 180.092.744 B.)*
- [x] Identificar o formato do conteúdo. *(XAPK `xapk_version` 2; `manifest.json`.)*
- [x] Identificar APKs divididos. *(1 base + 19 splits `config.*`; nativo só em `arm64_v8a`.)*
- [x] Inventariar DEX, nativos, recursos, assets e configurações. *(`apks.md`, `nativo-arm64.md`, `ativos-base.md`, `kmassets-base.md`.)*
- [~] Identificar certificados, manifesto e assinatura. *(Feito para o APK base: v2/v3, `O=NexStreaming`. **Não verificado nos splits.**)*
- [~] Identificar componentes ausentes, duplicados ou inconsistentes. *(Inconsistências de DEX/JADX encontradas e registradas na Fase 3; não há análise de componentes entre splits.)*
- [x] Registrar hashes e relações entre os arquivos. *(`xapk-sha256.txt`, 22 arquivos.)*
- [x] Produzir relatório de cobertura da extração. *(`relatorio-parcial.md` §1–2.)*

## Fase 3 — Análise estática do Android

- [x] Decodificar manifestos e recursos XML. *(Apktool 2.4.1: `apktool-base.txt`, 2.263 XML, 0 erros de log.)*
- [~] Recuperar tabelas de recursos e strings. *(`resources.arsc` decodificado pelo Apktool; **não auditado por tipo de recurso**.)*
- [~] Decompilar todos os DEX identificados. *(Smali: 52.903 de 52.903 `class_defs`. JADX: 45.235 `.java` de 41.297 classes de nível superior; 1 classe com falha: `FontBrowserViewModel`.)*
- [~] Examinar classes, métodos, campos, pacotes e referências. *(Cobertura: 258.710 métodos declarados, 243.263 com corpo. Referências cruzadas não analisadas.)*
- [ ] Identificar bibliotecas, SDKs e dependências. *(Não feito de forma sistemática; apenas `com.google`, `firebase`, `audience_network` observados.)*
- [x] Mapear permissões, atividades, serviços, receptores e provedores. *(`manifesto-base.md`: 32 permissões, 55 atividades, 19 serviços, 8 receptores, 8 provedores.)*
- [~] Identificar reflexão, carregamento dinâmico e código ofuscado. *(Carregamentos `loadLibrary`: 11 literais; com variável: não analisados. Ofuscação não caracterizada.)*
- [x] Registrar erros de descompilação por arquivo e por classe. *(`jadx-base-erros.md`: 124 nós; `jadx-base-showbad-erros.md`: 200 nós.)*
- [ ] Investigar todos os componentes não processados com sucesso. *(Pendente: 29 classes e 95 métodos com falha; causas não investigadas individualmente.)*
- [~] Explicar as diferenças entre as passagens do JADX. *(`show-bad-code` perde 40 classes que a passagem padrão escreveu; causa registrada: `Error saving class`. Não usar `show-bad` como substituto.)*

## Fase 4 — Bibliotecas nativas e código de baixo nível

- [x] Inventariar todos os arquivos ELF e bibliotecas nativas. *(22 `.so` em `arm64_v8a`.)*
- [x] Identificar ABI, arquitetura, símbolos, imports e exports. *(`nativo-arm64.md`.)*
- [x] Identificar dependências JNI e pontos de entrada. *(`jni-relacao.md`: 386 de 549 métodos `native` verificados por símbolo JNI.)*
- [~] Examinar bibliotecas sem símbolos de depuração. *(Só `librenderscript-toolkit.so` tem `.symtab`; 8 sem `JNI_OnLoad`.)*
- [!] Usar Ghidra ou descompilador equivalente. *(Ghidra indisponível: download bloqueado. Instruções em `bin/tools/MANIFESTO.md`.)*
- [ ] Avaliar angr/capstone para bibliotecas pequenas (ex.: `libbuffer_pgl.so`).
- [ ] Recuperar pseudocódigo, funções, referências e estruturas.
- [~] Investigar os métodos `native` sem símbolo. *(7 sem nome em nenhuma `.so`; 156 com nome só como string. `RegisterNatives` não é importado por nenhuma `.so`. Hipótese: registro via tabela JNIEnv — não verificada.)*
- [ ] Identificar bibliotecas empacotadas, criptografadas ou carregadas dinamicamente.
- [ ] Documentar funções e estruturas não recuperáveis.
- [~] Relacionar achados nativos às classes Java/Kotlin. *(Feito por símbolo JNI; a relação por fluxo de dados não.)*

## Fase 5 — Formatos proprietários e recursos especiais

- [~] Identificar formatos de projeto e arquivos internos do KineMaster. *(Parcial: `assets/kmassets.zip` (1.832 entradas), `kedl.xml`, `_info.json`, `audio_effect/*.json`. Formato de projeto de usuário não identificado no APK.)*
- [~] Investigar bancos de dados, configurações, templates e recursos de mídia. *(Nenhum SQLite identificado em `assets/`, `res/` ou raiz pela assinatura; mídia e templates catalogados; não interpretados.)*
- [ ] Identificar compactação, serialização e possíveis formatos proprietários. *(Parcial: `kmVAD_v512_fp16.bin` e `assets/dic` sem assinatura reconhecida.)*
- [ ] Investigar carregadores de módulos e recursos baixados em execução.
- [ ] Recuperar estruturas e metadados quando houver evidências suficientes.
- [~] Documentar formatos desconhecidos com amostras, hashes e hipóteses. *(Hashes e hipóteses em `relatorio-parcial.md` §4.)*
- [x] Não inventar especificações para formatos não compreendidos. *(Verificado na escrita do relatório.)*

## Fase 6 — Análise dinâmica autorizada

- [!] Preparar emulador ou dispositivo de teste compatível. *(Bloqueado: sem `/dev/kvm`, `adb` ou `emulator`; 2 CPUs, ~3 GB RAM; `dl.google.com` inacessível.)*
- [ ] Documentar versão do Android, arquitetura e configuração.
- [ ] Instalar o aplicativo de forma controlada.
- [ ] Capturar inicialização, logs e falhas.
- [ ] Observar carregamento de classes e bibliotecas.
- [ ] Examinar comportamento de componentes e fluxos.
- [ ] Correlacionar runtime com resultados estáticos.
- [ ] Registrar dependências de rede, armazenamento e serviços externos.
- [ ] Investigar código carregado apenas em execução.
- [ ] Documentar comportamentos não reproduzidos e condições de teste.

## Fase 7 — Reconstrução e validação

- [ ] Organizar o código decompilado em estrutura compreensível.
- [ ] Preservar nomes e metadados originais recuperáveis.
- [ ] Verificar referências cruzadas e dependências entre módulos.
- [ ] Comparar a reconstrução com os artefatos originais.
- [ ] Repetir extrações e descompilações para validar reprodutibilidade. *(Apktool reexecutado pelo script: 52.903 smali. Comparação byte a byte com a execução anterior não feita.)*
- [ ] Testar a compilação quando tecnicamente possível.
- [ ] Separar código recompilável de pseudocódigo e aproximações.
- [ ] Registrar diferenças, perdas e limitações irrecuperáveis.

## Fase 8 — Cobertura, lacunas e auditoria

- [~] Consolidar cobertura por APK, DEX, classe, método, biblioteca e recurso. *(Feito para DEX/classe/método do APK base; não por biblioteca nativa nem por recurso.)*
- [~] Verificar arquivos ignorados, falhas silenciosas e saídas vazias. *(Falha silenciosa encontrada e corrigida: 12 `Error saving class` fora do resumo; 0 arquivos `.java` vazios.)*
- [ ] Revisar todos os itens pendentes das fases anteriores.
- [ ] Produzir relatório final de evidências.
- [ ] Classificar limitações por causa e impacto. *(Feito parcialmente em `relatorio-parcial.md` §7.)*
- [~] Atualizar o `TODO.md` com resultados reais. *(Esta revisão; será atualizada a cada etapa.)*
- [ ] Definir explicitamente o que foi recuperado, parcialmente recuperado ou permanece desconhecido.

## Fase 9 — Entrega e documentação

- [~] Documentar a estrutura final do projeto. *(`docs/analise/README.md`; falta seção final.)*
- [~] Documentar como repetir cada etapa. *(`relatorio-parcial.md` §8.)*
- [ ] Incluir comandos reproduzíveis e versões das ferramentas em todos os scripts.
- [~] Garantir que todos os documentos estejam em português brasileiro. *(Revisão pendente de todos os arquivos.)*
- [ ] Revisar o estado do Git e arquivos ignorados indevidamente.
- [~] Preparar um pull request com evidências, limitações e resultados. *(PR #2 aberto, sem merge; será atualizado conforme o trabalho avança.)*
- [ ] Não declarar conclusão total enquanto houver lacunas relevantes sem justificativa.
