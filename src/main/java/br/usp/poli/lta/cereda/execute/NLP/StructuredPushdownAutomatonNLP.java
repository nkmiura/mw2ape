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
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.*;

/**
 * @author Paulo Roberto Massa Cereda, Newton Kiyotaka Miura
 * @version 1.3
 * @since 1.1
 */
public class StructuredPushdownAutomatonNLP extends StructuredPushdownAutomaton2  {

    private static final Logger logger = LoggerFactory.
            getLogger(StructuredPushdownAutomatonNLP.class);

    private NLPLexer lexer;
    private NLPOutputList nlpOutputList;
    private int state;
    private NLPSPAStackElement nlpState;
    private Token symbol;
    private Stack<String> machines;
    private List<Transition> query;
    private NLPTransducerStackList nlpTransducerStackList;
    private NLPOutputResult tempNLPOutputResult;
    private Stack<String> tempNLPTransducerStack;
    private int maxStackDepth = 100;
    // Stack com estado retorno e transicao associada
    //private Stack<NLPSPAStackElement> nlpStack; // declarado em StructuredPushdownAutomaton2

    public StructuredPushdownAutomatonNLP (NLPLexer lexer, NLPOutputList nlpOutputList,
                                           NLPTransducerStackList nlpTransducerStackList,
                                           NLPAction nlpAction) {
        this.lexer = lexer;
        this.nlpOutputList = nlpOutputList;
        this.nlpTransducerStackList = nlpTransducerStackList;
    }

    public StructuredPushdownAutomatonNLP (StructuredPushdownAutomatonNLP originalSPA, Transition transition)
    {
        this.state = originalSPA.state;
        this.nlpState = originalSPA.nlpState; // 2018.09.17
        this.states = originalSPA.states;
        this.lexer = originalSPA.lexer.clone(originalSPA.lexer);
        this.stack = originalSPA.stack.clone();
        this.nlpStack = originalSPA.nlpStack.clone(); // 2018.09.17
        this.tree = originalSPA.tree.clone();
        this.transitions = originalSPA.transitions;
        this.machines = originalSPA.machines.clone(); // 2018.10.09
        this.submachines = originalSPA.submachines;
        this.submachine = originalSPA.submachine;
        this.nlpTransducerStackList = originalSPA.nlpTransducerStackList;
        this.nlpOutputList = originalSPA.nlpOutputList;
        this.query = new ArrayList<>();
        this.query.add(transition);
        this.tempNLPOutputResult = new NLPOutputResult();
        this.tempNLPOutputResult.setOutputList(this.nlpOutputList.getOutputResult(Thread.currentThread().getId()));
        this.tempNLPTransducerStack = originalSPA.nlpTransducerStackList.getTransducerStackList(Thread.currentThread().getId()).clone();
    }

    public NLPOutputResult getTempNLPOutputResult() {
        return tempNLPOutputResult;
    }

    public Stack<String> getTempNLPTransducerStack() {
        return tempNLPTransducerStack;
    }

    public boolean parse(boolean isClone) {
        boolean isCloneLocal = isClone;
        logger.debug("Iniciando o processo de reconhecimento. Thread: " + Thread.currentThread().getName());
        if (isCloneLocal) { // Nao eh clone
            symbol = lexer.getNext();

        } else { // Eh clone - primeira execucao
            state = submachines.get(submachine).getFirst();
            stack.clear();
            nlpStack.clear(); // 2018.09.17

            machines = new Stack<>();
            machines.push(submachine);

            tree = new Stack<>();
            tree.push(new ArrayList());
            tree.top().add(submachine);

        }

        while (lexer.hasNext() || isCloneLocal) {
            logger.debug("# Token corrente: {}", symbol);
            logger.debug("# Estado corrente: {}", state);

            if (isCloneLocal) {
                isCloneLocal = false;
            } else {
                symbol = lexer.getNext();
                this.query = query(state, symbol);
            }

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
                        nlpState = nlpStack.pop(); // 2018.09.17

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
                        // Executa acao semantica pos-retorno de submaquina - 2018.09.17
                        for (ActionLabels actionLabel: nlpState.getTransition().getLabelActions()) {
                            logger.debug("Executando rotina de label: {}", actionLabel.getName());
                            actionLabel.execute(nlpState.getTransition().getPostLabelElements(), transducerStack);
                        }
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
                if (!deterministic(query)) {
                    logger.debug("Existem múltiplas transições válidas, "
                            + "portanto o passo é não-determinístico.");
                    // Clona spa e inicia nova thread
                    //int queryIndex = 0;
                    lexer.push(symbol);
                    for (Integer i = 1; i < query.size(); i++) {
                    //for (Transition tempTransition: query) {
                        //if (queryIndex > 0) {
                        StructuredPushdownAutomatonNLP newSpa =
                                new StructuredPushdownAutomatonNLP(this, query.get(i));
                        NLPSpaThread NLPSpaThread = new NLPSpaThread(newSpa, this.nlpOutputList,
                                this.nlpTransducerStackList, Thread.currentThread().getId());
                        Thread newThread = new Thread(NLPSpaThread);
                        newThread.start();
                        //}
                        //queryIndex++;
                    }
                    symbol = lexer.getNext();

                }
                else {
                }
                logger.debug("Existe apenas uma transição válida, "
                        + "portanto o passo é determinístico.");
                for (Action action : query.get(0).getPreActions()) {

                    logger.debug("Executando ação anterior: {}", action);
                    action.execute(symbol);
                }
                for (ActionLabels actionLabel:  query.get(0).getLabelActions()) {
                    logger.debug("Executando rotina de label pre: {}", actionLabel.getName());
                    actionLabel.execute(query.get(0).getPreLabelElements(), transducerStack);
                }

                if (query.get(0).isSubmachineCall()) {


                    machines.push(query.get(0).getSubmachine());
                    if (getRecursionCount(machines, query.get(0).getSubmachine()) < this.maxStackDepth) { // verifica tamanho da pilha. Se menor que o limiar continua.
                        stack.push(query.get(0).getTarget());
                        NLPSPAStackElement newNLPStackElement = new NLPSPAStackElement(query.get(0).getTarget(), query.get(0)); // 2018.09.17
                        nlpStack.push(newNLPStackElement);

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

                        //actionLabelsSubmachineInit.execute(initialLabelElements, transducerStack); // label de inicio de submaquina 2018.09.17
                    } else { // Pilha pasou do limiar. Aborta.
                        logger.debug("Limiar de chamada de submáquina {} atingido. Chamada à "
                                        + "submáquina '{}'", this.maxStackDepth,
                                query.get(0).getSubmachine());
                        return false;
                        //break;
                    }

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
                    for (Action action : query.get(0).getPostActions()) {
                        logger.debug("Executando ação posterior: {}", action.getName());
                        action.execute(symbol);
                    }
                    for (ActionLabels actionLabel:  query.get(0).getLabelActions()) {

                        logger.debug("Executando rotina de label pos: {}", actionLabel.getName());
                        actionLabel.execute(query.get(0).getPostLabelElements(), transducerStack);
                    }
                }
            }
        }

        logger.debug("Não há mais token a consumir.");
        logger.debug("Estado final da pilha: {}", stack);

        while (!stack.isEmpty()) {
            if (submachines.get(machines.top()).getSecond().contains(state)) {
                logger.debug("O estado corrente {} é de aceitação na submáquina corrente, retornando.", state);
                int reference = state;
                state = stack.pop();
                nlpState = nlpStack.pop(); // 2018.09.17
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
                // processar label
                // Executa acao semantica pos-retorno de submaquina - 2018.09.17
                for (ActionLabels actionLabel: nlpState.getTransition().getLabelActions()) {
                    logger.debug("Executando rotina de label pos no retorno de submaquina: {}", actionLabel.getName());
                    actionLabel.execute(nlpState.getTransition().getPostLabelElements(), transducerStack);
                }
                for (Action action : nlpState.getTransition().getPostActions()) {
                    logger.debug("Executando ação posterior no retorno de submaquina: {}", action.getName());
                    action.execute(symbol);
                }
            } else {
                //
                Integer newState = checkAndDoEmptyTransition(state,transducerStack);
                if (newState != -1) {
                    state = newState;
                }
                else {
                    return false;
                }
            }
        }

        if (stack.isEmpty()) {
            boolean done = false;
            while (!done) {
                Integer newState = checkAndDoEmptyTransition(state,transducerStack);
                if (newState != -1) {
                    state = newState;
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
            logger.debug("Resultado do reconhecimento: cadeia {}", (result ? "aceita" : "rejeitada"));
            logger.debug(this.nlpOutputList.getOutputResult(Thread.currentThread().getId()).toString());
            // Inserir print de resultado positivo aqui.
            printNLPOutput (this.nlpOutputList.getOutputResult(Thread.currentThread().getId()), Thread.currentThread().getId(), Thread.currentThread().getName());
            return result;
        } else {
            logger.debug("A pilha não está vazia e o estado corrente não "
                    + "é de aceitação. A cadeia não é válida.");
            return false;
        }
    }

    int getRecursionCount(Stack <String> machinesStack, String machineName)
    {
        int recursionCount = 0;
        ListIterator<String>  iter = machinesStack.getList().listIterator(machinesStack.size());
        if (!machinesStack.isEmpty()) {
            if (iter.equals(machineName)) {
                recursionCount++;
            }
        }
        while (iter.hasPrevious()) {
            if (iter.previous().equals(machineName)) {
                recursionCount++;
            }
        }
        return recursionCount;
    }

    void printNLPOutput (LinkedList<String> outputList, long threadID, String threadName)
    {
        NLPTreeNode<String> root = new NLPTreeNode<>("root");

        StringBuilder sbJson = new StringBuilder();
        StringBuilder sbLatex = new StringBuilder();

        String lastChar = "";

        for (String currentString: outputList) {
            String newString = "";
            if (currentString.matches("\\[\\(")) {
                newString="{\"nterm\":{\"children\":[";
                if (lastChar.equals("}")) {
                    newString = "," + newString;
                }
                logger.debug(" currentString: {}: match [( - out: {}",currentString,newString);
            } else if (currentString.matches("\\(\\)")) {
                newString="{\"name\":()";
                logger.debug(" currentString: {}: match () - out: {}",currentString,newString);
            } else if (currentString.matches("\\\".*\\\"")) {
                newString="{\"term\":{\"content\":" + currentString;
                if (lastChar.equals("}")) {
                    newString = "," + newString;
                }
                logger.debug(" currentString: {}: match term value - out: {}",currentString,newString);
            } else if (currentString.matches("\\(.*\\)")) {
                newString=",\"name\":\""+ currentString.substring(1,currentString.length()-1) + "\"}}";
                logger.debug(" currentString: {}: match (term) - out: {}",currentString,newString);
            } else if (currentString.matches(".*\\)")) {
                newString="],\"name\":\""+ currentString.substring(0,currentString.length()-1)+"\"}";
                logger.debug(" currentString: {}: match nterm - out: {}",currentString,newString);
            } else if (currentString.matches("\\).*")) {
                newString="],\"name\":\""+ currentString.substring(1) + "\"}";
                logger.debug(" currentString: {}: match )nterm - out: {}",currentString,newString);
            } else if (currentString.matches("\\)")) {
                newString="}";
                logger.debug(" currentString: {}: match ) - out: {}",currentString,newString);
            } else if (currentString.matches("\\(")) {
                newString="{";
                logger.debug(" currentString: {}: match ( - out: {}",currentString,newString);
            } else if (currentString.matches("\\]")) {
                newString="}";
                logger.debug(" currentString: {}: match ] - out: {}",currentString,newString);
            } else {
                logger.debug(" currentString: {}: unknown match",currentString);
            }
            sbJson.append(newString);
            lastChar = sbJson.substring(sbJson.length()-1);
        }


        logger.debug("###################################################################");
        logger.debug("\n Output JSON (ThreadId {} ThreadName {}): \n{}\n",threadID,threadName,sbJson);
        logger.debug("###################################################################");


        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(sbJson.toString());
        sbLatex.append(jsonOuputToLatex(element));

        logger.debug("###################################################################");
        logger.debug("\n Output Latex (ThreadId {} ThreadName {}): \n{}\n",threadID,threadName,sbLatex);
        logger.debug("###################################################################");
    }


    public String jsonOuputToLatex(JsonElement jsonElement)
    {
        StringBuilder sbLatex = new StringBuilder();

        JsonObject obj = jsonElement.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();//will return members of your object
        for (Map.Entry<String, JsonElement> entry: entries) {
            //System.out.println(entry.getKey());
            logger.debug(" JSON: key{}",entry.getKey());
            if (entry.getKey().equals("nterm")) {
                String name = getNameFromJson(entry.getValue().getAsJsonObject());
                if (!name.equals("")) {
                    String newString = "[\\nt{" + name.substring(1,name.length()-1) + "}";
                    sbLatex.append(newString).append(jsonOuputToLatex(entry.getValue())).append("]");
                }
            } else if (entry.getKey().equals("term")) {
                String name = getNameFromJson(entry.getValue().getAsJsonObject());
                if (!name.equals("")) {
                    String newString = "[\\nt{" + name.substring(1,name.length()-1) + "}";
                    sbLatex.append(newString).append(jsonOuputToLatex(entry.getValue())).append("]");
                }
            } else if (entry.getKey().equals("content")) {
                String newString = "[\\nt{``" + entry.getValue().toString().substring(1) + "}]";
                sbLatex.append(newString);
            } else if (entry.getKey().equals("children")) {
                //newString = "[\\nt{term";
                JsonArray newJsonArray = entry.getValue().getAsJsonArray();
                for (JsonElement currentJsonElement : newJsonArray) {
                    String  newString = jsonOuputToLatex(currentJsonElement);
                    sbLatex.append(newString);
                }
            }
        }
        return sbLatex.toString();
    }

    String getNameFromJson(JsonObject jsonObject) {
        String name = "";
        Set<Map.Entry<String, JsonElement>> currentNtermEntries = jsonObject.entrySet();
        for (Map.Entry<String, JsonElement> currentNtermEntry: currentNtermEntries) {
            if (currentNtermEntry.getKey().equals("name")) {
                name = currentNtermEntry.getValue().toString();
            }
        }
        return name;
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
