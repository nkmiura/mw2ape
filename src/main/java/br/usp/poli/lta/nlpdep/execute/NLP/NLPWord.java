package br.usp.poli.lta.nlpdep.execute.NLP;

public class NLPWord {
    String posTag;
    String value;
    Integer sentenceID = 0; // valor que indica que não foi inicializado
    NLPDictionaryEntry nlpDictionaryEntry;

    public NLPWord() {
        this.nlpDictionaryEntry = new NLPDictionaryEntry();
        this.posTag = "";
        this.value = "";
    }

    public NLPWord(String posTag, String value, Integer sentenceID) {
        this.posTag = posTag;
        this.value = value;
        this.sentenceID = sentenceID;
    }

    public NLPWord(String posTag, String value, NLPDictionaryEntry nlpDictionaryEntry, Integer sentenceID) {
        this.posTag = posTag;
        this.value = value;
        this.sentenceID = sentenceID;
        this.nlpDictionaryEntry = nlpDictionaryEntry;
    }

    public NLPDictionaryEntry getNlpDictionaryEntry() {
        return nlpDictionaryEntry;
    }

    public void setNlpDictionaryEntry(NLPDictionaryEntry nlpDictionaryEntry) {
        this.nlpDictionaryEntry = nlpDictionaryEntry;
    }

    public String getPosTag() {
        return posTag;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getSentenceID() { return sentenceID; }

    public void setSentenceID(Integer sentenceID) { this.sentenceID = sentenceID; }

    @Override
    public String toString() {
        return "NLPWord{" +
                "sentenceID=" + sentenceID +
                ", posTag='" + posTag + '\'' +
                ", value='" + value + '\'' +
                ", nlpDictionaryEntry=" + nlpDictionaryEntry +
                '}';
    }
}
