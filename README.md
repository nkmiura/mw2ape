# mwsn2spa

`mwsn2spa` is a command line tool written in Java that takes a grammar written in the Modified Wirth Syntax Notation (WSN) and generates a corresponding structured pushdown automaton (SPA), specified through a list of specs written in the YAML format. This tool is released under the GNU Public License version 3.0 and makes use of two helper tools, `wirth2ape` and `nfa2dfa`, written by Paulo Cereda, and modified by me.

Apache Maven and Java 8 are required to build `mwsn2spa` from sources. Run:

```bash
$ mvn assembly:assembly
```

Sample execution:

```bash
$ java -jar wsn2spa.jar 
                ___               
__ __ ______ _ |_  )____ __  __ _ 
\ V  V (_-< ' \ / /(_-< '_ \/ _` |
 \_/\_//__/_||_/___/__/ .__/\__,_|
                      |_|         

----------------------------------------------------------------------
                       AN EXCEPTION WAS THROWN                        
----------------------------------------------------------------------
Note that 'o' and 'y' flags are required to run this tool, as they
generate DOT and YAML files, respectively. Also, do not forget to
include the replacement pattern '%s' in order to generate files
corresponding to each submachine in the automaton model.
----------------------------------------------------------------------
```

Graphical interface:

![WSN2SPA](http://i.imgur.com/fnQZK2v.png)
