package br.usp.poli.lta.cereda.mwirth2ape.ape.conversion;

import br.usp.poli.lta.cereda.mwirth2ape.labeling.LabelElement;

import java.util.LinkedList;

public class State {
    private Integer id;
    private String submachine;
    private LinkedList<LabelElement> labelElements;

    public State(Integer id, String submachine, LinkedList<LabelElement> labelElements) {
        this.id = id;
        this.submachine = submachine;
        this.labelElements = labelElements;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubmachine() {
        return submachine;
    }

    public void setSubmachine(String submachine) {
        this.submachine = submachine;
    }

    public LinkedList<LabelElement> getLabelElements() {
        return labelElements;
    }

    public void setLabelElements(LinkedList<LabelElement> labelElements) {
        this.labelElements = labelElements;
    }
}
