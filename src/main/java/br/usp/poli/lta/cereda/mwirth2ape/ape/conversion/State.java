package br.usp.poli.lta.cereda.mwirth2ape.ape.conversion;

import br.usp.poli.lta.cereda.mwirth2ape.ape.ActionState;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.LabelElement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class State {
    private Integer id;
    private String submachine;
    //private LinkedList<LabelElement> labelElements;
    //private final List<ActionState> actionList;

    public State(Integer id, String submachine) {
        this.id = id;
        this.submachine = submachine;
        //this.labelElements = labelElements;
        //this.actionList = new ArrayList<>();
    }
/*
    public void addActionState(ActionState action) {
        actionList.add(action);
    }
*/
    public String getSubmachine() {
        return submachine;
    }

    public void setSubmachine(String submachine) {
        this.submachine = submachine;
    }
/*
    public LinkedList<LabelElement> getLabelElements() {
        return labelElements;
    }

    public void setLabelElements(LinkedList<LabelElement> labelElements) {
        this.labelElements = labelElements;
    }


    public List<ActionState> getActionList() {
        return actionList;
    }
*/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Estado ").append(this.id.toString());
/*
        if (this.labelElements != null) {
            sb.append(", Labels: {").append(this.labelElements.toString()).append("}");
        }
        else {
            sb.append(", Labels: {}");
        }
*/        return sb.toString();
    }
}
