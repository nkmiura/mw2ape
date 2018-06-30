package br.usp.poli.lta.cereda.execute.NLP;

import br.usp.poli.lta.cereda.mwirth2ape.ape.Action;
import br.usp.poli.lta.cereda.mwirth2ape.ape.ActionState;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.LabelElement;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.Production;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
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
    private Stack<String> transducerStack;

    public NLPAction(NLPOutputList nlpOutputList, HashSet<String> dictionaryTerm, Stack<String> transducerStack) {
        this.nlpOutputList = nlpOutputList;
        this.dictionaryTerm = dictionaryTerm;
        this.transducerStack = transducerStack.clone();
    }


    // Acao semantica associado a transicao com terminal
    public Action semanticActionTermTransition = new Action("semanticActionTermTransition") {
        @Override
        public void execute(Token token) {
            long threadId = Thread.currentThread().getId();
            if (token.getType().equals("term")) {
                nlpOutputList.insertOutputResult(threadId,"\"" + token.getValue() + "\"");
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
    public Action semanticActionNtermTransition = new Action("semanticActionNtermTransition") {
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
    public Action semanticActionEmptyTransition = new Action("semanticActionEmptyTransition") {
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
    /*
    // Acao semantica associado ao estado (maquina de Moore) para gerar saída e manipular pilha de acordo com rotulos
    public ActionState semanticActionState = new ActionState("semanticActionState") {
        @Override
        public void execute(LinkedList<LabelElement> labels) {
            long threadId = Thread.currentThread().getId();

            logger.debug("ThreadID {}: Ação semântica: Labels", String.valueOf(threadId));
            for (LabelElement singleLabelElement : labels) {
                if (singleLabelElement != null) { // verifica se não retornou elemento de rotulo nulo
                    String labelSymbol = singleLabelElement.getValue();
                    Production labelProduction = singleLabelElement.getProduction();
                    if (labelProduction == null) {
                        if (labelSymbol.equals("ε")) {
                            nlpOutputList.insertOutputResult(threadId,"()");
                            //outputList.addLast("()");
                        } else if (dictionaryTerm.contains(String.valueOf(labelSymbol))) {
                            nlpOutputList.insertOutputResult(threadId,"(" + labelSymbol + ")");
                            //outputList.addLast("(" + labelSymbol + ")");
                        } else if (labelSymbol.equals("[")) {
                            nlpOutputList.insertOutputResult(threadId,"[(");
                            //outputList.addLast("[(");
                            transducerStack.push("]");
                        } else if (labelSymbol.equals("]")) {
                            StringBuilder sb = new StringBuilder();
                            while (!transducerStack.top().equals("]")) {
                                sb.append(transducerStack.pop());
                            }
                            transducerStack.pop();
                            sb.reverse();
                            sb.append("]");
                            nlpOutputList.insertOutputResult(threadId,sb.toString());
                            //outputList.addLast(sb.toString());
                        }
                    }
                    else {
                        if (labelProduction.getRecursion().equals("right")) {
                            nlpOutputList.insertOutputResult(threadId,labelProduction.getIdentifier() + ")");
                            //outputList.addLast(labelProduction.getIdentifier() + ")");
                        }
                        else if (labelProduction.getRecursion().equals("left")) {
                            String stackElement = ")" + labelProduction.getIdentifier();
                            transducerStack.push(stackElement);
                            nlpOutputList.insertOutputResult(threadId,"(");
                            //outputList.addLast("(");
                        }
                        else {
                            StringBuilder sb = new StringBuilder();
                            while (!transducerStack.top().equals("]")) {
                                sb.append(transducerStack.pop());
                            }
                            sb.reverse();
                            sb.append(labelProduction.getIdentifier()).append(")");
                            nlpOutputList.insertOutputResult(threadId,sb.toString());
                            //outputList.addLast(sb.toString());
                        }
                    }
                }
            }
        }
    };
    */
}
