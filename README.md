# mwsn2spa

`mwsn2ape` is a command line tool written in Java that implements a NLP pipeline.

Input: a grammar written in the Modified Wirth Syntax Notation (MWSN), lexicon, dependency patterns
Output: phrase structure syntax tree, dependency tree

Intermediate results: structured pushdown automaton (SPA), phrase structure syntax parser

This tool is released under the GNU Public License version 3.0 and makes use of modified version of 2 helper tools, `wirth2ape` and `nfa2dfa` written by Paulo Cereda.

Apache Maven and Java 8 are required to build `mwsn2spa` from sources. Run:



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
