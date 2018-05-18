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
package br.usp.poli.lta.cereda.wirth2ape.wirth;

import br.usp.poli.lta.cereda.wirth2ape.lexer.Lexer;
import br.usp.poli.lta.cereda.wirth2ape.model.Token;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class WirthLexer extends Lexer {

    private Set<String> words;

    public WirthLexer(String input) {
        super(input);
        words = new HashSet<>();
    }

    public void setWords(Set<String> words) {
        this.words = words;
    }

    @Override
    public Token recognize() {

        int state = 1;
        char symbol;
        boolean done = false;
        String value = "";
        String type = "";

        while (!done) {

            symbol = input.charAt(cursor);

            switch (state) {

                case 1:

                    if (!contains(symbol, ' ', '\t', '\n', '\r')) {

                        if (symbol == '"') {
                            type = "term";
                            value = "";
                            state = 4;
                        } else {
                            if (Character.isDigit(symbol)) {
                                type = "num";
                                value = String.valueOf(symbol);
                                state = 2;
                            } else {
                                if (Character.isLetter(symbol) &&
                                        symbol != 'ε') {
                                    type = "nterm";
                                    value = String.valueOf(symbol);
                                    state = 3;
                                } else {
                                    value = String.valueOf(symbol);
                                    type = String.valueOf(symbol);
                                    done = true;
                                }
                            }
                        }
                    }
                    cursor++;
                    break;

                case 2:

                    if (Character.isDigit(symbol)) {
                        value = value.concat(String.valueOf(symbol));
                        cursor++;
                    } else {
                        done = true;
                    }

                    break;

                case 3:

                    if (Character.isDigit(symbol) ||
                            Character.isLetter(symbol)) {
                        value = value.concat(String.valueOf(symbol));
                        cursor++;
                    } else {
                        done = true;
                    }

                    break;

                case 4:

                    if (symbol == '"') {
                        state = 1;
                    } else {
                        if (symbol == '\\') {
                            state = 5;
                        } else {
                            value = value.concat(String.valueOf(symbol));
                            state = 6;
                        }
                    }
                    cursor++;
                    break;

                case 5:

                    value = value.concat(String.valueOf(symbol));
                    state = 6;
                    cursor++;
                    break;

                case 6:

                    if (symbol == '\\') {
                        state = 5;
                    } else {
                        if (symbol == '"') {
                            done = true;
                        } else {
                            value = value.concat(String.valueOf(symbol));
                        }
                    }
                    cursor++;
                    break;

            }

            done = done || cursor == input.length();

        }

        if (value.isEmpty()) {
            return new Token();
        }

        if (type.equals("nterm")) {
            if (words.contains(value)) {
                type = "term";
            }
        }

        return new Token(type, value);
    }

    private boolean contains(char symbol, char... symbols) {
        for (char s : symbols) {
            if (symbol == s) {
                return true;
            }
        }
        return false;
    }

}
