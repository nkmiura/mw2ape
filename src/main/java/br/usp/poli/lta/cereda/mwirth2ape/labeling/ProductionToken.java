package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import br.usp.poli.lta.cereda.mwirth2ape.model.Token;

import java.util.LinkedList;
import java.util.List;

public class ProductionToken {
    private Token token;
    private List<String> leftLabels;
    private List<String> rightLabels;

    public ProductionToken(Token token) {
        this.token = token;
        this.leftLabels = new LinkedList<>();
        this.rightLabels = new LinkedList<>();
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public void setLeftLabels(List<String> leftLabels) {
        this.leftLabels = leftLabels;
    }

    public List<String> getLeftLabels() {
        return leftLabels;
    }

    public void setRightLabels(List<String> rightLabels) {
        this.rightLabels = rightLabels;
    }

    public List<String> getRightLabels() {
        return rightLabels;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.token.toString());
        return sb.toString();
    }
}
