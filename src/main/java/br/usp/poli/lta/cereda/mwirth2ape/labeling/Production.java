package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import java.util.LinkedList;
import java.util.List;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;

public class Production {
    private int index;
    private Token identifier;
    private List<ProductionToken> expression;

    public Production(int index) {
        this.index = index;
        this.identifier = new Token();
        this.expression = new LinkedList<>();
    }

    public void setIdentifier(Token identifier) {
        this.identifier = identifier;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public void setExpression(List<ProductionToken> expression) {
        this.expression = expression;
    }

    public List<ProductionToken> getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nproduction ").append(index).append(":");
        sb.append(" identifier: " + identifier.toString());
        sb.append(" expression: " + expression.toString());
        return sb.toString();
    }
}
