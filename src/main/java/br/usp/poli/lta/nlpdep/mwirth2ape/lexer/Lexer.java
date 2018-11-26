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
package br.usp.poli.lta.nlpdep.mwirth2ape.lexer;

import br.usp.poli.lta.nlpdep.mwirth2ape.model.Token;
import br.usp.poli.lta.nlpdep.mwirth2ape.structure.Stack;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public abstract class Lexer {
    
    protected String input;
    protected int cursor;
    protected Stack<Token> buffer;

    public Lexer() {
        buffer = new Stack<>();
    }

    public Lexer(String input) {
        this.input = input;
        this.cursor = 0;
        buffer = new Stack<>();
    }

    public boolean hasNext() {
        return cursor < input.length() || !buffer.isEmpty();
    }
    
    public Token getNext() {
        return obtain();
    }
    
    private Token obtain() {
        
        if (!hasNext()) {
            return new Token();
        }
        
        if (buffer.isEmpty()) {
            return recognize();
        }
        else {
            return buffer.pop();
        }
    }

    public abstract Token recognize();

    public boolean bufferIsEmpty() {
        return buffer.isEmpty();
    }

    
    public void push(Token token) {
        buffer.push(token);
    }
    
}
