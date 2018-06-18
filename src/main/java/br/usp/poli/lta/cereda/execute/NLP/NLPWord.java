package br.usp.poli.lta.cereda.execute.NLP;

public class NLPWord {
    String posTag;
    String value;
    NLPDictionaryEntry nlpDictionaryEntry;

    public NLPWord() {
        this.nlpDictionaryEntry = new NLPDictionaryEntry("","","","","","");
        this.posTag = "";
        this.value = "";
    }

    public NLPWord(String posTag, String value) {
        this.posTag = posTag;
        this.value = value;

    }

    public NLPWord(String posTag, String value, NLPDictionaryEntry nlpDictionaryEntry) {
        this.posTag = posTag;
        this.value = value;
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
}
