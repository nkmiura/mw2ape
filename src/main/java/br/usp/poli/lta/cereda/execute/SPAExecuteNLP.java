package br.usp.poli.lta.cereda.execute;

import br.usp.poli.lta.cereda.execute.NLP.NLPLexer;
import br.usp.poli.lta.cereda.execute.NLP.NLPOutputList;
import br.usp.poli.lta.cereda.execute.NLP.SPAThread;
import br.usp.poli.lta.cereda.execute.NLP.StructuredPushdownAutomatonNLP;
import br.usp.poli.lta.cereda.mwirth2ape.ape.*;
import br.usp.poli.lta.cereda.mwirth2ape.ape.conversion.Sketch;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.LabelElement;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.Production;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import br.usp.poli.lta.cereda.mwirth2ape.mwirth.Generator;
import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class SPAExecuteNLP extends SPAExecute {
    private static final Logger logger = LoggerFactory.
            getLogger(SPAExecuteNLP.class);

    // APE Generator
    private NLPLexer nlpLexer;
    private NLPOutputList nlpOutputList;


    public SPAExecuteNLP(NLPLexer nlpLexer, Generator lmwg, HashSet<String> dictionaryTerm) {
        this.nlpLexer = nlpLexer;
        this.lmwg = lmwg;
        this.transducerStack = new Stack<>();
        this.transitions = lmwg.getTransitions();
        this.mapMachineStates = lmwg.getMapMachineStates();
        this.spaTransitions = new HashSet<>();
        this.stateCounter = 0;
        this.dictionaryTerm = dictionaryTerm;
        this.outputList = new LinkedList<>();
        this.nlpOutputList = new NLPOutputList();
    }

    public void parseInput() throws Exception {
        // Build SPA
        logger.debug("Started building SPA parser.");

        //this.nlpOutputList.incrementOutputList(Thread.currentThread().getId());
        //this.nlpOutputList.setParseResult(Thread.currentThread().getId(),true);
        StructuredPushdownAutomatonNLP spa = new StructuredPushdownAutomatonNLP(this.nlpLexer, this.nlpOutputList);
        //Runnable spa = new StructuredPushdownAutomatonNLP(threadIdCounter, this.nlpLexer, this.outputResults);

        spa.setSubmachine(this.lmwg.getMain());  // set main machine

        SPAGetStruct spaStruct = new SPAGetStruct(this.transitions);
        Map<String, List<Sketch>> machineSketchesMap = spaStruct.getMachinesFromTransitions(); // map list of Sketch (transitions) to a machine
        Map<String, Integer> machineMaxStateIdMap = spaStruct.getMaxStateIdFromMachineMap(machineSketchesMap);

        // Acao semantica associado a transicao com terminal
        Action semanticActionTermTransition = new Action("semanticActionTermTransition") {
            @Override
            public void execute(Token token) {
                long threadId = Thread.currentThread().getId();
                if ((nlpLexer != null) && (token.getType().equals("term"))) {
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
        Action semanticActionNtermTransition = new Action("semanticActionNtermTransition") {
            @Override
            public void execute(Token token) {
                long threadId = Thread.currentThread().getId();
                if ((nlpLexer != null)) {
                    logger.debug("ThreadID {}: Ação semântica: Transiçao com chamada de submáquina.",
                            String.valueOf(threadId));
                }
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        // Acao semantica associado a transicao em vazio
        Action semanticActionEmptyTransition = new Action("semanticActionEmptyTransition") {
            @Override
            public void execute(Token token) {
                long threadId = Thread.currentThread().getId();
                if ((nlpLexer != null)) {
                    logger.debug("ThreadID {}: Ação semântica: Transição em vazio.", String.valueOf(threadId));
                }
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        // Acao semantica associado ao estado (maquina de Moore) para gerar saída e manipular pilha de acordo com rotulos
        ActionState semanticActionState = new ActionState("semanticActionState") {
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

        // Construcao do automato
        buildSPA(spa, machineSketchesMap, stateCounter, machineMaxStateIdMap,
                semanticActionState,
                semanticActionNtermTransition, semanticActionTermTransition, semanticActionEmptyTransition);

        // end Build SPA
        spa.setup();

        // Executar automato
        SPAThread spaThread = new SPAThread(spa, this.nlpOutputList);
        Thread thread = new Thread(spaThread);

        logger.debug("Started parsing.");

        thread.start();
//        spa.parse();

        while (thread.isAlive()) {
            Thread.sleep(1000);
        }

        logger.debug("Finished parsing.");

        // Configurar saida (arquivo)
        //logger.debug("Cadeia de saída (outputlist): ");
        //logger.debug(this.outputList.toString());

        logger.debug("Cadeia de saída (outputResult): ");
        logger.debug(this.nlpOutputList.toString());

    }

}