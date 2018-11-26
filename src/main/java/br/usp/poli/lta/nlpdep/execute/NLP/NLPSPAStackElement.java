package br.usp.poli.lta.nlpdep.execute.NLP;

import br.usp.poli.lta.nlpdep.mwirth2ape.ape.Transition;

public class NLPSPAStackElement {
    private Integer returnState;
    private Transition transition;

    public NLPSPAStackElement(Integer returnState, Transition transition) {
        this.returnState = returnState;
        this.transition = transition;
    }

    public Integer getReturnState() {
        return returnState;
    }

    public Transition getTransition() {
        return transition;
    }

    public void setReturnState(Integer returnState) {
        this.returnState = returnState;
    }

    public void setTransition(Transition transition) {
        this.transition = transition;
    }

    @Override
    public String toString() {
        return "NLPSPAStackElement{" +
                "returnState=" + returnState +
                ", transition=" + transition +
                '}';
    }
}
