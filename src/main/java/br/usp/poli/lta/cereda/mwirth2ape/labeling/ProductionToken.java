package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import java.util.LinkedList;

public class ProductionToken extends Token {

    private NTerm nterm;
    private LinkedList<LabelElement> labels;

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
        //if (labels != null) {
            return labels;
        //}
        //else {
        //    return null;
        //}
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getType().equals("label")) {
            sb.append("{" + getType() + ", ");
            if (this.labels == null) {
                sb.append("}");
            }
            else {
                sb.append(this.labels.toString() + "}");
            }
        }
        else {
            sb.append("{" + getType() + ", " + getValue() + ", ");
            if (this.nterm == null) {
                sb.append("}");
            }
            else {
                sb.append(this.nterm.getValue() + "}");
            }
        }
        return sb.toString();
    }
}
