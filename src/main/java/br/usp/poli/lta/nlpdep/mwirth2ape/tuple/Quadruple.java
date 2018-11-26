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
package br.usp.poli.lta.nlpdep.mwirth2ape.tuple;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Quadruple<A, B, C, D> {

    private A first;
    private B second;
    private C third;
    private D fourth;

    public Quadruple(A first, B second, C third, D four) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = four;
    }

    public Quadruple() {
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public C getThird() {
        return third;
    }

    public void setThird(C third) {
        this.third = third;
    }

    public D getFourth() {
        return fourth;
    }

    public void setFourth(D fourth) {
        this.fourth = fourth;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(first);
        sb.append(", ").append(second);
        sb.append(", ").append(third);
        sb.append(", ").append(fourth);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(second).
                append(third).append(fourth).build();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Quadruple<?, ?, ?, ?> reference = (Quadruple<?, ?, ?, ?>) object;
        return new EqualsBuilder().append(second, reference.second).
                append(third, reference.third).
                append(fourth, reference.fourth).isEquals();
    }

}
