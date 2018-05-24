package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import java.util.LinkedList;


public class ProductionToken {
    private String type;
    private String value;
    private Production production;

    public ProductionToken( ) {
        this.type = "";
        this.value = "";
    }


    public ProductionToken(Token token) {
        this.type = token.getType();
        this.value = token.getValue();
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

    //@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{" + this.type + ", " + this.value + ",");
        if (this.production == null) {
            sb.append("}");
        }
        else {
            sb.append(this.production.toString() + "}");
        }
        return sb.toString();
    }
}
