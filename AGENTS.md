# Orientações para próximos agentes

- Trabalhe na branch da sessão fornecida pelo ambiente. Preserve alterações de colaboradores.
- Leia `TODO.md`, `docs/analise/RELATORIO.md`, os relatórios específicos e
  `bin/tools/MANIFESTO.md` antes de repetir análises.
- Todo material autoral deve ser em português brasileiro. Não traduza nomes,
  identificadores, bytes ou textos oficiais de terceiros indiscriminadamente.
- Leia a skill oficial preservada em `bin/tools/rea/skill-44-5375ff0.tar.gz`.
  O arquivo local `.agents/skills/reverse-engineer-anything/SKILL.md` é orientação
  em português, não instalação de um servidor MCP nem tradução integral oficial.
- Use apenas ferramentas realmente disponíveis. A CLI REA 6.7.0 foi usada para
  diagnóstico, não para análise Android/nativa. Não invente Evidence IDs.
- Os originais são quatro volumes 7z em `bin/`; compare SHA-256 com
  `docs/analise/inventarios/originais.json` antes/depois. Nunca sobrescreva a entrada.
- Ferramentas redistribuíveis ficam em `bin/tools/`, com licença, origem, versão
  e hash. Não ignore essa pasta. Distribuições grandes têm exceção explícita e
  script de obtenção validada; não alegue que foram instaladas sem executá-las.
- Não versione extrações redundantes, caches, logs gigantes ou todo o Java/smali.
  Regenere em `.trabalho/` e `decompilado/gerado/`, mantendo índices essenciais.
- O aplicativo e seus assets não são relicenciados pela licença MIT do repositório.
  Revise direitos e obrigações de fontes correspondentes antes de redistribuição.
- JADX 1.1.0 pode retornar zero com milhares de falhas. Preserve diagnósticos,
  aliases e stubs; nunca chame um stub de método recuperado.
- Não some as saídas JADX do APK e do DEX Audience Network: há sobreposição.
- Não chame desmontagem ELF de decompilação de funções nem presença de certificado
  de autenticidade. V2 foi verificada em escopo delimitado; v3/linhagem continuam abertas.
- A análise dinâmica **não aconteceu** neste host. Não converta diagnóstico de
  pré-requisitos, testes de parser ou inicialização futura em cobertura funcional.
- Não rode APK/bibliotecas nativas no host por conveniência. Use Android ARM64/API
  ≥ 26 dedicado e autorizado, sem dados pessoais, sem contornar login/DRM/licenças.
- Atualize o plano e a cadeia de evidências a cada avanço. Mantenha fatos,
  inferências e hipóteses separados; retome métodos e formatos ainda desconhecidos.
- Execute os testes e a auditoria documentados. Os índices existentes não
  substituem execução de ferramentas; cada novo resultado precisa de proveniência.
- Atualize o único PR da missão. Não faça merge sem autorização explícita.
