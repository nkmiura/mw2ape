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

import br.usp.poli.lta.cereda.mwirth2ape.exporter.Transition;
import br.usp.poli.lta.cereda.nfa2dfa.utils.SimpleTransition;
import br.usp.poli.lta.cereda.nfa2dfa.utils.Triple;
import br.usp.poli.lta.cereda.mwirth2ape.exporter.Spec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBox;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * 
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Utils {

    public static void printBanner() {
        StringBuilder sb = new StringBuilder();
        sb.append("                ___               ").append('\n');
        sb.append("__ __ ______ _ |_  )____ __  __ _ ").append('\n');
        sb.append("\\ V  V (_-< ' \\ / /(_-< '_ \\/ _` |").append('\n');
        sb.append(" \\_/\\_//__/_||_/___/__/ .__/\\__,_|").append('\n');
        sb.append("                      |_|         ").append('\n');
        System.out.println(sb.toString());
    }

    public static Options getOptions() {
        Options options = new Options();
        options.addOption("o", "output", true, "DOT output");
        options.addOption("y", "yaml", true, "YAML output");
        options.addOption("c", "convert", false, "DFA conversion");
        options.addOption("m", "minimize", false, "state minimization");
        options.addOption("g", "gui", false, "open graphical interface");
        options.addOption("i", "input_file", true, "input file to be parsed");
        options.addOption("i", "input_file", true, "input file to be parsed");
        options.addOption("n", "nlp_input_file", true, "nlp input file to be parsed");
        options.addOption("t", "type", true, "parsing type");
        return options;
    }

    public static Spec toFormat(Triple<Integer, Set<Integer>,
            List<SimpleTransition>> spec) {
        Spec result = new Spec();
        result.setInitial(spec.getFirst());
        result.setAccepting(new ArrayList<>(spec.getSecond()));
        result.setTransitions(toTransitions(spec.getThird()));
        return result;
    }

    private static List<Transition> toTransitions(List<SimpleTransition>
            spec) {
        List<Transition> transitions = new ArrayList<>();
        spec.stream().map((simple) -> {
            Transition transition = new Transition();
            transition.setFrom(simple.getSource());
            transition.setTo(simple.getTarget());
            if (!simple.epsilon()) {
                transition.setSymbol(simple.getSymbol().getValue());
            }
            return transition;
        }).forEach((t) -> {
            transitions.add(t);
        });
        return transitions;
    }

    public static boolean required(CommandLine line, String... opts) {
        for (String opt : opts) {
            if (!line.hasOption(opt)) {
                return false;
            }
        }
        return true;
    }

    public static boolean neither(CommandLine line, String... opts) {
        for (String opt : opts) {
            if (line.hasOption(opt)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean neither(JCheckBox... checkboxes) {
        for (JCheckBox box : checkboxes) {
            if (box.isSelected()) {
                return false;
            }
        }
        return true;
    }

    public static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("wsn2spa -o <pattern> -y"
                + " <pattern> [ -c ] [ -m ] [-g ]", getOptions());
        System.exit(0);
    }

    public static void printException(Exception exception) {
        System.out.println(StringUtils.repeat("-", 70));
        System.out.println(StringUtils.center("An exception was thrown".
                toUpperCase(), 70));
        System.out.println(StringUtils.repeat("-", 70));
        System.out.println(WordUtils.wrap(exception.getMessage(),
                70, "\n", true));
        System.out.println(StringUtils.repeat("-", 70));
        System.exit(0);
    }

}
