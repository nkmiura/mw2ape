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
package br.usp.poli.lta.nlpdep.mwirth2ape.exporter;

import java.util.List;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Spec {

    private String name;
    private int initial;
    private List<Integer> accepting;
    private List<Transition> Transitions;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
   
    public int getInitial() {
        return initial;
    }

    public void setInitial(int initial) {
        this.initial = initial;
    }

    public List<Transition> getTransitions() {
        return Transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.Transitions = transitions;
    }

    public List<Integer> getAccepting() {
        return accepting;
    }

    public void setAccepting(List<Integer> accepting) {
        this.accepting = accepting;
    }

}
