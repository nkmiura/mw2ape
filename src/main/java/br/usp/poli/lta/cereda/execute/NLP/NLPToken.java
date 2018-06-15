package br.usp.poli.lta.cereda.execute.NLP;

import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import java.util.ArrayList;

public class NLPToken extends Token {
    ArrayList<NLPWord> nlpWords;

    public NLPToken () {
        this.nlpWords = new ArrayList<>();
    }

    public NLPToken(ArrayList<NLPWord> nlpWords) {
        this.nlpWords = nlpWords;
    }

    public ArrayList<NLPWord> getNlpWords() {
        return nlpWords;
    }

    public void setNlpWords(ArrayList<NLPWord> nlpWords) {
        this.nlpWords = nlpWords;
    }

    public void addNlpWord(NLPWord nlpWord) {
        if (!nlpWord.equals("")) {
            if (this.nlpWords == null) {
                this.nlpWords = new ArrayList<>();
                this.nlpWords.add(nlpWord);
            }
        }
    }

    @Override
    public String toString() {
        return "NLPToken{" +
                "NLPToken=" + nlpWords +
                '}';
    }
}
