package br.usp.poli.lta.nlpdep.mwirth2ape.ape.conversion;

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
