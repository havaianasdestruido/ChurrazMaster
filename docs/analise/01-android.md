# Android: código, recursos, dependências e assinaturas

## Escopo e evidências

Fontes primárias: `inventarios/android.json`, `dex/*.json`, índices compactados
por classe/método/chamada, `smali.json`, `assinaturas-v2.json` e `cobertura.json`.
As evidências são de ferramentas locais; não são Evidence IDs do REA.

### Estrutura e declarações

- 20/20 manifestos decodificados; todos os APKs têm código de versão `37398`.
- Os 20 APKs declarados pelo XAPK estão presentes; nenhum APK adicional foi encontrado.
- `total_size` do XAPK: 179.968.781 bytes, exatamente a soma dos APKs.
- 2.294/2.294 XML adicionais decodificados ou validados, sem falhas reportadas.
- 19 tabelas ARSC encontradas/processadas; `config.arm64_v8a.apk` não contém ARSC,
  o que é compatível com seu conteúdo nativo. Strings multilíngues e recursos
  públicos foram extraídos; não se estabeleceu denominador de cada entrada ARSC.
- 32 permissões declaradas no APK base e 90 componentes: 55 atividades,
  19 serviços, 8 receptores e 8 provedores.
- Atividade MAIN: `com.kinemaster.app.screen.splash.SplashActivity`, com
  `android:exported="true"`. Entre os 90 componentes, 11 declaram `true`, 53
  `false` e 26 não declaram o atributo. Ausência não equivale a exportação efetiva:
  há defaults dependentes da API, intent-filters e regras de instalação.
- Permissões de mídia/áudio, rede, notificações e serviços são observações do
  manifesto, não prova de acesso efetivo ou concessão pelo usuário.

### Bytecode e nomes originais

Foram encontrados 7 DEX: `classes.dex` até `classes6.dex` na raiz do APK base e
`assets/audience_network/classes.dex`. Todos têm Adler-32 e SHA-1 internos
consistentes. Há **52.903 ocorrências de definições de classe**, **52.894 nomes
únicos**, **258.710 métodos definidos** e **243.263 métodos com código**.
Não somar `method_ids_size` como métodos implementados: essa tabela inclui referências.

Nove nomes de classe se repetem entre DEX, incluindo `kotlin.Metadata`, anotações
`javax.annotation`, duas anotações Error Prone e `com.google.common.base.Strings`.
A lista completa está em `correlacao.json`. O fato da repetição não demonstra
conflito em execução; o DEX em assets pode usar outro carregador, ainda não observado.

A desmontagem baksmali 2.3.4 produziu 52.903 classes e 258.710 métodos, conferindo
as contagens de corpos por DEX com o parser independente Androguard. Isso é
recuperação de **bytecode legível**, não fonte Java/Kotlin original nem validação
semântica de todos os corpos. Nomes, descritores, offsets e relações estão nos
índices compactados. O inventário não chama classes vazias de lógica recuperada.

## Decompilação: falhas reais e passagem corretiva

A primeira passagem usou JADX 1.1.0 por DEX, preservando nomes com
`--rename-flags none`. Cinco DEX principais registraram falha de `RenameVisitor`
com `NoSuchElementException`; houve também tipos e hierarquias desconhecidos.
O processo mesmo assim retornou zero. Esses resultados **não foram descartados
nem classificados como sucesso integral**. Logs brutos são locais; índices
preservam hashes dos logs e diagnósticos compactados, com contagens de repetição.

A alternativa testada usou o APK base completo e flags de nomes padrão (`all`),
preservando smali e descritores originais como autoridade. A falha de inicialização
do `RenameVisitor` deixou de ocorrer no ensaio e na passagem agrupada; falhas
por método persistiram. JADX pode gerar aliases e comentários `renamed from`:
esses aliases não são nomes originais recuperados.

A passagem agrupada gerou:

| Entrada | Arquivos Java | Arquivos com marcadores | Stubs explícitos de método |
|---|---:|---:|---:|
| APK base completo | 45.247 | 7.285 | 6.873 |
| DEX de Audience Network isolado | 3.539 | 422 | 403 |

**Não somar essas linhas como cobertura única**: o JADX também percorreu o DEX
em assets ao abrir o APK, comprovado pelos diagnósticos que citam
`assets/audience_network/classes.dex` e pelas classes emitidas. A segunda linha
é um ensaio separado e sobreposto. Arquivos Java não equivalem a classes DEX,
pois classes internas podem compartilhar arquivos, ser fundidas ou renomeadas.

Na passagem do APK, há 43.791 marcadores `JADX WARNING`, 95 `JADX ERROR` e
5.616 ocorrências de `Code decompiled incorrectly`. Os 6.873 stubs distintos
contêm `throw new UnsupportedOperationException("Method not decompiled: ...")`.
O índice `*.metodos-nao-recuperados.jsonl.gz` inclui arquivo, linha e assinatura
reportada. A ausência desses marcadores não prova correção semântica.
Não há denominador confiável de métodos Java semanticamente recuperados.

### Segunda opinião delimitada

O DAD do Androguard produziu texto para dois métodos: `KineMasterApplication.c()V`
e `ProjectDatabase_Impl.clearAllTables()V` (`segunda-opiniao.json`). O primeiro
é stub no JADX. A saída DAD de `c()` sugere inicialização do editor, `createProject`
e configuração de propriedades, mas contém inferência de tipos claramente
problemática: variáveis com tipo `EditorInitException` usadas como editor.
Portanto é **aproximação estrutural**, não código compilável validado.
O texto não substitui smali nem comprova inicialização em execução.

## Assinaturas: presença, autoconsistência e confiança são diferentes

O levantamento inicial identificou v2/v3, ausência de v1/v3.1 e o mesmo
certificado em todos os APKs. SHA-256 do certificado:
`b2b90f2f5a3679186fb82a6fb8c07f5e6ab4b55ea01b5625b28a9b5549fc6988`.

Depois, `verificar_assinaturas.py` conferiu **20/20 APKs no esquema v2**:

1. Estrutura do bloco de assinatura e EOCD;
2. Digests de conteúdo em blocos de até 1 MiB e composição final SHA-256;
3. Chave pública do certificado igual à chave do signatário;
4. Assinatura RSA PKCS#1 v1.5/SHA-256 (`0x0103`) sobre os dados assinados.

Foi observada chave RSA de 1.024 bits. Uma mutação de um byte, somente em memória,
foi rejeitada por divergência do digest; os originais permanecem intactos.
Isso demonstra **autoconsistência v2**, não vínculo confiável com o editor oficial.
V3, linhagem de rotação, source stamp, política Android e autenticidade do
certificado não foram validados integralmente. O parser usa API interna da versão
fixada do Androguard; não se apresenta como substituto completo do `apksigner`.

## Dependências, reflexão e carregamento

`dependencias-aplicativo.json` registra namespaces e versões declaradas nos
`.properties` empacotados, não versões deduzidas apenas de nomes. Exemplos:
Billing `9.1.0`, app-update `2.1.0`, asset-delivery `2.3.0`, Firebase Analytics
`23.2.0` e Firebase Auth `24.1.0`, conforme metadados dos respectivos arquivos.
Essas declarações não foram comparadas com upstream byte a byte.

Classes de Kotlin/corrotinas, AndroidX/Room, Square Wire/Protobuf, Facebook Ads,
Google Play/Firebase e outras famílias são indexadas. Os índices de chamadas
registram referências concretas a reflexão, `Class.forName`, carregadores Dalvik,
`System/Runtime.load*`, SQLite e ao `NexEditor` atual. Há código ofuscado em
classes sem pacote e identificadores curtos, além de metadados R8 e Kotlin;
não foram recuperados nomes perdidos sem mapas originais.

A declaração de um carregador não demonstra qual DEX foi carregado em runtime.
O banco Room de projetos referencia `project_table`; `e17` contém uma instrução
CREATE TABLE com chave `uuid` e campos de arquivo, título, proporção, tempos,
duração, tamanho e thumbnail. É esquema estático; não foi aberto banco de usuário.

## Lacunas prioritárias

Atualizar JADX quando a rede permitir; tratar as 6.873 assinaturas reportadas,
revisando smali/DAD e referências; conferir aliases e colisões por classe;
recuperar chamadas indiretas e fontes auxiliares; comparar recursos com parser
independente; validar v3 com ferramenta oficial; observar carregadores e fluxos
Android reais. Não há recompilação funcional do aplicativo.
