package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import java.util.LinkedList;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Production {
    private static final Logger logger = LoggerFactory.
            getLogger(Production.class);

    private int index;
    private String identifier;
    public LinkedList<ProductionToken> expression;
    public LinkedList<LabelToken> labels;

    public Production(int index) {
        this.index = index;
        this.identifier = new String();
        this.expression = new LinkedList<>();
        this.labels = new LinkedList<>();
    }

    public void setIdentifier(String value) {
        this.identifier = value + '_' + this.index;
    }

    public void setIdentifier(Token token) {
        this.identifier = token.getValue() + '_' + this.index;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void addProductionToken(ProductionToken productionToken) {
        this.expression.add(productionToken);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nproduction ").append(index).append(":");
        sb.append(" identifier: " + identifier);
        sb.append(" expression: " + expression.toString());
        return sb.toString();
    }
}
