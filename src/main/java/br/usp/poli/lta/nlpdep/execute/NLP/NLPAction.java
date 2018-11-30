package br.usp.poli.lta.nlpdep.execute.NLP;

import br.usp.poli.lta.nlpdep.execute.NLP.dependency.*;
import br.usp.poli.lta.nlpdep.execute.NLP.output.NLPOutputList;
import br.usp.poli.lta.nlpdep.mwirth2ape.ape.ActionLabels;
import br.usp.poli.lta.nlpdep.mwirth2ape.structure.Stack;
import br.usp.poli.lta.nlpdep.mwirth2ape.ape.Action;
import br.usp.poli.lta.nlpdep.mwirth2ape.labeling.LabelElement;
import br.usp.poli.lta.nlpdep.mwirth2ape.labeling.Production;
import br.usp.poli.lta.nlpdep.mwirth2ape.model.Token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class NLPAction {

    private static final Logger logger = LoggerFactory.
            getLogger(NLPAction.class);

    private NLPOutputList nlpOutputList;
    private HashSet<String> dictionaryTerm;
    //private br.usp.poli.lta.nlpdep.execute.NLP.NLPTransducerStackList NLPTransducerStackListList;

    public Action semanticActionTermTransition;
    public Action semanticActionNtermTransition;
    public Action semanticActionEmptyTransition;
    public ActionLabels semanticActionLabels;
    //public ActionState semanticActionState;

    public NLPAction(NLPOutputList nlpOutputList, HashSet<String> dictionaryTerm,
                     NLPTransducerStackList nlpTransducerStackList, DepStackList depStackList) {
        this.nlpOutputList = nlpOutputList;
        this.dictionaryTerm = dictionaryTerm;

        // Acao semantica associado a transicao com terminal
        this.semanticActionTermTransition = new Action("semanticActionTermTransition") {
            @Override
            public void execute(Token token) {
                long threadId = Thread.currentThread().getId();
                if (token.getType().equals("term")) {
                    nlpOutputList.insertOutputResult(threadId, "\"" + token.getValue() + "\"");
                    //outputList.addLast("\"" + token.getValue() + "\"");
                    logger.debug("ThreadID {}: Ação semântica: Terminal consumido: POS tag {}, value \"{}\".",
                            String.valueOf(threadId),
                            token.getNlpToken().getNlpWords().get(0).getPosTag(), token.getValue());
                    DepStackElementWord newDepStackElementWord = new DepStackElementWord (token.getValue());
                    newDepStackElementWord.setNlpDictionaryEntry(token.getNlpToken().getNlpWords().get(0).getNlpDictionaryEntry());
                    logger.debug("### DepStack Push: {}", newDepStackElementWord);
                    depStackList.getDepStackList(threadId).push(newDepStackElementWord);
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
                Stack<DepStackElement> depStack = depStackList.getDepStackList(threadId);
                //transducerStack = nlpTransducerStackList.getTransducerStackList(threadId); // 2018.11.09

                // logger.debug("ThreadID {}: Ação semântica: Labels", String.valueOf(threadId)); // 2018.11.11
                logger.debug(" ###ThreadID {} # Ação semântica labels: {} # Stack before: {}", String.valueOf(threadId),
                        labels.toString(), transducerStack.toString());

                if (labels != null) {
                    logger.debug("Com labels: {}", labels.toString());
                    for (LabelElement singleLabelElement : labels) {
                        if (singleLabelElement != null) { // verifica se não retornou elemento de rotulo nulo
                            String labelSymbol = singleLabelElement.getValue();
                            Production labelProduction = singleLabelElement.getProduction();
                            if (labelProduction == null) {
                                if (labelSymbol.equals("ε")) {
                                    nlpOutputList.insertOutputResult(threadId, "(ε)"); // plain
                                    DepStackElementEmpty newDepStackElement = new DepStackElementEmpty();
                                    logger.debug("### DepStack Push: {}", newDepStackElement);
                                    depStack.push(newDepStackElement);
                                } else if (dictionaryTerm.contains(String.valueOf(labelSymbol))) {
                                    nlpOutputList.insertOutputResult(threadId, "(" + labelSymbol + ")"); // plain
                                    DepStackElementTerm newDepStackElementTerm = new DepStackElementTerm(labelSymbol);
                                    logger.debug("### DepStack Push: {}", newDepStackElementTerm);
                                    depStack.push(newDepStackElementTerm);
                                } else if (labelSymbol.equals("[")) {
                                    String stackElement = "]";
                                    transducerStack.push(stackElement);
                                    nlpOutputList.insertOutputResult(threadId, "[(");
                                    DepStackElement newDepStackElement = new DepStackElement("]","]");
                                    logger.debug("### DepStack Push: {}", newDepStackElement);
                                    depStack.push(newDepStackElement);
                                } else if (labelSymbol.equals("]")) {
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
                                            logger.debug(" ### Dep parsing: encontrei {} na saída.", tempNtermIdentifier);
                                            depParsingNterm(threadId, depStack, labelProduction); // revisar
                                        } else {
                                            logger.debug(" ### Erro Dep parsing: encontrei {} na saída.",tempString);
                                        }
                                    }
//                                    DepStackElement newDepStackElement = depStack.pop();
//                                    if (! newDepStackElement.getType().equals("]")) {
//                                        logger.debug(" ###ThreadID {} # Erro no processamento de dependências - stack: {} no lugar de ]",
//                                                threadId, newDepStackElement.getType());
//                                    }
                                }
                            } else {
                                if (labelProduction.getRecursion().equals("right")) {
                                    nlpOutputList.insertOutputResult(threadId, labelProduction.getIdentifier() + ")");
                                    //outputList.addLast(labelProduction.getIdentifier() + ")");
                                    // Caso Xi) na saída

                                    depParsingNterm(threadId, depStack, labelProduction);

                                } else if (labelProduction.getRecursion().equals("left")) {
                                    //String stackElement1 = "]";
                                    //transducerStack.push(stackElement1);
                                    String stackElement2 = labelProduction.getIdentifier() + ")]"; // 2018.11.29
                                    transducerStack.push(stackElement2);
                                    nlpOutputList.insertOutputResult(threadId, "[("); // 2018.11.29
                                    DepStackElement newDepStackElement = new DepStackElement("]","]");
                                    logger.debug("### DepStack Push: {}", newDepStackElement);
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
                                            logger.debug(" ### Dep parsing: encontrei {} na saída.", tempNtermIdentifier);
                                            depParsingNterm(threadId, depStack, labelProduction);
                                        } else {
                                            logger.debug(" ### Erro Dep parsing: encontrei {} na saída.",tempString);
                                        }
                                    }
                                    sb.append(labelProduction.getIdentifier()).append(")");
                                    nlpOutputList.insertOutputResult(threadId, sb.toString());
                                    depParsingNterm(threadId, depStack, labelProduction);

                                    //outputList.addLast(sb.toString());
                                }
                            }
                        }
                    }

                }
                else {
                    logger.debug("Sem labels.");
                }
                logger.debug(" ###ThreadID {} # Ação semântica labels: {} # Stack after: {}", String.valueOf(threadId),
                        labels.toString(), transducerStack.toString());
            }
        };
    }

    private Boolean depParsingNterm(long threadID, Stack<DepStackElement> depStack, Production labelProduction ) {
        //Boolean result = false;
        ArrayList<DepStackElement> poppedDepStackElements = new ArrayList<>();
        while (!depStack.isEmpty()) {
            if (!depStack.top().getType().equals("]")) {
                DepStackElement tempDepStackElement = depStack.pop();
                logger.debug(" ### Nterm Analysis DepStack Popped: {}", tempDepStackElement);
                poppedDepStackElements.add(tempDepStackElement);
            } else {
                DepStackElement tempDepStackElement = depStack.pop();
                logger.debug(" ### Nterm Analysis DepStack Popped: {}", tempDepStackElement);
                poppedDepStackElements.add(tempDepStackElement);
                break;
            }
        }
        if (poppedDepStackElements.isEmpty()) {
            return false;
        }

        logger.debug("#### Dep Parsing finished Nterm: {}", labelProduction.getIdentifier());
        if (depStack.isEmpty()) {
            logger.debug("#### Finished Dep Parsing.");
        } else {
            DepStackElementNterm newDepStackElementNterm = new DepStackElementNterm(labelProduction.getIdentifier());
            logger.debug("### Nterm Analysis DepStack Push: {}", newDepStackElementNterm);
            depStack.push(newDepStackElementNterm);
        }
        return true;
    }

    private Boolean depParsingStep (long threadID, Stack<DepStackElement> depStack, Production labelProduction) {
        Boolean result = false;



        return result;
    }

}
