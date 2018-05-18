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
package br.usp.poli.lta.cereda.wirth2ape.ape.conversion;

import br.usp.poli.lta.cereda.wirth2ape.ape.Action;
import br.usp.poli.lta.cereda.wirth2ape.model.Token;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Sketch {
    
    private String name;
    private int source;
    private Token token;
    private String submachine;
    private int target;
    private List<Action> preActions;
    private List<Action> postActions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getSubmachine() {
        return submachine;
    }

    public void setSubmachine(String submachine) {
        this.submachine = submachine;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public List<Action> getPreActions() {
        return preActions;
    }

    public void setPreActions(List<Action> preActions) {
        this.preActions = preActions;
    }

    public List<Action> getPostActions() {
        return postActions;
    }

    public void setPostActions(List<Action> postActions) {
        this.postActions = postActions;
    }

    public Sketch(String name, int source, Token token, int target) {
        this.name = name;
        this.source = source;
        this.token = token;
        this.target = target;
        this.submachine = null;
        this.preActions = new ArrayList<>();
        this.postActions = new ArrayList<>();
    }

    public Sketch(String name, int source, Token token, int target,
            List<Action> preActions, List<Action> postActions) {
        this.name = name;
        this.source = source;
        this.token = token;
        this.target = target;
        this.preActions = preActions;
        this.postActions = postActions;
        this.submachine = null;
    }

    public Sketch(String name, int source, String submachine, int target) {
        this.name = name;
        this.source = source;
        this.submachine = submachine;
        this.target = target;
        this.token = null;
        this.preActions = new ArrayList<>();
        this.postActions = new ArrayList<>();
    }

    public Sketch(String name, int source, String submachine, int target,
            List<Action> preActions, List<Action> postActions) {
        this.name = name;
        this.source = source;
        this.submachine = submachine;
        this.target = target;
        this.preActions = preActions;
        this.postActions = postActions;
        this.token = null;
    }

    public Sketch(String name, int source, int target) {
        this.name = name;
        this.source = source;
        this.target = target;
        this.submachine = null;
        this.token = null;
        this.preActions = new ArrayList<>();
        this.postActions = new ArrayList<>();
    }

    public Sketch(String name, int source, int target,
            List<Action> preActions, List<Action> postActions) {
        this.name = name;
        this.source = source;
        this.target = target;
        this.preActions = preActions;
        this.postActions = postActions;
        this.submachine = null;
        this.token = null;
    }

    public boolean epsilon() {
        if (!call()) {
            return token == null;
        }
        else {
            return false;
        }
    }
    
    public boolean call() {
        return submachine != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sketch: (");
        sb.append("nome: ").append(name).append(", ");
        sb.append("origem: ").append(source).append(", ");
        if (call()) {
            sb.append("submáquina: ").append(submachine);
        }
        else {
            sb.append("token: ");
            if (epsilon()) {
                sb.append("transição em vazio");
            }
            else {
                sb.append(token);
            }
        }
        sb.append(",");
        sb.append("destino: ").append(target);
        if (!preActions.isEmpty()) {
            sb.append(", ações anteriores: (").append(
                    StringUtils.join(preActions, ", ")).append(")");
        }
        if (!postActions.isEmpty()) {
            sb.append(", ações posteriores: (").append(
                    StringUtils.join(postActions, ", ")).append(")");
        }
        sb.append(")");
        return sb.toString();
    }
    
}
