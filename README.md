# ChurrazMaster

Decompilação do KineMaster, com o objetivo de facilitar mods e adicionar novos conteúdos e features
para o aplicativo base. | Decompilation of KineMaster, aiming to facilitate mods and add new content
and features to the base app.

---

## Estado atual (pt-BR)

Missão em andamento: **análise estática conduzida até 98,49 % das classes**; análise dinâmica e
descompilação nativa **bloqueadas neste ambiente** (motivos técnicos documentados).

| Item | Situação |
|---|---|
| Alvo | KineMaster 8.2.14.37398.GP (`com.nexstreaming.app.kinemasterfree`), 20 APKs, AArch64 |
| Material de origem | `bin/` — 7z multi-volume → XAPK (180.092.744 B) |
| DEX descompilados | 7 de 7 (52.903 classes, 258.710 métodos) |
| Cobertura de classes | **98,49 %** (52.105 com `.java`; 3 classes de nível superior perdidas) |
| Bibliotecas nativas | 22 de 22 inventariadas; pseudocódigo **não** recuperado (sem Ghidra) |
| Análise dinâmica | **não executada** (sem KVM/ADB/dispositivo) |

## Documentação

- **Plano e progresso:** [`TODO.md`](TODO.md)
- **Relatório técnico:** [`docs/analise/relatorios/relatorio_tecnico.md`](docs/analise/relatorios/relatorio_tecnico.md)
- **Limitações:** [`docs/analise/relatorios/limitacoes.md`](docs/analise/relatorios/limitacoes.md)
- **Formatos proprietários:** [`docs/analise/relatorios/formatos_proprietarios.md`](docs/analise/relatorios/formatos_proprietarios.md)
- **Análise dinâmica (bloqueada + procedimento):** [`docs/analise/relatorios/analise_dinamica.md`](docs/analise/relatorios/analise_dinamica.md)
- **Como reproduzir:** [`docs/analise/relatorios/guia_reproducao.md`](docs/analise/relatorios/guia_reproducao.md)
- **Esquema dos bancos (Room):** [`docs/analise/relatorios/esquema_banco_de_dados.md`](docs/analise/relatorios/esquema_banco_de_dados.md)
- **Superfície de rede:** [`docs/analise/relatorios/superficie_de_rede.md`](docs/analise/relatorios/superficie_de_rede.md)
- **Guia de modificação:** [`docs/analise/relatorios/guia_modificacao.md`](docs/analise/relatorios/guia_modificacao.md)
- **Ferramentas (versões, licenças, hashes):** [`bin/tools/MANIFESTO.md`](bin/tools/MANIFESTO.md)
- **Inventários e índices:** `docs/analise/inventarios/` e `decompilado/indices/`

## Início rápido

```bash
python3 scripts/instalar_ferramentas.sh    # prepara JRE, jadx, Apktool e bibliotecas Python
bash scripts/00_verificar_integridade.sh   # confere hashes dos volumes e extrai o XAPK
bash scripts/01_extrair.sh                 # 20 APKs + 7 DEX
python3 scripts/02_inventariar.py          # inventário com SHA-256 de tudo
bash scripts/03_recursos.sh                # Apktool nos 20 APKs
bash scripts/04_decompilar_jadx.sh         # descompilação Java
python3 scripts/05_metricas_dex.py         # métricas e cobertura
python3 scripts/07_analise_nativa.py       # inventário ELF/JNI
python3 scripts/08_formatos_proprietarios.py  # assets, .nef, .kine/.kmproject
python3 scripts/11_esquema_room.py            # bancos Room a partir das anotações
python3 scripts/12_superficie_rede.py         # URLs e política de rede
python3 scripts/10_selecionar_artefatos.py    # atualiza decompilado/
```

## Aviso legal

Material de estudo de interoperabilidade e modificação do próprio pacote. Os binários originais
pertencem aos seus titulares; este repositório não redistribui o aplicativo modificado nem contorna
autenticação, DRM ou licenciamento. Use apenas em ambiente controlado e respeitando as leis aplicáveis.
