package br.usp.poli.lta.cereda.execute.NLP;

import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import java.util.ArrayList;

public class NLPToken extends Token {
    ArrayList<NLPDictionaryEntry> NLPToken;

    public NLPToken(ArrayList<NLPDictionaryEntry> NLPToken) {
        this.NLPToken = NLPToken;
    }

    public ArrayList<NLPDictionaryEntry> getNLPToken() {
        return NLPToken;
    }

    public void setNLPToken(ArrayList<NLPDictionaryEntry> NLPToken) {
        this.NLPToken = NLPToken;
    }

    @Override
    public String toString() {
        return "NLPToken{" +
                "NLPToken=" + NLPToken +
                '}';
    }
}
