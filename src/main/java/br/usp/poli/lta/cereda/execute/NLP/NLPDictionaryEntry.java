package br.usp.poli.lta.cereda.execute.NLP;

public class NLPDictionaryEntry {

    private String posTag;
    private String value;
    private String attribute1;
    private String attribute2;
    private String attribute3;
    private String attribute4;

    public NLPDictionaryEntry(String posTag, String value, String attribute1, String attribute2, String attribute3, String attribute4) {
        this.posTag = posTag;
        this.value = value;
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
        this.attribute3 = attribute3;
        this.attribute4 = attribute4;
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

    public String getAttribute1() {
        return attribute1;
    }

    public void setAttribute1(String attribute1) {
        this.attribute1 = attribute1;
    }

    public String getAttribute2() {
        return attribute2;
    }

    public void setAttribute2(String attribute2) {
        this.attribute2 = attribute2;
    }

    public String getAttribute3() {
        return attribute3;
    }

    public void setAttribute3(String attribute3) {
        this.attribute3 = attribute3;
    }

    public String getAttribute4() {
        return attribute4;
    }

    public void setAttribute4(String attribute4) {
        this.attribute4 = attribute4;
    }

    @Override
    public String toString() {
        return "NLPDictionaryEntry{" +
                "posTag='" + posTag + '\'' +
                ", value='" + value + '\'' +
                ", attribute1='" + attribute1 + '\'' +
                ", attribute2='" + attribute2 + '\'' +
                ", attribute3='" + attribute3 + '\'' +
                ", attribute4='" + attribute4 + '\'' +
                '}';
    }
}
