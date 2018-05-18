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
package br.usp.poli.lta.cereda.wirth2ape.dot;

import br.usp.poli.lta.cereda.wirth2ape.ape.conversion.Sketch;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Dot {

    private final List<Sketch> transitions;

    public Dot(List<Sketch> transitions) {
        this.transitions = transitions;
    }

    public void generate(String name) {
      
        StringBuilder sb;
        Set<String> states;
        Set<String> machines = new HashSet<>();

        for (Sketch transition : transitions) {
            if (!machines.contains(transition.getName())) {
                machines.add(transition.getName());
            }
        }

        for (String machine : machines) {

            states = new HashSet<>();

            sb = new StringBuilder();
            sb.append("digraph finite_state_machine {").append("\n");
            sb.append("\t").append("rankdir=LR;").append("\n");

            for (Sketch transition : transitions) {

                if (transition.getName().equals(machine)) {

                    if (!states.contains(generateName(transition.getName(),
                            transition.getSource()))) {
                        sb.append("\t").append(createState(
                                transition.getName(),
                                transition.getSource())).append("\n");
                        states.add(generateName(transition.getName(),
                                transition.getSource()));
                    }

                    if (!states.contains(generateName(transition.getName(),
                            transition.getTarget()))) {
                        sb.append("\t").append(createState(
                                transition.getName(),
                                transition.getTarget())).append("\n");
                        states.add(generateName(
                                transition.getName(),
                                transition.getTarget()));
                    }

                    sb.append("\t").append(
                            createTransition(transition)).append("\n");

                }

            }

            sb.append(generateStart(machine)).append("\n");
            sb.append("}").append("\n");

            try {
                write(String.format(name, machine), sb.toString());
            } catch (IOException exception) {
            }

        }

    }

    private String createTransition(Sketch transition) {
        String pattern = "%s%d -> %s%d [ label = \"%s\"%s ];";
        String symbol;
        String complement = "";
        if (transition.epsilon()) {
            symbol = "ɛ";
        } else if (transition.call()) {
            symbol = transition.getSubmachine();
            complement = ", color = \"black:invis:black\"";
        } else {
            symbol = transition.getToken().getValue();
        }
        return String.format(
                pattern,
                transition.getName(),
                transition.getSource(),
                transition.getName(),
                transition.getTarget(),
                symbol,
                complement
        );
    }

    private String createState(String name, int value) {
        String pattern = "node [shape = %s, color=black, fontcolor=black,"
                + " label=\"%d\" ]; %s%d;";
        String type = value == 1 ? "doublecircle" : "circle";
        return String.format(
                pattern,
                type,
                value,
                name,
                value
        );
    }

    private String generateName(String name, int value) {
        return String.format("%s%d", name, value);
    }

    private String generateStart(String name) {
        String start = "\tnode [shape = plaintext, color=white,"
                + " fontcolor=black, label=\"%s\"]; start%s;";
        String edge = "start%s -> %s0";
        return String.format(start, name, name).
                concat("\n\t").concat(String.format(edge, name, name));
    }

    private void write(String name, String content) throws IOException {
        try (FileWriter fw = new FileWriter(new File(name))) {
            fw.write(content);
        }
    }

}
