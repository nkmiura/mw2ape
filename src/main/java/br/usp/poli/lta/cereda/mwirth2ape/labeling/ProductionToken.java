package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import br.usp.poli.lta.cereda.mwirth2ape.model.Token;


public class ProductionToken extends Token {
/*    private String type;
    private String value; */
    private NTerm nterm;

    public ProductionToken(Token token) {
        setType(token.getType());
        setValue(token.getValue());
    }

    public ProductionToken(Token token, NTerm nterm) {
        setType(token.getType());
        setValue(token.getValue());
        this.nterm = nterm;
    }

    public NTerm getNterm() {
        return nterm;
    }

    public void setNterm(NTerm nterm) {
        this.nterm = nterm;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{" + getType() + ", " + getValue() + ",");
        if (this.nterm == null) {
            sb.append("}");
        }
        else {
            sb.append(this.nterm.getValue() + "}");
        }
        return sb.toString();
    }
}
