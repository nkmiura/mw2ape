package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import java.util.LinkedList;

public class LabelGrammar {
    public LinkedList<NTerm> nterms;

    public LabelGrammar()
    {
        this.nterms = new LinkedList<>();
    }

    public NTerm newNterm(String identifier)
    {
        // verificar se ja existe nterm com o mesmo identificador
        NTerm newNterm = searchNterm(identifier);
        if (newNterm == null) {
            newNterm = new NTerm(identifier);
            nterms.add(newNterm);
        }
        return newNterm;
    }

    public NTerm searchNterm(String nterm)
    {
        for (NTerm tempNterm : nterms) {
            if (nterm == tempNterm.getValue()) {
                return tempNterm;
            }
        }
        return null;
    }
}
