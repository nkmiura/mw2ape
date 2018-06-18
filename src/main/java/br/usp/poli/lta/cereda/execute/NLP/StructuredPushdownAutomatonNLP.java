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
package br.usp.poli.lta.cereda.execute.NLP;

import br.usp.poli.lta.cereda.mwirth2ape.ape.*;
import br.usp.poli.lta.cereda.mwirth2ape.ape.conversion.State;
import br.usp.poli.lta.cereda.mwirth2ape.lexer.Lexer;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
import br.usp.poli.lta.cereda.mwirth2ape.tuple.Pair;
import br.usp.poli.lta.cereda.mwirth2ape.tuple.Quadruple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.1
 * @since 1.0
 */
public class StructuredPushdownAutomatonNLP extends StructuredPushdownAutomaton2 {

    private static final Logger logger = LoggerFactory.
            getLogger(StructuredPushdownAutomatonNLP.class);


    @Override
    public boolean parse(Lexer lexer) {
        logger.debug("Iniciando o processo de reconhecimento.");
        int state = submachines.get(submachine).getFirst();
        stack.clear();
        Token symbol;
        Stack<String> machines = new Stack<>();
        machines.push(submachine);

        tree = new Stack<>();
        tree.push(new ArrayList());
        tree.top().add(submachine);

        checkAndDoActionState(state);

        while (lexer.hasNext()) {
            symbol = lexer.getNext();
            logger.debug("# Token corrente: {}", symbol);
            logger.debug("# Estado corrente: {}", state);
            List<Transition> query = query(state, symbol);
            logger.debug("# Estado corrente da pilha: {}", stack);
            logger.debug("# Transições válidas encontradas: {}", query);
            if (query.isEmpty()) {

                if (stack.isEmpty()) {
                    logger.debug("Não há transições válidas e a pilha está vazia. A cadeia não é válida.");
                    return false;
                } else {
                    String current = machines.pop();
                    if (submachines.get(current).getSecond().contains(state)) {
                        int reference = state;
                        state = stack.pop();
                        lexer.push(symbol);
                        logger.debug("Não há transições válidas e o estado corrente é de aceitação. A pilha contém "
                                + "elementos, retornando para o estado indicado no topo da pilha ({}) e "
                                + "devolvendo o token corrente ({}) ao analisador léxico.", state, symbol);
                        List branch = tree.pop();
                        if (branch.size() == 1) {
                            branch.add("ε");
                        }
                        logger.debug("Árvore da submáquina corrente: {}", branch);
                        if (operations.containsKey(current)) {
                            logger.debug("Executando ação semântica no retorno da submáquina.");
                            branch = operations.get(current).execute(reference, branch);
                            logger.debug("Árvore após a ação semântica no "
                                    + "retorno da submáquina: {}", branch);
                        }
                        tree.top().add(branch);
                        checkAndDoActionState(state); // acao semantica do estado
                    } else {
                        logger.debug("Não há transições válidas e o estado "
                                + "corrente não é de aceitação. Não é "
                                + "possível retornar para o estado de "
                                + "retorno do topo da pilha. A cadeia "
                                + "não é válida.");
                        return false;
                    }
                }
            } else {
                if (deterministic(query)) {
                    logger.debug("Existe apenas uma transição válida, "
                            + "portanto o passo é determinístico.");
                    for (Action action : query.get(0).getPreActions()) {
                        logger.debug("Executando ação anterior: {}", action);
                        action.execute(symbol);
                    }
                    if (query.get(0).isSubmachineCall()) {
                        machines.push(query.get(0).getSubmachine());
                        stack.push(query.get(0).getTarget());
                        state = query.get(0).getLookahead();
                        lexer.push(symbol);
                        logger.debug("A transição é uma chamada à "
                                + "submáquina '{}', empilhando o estado de "
                                + "retorno {} na pilha, desviando a execução "
                                + "para o estado {} e devolvendo o token "
                                + "corrente {} ao analisador léxico.",
                                query.get(0).getSubmachine(),
                                query.get(0).getTarget(), state, symbol);
                        tree.push(new ArrayList());
                        tree.top().add(query.get(0).getSubmachine());
                    } else {
                        state = query.get(0).getTarget();
                        if (!query.get(0).getToken().getType().equals("ε")) {
                            logger.debug("A transição é um consumo de símbolo, "
                                    + "o novo estado de destino é {}.", state);
                            tree.top().add(symbol);
                        }
                        else {
                            lexer.push(symbol);
                            logger.debug("A transição é uma chamada em vazio. "
                                            + "Devolvendo o token "
                                            + "corrente {} ao analisador léxico.",
                                    symbol);
                        }
                    }
                    for (Action action : query.get(0).getPostActions()) {   // Newton Dúvida: esta ação não seria aplicavel somente para transição com terminal?
                        logger.debug("Executando ação posterior: {}", action);
                        action.execute(symbol);
                    }
                    checkAndDoActionState(state); // acao semantica no estado
                } else {
// newton
                    logger.debug("Existem {} transições válidas. Iniciando "
                            + "operação de lookahead para descoberta da "
                            + "melhor transição.", query.size());
                    List<Quadruple<Integer, Stack<Integer>, Stack<String>,
                            Integer>> attempts = new ArrayList<>();
                    Stack<Token> payback = new Stack<>();


                    logger.debug("Definindo as possíveis escolhas.");
                    for (int i = 0; i < query.size(); i++) {
                        Quadruple<Integer, Stack<Integer>,
                                Stack<String>, Integer> attempt;
                        if (!query.get(i).isSubmachineCall()) {
                            attempt = new Quadruple<>(i, copy(stack),
                                    copy(machines), query.get(i).getTarget());
                        }
                        else {
                            Stack<Integer> s = copy(stack);
                            s.push(query.get(i).getTarget());
                            Stack<String> m = copy(machines);
                            m.push(query.get(i).getSubmachine());
                            attempt = new Quadruple<>(i, s, m,
                                    submachines.get(m.top()).getFirst());
                        }
                        attempts.add(attempt);
                    }
                    logger.debug("Lista de possíveis escolhas: {}", attempts);

                    int lookahead = 0;

                    logger.debug("Tentando decidir a melhor escolha com "
                            + "lookahead = 0 (análise do token corrente).");
                    Pair<List<Quadruple<Integer, Stack<Integer>, Stack<String>, Integer>>, List<Quadruple<Integer,                                                                     Stack<Integer>, Stack<String>, Integer>>> pair =
                            split(attempts, query);
                    attempts.clear();

                    if (!pair.getFirst().isEmpty()) {
                        attempts.addAll(pair.getFirst());
                    }

                    if (!pair.getSecond().isEmpty()) {
                        List<Quadruple<Integer, Stack<Integer>, Stack<String>,
                                Integer>> result = pair.getSecond();
                        result = evaluate(result, symbol);
                        if (!result.isEmpty()) {
                            attempts.addAll(result);
                        }
                    }

                    logger.debug("Resultado com lookahead = 0: {}", attempts);

                    // Inicio look ahead > 0
                    if (attempts.size() < 1) {
                        logger.debug("Iniciando a operação de lookahead.");
                    }

                    logger.debug("O não-determinismo foi resolvido com "
                            + "lookahead = {}, devolvendo símbolos ao "
                            + "analisador léxico.", lookahead);

                    logger.debug("Devolvendo tokens ao analisador "
                            + "sintático. Pilha de lookahead: {}", payback);
                    while (!payback.isEmpty()) {
                        lexer.push(payback.pop());
                    }

                    logger.debug("Tentativas: {}", attempts);
                    logger.debug("Transição escolhida pelo lookahead: {}",
                            query.get(attempts.get(0).getFirst()));

                    for (Action action : query.get(attempts.get(0).
                            getFirst()).getPreActions()) {
                        logger.debug("Executando ação anterior: {}", action);
                        action.execute(symbol);
                    }

                    if (query.get(attempts.get(0).getFirst()).isSubmachineCall()) {
                        machines.push(query.get(attempts.get(0).getFirst()).getSubmachine());
                        stack.push(query.get(attempts.get(0).getFirst()).getTarget());
                        state = query.get(attempts.get(0).getFirst()).getLookahead();
                        lexer.push(symbol);
                        logger.debug("A transição é uma chamada à submáquina "
                                + "'{}', empilhando o estado de retorno {} "
                                + "na pilha, desviando a execução para o "
                                + "estado {} e devolvendo o token corrente {} "
                                + "ao analisador léxico.", query.get(
                                        attempts.get(0).getFirst()).
                                        getSubmachine(), query.get(
                                                attempts.get(0).getFirst())
                                                .getTarget(), state, symbol);
                        tree.push(new ArrayList());
                        tree.top().add(query.get(attempts.get(0).getFirst()).getSubmachine());
                    } else {
                        state = query.get(attempts.get(0).getFirst()).getTarget();
                        logger.debug("A transição é um consumo de símbolo,"
                                + " o novo estado de destino é {}.", state);
                        tree.top().add(symbol);
                    }

                    for (Action action : query.get(attempts.get(0).getFirst()).
                            getPostActions()) {
                        logger.debug("Executando ação posterior: {}", action);
                        action.execute(symbol);
                    }
                    checkAndDoActionState(state); // acao semantica no estado
                }
            }
        }

        logger.debug("Não há mais token a consumir.");
        logger.debug("Estado final da pilha: {}", stack);

        while (!stack.isEmpty()) {
            if (submachines.get(machines.top()).getSecond().contains(state)) {
                logger.debug("O estado corrente {} é de aceitação na "
                        + "submáquina corrente, retornando.", state);
                int reference = state;
                state = stack.pop();
                String current = machines.pop();
                logger.debug("O novo estado corrente é {}.", state);
                List branch = tree.pop();
                if (branch.size() == 1) {
                    branch.add("ε");
                }
                if (operations.containsKey(current)) {
                    branch = operations.get(current).execute(reference, branch);
                }
                tree.top().add(branch);
                checkAndDoActionState(state); // acao semantica do estado
            } else {
                Integer newState = checkAndDoEmptyTransition(state);
                if (newState != -1) {
                    state = newState;
                    checkAndDoActionState(state); // acao semantica do estado
                }
                else {
                    return false;
                }
            }
        }

        if (stack.isEmpty()) {
            boolean done = false;
            while (!done) {
                Integer newState = checkAndDoEmptyTransition(state);
                if (newState != -1) {
                    state = newState;
                    checkAndDoActionState(state); // acao semantica do estado
                }
                else {
                    done = true;
                }
            }
            boolean result = submachines.get(submachine).getSecond().
                    contains(state);
            if (operations.containsKey(submachine)) {
                List top = tree.pop();
                top = operations.get(submachine).execute(state, top);
                tree.push(top);
            }
            logger.debug("Resultado do reconhecimento: cadeia {}",
                    (result ? "aceita" : "rejeitada"));
            return result;
        } else {
            logger.debug("A pilha não está vazia e o estado corrente não "
                    + "é de aceitação. A cadeia não é válida.");
            return false;
        }
    }

    @Override
    protected List<Transition> query(int state, Token symbol) {
        logger.debug("Executando consulta com estado {} e token {}.", state, symbol);
        List<Transition> result = new ArrayList<>();
        for (Transition transition : transitions) {
            if (transition.getSource() == state) {
                if (transition.isSubmachineCall()) {
                    result.add(transition);
                } else {
                    if (transition.getToken().getType().equals("ε")) {
                        result.add(transition);
                    }
                    else if (symbol.getNlpToken() != null) {
                        for (NLPWord currentNLPWord: symbol.getNlpToken().getNlpWords()) {
                            if (transition.getToken().getValue().equals(currentNLPWord.posTag) ) {
                                result.add(transition);
                            }
                        }
                    }
                }
            }
        }
        logger.debug("Transições encontradas: {}", result);
        return result;
    }
}
