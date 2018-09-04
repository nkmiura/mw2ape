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
package br.usp.poli.lta.cereda.wsn2spa;

import br.usp.poli.lta.cereda.execute.NLP.NLPDictionary;
import br.usp.poli.lta.cereda.execute.NLP.NLPLexer;
import br.usp.poli.lta.cereda.execute.SPAExecute;
import br.usp.poli.lta.cereda.execute.SPAExecuteNLP;
import br.usp.poli.lta.cereda.execute.SimpleLexer;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.LabelGrammar;
import br.usp.poli.lta.cereda.mwirth2ape.mwirth.LMWirthLexer;
import br.usp.poli.lta.cereda.nfa2dfa.utils.Conversion;
import br.usp.poli.lta.cereda.nfa2dfa.utils.Reader;
import br.usp.poli.lta.cereda.nfa2dfa.utils.SimpleTransition;
import br.usp.poli.lta.cereda.nfa2dfa.utils.Triple;
import br.usp.poli.lta.cereda.mwirth2ape.exporter.Spec;
import br.usp.poli.lta.cereda.mwirth2ape.exporter.Writer;
import br.usp.poli.lta.cereda.mwirth2ape.mwirth.Generator;
import br.usp.poli.lta.cereda.mwirth2ape.mwirth.MWirthLexer;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

/**
 * 
 * @author Paulo Roberto Massa Cereda
 * @version 1.2
 * @since 1.0
 */
public class Main {

    public static void main(String[] args) {        
        
        Utils.printBanner();
        CommandLineParser parser = new DefaultParser();

        try {

            CommandLine line = parser.parse(Utils.getOptions(), args);
            
            if (line.hasOption("g")) {
                System.out.println("Flag '-g' found, overriding other flags.");
                System.out.println("Please, wait...");
                try {
                    UIManager.setLookAndFeel(UIManager.
                            getSystemLookAndFeelClassName());
                }
                catch (Exception nothandled) {}
                SwingUtilities.invokeLater(() -> {
                    Editor e = new Editor();
                    e.setVisible(true);
                });
            }
            else {
                enableCLI(line);
            }
        } catch (ParseException nothandled) {
            Utils.printHelp();
        } catch (Exception exception) {
            Utils.printException(exception);
        }
    }

    private static void enableCLI(CommandLine line)
            throws IOException, Exception {
        //System.out.println("line.getArgs().length: " + line.getArgs().length);
        int type = 0;

        if (!Utils.required(line, "o", "y") || line.getArgs().length != 1) {
            throw new Exception("Note that 'o' and 'y' flags are required"
                    + " to run this tool, as they generate DOT and YAML"
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
        if (!line.getOptionValue("i").isEmpty()) {
            String inputFileName = line.getOptionValue("i");
            inputFile = new File(inputFileName);
            if (!inputFile.exists()) {
                throw new Exception("The provided input sentence file " +
                inputFileName +
                " does not exist. Make sure the location is correct and " +
                "try again.");
            }
        }

        /*
        File inputNLPFile = null;
        if (!line.getOptionValue("n").isEmpty()) {
            String inputNLPFileName = line.getOptionValue("n");
            inputNLPFile = new File(inputNLPFileName);
            if (!inputNLPFile.exists()) {
                throw new Exception("The provided input NLP sentence file " +
                        inputNLPFileName +
                        " does not exist. Make sure the location is correct and " +
                        "try again.");
            }
        }


        File inputNLPDictionaryFile = null;*/

        String inputNLPDictionaryFileName = "";


        File file = new File(line.getArgs()[0]);
        if (!file.exists()) {
            throw new Exception("The provided grammar file '" + "' does"
                    + " not exist. Make sure the location is correct and"
                    + " try again.");
        }

        if (!line.getOptionValue("t").isEmpty()) {
            try
            {
                type = Integer.parseInt(line.getOptionValue("t"));
                if (type < 0 || type > 2) {
                    System.out.println("Invalid parsing type specification. type = 0 will be enforced.");
                }
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
                case 2:
                    mwg = new Generator(mwl, 1);
                    mwg.generateAutomaton();
                    labelGrammar = mwg.getLabelGrammar();
                    lmwl = new LMWirthLexer();
                    lmwl.LGrammarToProductionTokens(labelGrammar);
                    System.out.println(lmwl.toString());
                    lmwg = new Generator(lmwl, 2);
                    lmwg.generateAutomaton();
                    mwg = lmwg;
                    // Parse input sentence
                    if (inputFile != null) {
                        String inputText = FileUtils.readFileToString(inputFile, "UTF-8").trim();
                        if (!line.hasOption("n")) { // Check if input is NLP
                            SimpleLexer simpleLexer = new SimpleLexer(inputText, labelGrammar.getTermsList());
                            SPAExecute spaExecute = new SPAExecute(simpleLexer, lmwg, labelGrammar.getTermsList());
                            spaExecute.parseInput();
                        }
                        else {
                            if (line.hasOption("d")) {
                                if (!line.getOptionValue("d").isEmpty()) {
                                    inputNLPDictionaryFileName = line.getOptionValue("d");
                                    NLPLexer nlpLexer = new NLPLexer(inputText, inputNLPDictionaryFileName, labelGrammar.getTermsList());
                                    SPAExecuteNLP spaExecute = new SPAExecuteNLP(nlpLexer, lmwg, labelGrammar.getTermsList());
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
                case 3:
                    break;
            }

            Writer writer = new Writer(mwg.getTransitions());
            Map<String, String> map =
                    writer.generateYAMLMap(line.getOptionValue("y"));

            if (Utils.neither(line, "c", "m")) {
                br.usp.poli.lta.cereda.mwirth2ape.dot.Dot dot =
                        new br.usp.poli.lta.cereda.mwirth2ape.dot.Dot(
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
                    br.usp.poli.lta.cereda.nfa2dfa.dot.Dot dot =
                            new br.usp.poli.lta.cereda.nfa2dfa.dot.Dot();
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
