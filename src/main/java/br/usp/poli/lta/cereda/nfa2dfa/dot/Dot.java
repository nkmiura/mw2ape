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
package br.usp.poli.lta.cereda.nfa2dfa.dot;

import br.usp.poli.lta.cereda.nfa2dfa.utils.SimpleTransition;
import br.usp.poli.lta.cereda.nfa2dfa.utils.Triple;
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
    
    private final StringBuilder sb;

    public Dot() {
        sb = new StringBuilder();
        sb.append("digraph finite_state_machine {").append("\n");
        sb.append("\t").append("rankdir=LR;").append("\n");
    }
    
    public void append(String name, String prefix, Triple<Integer, Set<Integer>,
                    List<SimpleTransition>> elements) {
        
        Set<Integer> states = new HashSet<>();   
        
        sb.append("\t").append(generateStart(name, prefix, elements.getFirst(),
                elements.getSecond().contains(elements.getFirst()))).
                append("\n");
        states.add(elements.getFirst());
        
        for (int state : elements.getSecond()) {
            if (!states.contains(state)) {
                states.add(state);
                sb.append("\t").append(createState(prefix, state, true)).
                        append("\n");
            }
        }
        
        for (SimpleTransition transition : elements.getThird()) {
            if (!states.contains(transition.getSource())) {
                states.add(transition.getSource());
                sb.append("\t").append(createState(prefix,
                        transition.getSource(), false)).append("\n");
            }
            
            if (!states.contains(transition.getTarget())) {
                states.add(transition.getTarget());
                sb.append("\t").append(createState(prefix,
                        transition.getTarget(), false)).append("\n");
            }
            
            sb.append("\t").append(createTransition(prefix,
                    transition)).append("\n");
        }
        
    }
    
    public void dump(String filename) {
        sb.append("}").append("\n");
        try {
            FileWriter fw = new FileWriter(new File(filename));
            fw.write(sb.toString());
            fw.close();
        }
        catch (IOException exception) {
            // do nothing
        }
    }
    
    private String createTransition(String prefix,
            SimpleTransition transition) {
        String pattern = "%s%d -> %s%d [ label = \"%s\" ];";
        String symbol;
        if (transition.epsilon()) {
            symbol = "ɛ";
            //symbol = "ɛ,";
        }
        else {
            symbol = transition.getSymbol().getValue();
        }
        return String.format(
                pattern,
                prefix,
                transition.getSource(),
                prefix,
                transition.getTarget(),
                symbol
                );
    }
    
    private String createState(String prefix, int state, boolean flag) {
        String pattern = "node [shape = %s, color=black, fontcolor=black, label=\"%d\" ]; %s%d;";
        String type = flag ? "doublecircle" : "circle";
        return String.format(
                pattern,
                type,
                state,
                prefix,
                state
                );
    }
    
    private String generateStart(String name, String prefix,
            int state, boolean flag) {
        String output = createState(prefix, state, flag);
        String start = "\tnode [shape = plaintext, color=white, fontcolor=black, label=\"%s\"]; start%s;";
        String edge = "start%s -> %s%d";
        return output.concat("\n\t").concat(String.format(start, name, prefix)).
                concat("\n\t").concat(String.format(edge, prefix,
                        prefix, state));
    }    
    
}
