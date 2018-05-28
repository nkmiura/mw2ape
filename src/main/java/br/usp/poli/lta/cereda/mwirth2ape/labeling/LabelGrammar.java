package br.usp.poli.lta.cereda.mwirth2ape.labeling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.ssl.Debug;

import java.util.LinkedList;

public class LabelGrammar {
    private static final Logger logger = LoggerFactory.
            getLogger(LabelGrammar.class);

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
            // logger.debug("tempNterm: " + tempNterm.getValue());
            if (nterm.equals(tempNterm.getValue())) {
                return tempNterm;
            }
        }
        return null;
    }

    public boolean fillNTermInProductions()
    {
        boolean result = true;
        for (NTerm tempNterm : nterms) {
            for (Production tempProduction : tempNterm.productions) {
                boolean firstProductionToken = true;
                for (ProductionToken tempProductionToken : tempProduction.expression) {
                    //logger.debug(" token type: " + tempProductionToken.getValue());
                    if (tempProductionToken.getType().equals("nterm")) {
                        NTerm foundNterm = searchNterm(tempProductionToken.getValue());
                        if (foundNterm == null) {
                            result = false;
                            // logger.debug("nterm " + tempProductionToken.getValue() + " not found in grammar productions.");
                        }
                        else {
                            tempProductionToken.setNterm(foundNterm);
                            if (firstProductionToken) {
                                // Primeiro token da produção
                                if (tempNterm.getValue().equals(tempProductionToken.getValue())) {
                                    // Recursao a esquerda
                                    tempProduction.setRecursion("left");
                                }
                            }
                        }
                    }
                    firstProductionToken = false;
                }
                if (tempProduction.getRecursion().equals("left")) {
                    tempProduction.setIdentifier(tempProduction.getIdentifier() + "\u2207");
                }
                else if (tempProduction.getLastProductionTerm().getType().equals("nterm")) {
                    if(tempProduction.getLastProductionTerm().getValue().equals(tempNterm.getValue())) {
                        tempProduction.setRecursion("right");
                        tempProduction.setIdentifier("\u2207" + tempProduction.getIdentifier());
                    }
                }
            }
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        //sb.append("{");
        if (this.nterms == null) {
            sb.append("\nLabelGrammar: []");
        }
        else {
            sb.append("\nLabelGrammar: " + this.nterms.toString());
        }
        return sb.toString();
    }
}
