package br.usp.poli.lta.cereda.execute;

import br.usp.poli.lta.cereda.mwirth2ape.ape.*;
import br.usp.poli.lta.cereda.mwirth2ape.ape.conversion.Sketch;
import br.usp.poli.lta.cereda.mwirth2ape.ape.conversion.State;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.LabelElement;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.Production;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import br.usp.poli.lta.cereda.mwirth2ape.mwirth.Generator;
import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.List;

public class SPAExecute {
    private static final Logger logger = LoggerFactory.
            getLogger(SPAExecute.class);
    // APE Generator
    protected SimpleLexer lexer;
    //private NLPLexer nlpLexer;
    protected Generator lmwg;
    protected Stack<String> transducerStack;
    protected List<Sketch> transitions;
    protected HashSet<Transition> spaTransitions;
    protected int stateCounter;
    protected HashMap<String, HashMap<Integer, LinkedList<LabelElement>>> mapMachineStates;
    protected LinkedList<String> outputList;
    protected HashSet<String> dictionaryTerm;

    public SPAExecute() {

    }

    public SPAExecute (SimpleLexer simpleLexer, Generator lmwg, HashSet<String> dictionaryTerm) {
        this.lexer = simpleLexer;
        this.lmwg = lmwg;
        this.transducerStack = new Stack<>();
        this.transitions = lmwg.getTransitions();
        this.mapMachineStates = lmwg.getMapMachineStatesLabels();
        this.spaTransitions = new HashSet<>();
        this.stateCounter = 0;
        this.dictionaryTerm = dictionaryTerm;
        this.outputList = new LinkedList<>();
    }

    /*
    public SPAExecute (NLPLexer nlpLexer, Generator lmwg, HashSet<String> dictionaryTerm) {
        this.nlpLexer = nlpLexer;
        this.lmwg = lmwg;
        this.transducerStack = new Stack<>();
        this.transitions = lmwg.getTransitions();
        this.mapMachineStates = lmwg.getMapMachineStatesLabels();
        this.spaTransitions = new HashSet<>();
        this.stateCounter = 0;
        this.dictionaryTerm = dictionaryTerm;
        this.outputList = new LinkedList<>();
    }
    */

    public void parseInput() throws Exception {
        // Build SPA
        logger.debug("Started building SPA parser.");

        StructuredPushdownAutomaton spa;
        //Thread spa = new Thread(new StructuredPushdownAutomatonNLP());


        spa = new StructuredPushdownAutomaton2();


        spa.setSubmachine(this.lmwg.getMain());  // set main machine

        SPAGetStruct spaStruct = new SPAGetStruct(this.transitions);
        Map<String, List<Sketch>> machineSketchesMap = spaStruct.getMachinesFromTransitions(); // map list of Sketch (transitions) to a machine
        Map<String, Integer> machineMaxStateIdMap = spaStruct.getMaxStateIdFromMachineMap(machineSketchesMap);

        // Acao semantica associado a transicao com terminal
        Action semanticActionTermTransition = new Action("semanticActionTermTransition") {
            @Override
            public void execute(Token token) {
            //    if ((nlpLexer != null) && (token.getType().equals("term"))) {
            //        outputList.addLast("\"" + token.getValue() + "\"");
            //        logger.debug("Ação semântica: Terminal consumido: POS tag {}, value \"{}\".",
            //                token.getNlpToken().getNlpWords().get(0).getPosTag(), token.getValue());
            //    }
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
             //   if ((nlpLexer != null)) {
                    logger.debug("Ação semântica: Transiçao com chamada de submáquina.");
             //   }
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
            //    if ((nlpLexer != null)) {
                    logger.debug("Ação semântica: Transição em vazio.");
            //    }
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        // Acao semantica associado a transicao para gerar saída e manipular pilha de acordo com rotulos
        ActionLabels semanticActionLabels = new ActionLabels ("semanticActionLabels") {
            @Override
            public void execute(LinkedList<LabelElement> labels, Stack<String> transducerStack) {
                long threadId = Thread.currentThread().getId();
                //transducerStack = nlpTransducerStackList.getTransducerStackList(threadId);

                logger.debug("ThreadID {}: Ação semântica: Labels", String.valueOf(threadId));
                for (LabelElement singleLabelElement : labels) {
                    if (singleLabelElement != null) { // verifica se não retornou elemento de rotulo nulo
                        String labelSymbol = singleLabelElement.getValue();
                        Production labelProduction = singleLabelElement.getProduction();
                        if (labelProduction == null) {
                            if (labelSymbol.equals("ε")) {
                                //nlpOutputList.insertOutputResult(threadId, "()");
                                outputList.addLast("()");
                            } else if (dictionaryTerm.contains(String.valueOf(labelSymbol))) {
                                //nlpOutputList.insertOutputResult(threadId, "(" + labelSymbol + ")");
                                outputList.addLast("(" + labelSymbol + ")");
                            } else if (labelSymbol.equals("[")) {
                                //nlpOutputList.insertOutputResult(threadId, "[(");
                                outputList.addLast("[(");
                                transducerStack.push("]");
                            } else if (labelSymbol.equals("]")) {
                                StringBuilder sb = new StringBuilder();
                                while (!transducerStack.top().equals("]")) {
                                    sb.append(transducerStack.pop());
                                }
                                transducerStack.pop();
                                sb.reverse();
                                sb.append("]");
                                //nlpOutputList.insertOutputResult(threadId, sb.toString());
                                outputList.addLast(sb.toString());
                            }
                        } else {
                            if (labelProduction.getRecursion().equals("right")) {
                                //nlpOutputList.insertOutputResult(threadId, labelProduction.getIdentifier() + ")");
                                outputList.addLast(labelProduction.getIdentifier() + ")");
                            } else if (labelProduction.getRecursion().equals("left")) {
                                String stackElement = ")" + labelProduction.getIdentifier();
                                transducerStack.push(stackElement);
                                //nlpOutputList.insertOutputResult(threadId, "(");
                                outputList.addLast("(");
                            } else {
                                StringBuilder sb = new StringBuilder();
                                while (!transducerStack.top().equals("]")) {
                                    sb.append(transducerStack.pop());
                                }
                                sb.reverse();
                                sb.append(labelProduction.getIdentifier()).append(")");
                                //nlpOutputList.insertOutputResult(threadId, sb.toString());
                                outputList.addLast(sb.toString());
                            }
                        }
                    }
                }
            }
        };


        // Acao semantica associado ao estado (maquina de Moore) para gerar saída e manipular pilha de acordo com rotulos
        ActionState semanticActionState = new ActionState("semanticActionState") {
            @Override
            public void execute(LinkedList<LabelElement> labels, Stack<String> transducerStack) {
                logger.debug("Ação semântica: Labels");
                for (LabelElement singleLabelElement : labels) {
                    if (singleLabelElement != null) { // verifica se não retornou elemento de rotulo nulo
                        String labelSymbol = singleLabelElement.getValue();
                        Production labelProduction = singleLabelElement.getProduction();
                        if (labelProduction == null) {
                            if (labelSymbol.equals("ε")) {
                                outputList.addLast("()");
                            } else if (dictionaryTerm.contains(String.valueOf(labelSymbol))) {
                                outputList.addLast("(" + labelSymbol + ")");
                            } else if (labelSymbol.equals("[")) {
                                outputList.addLast("[(");
                                transducerStack.push("]");
                            } else if (labelSymbol.equals("]")) {
                                StringBuilder sb = new StringBuilder();
                                while (!transducerStack.top().equals("]")) {
                                    sb.append(transducerStack.pop());
                                }
                                transducerStack.pop();
                                sb.reverse();
                                sb.append("]");
                                outputList.addLast(sb.toString());
                            }
                        }
                        else {
                            if (labelProduction.getRecursion().equals("right")) {
                                outputList.addLast(labelProduction.getIdentifier() + ")");
                            }
                            else if (labelProduction.getRecursion().equals("left")) {
                                String stackElement = ")" + labelProduction.getIdentifier();
                                transducerStack.push(stackElement);
                                outputList.addLast("(");
                            }
                            else {
                                StringBuilder sb = new StringBuilder();
                                while (!transducerStack.top().equals("]")) {
                                    sb.append(transducerStack.pop());
                                }
                                sb.reverse();
                                sb.append(labelProduction.getIdentifier()).append(")");
                                outputList.addLast(sb.toString());
                            }
                        }
                    }
                }
            }
        };



        buildSPA(spa, machineSketchesMap, stateCounter, machineMaxStateIdMap,
                semanticActionState,
                semanticActionNtermTransition, semanticActionTermTransition, semanticActionEmptyTransition,
                semanticActionLabels);
        // Construcao do automato
        /*
        for (String machine : machineSketchesMap.keySet()) {
            List<Sketch> tempSketches = machineSketchesMap.get(machine); // Get transitions for a machine
            // set submachine name with start, end
            spa.addSubmachine(machine, stateCounter,  getSet(stateCounter + 1));
            // get states
            for (Sketch tempSketch: tempSketches) {
                Transition newTransition;

                Integer tempSource = tempSketch.getSource() + stateCounter;
                Integer tempTarget = tempSketch.getTarget() + stateCounter;

                addSPAState(tempSource, machine, spa, this.mapMachineStates.get(machine).get(tempSketch.getSource()), semanticActionState);
                addSPAState(tempTarget, machine, spa, this.mapMachineStates.get(machine).get(tempSketch.getTarget()), semanticActionState);

                //State tempState = new State (tempSketch.getSource(), machine, this.mapMachineStates.get(machine).get(tempSketch.getSource()));

                if (tempSketch.getToken().getType().equals("nterm")) {
                    newTransition = new Transition(
                            tempSource, tempSketch.getToken().getValue(), tempTarget
                    );
                    newTransition.setSubmachineToken(tempSketch.getToken());
                    newTransition.addPostAction(semanticActionNtermTransition);
                }
                else if (tempSketch.getToken().getType().equals("ε")) {
                    Token tempToken = null;
                    newTransition = new Transition(
                            tempSource, tempToken, tempTarget
                    );
                    newTransition.setSubmachineToken(tempSketch.getToken());
                    newTransition.addPostAction(semanticActionEmptyTransition);
                }
                else {
                    newTransition = new Transition(
                            tempSource, tempSketch.getToken(), tempTarget
                    );
                    newTransition.addPostAction(semanticActionTermTransition);
                }
                spa.addTransition(newTransition);
            }
            stateCounter = stateCounter + machineMaxStateIdMap.get(machine) + 1;
        }
        logger.debug("Finished building SPA parser.");
        */
        // end Build SPA

        // Executar automato
        logger.debug("Started parsing.");

        spa.setup();
        //if (lexer != null) {
            boolean result = spa.parse(lexer);
        //} else {
        //    boolean result = spa.parse(nlpLexer);
        //}
        logger.debug("Finished parsing.");

        // Configurar saida (arquivo)
        logger.debug("Cadeia de saída: ");
        logger.debug(this.outputList.toString());
    }



    protected void buildSPA (StructuredPushdownAutomaton spa, Map<String, List<Sketch>> machineSketchesMap,
                             Integer stateCounter, Map<String, Integer> machineMaxStateIdMap, ActionState semanticActionState,
                             Action semanticActionNtermTransition, Action semanticActionTermTransition,
                             Action semanticActionEmptyTransition, ActionLabels semanticActionLabels)
    {

        // Construcao do automato
        for (String machine : machineSketchesMap.keySet()) {
            List<Sketch> tempSketches = machineSketchesMap.get(machine); // Get transitions for a machine
            // set submachine name with start, end
            spa.addSubmachine(machine, stateCounter,  getSet(stateCounter + 1));
            // get states
            for (Sketch tempSketch: tempSketches) {
                Transition newTransition;

                Integer tempSource = tempSketch.getSource() + stateCounter;
                Integer tempTarget = tempSketch.getTarget() + stateCounter;

                addSPAState(tempSource, machine, spa, this.mapMachineStates.get(machine).get(tempSketch.getSource()),
                        semanticActionState);
                addSPAState(tempTarget, machine, spa, this.mapMachineStates.get(machine).get(tempSketch.getTarget()),
                        semanticActionState);

                //State tempState = new State (tempSketch.getSource(), machine, this.mapMachineStates.get(machine).get(tempSketch.getSource()));

                if (tempSketch.getToken().getType().equals("nterm")) {
                    newTransition = new Transition(
                            tempSource, tempSketch.getToken().getValue(), tempTarget
                    );
                    newTransition.setSubmachineToken(tempSketch.getToken());
                    newTransition.addPostAction(semanticActionNtermTransition);
                }
                else if (tempSketch.getToken().getType().equals("ε")) {
                    Token tempToken = null;
                    newTransition = new Transition(
                            tempSource, tempToken, tempTarget
                    );
                    newTransition.setSubmachineToken(tempSketch.getToken());
                    newTransition.addPostAction(semanticActionEmptyTransition);
                }
                else {
                    newTransition = new Transition(
                            tempSource, tempSketch.getToken(), tempTarget
                    );
                    newTransition.addPostAction(semanticActionTermTransition);
                }
                newTransition.addLabelAction(semanticActionLabels);
                spa.addTransition(newTransition);
            }
            stateCounter = stateCounter + machineMaxStateIdMap.get(machine) + 1;
        }
        logger.debug("Finished building SPA parser.");

    }


    protected void addSPAState(Integer id, String submachine, Object spa, LinkedList<LabelElement> labels,
                               ActionState actionState) {
        if (spa instanceof StructuredPushdownAutomaton2) {
            if (labels != null) {
                if (((StructuredPushdownAutomaton2)spa).getState(id) == null) {
                    State newState = new State(id, submachine, labels);
                    newState.addActionState(actionState);
                    ((StructuredPushdownAutomaton2)spa).addState(id, newState);
                }
            } else {
                State newState = new State(id, submachine, null);
                ((StructuredPushdownAutomaton2)spa).addState(id, newState);
            }
        }
    }


    protected <T> Set<T> getSet(T... elements) {
        Set<T> result = new HashSet<>();
        result.addAll(Arrays.asList(elements));
        return result;
    }

}
