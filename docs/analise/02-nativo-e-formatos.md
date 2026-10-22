# Bibliotecas nativas e formatos internos

## ELF: análise estrutural e desmontagem, não decompilação integral

`inventarios/nativo/bibliotecas.json` e índices por biblioteca registram hashes,
seções, notas, símbolos, imports/exports, DT_NEEDED, relocação e entradas JNI.
Foram examinados **22/22 ELF**, todos AArch64/64 bits, em `config.arm64_v8a.apk`.
A rotina procura magic ELF em todos os arquivos extraídos dos APKs, não apenas
extensão `.so`. Camadas comprimidas adicionais foram examinadas separadamente
como assets; não se conclui ausência universal de código cifrado/baixado.

Capstone realizou desmontagem linear de seções executáveis, emitindo
9.083.268 instruções reconhecidas. **698 lacunas** foram registradas como dados
ou instruções não reconhecidas; não foram ocultadas. Uma varredura linear pode
interpretar dados intercalados como código. Esse total não é número de funções.
O denominador de funções nativas ainda não foi estabelecido.

Somente `librenderscript-toolkit.so` contém `.symtab` e `.debug_info` no exame.
As demais 21 não contêm essas seções, mas mantêm símbolos dinâmicos em diferentes
quantidades. Ausência de `.debug_info` não prova ofuscação, criptografia ou packer.

### Relações confirmadas e limites

- `libnexeditorsdk.so` declara dependências de ONNX Runtime, TensorFlow Lite CPU/GPU,
  GLESv3, EGL, Android, jnigraphics, zlib, log, dl, m e libc.
- As duas variantes `libkmSTTv4_armv80.so`/`armv82.so` dependem de
  `libonnxruntime.so`. Os nomes sugerem variantes de instrução para STT; a política
  real de seleção e execução não foi observada.
- `correlacao.json` diferencia dependências presentes no pacote e externas.
  `libc.so`, `libandroid.so`, EGL e afins não são marcadas como arquivos faltantes
  automaticamente: podem ser fornecidas pelo Android.
- Há 549 métodos `native` em DEX; 386 têm nome JNI compatível em exports e
  **163 não têm correspondência estática por nome**. O índice `jni.jsonl.gz`
  preserva assinatura, nomes JNI esperados, biblioteca/endereço compatíveis e estado.
  Correspondência nominal não prova binding real. `RegisterNatives`, `dlsym`,
  bibliotecas de plataforma e métodos não usados ainda precisam ser investigados.

`NexEditor` em `com.kinemaster.module.nexeditormodule.nexvideoeditor` declara
métodos nativos e carrega `nexeditorsdk` em inicializador estático. O código
Java aproximado também contém tentativas de carregar `OpenCL-pixel`, `OpenCL-car`
e `OpenCL`; essas bibliotecas não estão entre os 22 ELF empacotados. A hipótese
é de dependências opcionais do dispositivo, **não** componente comprovadamente
perdido. A confirmação exige examinar as condições/catches em smali e runtime.

### Leitura manual delimitada de funções

Biblioteca: `libdatastore_shared_counter.so`; consulte seu SHA-256 no inventário.
As funções exportadas abaixo foram correlacionadas a `androidx.datastore.core.NativeSharedCounter`
e examinadas em desmontagem, não apenas contadas.

| Símbolo original | Endereço virtual | Evidência e interpretação |
|---|---:|---|
| `_ZN9datastore15GetCounterValueEPNSt6__ndk16atomicIjEE` | `0xfc0` | `ldar w0, [x0]; ret`: leitura atômica de 32 bits com aquisição. |
| `_ZN9datastore27IncrementAndGetCounterValueEPNSt6__ndk16atomicIjEE` | `0xfc8` | Passa incremento 1 ao auxiliar `0xff0`; soma 1 ao valor anterior retornado. |
| Auxiliar em `0xff0` | `0xff0` | Seleção entre `ldaddal` e laço `ldaxr`/`stlxr`; compatível com soma atômica aquisição/liberação. |
| `_ZN9datastore12TruncateFileEi` | `0xf44` | Passa tamanho 4 a chamada resolvida pela PLT; retorno zero ou errno. |
| `_ZN9datastore19CreateSharedCounterEiPPv` | `0xf68` | Argumentos de mapeamento de 4 bytes, proteção 3 e flags `0x8001`; retorna ponteiro via parâmetro ou errno. |

A atribuição de nomes da PLT vem das relocação/símbolos, não de nomes inventados.
As propriedades de aquisição/liberação das instruções são sustentadas pelos
opcodes; a ordem C++ original não é identificável univocamente (por exemplo,
`seq_cst` pode gerar as mesmas instruções). A política de vida útil e a
sincronização completa do arquivo não foram validadas.

Pseudocódigo **manual e aproximado** dos dois acessores, não fonte original:

```c
// Representação conceitual C11; não foi compilada nem executada.
uint32_t ler_contador(_Atomic uint32_t *contador) {
    return atomic_load_explicit(contador, memory_order_acquire);
}
uint32_t incrementar_e_ler(_Atomic uint32_t *contador) {
    return atomic_fetch_add_explicit(contador, 1, memory_order_acq_rel) + 1;
}
```

Este exame é pequeno: **não** significa que todas as funções dessa biblioteca,
nem a lógica de edição/STT/ML dos outros ELF foram decompiladas.
Ghidra não foi executado: distribuição grande e downloads de releases bloqueados.
Versão, URL e hash oficial para preparação futura estão em `bin/tools/externas.json`.
O runtime Java reduzido não é JDK suficiente para Ghidra/REA Android.

## Camadas internas: kmassets.zip

`kmassets.zip` tem 2.421 entradas, incluindo diretórios. Foram extraídos e
calculados hashes de **1.832 arquivos regulares**, sem criptografia ZIP indicada.
`formatos/membros-internos.jsonl.gz` registra a relação com o ZIP pai.
A análise de formato inspecionou **1.991 arquivos de assets**, somando assets
externos e os membros recuperados; não somar novamente os membros para obter
um total fictício. Há 1.107 arquivos classificados como texto UTF-8, 696 PNG e
153 JPEG por assinatura, além dos demais formatos no resumo.

Observações confirmadas em amostras e parsers:

- `_info.json`: metadados de efeitos com `filename`, `id`, `label`, `ratios`,
  categorias e ícones; as chaves observadas são agregadas em `formatos/resumo.json`.
- `kedl.xml`: documentos de efeitos com texturas, valores animados, keyframes
  e parâmetros. Referências como `src="r2"` apontam, em amostra verificada, para
  bytes PNG, embora o membro não tenha extensão. Não se infere formato pela extensão.
- Há 28 `.lua` e 57 `.glsl` no ZIP inventariado. Foram preservados como texto,
  sem execução nem validação por compilador Lua/GLSL; o host não os executou.
- `DCI/dci_audio` começa com estrutura `ftyp` ISO Base Media File Format;
  o nome sem extensão não implica áudio proprietário ou cifrado.
- Modelo de segmentação tem identificador FlatBuffer `TFL3`. Isso não recupera
  automaticamente grafo, pesos ou contrato dos tensores; parser TFLite é pendente.
- `kmVAD_v512_fp16.bin` começa com `lmgg` e contém `silero-16k` no cabeçalho.
  A associação à família GGML/Silero é hipótese sustentada por bytes, não prova de
  versão, arquitetura interna ou equivalência com um modelo upstream.
- `dic` permanece binário desconhecido. Não se afirma criptografia por aparência.
- `AppstoreAuthenticationKey.pem` contém **chave pública**, não privada. O conteúdo
  não foi copiado ao relatório nem usado para contornar autenticação.

## NEF: hipótese rejeitada e progresso verificável

O teste inicial de interpretar os oito `.nef` como XML falhou nos oito. Isso está
explicitamente registrado em `formatos/resumo.json`; não são XML decodificados
nem erros silenciosamente descartados.

O parser experimental `investigar_nef.py` encontrou em `crack_transition.nef`:

| Deslocamento | Cabeçalho observado | Compactado | Expandido | Resultado |
|---|---|---:|---:|---|
| `0` | magic `01 da fe ef`, campos 4/8 desconhecidos | 191.377 | 197.446 | zlib completo, assinatura JPEG |
| `191397` | mesmo magic, campos 4/8 desconhecidos | 485.571 | 497.609 | zlib completo, assinatura JPEG |
| `676988` | cauda inicia `13 00 ff ee` | — | 1.872.108 bytes de cauda | estrutura não compreendida |

Os dois registros começam com cinco inteiros little-endian de 32 bits: magic,
dois campos de semântica desconhecida e dois comprimentos. Os comprimentos e
fim de fluxo zlib foram conferidos, com hashes das expansões registrados em
`formatos/nef.json`. Identificar JPEG por assinatura não equivale a decodificar
visualmente todas as imagens. Os outros sete NEF começam diretamente com
`13 00 ff ee`; o parser preserva suas caudas sem inventar registros.

Essa é uma **gramática parcial observada em uma amostra**, não especificação
universal de NEF. A semântica dos campos, geometria/animação da cauda, endianness
de todas as estruturas e relações de offsets precisam de mais evidências.

## Esquema de projeto: recuperação parcial por anotações Wire

`recuperar_esquema.py` extraiu de smali **589 campos com tags explícitas em 42
classes de mensagem** sob `com.nexstreaming.kinemaster.wire.KMProto`.
O índice `formatos/esquema-wire.json` guarda classe, campo, descritor, tag,
anotação original, atributos e hash do smali. Não é um `.proto` original inventado.

Exemplos observados em `KMProto$KMProject`: `primary_items` tag 1,
`secondary_items` tag 2, `bookmarks` tag 103, proporção tag 111,
largura/altura da proporção tags 133/134. Há mensagens de clips, timeline,
tracks, fades, transformações e keyframes. A presença de `com.squareup.wire.Message`
e dessas anotações sustenta serialização Wire/Protobuf estática; não estabelece
sozinha a gramática do contêiner `.kine`, migrações ou compatibilidade entre versões.
Não foi criado/aberto/exportado um projeto no aplicativo nesta execução.
