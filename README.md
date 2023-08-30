# mwsn2ape

Esta descrição contém informações parciais sobre este repositório.

`mwsn2ape` é um aplicativo de linha de comando escrito em Java que implementa um 'pipeline' de NLP para um subconjunto do português brasileiro culto.

- Entradas: uma gramática na notação Wirth modificada, um dicionário e padrões de dependência.
- Saídas: árvore sintática ('phrase structure') e árvore de dependências.
- Resultados intermediários: autômato de pilha estruturado correspondente à gramática de entrada, transdutor sintático.

Esta ferramenta é disponibilizada conforme 'GNU Public License version 3.0' e utiliza ferramentas auxiliares baseadas nos sistemas `wirth2ape` e `nfa2dfa` escrito por Paulo Cereda.

Apache Maven e Java 8 são necessários para compilar o `mwsn2ape` a partir do código fonte. Execute:

```bash
$ mvn assembly:assembly
```

Sample bash script

```bash

#!/bin/bash
#jar="~/IdeaProjects/mw2apeNLP/out/artifacts/mw2apeNLP/mw2apeNLP.jar"
jar="~/IdeaProjects/mw2apeNLP/target/mw2spaNLP-1.0-jar-with-dependencies.jar"
dict="~/Documentos/TeseExperimentos/dict/LuftDict.yml"
dot="~/Documentos/TeseExperimentos/grammar/automaton/%s.dot"
yml="~/Documentos/TeseExperimentos/grammar/yml/%s.yml"
gram="~/Documentos/TeseExperimentos/grammar/luft_all.gram"

java -jar "$jar" -n -o "$dot" -y "$yml" -d "$dict" -t 2 -i "$filename" "$gram" 
