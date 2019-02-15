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
package br.usp.poli.lta.nlpdep.execute.NLP;

import br.usp.poli.lta.nlpdep.execute.NLP.dependency.DepParseTree;
import br.usp.poli.lta.nlpdep.execute.NLP.dependency.DepStackElement;
import br.usp.poli.lta.nlpdep.execute.NLP.dependency.DepStackList;
import br.usp.poli.lta.nlpdep.execute.NLP.output.NLPOutputList;
import br.usp.poli.lta.nlpdep.execute.NLP.output.NLPOutputResult;
import br.usp.poli.lta.nlpdep.mwirth2ape.ape.*;
import br.usp.poli.lta.nlpdep.mwirth2ape.model.Token;
import br.usp.poli.lta.nlpdep.mwirth2ape.structure.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.*;

/**
 * @author Newton Kiyotaka Miura
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
    //private NLPOutputResult NLPOutputResult;
    private Stack<String> tempNLPTransducerStack;
    private int maxSubmachineRecursiveCalls = 4;
    // Stack com estado retorno e transicao associada
    //private Stack<NLPSPAStackElement> nlpStack; // declarado em StructuredPushdownAutomaton2
    private Stack<DepStackElement> depStack;
    private Stack<DepStackElement> tempDepStack;
    private DepStackList depStackList;
    private Properties appProperties;

    public StructuredPushdownAutomatonNLP (Properties appProperties, NLPLexer lexer, NLPOutputList nlpOutputList,
                                           NLPTransducerStackList nlpTransducerStackList,
                                           NLPAction nlpAction, DepStackList depStackList) {
        this.lexer = lexer;
        this.nlpOutputList = nlpOutputList;
        this.nlpTransducerStackList = nlpTransducerStackList;
        this.depStackList = depStackList;
        this.depStack = new Stack<>();
        this.tempDepStack = new Stack<>();
        this.appProperties = appProperties;
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
        this.transducerStack =  originalSPA.transducerStack.clone(); // 2018.11.11
        this.nlpOutputList = originalSPA.nlpOutputList;
        this.query = new ArrayList<>();
        this.query.add(transition);
        this.tempNLPOutputResult = new NLPOutputResult();
        this.tempNLPOutputResult.setOutputList(nlpOutputList.getOutputResult(Thread.currentThread().getId())); // 2018.11.11
        //this.tempNLPTransducerStack = originalSPA.nlpTransducerStackList.getTransducerStackList(Thread.currentThread().getId()).clone();   //
        this.tempNLPTransducerStack = nlpTransducerStackList.getTransducerStackList(Thread.currentThread().getId()); // 2018.11.11
        // Dependencies 2018.11.28
        this.depStackList = originalSPA.depStackList;
        //this.depStack = originalSPA.depStack.clone();
        this.tempDepStack = depStackList.getDepStackFromThreadID(Thread.currentThread().getId()).clone(); // 2019.02.14
        this.appProperties = originalSPA.appProperties;
    }

    public br.usp.poli.lta.nlpdep.execute.NLP.output.NLPOutputResult getTempNLPOutputResult() {
        return tempNLPOutputResult;
    }

    public Stack<String> getTempNLPTransducerStack() {
        return tempNLPTransducerStack;
    }

    public Stack<DepStackElement> getTempDepStack() { return tempDepStack; }

    public DepStackList getDepStackList() {
        return depStackList;
    }

    public boolean parse(boolean isClone) {
        boolean isCloneLocal = isClone;

        logger.info("ThreadId {} Iniciando o processo de reconhecimento.", Thread.currentThread().getId());
        if (isCloneLocal) { // É clone
            symbol = lexer.getNext();
            //tempNLPOutputResult.clear();

        } else { // Nao é clone - primeira execucao
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
                    logger.debug("ThreadId {} Não há transições válidas e a pilha está vazia. A cadeia não é válida.",
                            Thread.currentThread().getId());
                    return false;
                } else {
                    String current = machines.pop();
                    if (submachines.get(current).getSecond().contains(state)) {
                        int reference = state;
                        state = stack.pop();
                        nlpState = nlpStack.pop(); // 2018.09.17

                        lexer.push(symbol);
                        logger.debug("ThreadId {} Não há transições válidas e o estado corrente é de aceitação. A pilha contém "
                                + "elementos, retornando para o estado indicado no topo da pilha ({}) e "
                                + "devolvendo o token corrente ({}) ao analisador léxico.", Thread.currentThread().getId(), state, symbol);
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
                            //actionLabel.execute(nlpState.getTransition().getPostLabelElements(), tempNLPTransducerStack); // Newton 2018.11.09
                        }
                    } else {
                        logger.info("ThreadId {} Não há transições válidas e o estado "
                                + "corrente não é de aceitação. Não é "
                                + "possível retornar para o estado de "
                                + "retorno do topo da pilha. A cadeia "
                                + "não é válida.", Thread.currentThread().getId());
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
                        // Pergunta ao usuário se inicia thread
                        Scanner userInput = new Scanner(System.in);
                        String input = "y";   // teste forçado, decomentar abaixo e tirar para forçar interação
                        /*
                        while (!(input.equals("y") || input.equals("n"))) {
                            System.out.println(Thread.currentThread().getName() + ": Inicia nova thread? (y ou n)");
                            input = userInput.nextLine();
                        }
                        */

                        if (input.equals("y")) {
                            StructuredPushdownAutomatonNLP newSpa =
                                    new StructuredPushdownAutomatonNLP(this, query.get(i));
                            NLPSpaThread NLPSpaThread = new NLPSpaThread(newSpa, this.nlpOutputList,
                                    this.nlpTransducerStackList, this.depStackList, Thread.currentThread().getId());
                            //try {
                            Thread newThread = new Thread(NLPSpaThread);
                            newThread.start();
                            //}
                        } else {
                            System.out.println("Thread ID " + Thread.currentThread().getId() + ": Nova thread não iniciada.");
                        }
                        //catch (Exception e) {
                        //    logger.debug("Error in thread preorderParse. " + e.getMessage());
                        //    break;
                        //}
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
                    //actionLabel.execute(query.get(0).getPreLabelElements(), tempNLPTransducerStack); // Newton 2018.11.09
                }

                if (query.get(0).isSubmachineCall()) {
                    machines.push(query.get(0).getSubmachine());
                    if (getRecursionCount(machines, query.get(0).getSubmachine()) < this.maxSubmachineRecursiveCalls) { // verifica tamanho da pilha. Se menor que o limiar continua.
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

                    } else { // Pilha pasou do limiar. Aborta.
                        logger.info("ThreadId {} Limiar de chamada de submáquina {} atingido. Chamada à "
                                        + "submáquina '{}'",Thread.currentThread().getId(),this.maxSubmachineRecursiveCalls,
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
                        //actionLabel.execute(query.get(0).getPostLabelElements(), tempNLPTransducerStack); // Newton 2018.11.09
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
                    //actionLabel.execute(nlpState.getTransition().getPostLabelElements(), tempNLPTransducerStack); // Newton 2018.11.09
                }
                for (Action action : nlpState.getTransition().getPostActions()) {
                    logger.debug("Executando ação posterior no retorno de submaquina: {}", action.getName());
                    action.execute(symbol);
                }
            } else {
                //
                // Integer newState = checkAndDoEmptyTransition(state,transducerStack);
                Integer newState = checkAndDoEmptyTransition(state,tempNLPTransducerStack); // Newton 2018.11.09
                if (newState != -1) {
                    state = newState;
                }
                else {
                    //break;

                    return false;
                }
            }
        }

        if (stack.isEmpty()) {
            boolean done = false;
            while (!done) {
                // Integer newState = checkAndDoEmptyTransition(state,transducerStack);
                Integer newState = checkAndDoEmptyTransition(state,tempNLPTransducerStack); // Newton 2018.11.09
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
            logger.info("Resultado do reconhecimento: cadeia {}", (result ? "aceita" : "rejeitada"));

            if (result) {
                printContextFreeNLPOutput(this.nlpOutputList.getOutputResult(Thread.currentThread().getId()), Thread.currentThread().getId(), Thread.currentThread().getName());

                // Processamento de padrões de dependências
                Integer type = Integer.valueOf(appProperties.getProperty("type"));
                if (appProperties.getProperty("inputNLPDependencyPatternsFileName").isEmpty() || (type < 3)) {
                    logger.info("Parsing de dependências não foi solicitado.");
                    System.out.println(Thread.currentThread().getName() + " Parsing de dependências não foi solicitado.");
                } else {
                    switch (type) {
                        case 0:
                        case 1:
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            DepParseTree depParseTree = new DepParseTree(this);
                            StringBuilder conlluOutput = new StringBuilder();
                            boolean depResult = depParseTree.parsePreorderFromLeaf(conlluOutput);
                            logger.info("Resultado do parsing de dependências: {}", (depResult ? "OK" : "NOK"));
                            if (depResult) {
                                logger.info("Conllu output:\n{}", conlluOutput.toString());
                                System.out.println(Thread.currentThread().getName() + " Conllu output:\n" + conlluOutput.toString());
                            }
                            break;
                    }
                }
            }
            return result;
        } else {
            logger.info("A pilha não está vazia e o estado corrente não é de aceitação. A cadeia não é válida.");
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

    synchronized void printContextFreeNLPOutput(LinkedList<String> outputList, long threadID, String threadName)
    {
        NLPTreeNode<String> root = new NLPTreeNode<>("root");

        StringBuilder sbPlain = new StringBuilder();
        StringBuilder sbJson = new StringBuilder();
        StringBuilder sbLatex = new StringBuilder();
        String lastChar = "";


        // Simple print
        for (String currentString: outputList) {
            sbPlain.append(currentString);
        }
        // Inserir print de resultado positivo aqui.
        //logger.info("\n###################################################################");
        logger.info("\n#! Output Plain: \n{}\n",sbPlain);
        System.out.println("\n#! " + threadName+ " Output Plain: "+sbPlain+"\n");
        //logger.info("###################################################################\n");

        for (String currentString: outputList) {
            String newString = "";
            //if (currentString.matches("\\[\\(")) {
            if (currentString.matches("\\[\\(")) {  // Inicio de producao
                newString="{\"nterm\":{\"children\":[";
                if (lastChar.equals("}")) {  // Se o último caractere de saída foi fechamento é adicionado o separador ","
                    newString = "," + newString;
                }
                logger.debug(" currentString: {}: match [( - out: {}",currentString,newString);
            } else if (currentString.matches("\\\".*\\\"")) { // Conteúdo do Terminal - palavra em linguagem natural
                newString="{\"term\":{\"content\":" + currentString;
                if (lastChar.equals("}")) { // Se o último caractere de saída foi fechamento é adicionado o separador ","
                    newString = "," + newString;
                }
                logger.debug(" currentString: {}: match term value - out: {}",currentString,newString);
            } else if (currentString.matches("\\(ε\\)")) { // vazio
                newString="{\"empty\":\"\"}";
                logger.debug(" currentString: {}: match (term) - out: {}",currentString,newString);
            } else if (currentString.matches("\\(.*\\)")) { // Terminal
                newString=",\"name\":\""+ currentString.substring(1,currentString.length()-1) + "\"}}";
                logger.debug(" currentString: {}: match (term) - out: {}",currentString,newString);
            } else if (currentString.matches(".*\\)")) { // Não terminal
                newString="],\"name\":\""+ currentString.substring(0,currentString.length()-1)+"\"}";
                logger.debug(" currentString: {}: match nterm - out: {}",currentString,newString);
            } else if (currentString.matches("\\).*")) { // Não terminal
                newString="],\"name\":\""+ currentString.substring(1) + "\"}";
                logger.debug(" currentString: {}: match )nterm - out: {}",currentString,newString);
            } else if (currentString.matches("\\)")) { // Fim elemento (terminal ou não terminal)
                newString="}";
                logger.debug(" currentString: {}: match ) - out: {}",currentString,newString);
            } else if (currentString.matches("\\(")) { // Início
                newString="{";
                logger.debug(" currentString: {}: match ( - out: {}",currentString,newString);
            } else if (currentString.matches("\\]")) { // Fim de lista
                newString="}";
                logger.debug(" currentString: {}: match ] - out: {}",currentString,newString);
            } else { // Erro
                logger.debug(" currentString: {}: unknown match",currentString);
            }
            sbJson.append(newString);
            lastChar = sbJson.substring(sbJson.length()-1);
        }


        //logger.info("###################################################################");
        logger.info("\n#! Output JSON : \n{}\n",sbJson);
        System.out.println("\n#! "+threadName+" Output JSON: "+sbJson+"\n");
        //logger.info("###################################################################");


        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(sbJson.toString());
        sbLatex.append(jsonOuputToLatex(element));

        //logger.info("###################################################################");
        logger.info("\n#! Output Latex: \n{}\n",sbLatex);
        System.out.println("\n#! "+threadName+" Output Latex: "+sbLatex+"\n");
        //logger.info("###################################################################");
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
            } else if (entry.getKey().equals("empty")) {
                String newString = "[\\nt{\\epsilon}]";
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
