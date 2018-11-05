package br.usp.poli.lta.cereda.execute.NLP;

import br.usp.poli.lta.cereda.mwirth2ape.ape.ActionLabels;
import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
import br.usp.poli.lta.cereda.mwirth2ape.ape.Action;
import br.usp.poli.lta.cereda.mwirth2ape.ape.ActionState;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.LabelElement;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.Production;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class NLPAction {

    private static final Logger logger = LoggerFactory.
            getLogger(NLPAction.class);

    private NLPOutputList nlpOutputList;
    private HashSet<String> dictionaryTerm;
    //private br.usp.poli.lta.cereda.execute.NLP.NLPTransducerStackList NLPTransducerStackListList;

    public Action semanticActionTermTransition;
    public Action semanticActionNtermTransition;
    public Action semanticActionEmptyTransition;
    public ActionLabels semanticActionLabels;
    //public ActionState semanticActionState;

    public NLPAction(NLPOutputList nlpOutputList, HashSet<String> dictionaryTerm,
                     br.usp.poli.lta.cereda.execute.NLP.NLPTransducerStackList nlpTransducerStackList) {
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
                long threadId = Thread.currentThread().getId();
                transducerStack = nlpTransducerStackList.getTransducerStackList(threadId);

                logger.debug("ThreadID {}: Ação semântica: Labels", String.valueOf(threadId));
                if (labels != null) {
                    logger.debug("Com labels: {}", labels.toString());
                    for (LabelElement singleLabelElement : labels) {
                        if (singleLabelElement != null) { // verifica se não retornou elemento de rotulo nulo
                            String labelSymbol = singleLabelElement.getValue();
                            Production labelProduction = singleLabelElement.getProduction();
                            if (labelProduction == null) {
                                if (labelSymbol.equals("ε")) {
                                    nlpOutputList.insertOutputResult(threadId, "()"); // plain
                                    //nlpOutputList.insertOutputResult(threadId, "\n\"term\": {\"value\": \"()\",");
                                } else if (dictionaryTerm.contains(String.valueOf(labelSymbol))) {
                                    nlpOutputList.insertOutputResult(threadId, "(" + labelSymbol + ")"); // plain
                                    //nlpOutputList.insertOutputResult(threadId, "type: \"" + labelSymbol + "\"}");
                                } else if (labelSymbol.equals("[")) {
                                    nlpOutputList.insertOutputResult(threadId, "[(");
                                } else if (labelSymbol.equals("]")) {
                                    StringBuilder sb = new StringBuilder();
                                    //while (!transducerStack.top().equals("]")) {
                                    //    sb.append(transducerStack.pop());
                                    //}
                                    while (!transducerStack.isEmpty()) {
                                        if (!transducerStack.top().equals("]")) {
                                            sb.append(transducerStack.pop());
                                        } else {
                                            transducerStack.pop();
                                            break;
                                        }
                                    }
                                    //transducerStack.pop();
                                    sb.reverse();
                                    sb.append("]");
                                    nlpOutputList.insertOutputResult(threadId, sb.toString());
                                    //outputList.addLast(sb.toString());
                                }
                            } else {
                                if (labelProduction.getRecursion().equals("right")) {
                                    nlpOutputList.insertOutputResult(threadId, labelProduction.getIdentifier() + ")");
                                    //outputList.addLast(labelProduction.getIdentifier() + ")");
                                } else if (labelProduction.getRecursion().equals("left")) {
                                    String stackElement = ")" + labelProduction.getIdentifier();
                                    transducerStack.push(stackElement);
                                    nlpOutputList.insertOutputResult(threadId, "(");
                                    //outputList.addLast("(");
                                } else {
                                    StringBuilder sb = new StringBuilder();
                                    //while (!transducerStack.top().equals("]")) {
                                    //    sb.append(transducerStack.pop());
                                    //}
                                    while (!transducerStack.isEmpty()) {
                                        if (!transducerStack.top().equals("]")) {
                                            sb.append(transducerStack.pop());
                                        } else {
                                            break;
                                        }
                                    }
                                    sb.reverse();
                                    sb.append(labelProduction.getIdentifier()).append(")");
                                    nlpOutputList.insertOutputResult(threadId, sb.toString());
                                    //outputList.addLast(sb.toString());
                                }
                            }
                        }
                    }
                }
                else {
                    logger.debug("Sem labels.");
                }
            }
        };
    }
}
