package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import sun.awt.image.ImageWatched;

import java.util.LinkedList;

public class ProductionToken extends Token {

    private NTerm nterm;
    private LinkedList<LabelElement> labels;
    private LinkedList<LabelElement> preLabels;
    private LinkedList<LabelElement> postLabels;

    public ProductionToken(Token token) {
        setType(token.getType());
        setValue(token.getValue());
        this.preLabels = new LinkedList<>();
        this.postLabels = new LinkedList<>();
    }

    public ProductionToken(String type, String value) {
        setType(type);
        setValue(value);
        this.preLabels = new LinkedList<>();
        this.postLabels = new LinkedList<>();
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
            LinkedList<LabelElement> addPostLabels = (LinkedList<LabelElement>) newPostLabels.clone();
            addPostLabels.addAll(this.postLabels);
            this.postLabels = addPostLabels;
        }
    }

    public void addPostLabels(LinkedList<LabelElement> newPostLabels) {
        if (newPostLabels != null) {
            if (this.postLabels == null) {
                this.postLabels = new LinkedList<>();
            }
            LinkedList<LabelElement> addPostLabels = (LinkedList<LabelElement>) newPostLabels.clone();
            this.postLabels.addAll(addPostLabels);
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
            LinkedList<LabelElement> addPreLabels = (LinkedList<LabelElement>) newPreLabels.clone();
            addPreLabels.addAll(this.preLabels);
            this.preLabels = addPreLabels;
        }
    }

    public void addPreLabels(LinkedList<LabelElement> newPreLabels) {
        if (newPreLabels != null) {
            if (this.preLabels == null) {
                this.preLabels = new LinkedList<>();
            }
            LinkedList<LabelElement> addPreLabels = (LinkedList<LabelElement>) newPreLabels.clone();
            this.preLabels.addAll(addPreLabels);
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
