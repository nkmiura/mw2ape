package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NTerm {
    private static final Logger logger = LoggerFactory.
            getLogger(NTerm.class);

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

    public Production addProduction(String identifier)
    {
        Production newProduction = new Production(this.counter);
        newProduction.setIdentifier(identifier);
        this.productions.add(newProduction);
        this.counter++;
        return newProduction;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n[NTerm: " + this.value + ", Qty.: ");
        if (this.productions == null) {
            sb.append("0, productions:[] ]");
        }
        else {
            sb.append(this.productions.size() + ", productions:");
            sb.append(this.productions.toString() + "]");
        }
        return sb.toString();
    }
}
