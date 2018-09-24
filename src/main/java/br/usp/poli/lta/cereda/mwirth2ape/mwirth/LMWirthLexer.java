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
            for (NTerm tempNterm: labelGrammar.nterms) {
                // Loop para cada nterm
                LinkedList<LabelElement> newLabels1 = new LinkedList<>();
                Integer productionIndex = 0;
                for (Production tempProduction: tempNterm.productions) {
                    // token nterm inicial
                    if (productionIndex == 0) {
                        ProductionToken newProductionToken1 = new ProductionToken("nterm", tempNterm.getValue());
                        newProductionToken1.setNterm(tempNterm);
                        this.productionTokens.add(newProductionToken1);
                        // token "=" e label "[" associado
                        ProductionToken newProductionToken2 = new ProductionToken("=", "=");
                        LabelElement newLabelElement1 = new LabelElement();
                        newLabelElement1.setValue("[");
                        newLabels1.add(newLabelElement1);
                        newProductionToken2.setPostLabels(newLabels1);
                        this.productionTokens.add(newProductionToken2);
                        // Se tiver mais de uma produção, abre parênteses
                        if (tempNterm.productions.size() > 1) {
                            ProductionToken newProductionToken3 = new ProductionToken("(","("); // novo token (
                            this.productionTokens.add(newProductionToken3); // adiciona token (

                            tempProduction.expression.getLast().setValue(")");
                            tempProduction.expression.getLast().setType(")");
                            tempProduction.expression.getLast().getPostLabels().removeLast();

                            // Edição de labels
                            Integer expressionSize = tempProduction.expression.size();
                            if (tempProduction.expression.get(expressionSize-2).getPostLabels() == null) {
                                LinkedList<LabelElement> newLabels = new LinkedList<>();
                                tempProduction.expression.get(expressionSize-2).setPostLabels(newLabels);
                            }
                            tempProduction.expression.get(expressionSize-2).getPostLabels().addAll(tempProduction.expression.getLast().getPostLabels());
                            tempProduction.expression.getLast().getPostLabels().clear();

                            // tokens da descricao da primeira producao
                            for (ProductionToken tempProductionToken : tempProduction.expression) {
                                this.productionTokens.add(tempProductionToken);
                            }

                            ProductionToken newProductionToken4 = new ProductionToken("|","|"); // novo token )
                            this.productionTokens.add(newProductionToken4);
                        }
                        else {
                            // tokens da descricao da producao unica
                            for (ProductionToken tempProductionToken : tempProduction.expression) {
                                this.productionTokens.add(tempProductionToken);
                            }
                        }
                    }
                    else { // A partir da 2a produção

                        ProductionToken newProductionToken3 = new ProductionToken("(","("); // novo token (
                        this.productionTokens.add(newProductionToken3); // adiciona token (                            // ajuste do ultimo elemento de . para |

                        tempProduction.expression.getLast().setValue(")");
                        tempProduction.expression.getLast().setType(")");
                        tempProduction.expression.getLast().getPostLabels().removeLast();

                        // Edição de labels
                        Integer expressionSize = tempProduction.expression.size();
                        if (tempProduction.expression.get(expressionSize-2).getPostLabels() == null) {
                            LinkedList<LabelElement> newLabels = new LinkedList<>();
                            tempProduction.expression.get(expressionSize-2).setPostLabels(newLabels);
                        }
                        tempProduction.expression.get(expressionSize-2).getPostLabels().addAll(tempProduction.expression.getLast().getPostLabels());
                        tempProduction.expression.getLast().getPostLabels().clear();

                        // tokens da descricao da primeira producao
                        for (ProductionToken tempProductionToken : tempProduction.expression) {
                            this.productionTokens.add(tempProductionToken);
                        }

                        if (productionIndex < (tempNterm.productions.size() - 1)) {
                            ProductionToken newProductionToken4 = new ProductionToken("|","|"); // novo token )
                            this.productionTokens.add(newProductionToken4);
                        }
                        else {
                            ProductionToken newProductionToken4 = new ProductionToken(".","."); // novo token .
                            LinkedList<LabelElement> newLabels = new LinkedList<>();
                            LabelElement newLabelElement = new LabelElement();
                            newLabelElement.setValue("]");
                            newLabels.add(newLabelElement);
                            newProductionToken4.setPostLabels(newLabels);
                            this.productionTokens.add(newProductionToken4);
                        }
                    }
                    productionIndex++;
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
        sb.append(this.productionTokens);
        return sb.toString();
    }

}
