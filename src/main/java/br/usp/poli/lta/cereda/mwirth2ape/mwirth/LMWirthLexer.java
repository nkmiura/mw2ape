/**
 * ------------------------------------------------------
 *    Laboratório de Linguagens e Técnicas Adaptativas
 *       Escola Politécnica, Universidade São Paulo
 * ------------------------------------------------------
 *
 * This program is free software: you can redistribute it
 * and/or modify  it under the  terms of the  GNU General
 * Public  License  as  published by  the  Free  Software
 * Foundation, either  version 3  of the License,  or (at
 * your option) any later version.
 *
 * This program is  distributed in the hope  that it will
 * be useful, but WITHOUT  ANY WARRANTY; without even the
 * implied warranty  of MERCHANTABILITY or FITNESS  FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 **/
package br.usp.poli.lta.cereda.mwirth2ape.mwirth;

import br.usp.poli.lta.cereda.mwirth2ape.labeling.*;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;

import java.util.*;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class LMWirthLexer extends MWirthLexer {

    private LinkedList<ProductionToken> productionTokens;

    public void LGrammarToProductionTokens () {
        this.productionTokens = new LinkedList<>();
    }

    public void LGrammarToProductionTokens (LabelGrammar labelGrammar) {  // Gera cadeia de entrada composta por estrutura de dados

        this.productionTokens = new LinkedList<>();

        try {
            for (NTerm tempNterm : labelGrammar.nterms) {
                // Loop para cada nterm
                ProductionToken newProductionToken1 = new ProductionToken("nterm", tempNterm.getValue());
                newProductionToken1.setNterm(tempNterm);
                this.productionTokens.add(newProductionToken1);
                // token "=" e label "[" associado
                ProductionToken newProductionToken2 = new ProductionToken("=", "=");
                LabelElement newLabelElement1 = new LabelElement();
                newLabelElement1.setValue("[");
                LinkedList<LabelElement> newLabels1 = new LinkedList<>();
                newLabels1.add(newLabelElement1);
                newProductionToken2.setPostLabels(newLabels1);
                this.productionTokens.add(newProductionToken2);

                //
                Integer productionIndex = 0;

                if (tempNterm.productions.size() == 1) {
                    // tokens da descricao da producao unica
                    for (ProductionToken tempProductionToken : tempNterm.productions.getFirst().expression) {
                        this.productionTokens.add(tempProductionToken);
                        // label de fechamento ja faz parte da expressao original
                    }
                } else {
                    // Multiplas producoes

                    ProductionToken newProductionToken5 = new ProductionToken("(", "("); // novo token (
                    this.productionTokens.add(newProductionToken5); // adiciona token (

                    for (Production tempProduction : tempNterm.productions) {
                            // abre parênteses

                        ProductionToken newProductionToken3 = new ProductionToken("(", "("); // novo token (
                        this.productionTokens.add(newProductionToken3); // adiciona token (

                        tempProduction.expression.getLast().setValue(")");
                        tempProduction.expression.getLast().setType(")");
                        tempProduction.expression.getLast().getPostLabels().removeLast(); // Precisa remover?

                        // Edição de labels

                        /*
                        Integer expressionSize = tempProduction.expression.size();
                        if (tempProduction.expression.get(expressionSize - 2).getPostLabels() == null) {
                            LinkedList<LabelElement> newLabels = new LinkedList<>();
                            tempProduction.expression.get(expressionSize - 2).setPostLabels(newLabels);
                        }
                        tempProduction.expression.get(expressionSize - 2).getPostLabels().addAll(tempProduction.expression.getLast().getPostLabels());
                        tempProduction.expression.getLast().getPostLabels().clear();
                        */


                        for (ProductionToken tempProductionToken : tempProduction.expression) {
                            this.productionTokens.add(tempProductionToken);
                        }

                        if (productionIndex < (tempNterm.productions.size() - 1)) {
                            ProductionToken newProductionToken4 = new ProductionToken("|", "|"); // novo token |
                            this.productionTokens.add(newProductionToken4);
                        } else {
                            ProductionToken newProductionToken6 = new ProductionToken(")", ")"); // novo token )
                            this.productionTokens.add(newProductionToken6); // adiciona token )

                            ProductionToken newProductionToken7 = new ProductionToken(".", "."); // novo token .
                            LinkedList<LabelElement> newLabels = new LinkedList<>();
                            LabelElement newLabelElement = new LabelElement();
                            newLabelElement.setValue("]");
                            newLabels.add(newLabelElement);
                            newProductionToken7.setPostLabels(newLabels);
                            this.productionTokens.add(newProductionToken7);
                        }
                        productionIndex++;
                    }
                    // Fecha produção

                }
            }
        }
        catch (Exception exception) {
            System.out.println("An exception was thrown.");
            System.out.println(exception.toString());
        }
    }

    public Token recognize() {
        Token newToken = new Token();
        newToken.setProductionToken(this.productionTokens.pop());
        return newToken;
    }

    public boolean hasNext() {
        return !this.productionTokens.isEmpty() || !bufferIsEmpty();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        this.productionTokens.forEach(productionToken -> {
            sb.append("  ");
            if (productionToken.getPreLabels() != null) {
                sb.append(productionToken.getPreLabels().toString());
            }
            sb.append(";").append(productionToken.getType()).append(",").append(productionToken.getValue()).append(";");
            if (productionToken.getPostLabels() != null) {
                sb.append(productionToken.getPostLabels().toString());
            }
            if (productionToken.getType().equals(".")) {
                sb.append("\n");
            }
        });
        sb.append("\n");
        return sb.toString();
    }

/*
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.productionTokens);
        return sb.toString();
    }
*/
}
