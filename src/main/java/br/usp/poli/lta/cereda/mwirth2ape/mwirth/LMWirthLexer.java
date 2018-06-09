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
                for (Production tempProduction: tempNterm.productions) {
                    // token nterm inicial
                    ProductionToken newProductionToken1 = new ProductionToken("nterm", tempNterm.getValue());
                    newProductionToken1.setNterm(tempNterm);
                    this.productionTokens.add(newProductionToken1);
                    // token "=" e label "[" associado
                    ProductionToken newProductionToken2 = new ProductionToken("=", "=");
                    LabelElement newLabelElement = new LabelElement();
                    newLabelElement.setValue("[");
                    LinkedList<LabelElement> newLabels = new LinkedList<>();
                    newLabels.add(newLabelElement);
                    newProductionToken2.setNextLabels(newLabels);
                    this.productionTokens.add(newProductionToken2);
                    // tokens da descricao da producao
                    for (ProductionToken tempProductionToken: tempProduction.expression) {
                        this.productionTokens.add(tempProductionToken);
                    }
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
