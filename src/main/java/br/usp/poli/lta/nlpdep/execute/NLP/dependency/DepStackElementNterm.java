package br.usp.poli.lta.nlpdep.execute.NLP.dependency;

import java.util.List;

public class DepStackElementNterm extends DepStackElement {
    private Integer mainConstituent;
    private String headDirection;
    private List<DepPatternConstituent> depPatternConstituents = null;

    public DepStackElementNterm(String value) {
        this.value = value;
        this.type = "nterm";
    }
}
