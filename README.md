# ChurrazMaster

Projeto de engenharia reversa estática e dinâmica do KineMaster, voltado a
compreender o aplicativo e estudar novos conteúdos e funcionalidades sem
contornar autenticação, DRM ou licenciamento.

**Estado: investigação parcial, não decompilação completa.** O pacote identificado
é `com.nexstreaming.app.kinemasterfree`, versão `8.2.14.37398.GP`. Foram extraídos
20 APKs, indexados/desmontados 7 DEX e examinadas estruturalmente 22 bibliotecas
ARM64. A análise dinâmica permanece bloqueada por ausência de ambiente Android
compatível. Há milhares de métodos Java não recuperados integralmente.

- [Plano persistente e pendências](TODO.md)
- [Relatório desta etapa e cobertura](docs/analise/RELATORIO.md)
- [Comandos de reprodução](docs/analise/REPRODUZIR.md)
- [Ferramentas, hashes, licenças e exceções](bin/tools/MANIFESTO.md)
- [Organização das representações recuperadas](decompilado/README.md)

O material original em `bin/` foi preservado. Extrações grandes são regeneráveis
e separadas dos originais. A licença MIT deste repositório não concede direitos
sobre o KineMaster, suas fontes recuperadas, modelos ou mídias de terceiros.
