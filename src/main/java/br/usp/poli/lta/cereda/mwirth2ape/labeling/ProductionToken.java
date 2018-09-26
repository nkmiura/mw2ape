package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import java.util.LinkedList;

public class ProductionToken extends Token {

    private NTerm nterm;
    private LinkedList<LabelElement> labels;
    private LinkedList<LabelElement> preLabels;
    private LinkedList<LabelElement> postLabels;

    public ProductionToken(Token token) {
        setType(token.getType());
        setValue(token.getValue());
    }

    public ProductionToken(String type, String value) {
        setType(type);
        setValue(value);
    }

    public NTerm getNterm() {
        return nterm;
    }

    public void setNterm(NTerm nterm) {
        this.nterm = nterm;
    }

    public LinkedList<LabelElement> getLabels() {
            return labels;
    }

    public void setLabels(LinkedList<LabelElement> labels) {
        this.labels = labels;
    }

    public void pushLabel(String label) {
        if (this.labels == null) {
            this.labels = new LinkedList<>();
        }
        LabelElement newLabelElement = new LabelElement();
        newLabelElement.setValue(label);
        //newLabelElement.setProduction(null);
        this.labels.push(newLabelElement);
    }

    public void pushLabel(String label, Production production) {
        if (this.labels == null) {
            this.labels = new LinkedList<>();
        }
        LabelElement newLabelElement = new LabelElement();
        newLabelElement.setValue(label);
        newLabelElement.setProduction(production);
        this.labels.push(newLabelElement);
    }

    /////
    public LinkedList<LabelElement> getPostLabels() {
        return postLabels;
    }

    public void setPostLabels(LinkedList<LabelElement> newPostLabels) {
        if (newPostLabels != null) {
            if (this.postLabels == null) {
                this.postLabels = new LinkedList<>();
            }
            this.postLabels = newPostLabels;
        }
    }

    public void pushPostLabels(LinkedList<LabelElement> newPostLabels) {
        if (newPostLabels != null) {
            if (this.postLabels == null) {
                this.postLabels = new LinkedList<>();
            }
            newPostLabels.addAll(this.postLabels);
            this.postLabels = newPostLabels;
        }
    }

    public void addPostLabels(LinkedList<LabelElement> newPostLabels) {
        if (newPostLabels != null) {
            if (this.postLabels == null) {
                this.postLabels = new LinkedList<>();
            }
            this.postLabels.addAll(newPostLabels);
        }
    }

    /////

    public LinkedList<LabelElement> getPreLabels() {
        return preLabels;
    }

    public void setPreLabels(LinkedList<LabelElement> newPreLabels) {
        if (newPreLabels != null) {
            if (this.preLabels == null) {
                this.preLabels = new LinkedList<>();
            }
            this.preLabels = newPreLabels;
        }
    }

    public void pushPreLabels(LinkedList<LabelElement> newPreLabels) {
        if (newPreLabels != null) {
            if (this.preLabels == null) {
                this.preLabels = new LinkedList<>();
            }
            newPreLabels.addAll(this.preLabels);
            this.preLabels = newPreLabels;
        }
    }

    public void addPreLabels(LinkedList<LabelElement> newPreLabels) {
        if (newPreLabels != null) {
            if (this.preLabels == null) {
                this.preLabels = new LinkedList<>();
            }
            this.preLabels.addAll(newPreLabels);
        }
    }

    @Override
    public String toString() {
        return "ProductionToken{" +
                "nterm=" + nterm +
                ", labels=" + labels +
                ", preLabels=" + preLabels +
                ", postLabels=" + postLabels +
                '}';
    }
}
