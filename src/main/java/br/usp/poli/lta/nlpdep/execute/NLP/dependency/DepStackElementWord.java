package br.usp.poli.lta.nlpdep.execute.NLP.dependency;

import br.usp.poli.lta.nlpdep.execute.NLP.NLPDictionaryEntry;

public class DepStackElementWord extends DepStackElement {
    private Integer idSentence;
    private NLPDictionaryEntry nlpDictionaryEntry;

    public DepStackElementWord(String value) {
        this.value = value;
        this.type = "word";
    }

    public Integer getIdSentence() {
        return idSentence;
    }

    public void setIdSentence(Integer idSentence) {
        this.idSentence = idSentence;
    }

    public NLPDictionaryEntry getNlpDictionaryEntry() {
        return nlpDictionaryEntry;
    }

    public void setNlpDictionaryEntry(NLPDictionaryEntry nlpDictionaryEntry) {
        this.nlpDictionaryEntry = nlpDictionaryEntry;
    }
}
