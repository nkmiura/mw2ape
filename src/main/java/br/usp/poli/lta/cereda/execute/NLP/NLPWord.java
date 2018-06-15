package br.usp.poli.lta.cereda.execute.NLP;

public class NLPWord {
    String type;
    String value;
    NLPDictionaryEntry nlpDictionaryEntry;

    public NLPWord() {
        this.nlpDictionaryEntry = new NLPDictionaryEntry("","","","","","");
        this.type = "";
        this.value = "";
    }

    public NLPWord(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public NLPWord(String type, String value, NLPDictionaryEntry nlpDictionaryEntry) {
        this.type = type;
        this.value = value;
        this.nlpDictionaryEntry = nlpDictionaryEntry;
    }

    public NLPDictionaryEntry getNlpDictionaryEntry() {
        return nlpDictionaryEntry;
    }

    public void setNlpDictionaryEntry(NLPDictionaryEntry nlpDictionaryEntry) {
        this.nlpDictionaryEntry = nlpDictionaryEntry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
