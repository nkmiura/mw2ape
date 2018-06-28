package br.usp.poli.lta.cereda.execute.NLP;

import java.util.ArrayList;

public class NLPDictionaryEntry {

    private String posTag;
    private String canonical;
    private ArrayList<String> attributes;

    public NLPDictionaryEntry() {
        this.posTag = "";
        this.canonical = "";
        this.attributes = new ArrayList<>();
    }

    public NLPDictionaryEntry(ArrayList<String> attributes) {
        this.posTag = attributes.get(0);
        this.canonical = attributes.get(1);
        this.attributes = new ArrayList<>();
        for (int i = 2; i < attributes.size(); i++) {
            this.attributes.add(attributes.get(i));
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

    public ArrayList<String> getAttributes() {
        return this.attributes;
    }

    @Override
    public String toString() {
        return "NLPDictionaryEntry{" +
                "posTag='" + posTag + '\'' +
                ", canonical='" + canonical + '\'' +
                ", attributes=" + attributes.toString() +
                '}';
    }
}
