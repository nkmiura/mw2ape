package br.usp.poli.lta.nlpdep.execute.NLP;

import br.usp.poli.lta.nlpdep.mwirth2ape.lexer.Lexer;
import br.usp.poli.lta.nlpdep.mwirth2ape.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NLPLexer extends Lexer {

    private static final Logger logger = LoggerFactory.
            getLogger(NLPLexer.class);

    int state;
    NLPDictionary nlpDictionary;
    Integer wordCounter = 1;
    private Set<String> termDictionary;

    public NLPLexer(String input, NLPDictionary nlpDictionary, Set<String> termDictionary)
    {
        this.input = input;
        this.nlpDictionary = nlpDictionary;
        this.termDictionary = termDictionary;
        //this.state = state;
    }

    public NLPLexer(String input, String yamlDictionaryFileName, HashSet<String> termDictionary) {
        super(input);
        nlpDictionary = new NLPDictionary(yamlDictionaryFileName);
        this.termDictionary = termDictionary;
        splitPrepDetUnion();
    }


    public NLPLexer clone(NLPLexer nlpLexer)
    {
        NLPLexer newLexer = new NLPLexer(this.input, this.nlpDictionary, this.termDictionary);
        newLexer.state = nlpLexer.state;
        newLexer.cursor = nlpLexer.cursor;
        newLexer.buffer = nlpLexer.buffer.clone();
        newLexer.wordCounter = nlpLexer.wordCounter;
        return newLexer;
    }

    @Override
    public Token recognize() {
        Token newToken = new Token();

        int state = 0; // estado inicial
        char symbol;
        boolean done = false;
        String value = "";
        String type = "";

        while (!done) {

            symbol = input.charAt(cursor);

            switch (state) {
                case 0:
                    if (isPunct(symbol)) {
                        type = "punct";
                        value = String.valueOf(symbol);
                        done = true;
                    } else if (contains(symbol, ' ', '\t', '\n', '\r')) {
                    } else {
                        value = value.concat(String.valueOf(symbol));
                        state = 1;
                    }
                    cursor++;
                    break;
                case 1:
                    if (isPunct(symbol)) {
                        done = true;
                    } else if (contains(symbol, ' ', '\t', '\n', '\r')) {
                        done = true;
                    } else {
                        value = value.concat(String.valueOf(symbol));
                        cursor++;
                    }
                    break;
            }
            done = done || cursor == input.length();
        }

        // não há mais palavras
        if (!value.isEmpty()) {
            newToken.setValue(value);
            // Pontuacao
            if (type.equals("punct")) {
                NLPWord nlpWord = new NLPWord(type, value, wordCounter);
                wordCounter++;
                ArrayList<String> newDictionaryEntry = new ArrayList<>();
                newDictionaryEntry.add(value);
                newDictionaryEntry.add("punct");
                NLPDictionaryEntry nlpDictionaryEntry = new NLPDictionaryEntry(newDictionaryEntry);
                nlpWord.setNlpDictionaryEntry(nlpDictionaryEntry);
                NLPToken nlpToken = new NLPToken();
                nlpToken.addNlpWord(nlpWord);
                newToken.setNlpToken(nlpToken);
                newToken.setType("term");
            } else {
                // Procura a palavra no dicionario
                ArrayList<NLPDictionaryEntry> nlpDictionaryEntries = nlpDictionary.getEntry(value);

                if (!nlpDictionaryEntries.isEmpty()) {
                    logger.debug("Palavra {} encontrada no dicionário com {} classificações.",
                            value, nlpDictionaryEntries.size());
                    NLPToken nlpToken = new NLPToken();
                    for (NLPDictionaryEntry dictionaryEntry: nlpDictionaryEntries) {
                        NLPWord nlpWord = new NLPWord(dictionaryEntry.getPosTag(),
                                value, dictionaryEntry, wordCounter);
                        nlpToken.addNlpWord(nlpWord);
                    }
                    wordCounter++;
                    newToken.setNlpToken(nlpToken);
                    newToken.setType("term");
                }
            }
        }

        return newToken;
    }

    private boolean isPunct(char symbol) {
        boolean result = false;
        switch (symbol) {
            case ',':
            case ';':
            case '.':
            case ':':
            case '!':
            case '?':
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    private boolean contains(char symbol, char... symbols) {
        for (char s : symbols) {
            if (symbol == s) {
                return true;
            }
        }
        return false;
    }

    private void splitPrepDetUnion() {
        //String sentence = sentence;
        input = input.replaceAll(" à ", " a a ");
        input = input.replaceAll("À ", "A a ");
        input = input.replaceAll(" ao ", " a o ");
        input = input.replaceAll("Ao ", "A o ");
        input = input.replaceAll(" do ", " de o ");
        input = input.replaceAll(" dos ", " de os ");
        input = input.replaceAll("Do ", "De o ");
        input = input.replaceAll("Dos ", "De os ");
        input = input.replaceAll(" da ", " de a ");
        input = input.replaceAll(" das ", " de as ");
        input = input.replaceAll("Da ", "De a ");
        input = input.replaceAll("Das ", "De as ");
        input = input.replaceAll(" no ", " em+ o ");
        input = input.replaceAll(" nos ", " em+ os ");
        input = input.replaceAll("No ", "Em+ o ");
        input = input.replaceAll("Nos ", "Em+ os ");
        input = input.replaceAll(" na ", " em+ a ");
        input = input.replaceAll(" numa ", " em+ uma ");
        input = input.replaceAll(" numas ", " em+ umas ");
        input = input.replaceAll(" nas ", " em+ as ");
        input = input.replaceAll("Na ", "Em+ a ");
        input = input.replaceAll("Nas ", "Em+ as ");
        input = input.replaceAll("Numa ", "Em+ uma ");
        input = input.replaceAll("Numas ", "Em+ umas ");
        input = input.replaceAll(" àquela ", " a_  aquela ");
        input = input.replaceAll(" àquele ", " a_  aquele ");

   }
}
