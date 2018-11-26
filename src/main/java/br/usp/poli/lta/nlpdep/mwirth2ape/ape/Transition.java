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
package br.usp.poli.lta.nlpdep.mwirth2ape.ape;

import br.usp.poli.lta.nlpdep.mwirth2ape.labeling.LabelElement;
import br.usp.poli.lta.nlpdep.mwirth2ape.model.Token;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Transition {

    private int source;
    private Token token;
    private int target;
    private String submachine;
    private int lookahead;
    private final List<Action> preActions;
    private final List<Action> postActions;
    private final List<ActionLabels> labelActions;

    public Transition(int source, Token token, int target) {
        this.source = source;
        this.token = token;
        this.target = target;
        this.submachine = null;
        this.lookahead = -1;
        this.preActions = new ArrayList<>();
        this.postActions = new ArrayList<>();
        this.labelActions = new ArrayList<>();
    }

    public Transition() {
        this.preActions = new ArrayList<>();
        this.postActions = new ArrayList<>();
        this.labelActions = new ArrayList<>();
    }

    public Transition(int source, String submachine, int target) {
        this.source = source;
        this.target = target;
        this.submachine = submachine;
        this.token = null;
        this.lookahead = -1;
        this.preActions = new ArrayList<>();
        this.postActions = new ArrayList<>();
        this.labelActions = new ArrayList<>();
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
        this.submachine = null;
        this.token = token;
    }

    public void setSubmachineToken(Token token) {
        this.token = token;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getSubmachine() {
        return submachine;
    }

    public void setSubmachine(String submachine) {
        this.token = null;
        this.submachine = submachine;
    }

    public void addPostAction(Action action) { postActions.add(action); }

    public void addPreAction(Action action) {
        preActions.add(action);
    }

    public void addLabelAction(ActionLabels actionLabels) { labelActions.add(actionLabels); }

    public void setLookahead(int lookahead) {
        this.lookahead = lookahead;
    }

    public int getLookahead() {
        return lookahead;
    }

    public boolean isSubmachineCall() {
        return submachine != null;
    }

    public List<Action> getPreActions() {
        return preActions;
    }

    public List<Action> getPostActions() {
        return postActions;
    }

    public List<ActionLabels> getLabelActions() {
        return labelActions;
    }

    public LinkedList<LabelElement> getPostLabelElements() {  // Retorna labels associados a direita
        return token.getProductionToken().getPostLabels();
    }

    public LinkedList<LabelElement> getPreLabelElements() {  // Retorna labels associados a direita
        return token.getProductionToken().getPreLabels();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transição: (").append("origem: ").append(source);
        sb.append(", ");
        if (isSubmachineCall()) {
            sb.append("submáquina: ").append(submachine);
        } else {
            sb.append("token: ").append(token);
        }
        sb.append(", destino: ").append(target);
        if (!preActions.isEmpty()) {
            sb.append(", ações anteriores: ").append("(");
            for (Action tempAction : preActions) {
                sb.append(StringUtils.join(tempAction.toString(), ", "));
            }
            sb.append(")");
        }
        if (!postActions.isEmpty()) {
            sb.append(", ações posteriores: ").append("(");
            sb.append(StringUtils.join(postActions, ", "));
            sb.append(")");
        }
        sb.append(")");
        return sb.toString();
    }

}
