/**
* ------------------------------------------------------
*    Laboratório de Linguagens e Técnicas Adaptativas
*       Escola Politécnica, Universidade São Paulo
* ------------------------------------------------------
* 
* This program is free software: you can redistribute it
* and/or modify  it under the  terms of the  GNU General
* Public  License  as  published by  the  Free  Software
* Foundation, either  version 3  of the License,  or (at
* your option) any later version.
* 
* This program is  distributed in the hope  that it will
* be useful, but WITHOUT  ANY WARRANTY; without even the
* implied warranty  of MERCHANTABILITY or FITNESS  FOR A
* PARTICULAR PURPOSE. See the GNU General Public License
* for more details.
* 
**/
package br.usp.poli.lta.nlpdep.wsn2spa;

import br.usp.poli.lta.nlpdep.execute.NLP.NLPLexer;
import br.usp.poli.lta.nlpdep.execute.NLP.dependency.DepPattern;
import br.usp.poli.lta.nlpdep.execute.NLP.dependency.DepPatternList;
import br.usp.poli.lta.nlpdep.execute.SPAExecute;
import br.usp.poli.lta.nlpdep.execute.SPAExecuteNLP;
import br.usp.poli.lta.nlpdep.execute.SimpleLexer;
import br.usp.poli.lta.nlpdep.mwirth2ape.labeling.LabelGrammar;
import br.usp.poli.lta.nlpdep.mwirth2ape.mwirth.LMWirthLexer;
import br.usp.poli.lta.nlpdep.nfa2dfa.utils.Conversion;
import br.usp.poli.lta.nlpdep.nfa2dfa.utils.Reader;
import br.usp.poli.lta.nlpdep.nfa2dfa.utils.SimpleTransition;
import br.usp.poli.lta.nlpdep.nfa2dfa.utils.Triple;
import br.usp.poli.lta.nlpdep.mwirth2ape.exporter.Spec;
import br.usp.poli.lta.nlpdep.mwirth2ape.exporter.Writer;
import br.usp.poli.lta.nlpdep.mwirth2ape.mwirth.Generator;
import br.usp.poli.lta.nlpdep.mwirth2ape.mwirth.MWirthLexer;

import java.io.*;
import java.util.*;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Newton Kiyotaka Miura & Paulo Roberto Massa Cereda
 * @version 1.4
 * @since 1.0
 */
public class Main {

    public static Properties appProperties = new Properties();  // Alterar para usar enum no futuro
    public String version = "1.0";

    private static final Logger logger = LoggerFactory.
            getLogger(Main.class);

    public static void main(String[] args) {        

        Utils.printBanner();
        CommandLineParser parser = new DefaultParser();

        try {
            System.out.println("Command-line arguments:");
            StringBuilder sbArgs = new StringBuilder();
            for (String arg : args) {
                sbArgs.append(arg + " ");
            }
            System.out.println(sbArgs);
            CommandLine line = parser.parse(Utils.getOptions(), args);
            enableCLI(line, appProperties);
        } catch (ParseException nothandled) {
            Utils.printHelp();
        } catch (Exception exception) {
            Utils.printException(exception);
        }
    }

    private static void enableCLI(CommandLine line, Properties appProperties)
            throws IOException, Exception {
        //System.out.println("line.getArgs().length: " + line.getArgs().length);
        int type = 0;
        int maxTypeValue = 5;

        if (!Utils.required(line, "o", "y") || line.getArgs().length != 1) {
            throw new Exception("Note that 'o' and 'y' flags are required"
                    + " to preorderParse this tool, as they generate DOT and YAML"
                    + " files, respectively. Also, do not forget to include"
                    + " the replacement pattern '%s' in order to generate"
                    + " files corresponding to each submachine in the"
                    + " automaton model.");
        }
        //System.out.println("line.getOptionValue(\"o\")" + line.getOptionValue("o"));
        //System.out.println("line.getOptionValue(\"y\")" + line.getOptionValue("y"));
        //System.out.println("line.getOptionValue(\"o\").contains(\"%s\"))" + line.getOptionValue("o").contains("%s"));
        //System.out.println("line.getOptionValue(\"y\").contains(\"%s\"))" + line.getOptionValue("y").contains("%s"));
        if (!line.getOptionValue("o").contains("%s")
                || !line.getOptionValue("y").contains("%s")) {
            throw new Exception("Flags 'o' and 'y' lack the replacement"
                    + " pattern '%s' in order to generate files"
                    + " corresponding to each submachine in the automaton"
                    + " model.");
        }

        File inputFile = null;
        if (line.hasOption("i")) {
            String inputFileName = line.getOptionValue("i");
            inputFile = new File(inputFileName);
            if (!inputFile.exists()) {
                throw new Exception("The provided input sentence file " +
                        inputFileName +
                        " does not exist. Make sure the location is correct and " +
                        "try again.");
            }
            appProperties.setProperty("inputFileName",inputFileName);
        }


        String inputNLPDictionaryFileName = "";
        String inputNLPDependencyPatternsFileName = "";

        File file = new File(line.getArgs()[0]);
        if (!file.exists()) {
            throw new Exception("The provided grammar file '" + "' does"
                    + " not exist. Make sure the location is correct and"
                    + " try again.");
        }
        appProperties.setProperty("inputGrammarFileName",line.getArgs()[0]);


        if (!line.getOptionValue("t").isEmpty()) {
            try
            {
                type = Integer.parseInt(line.getOptionValue("t"));
                if (type < 0 || type > maxTypeValue) {
                    System.out.println("Invalid parsing type specification. type = 0 will be enforced.");
                }
                appProperties.setProperty("type", Integer.toString(type));
            }
            catch (Exception exception) {
                System.out.println("An exception was thrown in parsing type specification.");
                System.out.println(exception.toString());
            }
        }

        String text = FileUtils.readFileToString(file, "UTF-8").trim();  // Original grammar in plain text format
        MWirthLexer mwl = new MWirthLexer(text); // Lexer for grammar in plain text format
        Generator mwg = null; // SPA generator for plain text grammar and parsing for grammar labeling
        LabelGrammar labelGrammar;  // Grammar with labels
        LMWirthLexer lmwl; // Lexer for grammar format with labels
        Generator lmwg = null; // SPA generator for grammar format with labels
        DepPatternList depPatternList = new DepPatternList(); // Dependency pattern list to be loaded rom JSON file

        try {
            switch (type) {
                case 0:
                    mwg = new Generator(mwl, 0);
                    mwg.generateAutomaton();
                    break;
                case 1:
                    mwg = new Generator(mwl, 1);
                    mwg.generateAutomaton();
                    break;
                case 2: // faz parsing livre de contexto
                case 3: // gera rascunho de padrões de dep (sem as relações) em json
                case 4: // gera padrões de dep em json a partir de input no formato CONLLU
                case 5: // gera padrões de dep em json a partir de input no formato CONLLU
                    mwg = new Generator(mwl, 1);
                    mwg.generateAutomaton();  // parsing da gramática para obter labels
                    labelGrammar = mwg.getLabelGrammar(); // gramática com lebels
                    lmwl = new LMWirthLexer(); //
                    lmwl.LGrammarToProductionTokens(labelGrammar); // geração de nova cadeia de entrada com gramática com labels
                    //System.out.println(lmwl.toString());
                    lmwg = new Generator(lmwl, 2);  // parsing da gramática com labels para obter estrutura de dados para montagem do autômato final
                    lmwg.generateAutomaton(); // geração do autômato
                    mwg = lmwg;
                    // Parse input sentence
                    if (inputFile != null) {
                        String inputText = FileUtils.readFileToString(inputFile, "UTF-8").trim();
                        if (!line.hasOption("n")) { // Check if input is NLP
                            appProperties.setProperty("nlpInput","false");
                            SimpleLexer simpleLexer = new SimpleLexer(inputText, labelGrammar.getTermsList());
                            SPAExecute spaExecute = new SPAExecute(simpleLexer, lmwg, labelGrammar.getTermsList());
                            spaExecute.parseInput();
                        }
                        else {
                            appProperties.setProperty("nlpInput","true");
                            if (line.hasOption("d")) {
                                if (!line.getOptionValue("d").isEmpty()) {
                                    inputNLPDictionaryFileName = line.getOptionValue("d");

                                    StringBuilder sbMainParamLogMsg = new StringBuilder();
                                    //sbMainParamLogMsg.append("Command line: " + line.getArgList().toString());

                                    sbMainParamLogMsg.append("\nDiretório de trabalho: " + System.getProperty("user.dir"));
                                    sbMainParamLogMsg.append("\nProcessamento de entrada NLP: arquivo " +  inputFile.getAbsolutePath());
                                    sbMainParamLogMsg.append("\nGramática NLP: arquivo " +  file.getAbsolutePath());
                                    sbMainParamLogMsg.append("\nDicionário NLP: arquivo " + inputNLPDictionaryFileName);
                                    sbMainParamLogMsg.append("\nType: " +  appProperties.getProperty("type"));

                                    appProperties.setProperty("workingDirectory",System.getProperty("user.dir"));
                                    appProperties.setProperty("inputNLPDictionaryFileName",inputNLPDictionaryFileName);
                                    NLPLexer nlpLexer = new NLPLexer(inputText, inputNLPDictionaryFileName, labelGrammar.getTermsList());
                                    if (line.hasOption("p")) {
                                        if (!line.getOptionValue("p").isEmpty()) {
                                            inputNLPDependencyPatternsFileName = line.getOptionValue("p");
                                            appProperties.setProperty("inputNLPDependencyPatternsFileName",inputNLPDependencyPatternsFileName);

                                            sbMainParamLogMsg.append("\nPadrões de dependências: arquivo " +  inputNLPDependencyPatternsFileName);
                                            depPatternList = depPatternList.loadDepPatternsFromJson(inputNLPDependencyPatternsFileName);
                                            if (depPatternList.getDepPatterns() != null) {
                                                depPatternList.insertDepPatternsToNterms(labelGrammar);
                                            }
                                        }
                                    }

                                    System.out.println(sbMainParamLogMsg);
                                    logger.info("#####################################################################################################");
                                    logger.info(sbMainParamLogMsg.toString());
                                    logger.info("#####################################################################################################\n");

                                    SPAExecuteNLP spaExecute = new SPAExecuteNLP(nlpLexer, lmwg, labelGrammar.getTermsList(), appProperties);
                                    spaExecute.parseInput();
                                }
                                else {
                                    throw new Exception("NLP processing requires an NLP dictionary file. " +
                                            " Provide the dictionary and try again.");
                                }
                            } else {
                                throw new Exception("NLP processing requires an NLP dictionary file. " +
                                        " Provide the dictionary and try again.");
                            }
                        }
                    }
                    break;
            }

            Writer writer = new Writer(mwg.getTransitions());
            Map<String, String> map =
                    writer.generateYAMLMap(line.getOptionValue("y"));

            if (Utils.neither(line, "c", "m")) {
                br.usp.poli.lta.nlpdep.mwirth2ape.dot.Dot dot =
                        new br.usp.poli.lta.nlpdep.mwirth2ape.dot.Dot(
                                mwg.getTransitions()
                        );
                dot.generate(line.getOptionValue("o"));
                for (String key : map.keySet()) {
                    FileUtils.write(new File(key), map.get(key), "UTF-8");
                }
            } else {
                System.out.println("Additional operations:");
                if (line.hasOption("c")) {
                    System.out.println("- Submachines translated to DFA's.");
                }
                if (line.hasOption("m")) {
                    System.out.println("- State minimization applied.");
                }

                for (String key : map.keySet()) {

                    Triple<Integer, Set<Integer>, List<SimpleTransition>> spec =
                            Reader.read(map.get(key));
                    br.usp.poli.lta.nlpdep.nfa2dfa.dot.Dot dot =
                            new br.usp.poli.lta.nlpdep.nfa2dfa.dot.Dot();
                    dot.append(Reader.getName(), "original", spec);

                    Conversion c;

                    if (line.hasOption("c")) {
                        c = new Conversion(spec.getThird(), spec.getFirst(),
                                spec.getSecond());
                        spec = c.convert();
                        dot.append(Reader.getName().concat("'"),
                                "converted", spec);
                    }

                    if (line.hasOption("m")) {
                        if (!line.hasOption("c")) {
                            throw new Exception("State minimization cannot be"
                                    + "applied if the DFA conversion was not"
                                    + "specified. Make sure to include the"
                                    + "'-c' flag as well and try again.");
                        }
                        c = new Conversion(spec.getThird(), spec.getFirst(),
                                spec.getSecond());
                        spec = c.minimize();
                        dot.append(Reader.getName().concat("''"), "minimized",
                                spec);
                    }

                    Yaml yaml = new Yaml();
                    Spec result = Utils.toFormat(spec);
                    result.setName(Reader.getName());
                    map.put(key, yaml.dump(result));

                    String dotname = String.format(line.getOptionValue("o"),
                            Reader.getName());
                    dot.dump(dotname);

                }

                for (String key : map.keySet()) {
                    FileUtils.write(new File(key), map.get(key), "UTF-8");
                }
            }

        }
        catch (Exception exception) {
            System.out.println("An exception was thrown.");
            System.out.println(exception.toString());
        }
        
        System.out.println("Done.");
    }

}
