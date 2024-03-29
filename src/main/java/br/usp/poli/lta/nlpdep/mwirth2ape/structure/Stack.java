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
package br.usp.poli.lta.nlpdep.mwirth2ape.structure;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Paulo Roberto Massa Cereda, Newton Kiyotaka Miura
 * @version 1.0
 * @since 1.0
 */
public class Stack<T> {

    private final List<T> list;

    public Stack() {
        this.list = new ArrayList<>();
    }

    public Stack clone() {
        Stack newStack = new Stack();
        newStack.list.addAll(this.list);
        return newStack;
    }

    public void push(T entry) {
        list.add(entry);
    }

    public T pop() {
        return list.remove(list.size() - 1);
    }

    public T top() {
        if (list.size() > 0) {
            return list.get(list.size() - 1);
        } else {
            return null;
        }
    }

    public T bottom() {
        return list.get(0);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void clear() {
        list.clear();
    }

    public List<T> getList() {
        return list;
    }

    public int size() {
        return list.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pilha: [");
        if (isEmpty()) {
            sb.append("vazia");
        }
        else {
            try {
                List<T> newList = new  ArrayList<>();
                newList.addAll(list);
                sb.append(StringUtils.join(newList, ", "));
            }
            catch (Exception exception) {
                System.out.println("Thread ID " + Thread.currentThread().getId() + ": An exception was thrown in Stack toString operation - Exception: " + exception.toString());
                Thread.interrupted();
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(list).build();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Stack<?> reference = (Stack<?>) object;
        return new EqualsBuilder().append(list, reference.list).isEquals();
    }
    
}
