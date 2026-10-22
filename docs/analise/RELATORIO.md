# Relatório incremental de engenharia reversa do KineMaster

**Data: 22/10/2026. Estado: missão parcialmente executada, com lacunas relevantes.**
Este é o relatório da etapa atual, não declaração de decompilação completa.

[PR #3 em rascunho](https://github.com/havaianasdestruido/ChurrazMaster/pull/3),
sem merge automático.

## Resultado verificável

A entrada original são quatro volumes 7z em `bin/`, preservados e verificados
por SHA-256. O XAPK recuperado contém KineMaster `8.2.14.37398.GP`, pacote
`com.nexstreaming.app.kinemasterfree`, Android mínimo 26, alvo 36.

| Camada | Encontrado/denominador | Resultado observado | O que não significa |
|---|---:|---|---|
| XAPK | 1 em 4 volumes | CRC validado; SHA-256 do XAPK e volumes registrado | Autenticidade do editor |
| APK | 20 | 20 manifestos; todos os APKs declarados presentes | Aplicativo instalado |
| Assinatura v2 | 20 APKs | Digests, assinatura RSA e chave do certificado conferem; mutação em memória rejeitada | Validação integral v3/linhagem ou confiança no editor |
| XML adicional | 2.294 | 2.294 decodificados/validados, com hashes de saída | Semântica de todos os recursos compreendida |
| ARSC | 19 tabelas | Strings multilíngues e recursos públicos extraídos | Contagem/validação integral de todas as entradas |
| DEX | 7 | 7 indexados; checksums internos conferem | Java semanticamente correto |
| Classes | 52.903 definições; 52.894 nomes únicos | Todas indexadas e emitidas em smali | Todos os nomes originais anteriores à ofuscação |
| Métodos | 258.710 definidos; 243.263 com código | Totais smali conferem por DEX com Androguard | Todos decompilados ou recompiláveis |
| Java aproximado | 45.247 arquivos na passagem do APK | 6.873 stubs explícitos; 7.285 arquivos com marcadores | Cobertura de classes/métodos por porcentagem |
| ELF | 22 | Estruturas, dependências, símbolos e desmontagem linear; 698 lacunas | Decompilação completa de funções |
| JNI | 549 métodos `native` | 386 com símbolo exportado compatível; 163 não resolvidos | Binding efetivo em execução |
| ZIP interno | 2.421 entradas | 1.832 arquivos regulares com hash extraídos | Todos os formatos interpretados |
| Esquema Wire | 42 classes de mensagem com tags | 589 campos explícitos recuperados de anotações smali | `.proto`/contêiner de projeto original integral |
| NEF | 8 arquivos | 2 registros zlib em 1 arquivo expandidos; caudas desconhecidas | Especificação universal de NEF |
| Dinâmica | 90 componentes conhecidos no base | **0 observados; 0 instalações** | Aplicativo executado neste ambiente |

Não somar novamente o DEX de Audience Network isolado à passagem Java do APK:
o JADX também o percorreu ao abrir o APK. A passagem separada emitiu 3.539 arquivos
Java e 403 stubs, sobrepostos aos resultados do APK. Métodos sem marcadores não
são automaticamente considerados recuperados. O denominador de funções nativas
não foi estabelecido, assim como o de métodos Java semanticamente corretos.

## Achados e grau de certeza

1. **Confirmado por bytes e parsers:** pacote, versão, splits, ABI ARM64, DEX em
   assets, permissões/componentes, tabelas, símbolos e relações DT_NEEDED.
2. **Confirmado em escopo criptográfico delimitado:** autoconsistência v2 dos
   20 APKs; mesmo certificado, SHA-256
   `b2b90f2f5a3679186fb82a6fb8c07f5e6ab4b55ea01b5625b28a9b5549fc6988`.
   Não há veredito de autenticidade nem validação completa v3.
3. **Confirmado estaticamente:** `NexEditor` declara métodos JNI e inicializador
   com `System.loadLibrary("nexeditorsdk")`. Relações nominais Java/JNI estão indexadas.
4. **Inferência sustentada por instruções:** duas funções do contador DataStore
   realizam leitura/soma atômica de 32 bits. Pseudocódigo manual está no relatório
   nativo; não generalizar para todos os ELF.
5. **Confirmado por anotações:** tags Wire de mensagens de projeto. Compatibilidade
   do arquivo `.kine`, migrações e ciclo completo de persistência ainda desconhecidos.
6. **Hipóteses delimitadas:** cabeçalho `lmgg`/`silero-16k` sugere família de modelo
   GGML/Silero; caudas NEF e `dic` seguem sem especificação validada.
7. **Não observado:** carregamento de DEX/bibliotecas, edição, STT, exportação,
   login, rede e bancos de dados em execução. Nenhum achado estático é apresentado
   como comportamento efetivamente executado.

## Ferramentas e metodologia

A skill oficial Reverse Engineer Anything versão 44 foi lida, com referências
Android, conexão/recuperação e evidências; o conteúdo oficial está preservado com
proveniência. A orientação local em português não é um servidor MCP.

REA CLI 6.7.0 executou somente diagnóstico, identificando provedores ausentes e
nenhuma inscrição MCP. Nenhuma chamada REA de análise Android/nativa foi usada;
não há Evidence IDs inventados. A investigação usou p7zip 16.02, JADX 1.1.0,
baksmali/smali 2.3.4, Java runtime 21.0.8, Androguard 4.1.4/DAD, pyelftools 0.32,
Capstone 5.0.6 e dependências locais versionadas, com 44 arquivos de distribuição
registrados por hash. Ghidra/JADX moderno/JDK completo têm preparação futura
fixada por URL/hash oficial, mas não foram instalados nem executados.

Os releases GitHub redirecionam para host bloqueado. Isso motivou alternativas
por npm/PyPI e registro explícito de idade e limitações, não silêncio sobre falhas.
As ferramentas e licenças estão em `bin/tools/`; dados proprietários recuperados
e saídas enormes permanecem locais, regeneráveis e fora do Git.

## Testes e validações realmente executados

- Preparação offline numa `.venv` nova com wheels locais e `--no-index`.
- Dez testes de extração segura, hashes das ferramentas, JNI, NEF e tags Wire:
  **10 aprovados, 0 falhas**. `pip check` sem dependências quebradas e verificação
  de sintaxe de todos os scripts executada.
- Extração repetida: todos os 3.163 membros primários conferem em pai, nome,
  tamanho, CRC e SHA-256.
- Smali de 7/7 DEX: classes, métodos e corpos conferem com parser independente.
- Remontagem de `classes2.dex`: uma classe/um método; assinaturas e instruções
  simbólicas conferem, mas hash/tamanho binários diferem. **Não houve build do APK.**
- JADX repetido em `ProjectDatabase_Impl`: um arquivo emitido com mesmo hash
  nas duas execuções. Não prova determinismo de toda a decompilação.
- Segunda opinião DAD em dois métodos: texto não vazio, com erros de inferência
  de tipos ainda visíveis; não validado como Java compilável.
- Verificação v2 e teste negativo de mutação apenas em memória.
- Auditoria de falhas silenciosas do JADX: códigos zero convivem com erros,
  warnings e stubs; índices preservam diagnósticos por arquivo/assinatura.

## Lacunas e próxima sequência recomendada

1. Preparar JADX moderno e JDK completo com os hashes oficiais; comparar falhas
   por assinatura, revisar aliases, erros de tipos e métodos não emitidos.
2. Fazer análise nativa profunda com Ghidra, aproveitando depuração de
   `librenderscript-toolkit.so`, e resolver 163 métodos JNI sem export compatível.
3. Investigar carregadores, registro JNI dinâmico, cadeias de projeto/asset e
   cada uma das falhas catalogadas, não apenas marcar processamento como sucesso.
4. Expandir o esquema Wire com enums/defaults e validar projeto sintético;
   investigar NEF, modelos, templates e binários desconhecidos.
5. Fornecer Android ARM64/API ≥ 26 dedicado e autorizado; executar os cenários
   técnicos de `03-dinamica-e-lacunas.md`. O host atual não tem ADB, emulador,
   QEMU, KVM nem USB disponível. A modalidade dinâmica permanece **não cumprida**.
6. Validar reconstrução, recursos e assinaturas com ferramentas independentes,
   sem contornar autenticação/DRM/licenças, e atualizar o único PR em rascunho.

## Navegação e rastreabilidade

- [Integridade e licenciamento](00-integridade.md)
- [Análise Android, falhas e assinaturas](01-android.md)
- [Nativo, formatos e esquema parcial](02-nativo-e-formatos.md)
- [Dinâmica e classificação de limitações](03-dinamica-e-lacunas.md)
- [Comandos reproduzíveis](REPRODUZIR.md)
- [Cobertura legível por máquina](inventarios/cobertura.json)
- [Plano persistente](../../TODO.md)

Os relatórios distinguem inventário, bytecode, aproximações, inferências e
observação de execução. **Nenhuma conclusão integral da missão foi declarada.**
