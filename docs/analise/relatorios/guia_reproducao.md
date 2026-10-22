# Guia de reprodução — do XAPK ao código descompilado

Todos os comandos foram executados neste repositório. O que não pôde ser executado está
marcado como tal. Tempo aproximado total: **25–40 min** (a descompilação do jadx domina o tempo).

## 0. Requisitos

- Linux x86_64, Python 3.11+, `pip3`, `git`.
- ~3 GB de disco livre para o diretório de trabalho (`/tmp/churraz-trabalho` por padrão).
- Rede: os wheels e o JDK já estão versionados em `bin/tools/`; rede só é necessária para as
  dependências transitivas Python.

## 1. Preparar o ambiente

```bash
python3 scripts/instalar_ferramentas.sh
# instala wheels versionados, extrai o JRE do wheel jdk4py em bin/tools/java/jdk
# e valida jadx/Apktool
```

## 2. Integridade e extração (Fases 0–2)

```bash
bash scripts/00_verificar_integridade.sh   # hashes dos 4 volumes + extração do XAPK
bash scripts/01_extrair.sh                 # 20 APKs + 7 DEX
python3 scripts/02_inventariar.py          # inventário JSON/MD com hashes de tudo
```

Saídas: `docs/analise/inventarios/integridade.json`, `inventario_xapk.json`, `inventario_xapk.md`.

## 3. Recursos (Fase 3)

```bash
bash scripts/03_recursos.sh                # Apktool em todos os 20 APKs (-s: só recursos)
```

Saída: `/tmp/churraz-trabalho/05_apktool/<apk>/{AndroidManifest.xml,res,apktool.yml}`
(201 MB; **não versionado** — ver `decompilado/README.md`).

## 4. Código gerenciado (Fase 3)

```bash
bash scripts/04_decompilar_jadx.sh         # jadx no APK base + no DEX de assets/
python3 scripts/05_metricas_dex.py         # classes/métodos e cobertura da descompilação
```

Saídas: `/tmp/churraz-trabalho/04_jadx_base/sources` (45.195 `.java`, 385 MB),
`/tmp/churraz-trabalho/04_jadx_audience/sources` (3.538 `.java`) e
`docs/analise/inventarios/metricas_dex.json`.

## 5. Código nativo (Fase 4)

```bash
python3 scripts/07_analise_nativa.py       # ELF: seções, DT_NEEDED, JNI, protetores
```

Saída: `docs/analise/inventarios/nativo.json`.

## 6. Formatos proprietários (Fase 5)

```bash
python3 scripts/08_formatos_proprietarios.py
```

Saídas: `docs/analise/relatorios/formatos_proprietarios.md` e
`docs/analise/inventarios/formatos.json`.

## 6.1 Bancos de dados e rede (Fase 5)

```bash
python3 scripts/11_esquema_room.py        # 12 bancos, 49 entidades, 517 campos
python3 scripts/12_superficie_rede.py     # 173 URLs, política de rede
```

Saídas: `docs/analise/relatorios/esquema_banco_de_dados.md`,
`docs/analise/relatorios/superficie_de_rede.md` e os JSON correspondentes em
`docs/analise/inventarios/`.

## 7. Consulta interativa ao decompilador (ferramenta MCP)

```bash
# listar as 21 ferramentas do servidor
python3 scripts/09_mcp_jadx.py --apk /tmp/churraz-trabalho/02_apks/com.nexstreaming.app.kinemasterfree.apk --listar

# ver o código de uma classe
python3 scripts/09_mcp_jadx.py --apk <apk> --chamar get_class_source \
  --args '{"class":"com.kinemaster.app.base.KineMasterApplication"}'

# referências de entrada a uma classe
python3 scripts/09_mcp_jadx.py --apk <apk> --chamar get_xrefs_to_class \
  --args '{"class":"com.kinemaster.module.nexeditormodule.nexvideoeditor.NexEditor"}'
```

## 8. Artefatos selecionados para o Git (Fase 7)

```bash
python3 scripts/10_selecionar_artefatos.py
```

Atualiza `decompilado/` com manifesto, índices e as classes de interesse.

## 9. Repetibilidade

- Os hashes de entrada e saída estão em `docs/analise/inventarios/`. Repetir os passos 2 e 4 e
  comparar contagens é a forma de validar a reprodutibilidade.
- **Não** se espera byte-a-byte igualdade na saída do jadx entre versões diferentes da ferramenta;
  por isso a versão está fixada no manifesto.
- Limpeza: `rm -rf /tmp/churraz-trabalho` remove todos os intermediários sem afetar o repositório.

## 10. O que **não** é reproduzível aqui

- Fase 6 (dinâmica) — ver `analise_dinamica.md`.
- Descompilação nativa (Ghidra) — ver `bin/tools/MANIFESTO.md` §4.2, com o comando exato de download
  e verificação de hash para uso em um host com rede liberada.
