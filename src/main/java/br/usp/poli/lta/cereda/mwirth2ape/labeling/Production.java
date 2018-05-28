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
    private String recursion;
    public LinkedList<ProductionToken> expression;
    public LinkedList<ProductionToken> labels;
    public LinkedList<ProductionToken> all;

    public Production(int index) {
        this.index = index;
        this.identifier = "";
        this.recursion = "";
        this.expression = new LinkedList<>();
        this.labels = new LinkedList<>();
        this.all = new LinkedList<>();
    }

    public void setIdentifier(String value) {
        this.identifier = value;
    }

    public void setIdentifier(Token token) {
        this.identifier = token.getValue() + '_' + this.index;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getRecursion() { return recursion; }

    public void setRecursion(String recursion) { this.recursion = recursion; }

    public void addExpressionToken(ProductionToken productionToken) {
        this.expression.add(productionToken);
    }

    public ProductionToken getLastProductionTerm() {
        logger.debug("   getLastProductionTerm: " + this.expression.get(this.expression.size() - 2).getType() + ", " + this.expression.get(this.expression.size() - 2).getValue());
        return this.expression.get(this.expression.size() - 2);
        //return this.expression.getLast();
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
        sb.append("\nproduction ").append(this.index);
        sb.append(": identifier: " + this.identifier);
        sb.append("\n recursion: " + this.recursion);
        sb.append("\n expression: " + this.expression.toString());
        sb.append("\n labels: " + this.labels.toString());
        sb.append("\n all: " + this.all.toString());

        return sb.toString();
    }
}
