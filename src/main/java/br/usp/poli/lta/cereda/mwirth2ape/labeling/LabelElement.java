package br.usp.poli.lta.cereda.mwirth2ape.labeling;

public class LabelElement {
    private String value;
    private Production production;

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setProduction(Production production) {
        this.production = production;
    }

    public Production getProduction() {
        return this.production;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{" + this.value + ", ");
        if (this.production == null) {
            sb.append("}");
        }
        else {
            sb.append(this.production.getIdentifier() + "}");
        }
        return sb.toString();
    }
}
