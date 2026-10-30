# Representações recuperadas

`gerado/` é uma saída **local regenerável**, excluída do Git por tamanho e direitos
de redistribuição do aplicativo. Não é uma pasta de ferramentas. O código original
não foi recuperado integralmente e não se promete um projeto Android compilável.

- `android/`: manifestos, XML decodificados, strings multilíngues e recursos públicos.
- `jadx/`: primeira passagem Java por DEX, com falhas conhecidas de hierarquia/nomes.
- `jadx-agrupado/`: passagem corretiva, APK base multidex e DEX de assets separados.
- `smali/`: representação de bytecode com classes, descritores e nomes originais.
- `nativo/`: desmontagem linear AArch64, não pseudocódigo nem limites confiáveis de funções.
- `assets-internos/`: membros de ZIPs de assets, mantendo estrutura e nomes.
- `nef/`: corpos zlib recuperados de prefixos NEF; cauda não compreendida.

Índices em `docs/analise/inventarios/` registram entradas, saídas, hashes e falhas.
Reprodução: `docs/analise/REPRODUZIR.md`. Nomes e comentários provenientes do
aplicativo e das ferramentas são preservados, não traduzidos indiscriminadamente.
As explicações autorais desta investigação são em português brasileiro.
