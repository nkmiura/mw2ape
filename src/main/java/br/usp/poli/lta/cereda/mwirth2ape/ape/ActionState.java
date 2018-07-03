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
package br.usp.poli.lta.cereda.mwirth2ape.ape;

import br.usp.poli.lta.cereda.mwirth2ape.labeling.LabelElement;
import java.util.LinkedList;
import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Paulo Roberto Massa Cereda, Newton Kiyotaka Miura
 * @version 1.1
 * @since 1.0
 */
public abstract class ActionState {

    protected static String name;

    public ActionState () {}

    public ActionState(String name) {
        this.name = name;
    }

    public abstract void execute(LinkedList<LabelElement> labels, Stack<String> transducerStack);
    //public abstract List execute(int state, List tree);

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).build();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ActionState reference = (ActionState) object;
        return new EqualsBuilder().append(name, reference.getName()).isEquals();
    }

}
