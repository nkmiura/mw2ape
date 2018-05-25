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
    public LinkedList<ProductionToken> labels;
    public LinkedList<ProductionToken> all;

    public Production(int index) {
        this.index = index;
        this.identifier = "";
        this.expression = new LinkedList<>();
        this.labels = new LinkedList<>();
        this.all = new LinkedList<>();
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

    public void addExpressionToken(ProductionToken productionToken) {
        this.expression.add(productionToken);
    }

    public void addLabelsToken(ProductionToken labelToken) {
        this.labels.add(labelToken);
    }

    public void addAllToken(ProductionToken allToken) {
        this.all.add(allToken);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nproduction ").append(index).append(":");
        sb.append(" identifier: " + identifier);
        sb.append("\n expression: " + expression.toString());
        sb.append("\n labels: " + labels.toString());
        sb.append("\n all: " + all.toString());

        return sb.toString();
    }
}
