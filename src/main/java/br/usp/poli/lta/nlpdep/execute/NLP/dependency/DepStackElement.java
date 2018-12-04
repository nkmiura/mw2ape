package br.usp.poli.lta.nlpdep.execute.NLP.dependency;

public class DepStackElement {

    protected String value;
    protected String type;

    public DepStackElement() {
        this.value = "";
        this.type = "";
    }

    public DepStackElement (String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DepStackElement{" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
