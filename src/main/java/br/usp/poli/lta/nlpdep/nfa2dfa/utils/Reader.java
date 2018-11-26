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
package br.usp.poli.lta.nlpdep.nfa2dfa.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.yaml.snakeyaml.Yaml;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Reader {
    
    private static String name;
    
    public static Triple<
        Integer,
         Set<Integer>,
         List<SimpleTransition>
        > read(String reference) throws Exception {

            Yaml yaml = new Yaml();
            Spec spec = yaml.loadAs(reference, Spec.class);
            name = spec.getName();
            
            List<SimpleTransition> transitions = new ArrayList<>();
            for (Transition t : spec.getTransitions()) {
                if (t.getSymbol() == null) {
                    transitions.add(
                            new SimpleTransition(
                                    t.getFrom(),
                                    t.getTo()
                            )
                    );
                }
                else {
                    transitions.add(
                            new SimpleTransition(
                                    t.getFrom(),
                                    new Token(
                                            t.getSymbol(),
                                            t.getSymbol()
                                    ),
                                    t.getTo()
                            )
                    );
                }
            }
            
            Triple<
                    Integer,
                    Set<Integer>,
                    List<SimpleTransition>
                    > result = new Triple<>();
            
            result.setFirst(spec.getInitial());
            result.setSecond(new HashSet<>(spec.getAccepting()));
            result.setThird(transitions);
            
            return result;
    }

    public static String getName() {
        return name == null ? "M" : name;
    }
   
}
