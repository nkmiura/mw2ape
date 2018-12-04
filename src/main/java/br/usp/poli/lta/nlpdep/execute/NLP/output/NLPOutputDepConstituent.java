package br.usp.poli.lta.nlpdep.execute.NLP.output;

import br.usp.poli.lta.nlpdep.execute.NLP.dependency.DepPatternConstituent;

public class NLPOutputDepConstituent extends DepPatternConstituent {

    public NLPOutputDepConstituent copyNLPOutputDepConstituent() {
        NLPOutputDepConstituent nlpOutputDepConstituent = new NLPOutputDepConstituent();

        nlpOutputDepConstituent.setId(this.getId());
        nlpOutputDepConstituent.setValue(this.getValue());
        nlpOutputDepConstituent.setType(this.getType());
        nlpOutputDepConstituent.setHead(this.getHead());
        nlpOutputDepConstituent.setDepRel(this.getDepRel());
        nlpOutputDepConstituent.setLeftDeps(this.getLeftDeps());
        nlpOutputDepConstituent.setRightDeps(this.getRightDeps());

        return nlpOutputDepConstituent;
    }

    public void copyDepPatternConstituent(DepPatternConstituent depPatternConstituent) {
        this.setId(depPatternConstituent.getId());
        this.setValue(depPatternConstituent.getValue());
        this.setType(depPatternConstituent.getType());
        this.setHead(depPatternConstituent.getHead());
        this.setDepRel(depPatternConstituent.getDepRel());
        this.setLeftDeps(depPatternConstituent.getLeftDeps());
        this.setRightDeps(depPatternConstituent.getRightDeps());
    }

}
