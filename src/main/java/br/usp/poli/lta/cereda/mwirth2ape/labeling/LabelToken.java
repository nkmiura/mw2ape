package br.usp.poli.lta.cereda.mwirth2ape.labeling;

public class LabelToken {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{" + this.value + "}");
        return sb.toString();
    }
}
