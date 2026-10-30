---
name: reverse-engineer-anything
description: Engenharia reversa de artefatos com evidências, limites explícitos e correlação estática e dinâmica.
metadata:
  version: "44-pt-BR-local"
---

# Reverse Engineer Anything — orientação local em português

A skill oficial versão 44 está preservada, sem alterações, em
`bin/tools/rea/skill-44-5375ff0.tar.gz` (origem e hash no manifesto).
Esta orientação local não se apresenta como tradução integral nem como ferramenta MCP.
Leia o `SKILL.md` oficial e suas referências pertinentes antes de investigar.

- Resolva a identidade do artefato com hashes e inventário antes de analisar código.
- Use apenas ferramentas realmente disponíveis; a skill não instala provedores.
- Se não houver MCP, diagnostique com a CLI fixada, sem reescrever configurações.
  Um cliente Arena sem integração REA não deve ser tratado como Codex.
- Escolha o menor resumo útil; investigue questões concretas e registre o escopo.
- Para Android, consulte a referência oficial `android-applications.md`.
- Separe observações, inferências e desconhecidos. Saídas de scripts locais não
  são Evidence IDs do REA. Não invente IDs nem afirme execução dinâmica a partir
  de código estático.
- Preserve diagnósticos, falhas, versões e limitações. Não repita chamadas
  bloqueadas sem corrigir a causa. Use alternativas especializadas autorizadas.
- Atualize `TODO.md` e o registro de achados; não declare conclusão com lacunas.
