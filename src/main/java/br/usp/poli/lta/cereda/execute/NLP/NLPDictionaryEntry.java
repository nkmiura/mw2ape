package br.usp.poli.lta.cereda.execute.NLP;

import java.util.ArrayList;

public class NLPDictionaryEntry {

    private String posTag;
    private String canonical;
    private String attribute1;
    private String attribute2;
    private String attribute3;
    private String attribute4;

    public NLPDictionaryEntry(String posTag, String canonical, String attribute1, String attribute2, String attribute3, String attribute4) {
        this.posTag = posTag;
        this.canonical = canonical;
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
        this.attribute3 = attribute3;
        this.attribute4 = attribute4;
    }

    public NLPDictionaryEntry(ArrayList<String> attributes) {
        if (attributes.size() == 6) {
            this.posTag = attributes.get(0);
            this.canonical = attributes.get(1);
            this.attribute1 = attributes.get(2);
            this.attribute2 = attributes.get(3);
            this.attribute3 = attributes.get(4);
            this.attribute4 = attributes.get(5);
        }
    }

    public String getPosTag() {
        return posTag;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    public String getCanonical() {
        return canonical;
    }

    public void setCanonical(String canonical) {
        this.canonical = canonical;
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
                ", canonical='" + canonical + '\'' +
                ", attribute1='" + attribute1 + '\'' +
                ", attribute2='" + attribute2 + '\'' +
                ", attribute3='" + attribute3 + '\'' +
                ", attribute4='" + attribute4 + '\'' +
                '}';
    }
}