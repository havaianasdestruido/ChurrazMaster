# Preparação, integridade e escopo

## Identificação confirmada em 2026-10-22

A branch inicial é `arena/d089b206-churrazmaster`, inicialmente sem alterações.
O repositório continha apenas README, licença MIT e `bin/`. Não havia `.gitignore`.
A entrada são **quatro volumes 7z**, não um XAPK isolado. Sua soma é
96.560.538 bytes. Os hashes de cada volume estão em
[inventarios/originais.json](inventarios/originais.json).

O p7zip verificou o contêiner e seu CRC antes da extração. O membro recuperado é
`KineMaster+-+AI+Video+Editor_8.2.14.37398.GP_APKPure.xapk`, com
180.092.744 bytes e SHA-256
`07025005a02fd1874cf03f422e86182dcd053b950ee97f15f6fd3a0e0b092720`.
O caminho de trabalho e os metadados estão em `inventarios/xapk.json`.
Nenhum original foi sobrescrito: hashes anteriores e posteriores coincidem.

O `manifest.json` interno e o manifesto Android decodificado concordam:

- Pacote: `com.nexstreaming.app.kinemasterfree`.
- Nome: KineMaster; versão `8.2.14.37398.GP`, código `37398`.
- Android mínimo: API 26; alvo: API 36.
- 1 APK base e 19 APKs de configuração: 17 idiomas, `arm64_v8a`, `xxhdpi`.
- 7 DEX, incluindo um em `assets/audience_network/classes.dex`.
- 22 bibliotecas `.so`, todas no split `arm64_v8a`.

O nome APKPure indica o empacotamento recebido, não uma prova de autenticidade.
A presença dos certificados não é validação criptográfica integral da assinatura.

## Preservação e armazenamento

A cópia lógica do original é a série de volumes já rastreada no Git, verificada
por SHA-256; não se duplica o XAPK de 180 MB no histórico. Saídas regeneráveis
ficam em `.trabalho/` e `decompilado/gerado/`, deliberadamente fora do Git.
Scripts, inventários, hashes e relatórios essenciais são versionados.
`arquivos.jsonl` relaciona cada membro ao APK/XAPK pai, caminho, tamanho, CRC e hash.
A extração recusa duplicatas de nome, travessias e ligações simbólicas em ZIP.

O inventário inicial cobre 22 membros do XAPK e 3.141 membros dos APKs, totalizando
3.163 registros. Arquivos ZIP internos são uma camada adicional, examinada pela
análise de formatos; esses totais não incluem seus membros implicitamente.

## Licenciamento e segurança

A licença MIT do repositório **não relicencia o KineMaster**, suas fontes
recuperadas, modelos, fontes tipográficas ou mídias. A autorização de uso do
material de entrada não demonstra direito de redistribuí-lo. Não serão enviados
APKs, bibliotecas ou dumps a serviços externos. O PR conterá ferramentas
redistribuíveis e evidências derivadas, não uma nova cópia do aplicativo.

Ferramentas de terceiros mantêm licenças e nomes originais. Textos oficiais,
bytes extraídos, diagnósticos de terceiros e identificadores não são traduzidos;
toda documentação e código autorais da investigação são em português brasileiro.
Não há tentativa de contornar autenticação, assinatura, DRM ou licença.

## Ambiente inicial e critério de conclusão

Linux Debian 12, x86_64; Python 3.11.2, Node.js 22.22.3, npm 10.9.8.
Sem Java inicial, ADB, emulador, `/dev/kvm` ou barramento USB disponível.
`readelf` e `objdump` do host são GNU Binutils 2.40, apenas consultados para
inventário do ambiente, não dependências exclusivas da análise ELF.

Inventário, desmontagem, decompilação aproximada e execução são métricas distintas.
Nenhuma fase depende de presumir sucesso de outra. Conclusão total exigiria
tratar as falhas por classe/método, funções nativas, formatos e comportamento
Android efetivamente observado. O plano em `TODO.md` permanece aberto.
