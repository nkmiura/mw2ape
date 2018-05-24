package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import java.util.LinkedList;

public class NTerm {
    private String value;
    private int counter;
    public LinkedList<Production> productions;

    public NTerm(String value)
    {
        setValue(value);
        this.counter = 1;
        this.productions = new LinkedList<>();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void add(String identifier)
    {
        Production newProduction = new Production(counter);
        newProduction.setIdentifier(identifier);
        this.productions.add(newProduction);
        counter++;
    }
}
