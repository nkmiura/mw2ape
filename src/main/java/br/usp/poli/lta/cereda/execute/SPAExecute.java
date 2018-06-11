package br.usp.poli.lta.cereda.execute;

import br.usp.poli.lta.cereda.mwirth2ape.ape.Action;
import br.usp.poli.lta.cereda.mwirth2ape.ape.ActionState;
import br.usp.poli.lta.cereda.mwirth2ape.ape.StructuredPushdownAutomaton2;
import br.usp.poli.lta.cereda.mwirth2ape.ape.Transition;
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
    private SimpleLexer lexer;
    private Generator lmwg;
    private Stack<String> transducerStack;
    private List<Sketch> transitions;
    private HashSet<Transition> spaTransitions;
    private int stateCounter;
    private HashMap<String, HashMap<Integer, LinkedList<LabelElement>>> mapMachineStates;
    private LinkedList<String> outputList;
    private HashSet<String> dictionaryTerm;

    public SPAExecute (SimpleLexer simpleLexer, Generator lmwg, HashSet<String> dictionaryTerm) {
        this.lexer = simpleLexer;
        this.lmwg = lmwg;
        this.transducerStack = new Stack<>();
        this.transitions = lmwg.getTransitions();
        this.mapMachineStates = lmwg.getMapMachineStates();
        this.spaTransitions = new HashSet<>();
        this.stateCounter = 0;
        this.dictionaryTerm = dictionaryTerm;
        this.outputList = new LinkedList<>();
    }


    public void parseInput() throws Exception {
        // Build SPA
        logger.debug("Started building SPA parser.");
        StructuredPushdownAutomaton2 spa = new StructuredPushdownAutomaton2();
        spa.setSubmachine(this.lmwg.getMain());  // set main machine

        SPAGetStruct spaStruct = new SPAGetStruct(this.transitions);
        Map<String, List<Sketch>> machineSketchesMap = spaStruct.getMachinesFromTransitions(); // map list of Sketch (transitions) to a machine
        Map<String, Integer> machineStateQtyMap = spaStruct.getStateQtyFromMachineMap(machineSketchesMap);


        Action semanticActionTransition = new Action("semanticActionTransition") {
            @Override
            public void execute(Token token) {
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        ActionState semanticActionState = new ActionState("semanticActionState") {
            @Override
            public void execute(LinkedList<LabelElement> labels) {
                for (LabelElement singleLabelElement : labels) {
                    if (singleLabelElement != null) {
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

                //@Override
                //public List execute(int state, List tree){ return null; }
            }
        };


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
                }
                else if (tempSketch.getToken().getType().equals("ε")) {
                    Token tempToken = null;
                    newTransition = new Transition(
                            tempSource, tempToken, tempTarget
                    );
                    newTransition.setSubmachineToken(tempSketch.getToken());
                }
                else {
                    newTransition = new Transition(
                            tempSource, tempSketch.getToken(), tempTarget
                    );
                }
                newTransition.addPreAction(semanticActionTransition);

                spa.addTransition(newTransition);
            }

            stateCounter = stateCounter + machineStateQtyMap.get(machine);

        }
        logger.debug("Finished building SPA parser.");
        // end Build SPA

        // Executar automato
        logger.debug("Started parsing.");
        spa.setup();
        boolean result = spa.parse(lexer);
        logger.debug("Finished parsing.");

        // Configurar saida (arquivo)
        logger.debug("Cadeia de saída: ");
        logger.debug(this.outputList.toString());

    }


    private void addSPAState(Integer id, String submachine, StructuredPushdownAutomaton2 spa, LinkedList<LabelElement> labels, ActionState actionState) {

        if (labels != null) {
            if (spa.getState(id) == null) {
                State newState = new State(id, submachine, labels);
                newState.addActionState(actionState);
                spa.addState(id, newState);
            }
        }
        else {
            State newState = new State (id, submachine, null);
            spa.addState(id, newState);
        }
    }



    private <T> Set<T> getSet(T... elements) {
        Set<T> result = new HashSet<>();
        result.addAll(Arrays.asList(elements));
        return result;
    }

}
