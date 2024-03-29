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

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class SimpleTransition {

    private final int source;
    private final Token symbol;
    private final int target;

    public SimpleTransition(int source, Token symbol, int target) {
        this.source = source;
        this.symbol = symbol;
        this.target = target;
    }

    public SimpleTransition(int source, int target) {
        this.source = source;
        this.target = target;
        this.symbol = null;
    }

    public int getSource() {
        return source;
    }

    public Token getSymbol() {
        return symbol;
    }

    public int getTarget() {
        return target;
    }

    public boolean epsilon() {
        return symbol == null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transição: (");
        sb.append("origem: ").append(source).append(", ");
        sb.append("símbolo: ");
        if (epsilon()) {
            sb.append("transição em vazio");
        } else {
            sb.append(symbol.getValue());
        }
        sb.append(", destino: ").append(target);
        sb.append(")");
        return sb.toString();
    }

}
