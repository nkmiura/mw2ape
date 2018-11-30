package br.usp.poli.lta.nlpdep.execute.NLP.dependency;

public class DepStackElementEmpty extends DepStackElement {
    private Integer idSentence;

    public DepStackElementEmpty() {
        this.value = "ε";
        this.type = "empty";
    }
}
