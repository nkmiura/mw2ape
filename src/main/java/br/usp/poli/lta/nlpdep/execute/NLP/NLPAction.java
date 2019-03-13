package br.usp.poli.lta.nlpdep.execute.NLP;

import br.usp.poli.lta.nlpdep.execute.NLP.dependency.*;
import br.usp.poli.lta.nlpdep.execute.NLP.output.*;
import br.usp.poli.lta.nlpdep.mwirth2ape.ape.ActionLabels;
import br.usp.poli.lta.nlpdep.mwirth2ape.structure.Stack;
import br.usp.poli.lta.nlpdep.mwirth2ape.ape.Action;
import br.usp.poli.lta.nlpdep.mwirth2ape.labeling.LabelElement;
import br.usp.poli.lta.nlpdep.mwirth2ape.labeling.Production;
import br.usp.poli.lta.nlpdep.mwirth2ape.model.Token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class NLPAction {

    private static final Logger logger = LoggerFactory.
            getLogger(NLPAction.class);

    private NLPOutputList nlpOutputList;
    private HashSet<String> dictionaryTerm;
    private Properties appProperties;
    //private br.usp.poli.lta.nlpdep.execute.NLP.NLPTransducerStackList NLPTransducerStackListList;

    public Action semanticActionTermTransition;
    public Action semanticActionNtermTransition;
    public Action semanticActionEmptyTransition;
    public ActionLabels semanticActionLabels;
    //public ActionState semanticActionState;

    public NLPAction(NLPOutputList nlpOutputList, HashSet<String> dictionaryTerm,
                     NLPTransducerStackList nlpTransducerStackList, DepStackList depStackList, Properties appProperties) {
        this.nlpOutputList = nlpOutputList;
        this.dictionaryTerm = dictionaryTerm;
        this.appProperties = appProperties;

        // Acao semantica associado a transicao com terminal
        this.semanticActionTermTransition = new Action("semanticActionTermTransition") {
            @Override
            public void execute(Token token) {
                long threadId = Thread.currentThread().getId();
                if (token.getType().equals("term")) {
                    nlpOutputList.insertOutputResult(threadId, "\"" + token.getValue() + "\"");
                    //outputList.addLast("\"" + token.getValue() + "\"");
                    logger.debug("ThreadID {}Ação semântica: Terminal consumido: POS tag {}, value \"{}\".",
                            String.valueOf(threadId),
                            token.getNlpToken().getNlpWords().get(0).getPosTag(), token.getValue());
                    DepStackElementWord newDepStackElementWord = new DepStackElementWord (token.getValue());
                    newDepStackElementWord.setNlpDictionaryEntry(token.getNlpToken().getNlpWords().get(0).getNlpDictionaryEntry());
                    newDepStackElementWord.setIdSentence(token.getNlpToken().getNlpWords().get(0).getSentenceID());
                    logger.debug("### DepStack Push: {}",  newDepStackElementWord);
                    depStackList.getDepStackFromThreadID(threadId).push(newDepStackElementWord);
                }
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        // Acao semantica associado a transicao com não terminal
        this.semanticActionNtermTransition = new Action("semanticActionNtermTransition") {
            @Override
            public void execute(Token token) {
                long threadId = Thread.currentThread().getId();
                logger.debug("ThreadID {}: Ação semântica: Transiçao com chamada de submáquina.",
                        String.valueOf(threadId));
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        // Acao semantica associado a transicao em vazio
        this.semanticActionEmptyTransition = new Action("semanticActionEmptyTransition") {
            @Override
            public void execute(Token token) {
                long threadId = Thread.currentThread().getId();
                logger.debug("ThreadID {}: Ação semântica: Transição em vazio.", String.valueOf(threadId));
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        // Acao semantica associado a transicao para gerar saída e manipular pilha de acordo com rotulos
        this.semanticActionLabels = new ActionLabels ("semanticActionLabels") {
            @Override
            public void execute(LinkedList<LabelElement> labels, Stack<String> transducerStack) {
            //public void execute(LinkedList<LabelElement> labels) {
                long threadId = Thread.currentThread().getId(); // 2018.11.09
                Stack<DepStackElement> depStack = depStackList.getDepStackFromThreadID(threadId);
                //transducerStack = nlpTransducerStackList.getTransducerStackList(threadId); // 2018.11.09

                // logger.debug("ThreadID {}: Ação semântica: Labels", String.valueOf(threadId)); // 2018.11.11
                logger.debug(" #### Ação semântica labels: {} # Stack before: {}", String.valueOf(threadId),
                        labels.toString(), transducerStack.toString());

                if (labels != null) {
                    logger.debug("Com labels: {}", labels.toString());
                    for (LabelElement singleLabelElement : labels) {
                        if (singleLabelElement != null) { // verifica se não retornou elemento de rotulo nulo
                            String labelSymbol = singleLabelElement.getValue();
                            Production labelProduction = singleLabelElement.getProduction();
                            logger.debug("### DepStack before: {}", depStack);
                            if (labelProduction == null) {
                                if (labelSymbol.equals("ε")) { // vazio
                                    nlpOutputList.insertOutputResult(threadId, "(ε)"); // plain
                                    DepStackElementEmpty newDepStackElement = new DepStackElementEmpty();
                                    logger.debug("### DepStack Push: {}",  newDepStackElement);
                                    depStack.push(newDepStackElement);
                                } else if (dictionaryTerm.contains(String.valueOf(labelSymbol))) { // term
                                    nlpOutputList.insertOutputResult(threadId, "(" + labelSymbol + ")"); // plain
                                    DepStackElementTerm newDepStackElementTerm = new DepStackElementTerm(labelSymbol);
                                    DepStackElement newDepStackElementWord = (DepStackElementWord) depStack.pop();
                                    if (newDepStackElementWord.getClass() != DepStackElementWord.class) {
                                        logger.info("### DepStack Error: word not found in the top of stack when processing term {}.", newDepStackElementTerm);
                                        throw new IllegalStateException ("### DepStack Error: word not found in the top of stack when processing term " + newDepStackElementTerm.toString());
                                    }
                                    NLPOutputToken nlpOutputTokenTerm = new NLPOutputToken(labelSymbol, "term");
                                    nlpOutputTokenTerm.setNlpDictionaryEntry(((DepStackElementWord)newDepStackElementWord).getNlpDictionaryEntry());
                                    nlpOutputTokenTerm.setIdSentence(((DepStackElementWord)newDepStackElementWord).getIdSentence());
                                    nlpOutputTokenTerm.setNlpWord(((DepStackElementWord)newDepStackElementWord).getValue());
                                    Node<NLPOutputToken> node = new Node<>(nlpOutputTokenTerm);
                                    newDepStackElementTerm.setNode(node);
                                    logger.debug("### DepStack Push: {}", newDepStackElementTerm);
                                    depStack.push(newDepStackElementTerm);
                                } else if (labelSymbol.equals("[")) {  // [
                                    String stackElement = "]";
                                    transducerStack.push(stackElement);
                                    nlpOutputList.insertOutputResult(threadId, "[(");
                                    DepStackElement newDepStackElement = new DepStackElement("]","]");
                                    logger.debug("### DepStack Push: {}",  newDepStackElement);
                                    depStack.push(newDepStackElement);
                                } else if (labelSymbol.equals("]")) { // ]
                                    StringBuilder sb = new StringBuilder();
                                    LinkedList<String> poppedTransducerStackElements = new LinkedList<>();

                                    while (!transducerStack.isEmpty()) {
                                        if (!transducerStack.top().equals("]")) {
                                            String tempString = transducerStack.pop();
                                            poppedTransducerStackElements.push(tempString);
                                            sb.append(tempString);
                                        } else {
                                            transducerStack.pop();
                                            //logger.debug("## Error label action: empty stack before ]");
                                            break;
                                        }
                                    }
                                    sb.reverse();
                                    sb.append("]"); // Newton 2018.11.08
                                    nlpOutputList.insertOutputResult(threadId, sb.toString());
                                    for (String tempString : poppedTransducerStackElements) {
                                        if (tempString.matches(".*\\)\\]")) {
                                            String tempNtermIdentifier = tempString.substring(0,tempString.length()-2);
                                            logger.debug(" ### Dep parsing: encontrei {} na saída.",  tempNtermIdentifier);
                                            depParseNtermStackElement(threadId, depStack, labelProduction); // revisar
                                        } else {
                                            logger.debug(" ### Erro Dep parsing: encontrei {} na saída.", tempString);
                                        }
                                    }
                                }
                            } else {
                                if (labelProduction.getRecursion().equals("right")) {
                                    nlpOutputList.insertOutputResult(threadId, labelProduction.getIdentifier() + ")");
                                    // Caso Xi) na saída

                                    depParseNtermStackElement(threadId, depStack, labelProduction);

                                } else if (labelProduction.getRecursion().equals("left")) {
                                    //String stackElement1 = "]";
                                    //transducerStack.push(stackElement1);
                                    String stackElement2 = labelProduction.getIdentifier() + ")]"; // 2018.11.29
                                    transducerStack.push(stackElement2);
                                    nlpOutputList.insertOutputResult(threadId, "[("); // 2018.11.29
                                    DepStackElement newDepStackElement = new DepStackElement("]","]");
                                    logger.debug("### DepStack Push: {}",  newDepStackElement);
                                    depStack.push(newDepStackElement);
                                } else {
                                    StringBuilder sb = new StringBuilder();
                                    LinkedList<String> poppedTransducerStackElements = new LinkedList<>();
                                    while (!transducerStack.isEmpty()) {
                                        if (!transducerStack.top().equals("]")) {
                                            String tempString = transducerStack.pop();
                                            poppedTransducerStackElements.push(tempString);
                                            sb.append(tempString);
                                        } else {
                                            break;
                                        }
                                    }
                                    sb.reverse();
                                    for (String tempString : poppedTransducerStackElements) {
                                        if (tempString.matches(".*\\)\\]")) {
                                            String tempNtermIdentifier = tempString.substring(0,tempString.length()-2);
                                            logger.debug(" ### Dep parsing: encontrei {} na saída.",tempNtermIdentifier);
                                            depParseNtermStackElement(threadId, depStack, labelProduction);
                                        } else {
                                            logger.debug(" ### Erro Dep parsing: encontrei {} na saída.",tempString);
                                        }
                                    }
                                    sb.append(labelProduction.getIdentifier()).append(")");
                                    nlpOutputList.insertOutputResult(threadId, sb.toString());
                                    depParseNtermStackElement(threadId, depStack, labelProduction);
                                }
                            }
                            logger.debug("### DepStack after: {}", depStack);
                        }
                    }
                }
                else {
                    logger.debug("Sem labels.",threadId);
                }
                logger.debug(" ### # Ação semântica labels: {} # Stack after: {}", String.valueOf(threadId),
                        labels.toString(), transducerStack.toString());
            }
        };
    }


    // Processa não terminal após obtenção do elemento da pilha para parsing de dependencias
    private boolean depParseNtermStackElement(long threadID, Stack<DepStackElement> depStack, Production labelProduction ) {
        //boolean result = false;
        // monta lista com com os elementos obtidos da pilha
        LinkedList<DepStackElement> poppedDepStackElements = new LinkedList<>();
        while (!depStack.isEmpty()) {
            if (!depStack.top().getType().equals("]")) {
                DepStackElement tempDepStackElement = depStack.pop();
                logger.debug(" ### Nterm Analysis DepStack Popped: {}",  tempDepStackElement);
                poppedDepStackElements.push(tempDepStackElement);
            } else {
                DepStackElement tempDepStackElement = depStack.pop();
                logger.debug(" ### Nterm Analysis DepStack Popped: {}",  tempDepStackElement);
                //poppedDepStackElements.add(tempDepStackElement);
                break;
            }
        }
        if (poppedDepStackElements.isEmpty()) {
            return false;
        }
        logger.debug(" ### DepStackElements Popped ({}) {}",poppedDepStackElements.size(), poppedDepStackElements);

        ArrayList<DepPattern> depPatternArrayList = getDepPatterns(labelProduction, poppedDepStackElements, true);  // parâmetro strict para considerar a instancia da regra de produção de um não terminal

        if (appProperties.getProperty("type").equals("5")) {
            // Carrega em depPatternArrayList os padrões de dependência que correspondem à sequência que estava na pilha se type = 5
            if (depPatternArrayList.size() == 0) {
                logger.debug("##### Dep Parsing did not found any DepPatterns");
                return false;
            }
            logger.debug("##### Dep Parsing found DepPatterns: {}", depPatternArrayList);
        }


        NLPOutputToken newNlpOutputTokenNterm = new NLPOutputToken(labelProduction.getIdentifier(), "nterm");  // nova estrutura de dados associados a nterm
        newNlpOutputTokenNterm.setDepPatternArrayList(depPatternArrayList); // associa a lista de padroes de dependencia encontrados

        logger.debug("#### Dep Parsing finished Nterm: {}",  labelProduction.getIdentifier());

        if (depStack.isEmpty()) {
            logger.debug("#### Finished Dep Parsing. root Nterm will be pushed.");
        }

        // Atribui o nó ao novo elemento de pilha do tipo nterm e empilha
        DepStackElementNterm newDepStackElementNterm = new DepStackElementNterm(labelProduction.getIdentifier());
        Node<NLPOutputToken> newNlpOutputTokenNtermNode = new Node<NLPOutputToken>(newNlpOutputTokenNterm);
        newDepStackElementNterm.setNode(newNlpOutputTokenNtermNode);
        // adiciona os elementos como nós filhos.

        for (DepStackElement tempDepStackElement: poppedDepStackElements) {
            if (tempDepStackElement.getClass() == DepStackElementTerm.class) {
                newNlpOutputTokenNtermNode.addChild(((DepStackElementTerm)tempDepStackElement).getNode());
            } else if (tempDepStackElement.getClass() == DepStackElementNterm.class) {
                newNlpOutputTokenNtermNode.addChild(((DepStackElementNterm)tempDepStackElement).getNode());
            }
        }
        logger.debug("### Nterm Analysis DepStack Push: {}",  newDepStackElementNterm);
        depStack.push(newDepStackElementNterm);
        return true;
    }



    // Faz busca de padrões de dependências que coincidam com a sequência de nós filhos de um não terminal
    private ArrayList<DepPattern> getDepPatterns (Production labelProduction, LinkedList<DepStackElement> depStackElementList, boolean strict) {
        ArrayList<DepPattern> depPatternArrayList =  new ArrayList<>();

        for (DepPattern depPattern: labelProduction.getDepPatterns() ) {
            if (depPattern.getDepPatternConstituents().size() == depStackElementList.size()) {
                //Integer i = depStackElementArrayList.size() - 1;
                boolean match = true;
                for (Integer i = 0; i < depStackElementList.size(); i++) {
                    if (strict) {
                        if (!depStackElementList.get(i).getValue().equals(depStackElementList.get(i).getValue())) {
                            match = false;
                            break;
                        }
                    } else {
                        String depPatternConstituentValue = depStackElementList.get(i).getValue();
                        Integer depPatternlastIdx = depStackElementList.get(i).getValue().lastIndexOf('_');
                        if (depPatternlastIdx > 0) {
                            depPatternConstituentValue = depStackElementList.get(i).getValue().substring(0,depPatternlastIdx);
                        }
                        String depStackElement = depStackElementList.get(i).getValue();
                        Integer depStackElementIdx = depStackElementList.get(i).getValue().lastIndexOf('_');
                        if (depStackElementIdx > 0) {
                            depStackElement = depStackElementList.get(i).getValue().substring(0,depStackElementIdx);
                        }
                        if (!depPatternConstituentValue.equals(depStackElement)) {
                            match = false;
                            break;
                        }
                    }
                }
                if (match) {
                    depPatternArrayList.add(depPattern);
                }
            }
        }

        return depPatternArrayList;
    }

}
