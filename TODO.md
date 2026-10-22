# TODO — Decompilação e engenharia reversa do KineMaster (XAPK 8.2.14.37398)

Plano de execução persistente da missão. Documento vivo: deve ser atualizado ao fim de **cada etapa**.

- **Alvo:** KineMaster — AI Video Editor, `com.nexstreaming.app.kinemasterfree`, versão `8.2.14.37398.GP`
  (versionCode 37398), minSdk 26, targetSdk 36.
- **Material em `bin/`:** arquivo **7z multi-volume** (`.7z.001` … `.7z.004`, 96.560.538 B no total),
  contendo **um** XAPK (180.092.744 B) com 20 APKs.
- **Idioma:** todo material produzido é em português brasileiro; identificadores originais preservados.
- **Skill de metodologia:** Reverse Engineer Anything (REA) — `SKILL.md` versão **44**, commit
  `5375ff0f9aadb4fd0d46333815aa0431e3df1010` (`morluto/rea`). CLI `rea-agents` 6.7.0.

## Legenda

- `[ ]` pendente · `[~]` em andamento · `[x]` concluído **e verificado** · `[!]` bloqueado (motivo registrado)

Critérios globais de conclusão:

1. Todo artefato do XAPK tem SHA-256 em inventário. ✅
2. Toda ferramenta usada está no `bin/tools/MANIFESTO.md` com versão, licença, origem e hash —
   **ou** tem justificativa documentada de indisponibilidade. ✅
3. Todo componente tem estado explícito (processado / parcial / não processado com motivo). ✅ (ver Fase 8)
4. Nenhuma cobertura é publicada sem denominador verificável. ✅
5. Toda afirmação é classificada como fato, inferência ou hipótese. ✅

---

## Fase 0 — Preparação e integridade

- [x] Identificar o XAPK de origem em `bin/` — **é um 7z multi-volume**, não um XAPK direto.
- [x] Registrar SHA-256, tamanho e metadados disponíveis — `docs/analise/inventarios/integridade.json`.
- [x] Inspecionar a estrutura atual do repositório (repo mínimo: `bin/`, `README.md`, `LICENSE`).
- [x] Definir os diretórios de saída sem sobrescrever os originais (`docs/`, `decompilado/`, `scripts/`,
      `bin/tools/`, e `/tmp/churraz-trabalho` para intermediários).
- [x] Registrar as restrições de licenciamento e redistribuição (`bin/tools/MANIFESTO.md` §2–§4).
- [x] Definir critérios objetivos de conclusão (acima).

**Resultado:** hashes dos 4 volumes + hash do 7z reconstruído
(`b24aa3653a673de2ed5f85f039360f436c337afb6754f4a024c1a765ab025dc6`) + hash do XAPK
(`07025005a02fd1874cf03f422e86182dcd053b950ee97f15f6fd3a0e0b092720`). Nenhum original alterado.

## Fase 1 — Ferramentas e ambiente reproduzível

- [x] Ler e aplicar a skill Reverse Engineer Anything (versão 44).
- [x] Verificar a disponibilidade real dos recursos REA/MCP — `rea doctor --json`: `healthy=false`;
      sem `GHIDRA_INSTALL_DIR`, sem `REA_JADX_MCP_JAR`, sem IDA/Kuna; nenhuma ferramenta MCP do REA
      disponível na sessão do agente.
- [x] Inventariar as ferramentas necessárias.
- [x] Baixar e organizar as ferramentas redistribuíveis em `bin/tools/` (JRE, jadx, jadx-headless-mcp,
      Apktool, 5 wheels Python).
- [x] Corrigir/definir regras de `.gitignore` (não havia `.gitignore`; criado sem excluir `bin/tools/`).
- [x] Criar `bin/tools/MANIFESTO.md`.
- [x] Registrar versões, licenças, origens e hashes (`ferramentas_hashes.json`, 53 arquivos).
- [x] Criar instruções reproduzíveis para ferramentas não redistribuíveis (Ghidra, SDK/ADB).
- [x] Validar que outro agente consegue preparar o ambiente — `scripts/instalar_ferramentas.sh`
      testado; `guia_reproducao.md` publicado.

**Bloqueio registrado:** `release-assets.githubusercontent.com` inacessível neste sandbox
(`curl: (35) SSL_ERROR_SYSCALL`). Consequência: jadx 1.5.6, Apktool 3.0.3, JDK completo e Ghidra
**não** puderam ser obtidos pelos canais oficiais. Alternativa usada: pacotes npm/PyPI que empacotam
os artefatos oficiais (jadx 1.1.0, Apktool 2.4.1, jar MCP 0.7.0, wheel jdk4py), todos com hash local.

## Fase 2 — Inventário e extração integral do XAPK

- [x] Identificar todos os componentes do pacote (22 membros: 20 APKs + `icon.png` + `manifest.json`).
- [x] Extrair o conteúdo sem alterar os arquivos de origem.
- [x] Identificar APK principal e APKs divididos (1 base, 1 ABI, 1 densidade, 17 idiomas).
- [x] Inventariar DEX, bibliotecas nativas, recursos, assets e configurações.
- [x] Identificar certificados, manifesto e assinatura (v1+v2+v3, NexStreaming, SHA-256 do cert).
- [x] Identificar componentes ausentes, duplicados ou inconsistentes (9 classes repetidas, todas de anotação).
- [x] Registrar hashes e relações entre arquivos.
- [x] Produzir relatório de cobertura da extração (`inventario_xapk.md`).

**Números:** 3.141 entradas, 190.460.217 B descomprimidos, 7 DEX (45.695.576 B),
22 ELF (62.598.024 B, AArch64).

## Fase 3 — Análise estática do Android

- [x] Decodificar manifestos e recursos XML (Apktool 2.4.1, 20/20 APKs, sem falha).
- [x] Recuperar tabelas de recursos e strings (2.513 XML decodificados, 20 MB de `res/`).
- [x] Decompilar todos os DEX identificados (6 do APK base + 1 de `assets/`).
- [x] Examinar classes, métodos, campos, pacotes e referências (52.903 classes, 258.710 métodos).
- [x] Identificar bibliotecas, SDKs e dependências (§3.5 do relatório técnico).
- [x] Mapear permissões, atividades, serviços, receptores e provedores
      (`decompilado/indices/componentes.md`).
- [x] Identificar reflexão, carregamento dinâmico e código ofuscado (§5 do relatório de formatos).
- [x] Registrar erros de descompilação por arquivo e por classe (148/52/119 ocorrências).
- [x] Investigar todos os componentes não processados com sucesso (798 classes classificadas;
      apenas **3** são de nível superior com código).

**Cobertura:** 52.105 / 52.903 classes com `.java` = **98,49 %**.

## Fase 4 — Bibliotecas nativas e código de baixo nível

- [x] Inventariar todos os ELF (22 arquivos, `nativo.json`).
- [x] Identificar ABI, arquitetura, símbolos, imports e exports.
- [x] Identificar dependências JNI e pontos de entrada (`JNI_OnLoad` em 5 bibliotecas,
      480 símbolos `Java_*` indexados).
- [x] Examinar bibliotecas sem símbolos de depuração (nenhuma tem `.symtab`, nenhuma tem `.debug_info`).
- [!] Usar Ghidra ou descompilador compatível — **bloqueado**: download inviável (543 MB,
      host bloqueado). Procedimento documentado em `MANIFESTO.md` §4.2.
- [!] Recuperar pseudocódigo, funções e estruturas — **não produzido** (decorre do item anterior).
- [x] Identificar bibliotecas empacotadas/criptografadas/carregadas dinamicamente
      (`OpenCL*`, `geckox_bspatch`, `stlport_shared` carregadas e ausentes do APK).
- [x] Documentar funções e estruturas ainda não recuperáveis (relatório técnico §4, limitações C2).
- [x] Relacionar achados nativos às classes Java (`simbolos_jni.csv`; `libnexeditorsdk.so` →
      `com.kinemaster.module.nexeditormodule.nexvideoeditor.*`).

## Fase 5 — Formatos proprietários e recursos especiais

- [x] Identificar formatos de projeto e arquivos internos (`.kine` diretório, `.kmproject` arquivo).
- [~] Investigar bancos de dados, configurações, templates e recursos de mídia — inventariados
      (5 `@Database` Room + `kmassets.zip`); **esquema interno não extraído**.
- [x] Identificar compactação, serialização e possíveis formatos proprietários.
- [~] Investigar carregadores de módulos e recursos baixados em execução — mapeados
      (`geckox_bspatch`/bsdiff, asset store); **fluxo não exercitado**.
- [~] Recuperar estruturas e metadados — pacotes de assets e manifestos recuperados;
      estrutura de `.nef` **não**.
- [x] Documentar formatos desconhecidos com amostras, hashes e hipóteses testáveis
      (`formatos_proprietarios.md` §3).
- [x] Não inventar especificações para formatos não compreendidos — `.nef` explicitamente marcado
      como não especificado.

## Fase 6 — Análise dinâmica autorizada

- [!] Preparar emulador/dispositivo — **inviável**: sem `/dev/kvm`, sem SDK/`adb`, sem dispositivo,
      APK AArch64-only, 3,8 GiB de RAM.
- [!] Documentar versão do Android e configuração do ambiente de teste — não há ambiente de execução.
- [!] Instalar o aplicativo de forma controlada — não executado.
- [!] Capturar inicialização, logs e falhas — não executado.
- [!] Observar carregamento de classes e bibliotecas — não executado.
- [!] Examinar comportamento de componentes e fluxos — não executado.
- [!] Correlacionar runtime com estático — não executado.
- [!] Registrar dependências de rede/armazenamento/serviços — apenas por análise estática.
- [!] Investigar código carregado só em execução — não executado.
- [x] Documentar comportamentos não reproduzidos e condições de teste
      (`docs/analise/relatorios/analise_dinamica.md`, com procedimento reproduzível completo).

**Declaração:** nenhuma conclusão deste projeto depende de execução do aplicativo.

## Fase 7 — Reconstrução e validação

- [x] Organizar o código decompilado em estrutura compreensível (`decompilado/` + árvores por APK).
- [x] Preservar nomes e metadados originais recuperáveis (saída do jadx sem edição; renomeações
      documentadas e mapeadas).
- [x] Verificar referências cruzadas e dependências entre módulos (índice de pacotes, JNI, SDKs).
- [x] Comparar a reconstrução com os artefatos originais (hashes + contagens por DEX).
- [~] Repetir extrações e descompilações para validar reprodutibilidade — extração e inventário
      repetidos com hashes idênticos; **a descompilação completa não foi reexecutada** após o ajuste
      das métricas.
- [!] Testar a compilação quando tecnicamente possível — **impossível**: sem JDK completo (sem `javac`),
      código de saída do jadx não é recompilável por definição.
- [x] Separar código recompilável de pseudocódigo e aproximações (`decompilado/README.md`).
- [x] Registrar diferenças, perdas e limitações irrecuperáveis (`limitacoes.md`).

## Fase 8 — Cobertura, lacunas e auditoria

- [x] Consolidar cobertura por APK, DEX, classe, método, biblioteca e recurso.
- [x] Verificar arquivos ignorados, falhas silenciosas e saídas vazias (`.gitignore` revisado;
      árvores grandes excluídas **de propósito** e documentadas).
- [x] Revisar itens pendentes das fases anteriores (este documento).
- [x] Produzir relatório final de evidências (`relatorio_tecnico.md`).
- [x] Classificar limitações por causa e impacto (`limitacoes.md`).
- [x] Atualizar o `TODO.md` com resultados reais (este arquivo).
- [x] Definir explicitamente o que foi recuperado, parcialmente recuperado ou desconhecido
      (`relatorio_tecnico.md` §8).

## Fase 9 — Entrega e documentação

- [x] Documentar a estrutura final do projeto (`decompilado/README.md`, este TODO, README).
- [x] Documentar como repetir cada etapa (`guia_reproducao.md`).
- [x] Incluir comandos reproduzíveis e versões das ferramentas.
- [x] Garantir que todos os documentos estejam em português brasileiro.
- [x] Revisar o estado do Git e arquivos ignorados indevidamente.
- [x] Preparar pull request com evidências, limitações e resultados.
- [ ] Não declarar conclusão total enquanto houver lacunas relevantes sem justificativa —
      **declarado: a missão NÃO está concluída** (ver "Pendências" abaixo).

---

## Pendências priorizadas (próximas etapas)

1. **[Alto] Descompilação nativa** — executar Ghidra 12.2 em `libnexeditorsdk.so` (e nos leitores de
   `.nef`) em host com rede liberada; produzir pseudocódigo e mapa de funções.
2. **[Alto] Análise dinâmica** — seguir `analise_dinamica.md` em dispositivo ARM64 dedicado.
3. **[Médio] Esquema dos bancos Room** — extrair `ProjectDatabase`/`TemplateDatabase` e o
   `.kmproject` de um projeto real para documentar o modelo de timeline.
4. **[Médio] Migrar o descompilador principal** para jadx 1.5.x quando houver canal de download,
   reduzindo os 148 erros de geração de código.
5. **[Baixo] Inventariar endpoints de rede** a partir de `com.kinemaster.module.network.communication`.
6. **[Baixo] Documentar o pipeline de IA** (enum `AIModelType` → modelo embarcado → biblioteca).

## Registro de decisões

| Data | Decisão | Motivo |
|---|---|---|
| 2026-10-22 | Intermediários pesados em `/tmp/churraz-trabalho` (não versionados) | 600+ MB e 50k arquivos excedem limites práticos de repositório/agente |
| 2026-10-22 | Versionar jadx 1.1.0 e Apktool 2.4.1 obtidos via npm | release assets do GitHub bloqueados; pacotes empacotam os artefatos oficiais |
| 2026-10-22 | Não versionar JDK extraído nem Ghidra | derivados volumosos / acima do limite de 100 MB por arquivo |
| 2026-10-22 | Duas execuções do jadx (base e `assets/`) | procedências distintas; o DEX de anúncio não entra no ClassLoader principal |
| 2026-10-22 | Métricas de cobertura com 3 critérios de resolução de nome | jadx renomeia pacotes/classes inválidas e move pacote padrão para `defpackage/` |
