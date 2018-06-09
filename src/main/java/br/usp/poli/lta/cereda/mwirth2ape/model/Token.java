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
package br.usp.poli.lta.cereda.mwirth2ape.model;

import br.usp.poli.lta.cereda.mwirth2ape.labeling.LabelElement;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.ProductionToken;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedList;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Token {

    private String type;
    private String value;
    //Newton
    private ProductionToken ProductionToken;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token() {
        this.value = "";
        this.type = "eof";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ProductionToken getProductionToken() {
        return ProductionToken;
    }

    public void setProductionToken(ProductionToken productionToken) {
        this.ProductionToken = productionToken;
        this.type = productionToken.getType();
        this.value = productionToken.getValue();
    }

    public boolean isValid() {
        return !type.equals("eof");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (isValid()) {
            sb.append(type).append(", ");
            sb.append(value);
        } else {
            sb.append("end of file");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(type).build();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Token reference = (Token) object;
        return new EqualsBuilder().append(type, reference.type).isEquals();
    }

}
