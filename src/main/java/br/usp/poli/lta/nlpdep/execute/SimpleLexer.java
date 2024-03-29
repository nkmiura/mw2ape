package br.usp.poli.lta.nlpdep.execute;

import br.usp.poli.lta.nlpdep.mwirth2ape.lexer.Lexer;
import br.usp.poli.lta.nlpdep.mwirth2ape.model.Token;

import java.util.HashSet;
import java.util.Set;

public class SimpleLexer extends Lexer {

    private Set<String> dictionary;

    public SimpleLexer() {
        dictionary = new HashSet<>();
    }

    public SimpleLexer(String input, HashSet<String> dictionary) {
        super(input);
        this.dictionary = dictionary;
    }

    @Override
    public Token recognize() {
        char symbol;
        boolean done = false;
        String value = "";
        String type = "";

        while (!done) {
            symbol = input.charAt(cursor);
            if (this.dictionary.contains(String.valueOf(symbol))) {
                value = String.valueOf(symbol);
                type = "term";
                done = true;
            }
            cursor++;
            done = done || cursor == input.length();
        }

        if (value.isEmpty()) {
            return new Token();
        }

        return new Token(type, value);
    }

}
