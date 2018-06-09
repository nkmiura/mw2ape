package br.usp.poli.lta.cereda.execute;

import br.usp.poli.lta.cereda.mwirth2ape.ape.Action;
import br.usp.poli.lta.cereda.mwirth2ape.ape.StructuredPushdownAutomaton2;
import br.usp.poli.lta.cereda.mwirth2ape.ape.Transition;
import br.usp.poli.lta.cereda.mwirth2ape.ape.conversion.Sketch;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.LabelElement;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import br.usp.poli.lta.cereda.mwirth2ape.mwirth.Generator;
import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
import br.usp.poli.lta.cereda.mwirth2ape.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SPAExecute {
    private static final Logger logger = LoggerFactory.
            getLogger(SPAExecute.class);
    // APE Generator
    private SimpleLexer lexer;
    private Generator lmwg;
    private Stack<Pair<Integer, Integer>> helper;
    private List<Sketch> transitions;
    private HashSet<Transition> spaTransitions;
    private int stateCounter;
    private HashMap<String, HashMap<Integer, LinkedList<LabelElement>>> mapMachineStates;

    public SPAExecute (SimpleLexer simpleLexer, Generator lmwg) {
        this.lexer = simpleLexer;
        this.lmwg = lmwg;
        this.helper = new Stack<>();
        this.transitions = lmwg.getTransitions();
        this.mapMachineStates = lmwg.getMapMachineStates();
        this.spaTransitions = new HashSet<>();
        this.stateCounter = 0;
    }



    public void parseInput() throws Exception {
        // Build SPA
        logger.debug("Started building SPA parser.");
        StructuredPushdownAutomaton2 spa = new StructuredPushdownAutomaton2();
        spa.setSubmachine(this.lmwg.getMain());  // set main machine

        SPAGetStruct spaStruct = new SPAGetStruct(this.transitions);
        Map<String, List<Sketch>> machineSketchesMap = spaStruct.getMachinesFromTransitions(); // map list of Sketch (transitions) to a machine
        Map<String, Integer> machineStateQtyMap = spaStruct.getStateQtyFromMachineMap(machineSketchesMap);

        for (String machine : machineSketchesMap.keySet()) {
            List<Sketch> tempSketches = machineSketchesMap.get(machine); // Get transitions for a machine
            // set submachine name with start, end
            spa.addSubmachine(machine, stateCounter,  getSet(stateCounter + 1));
            // get states
            for (Sketch tempSketch: tempSketches) {
                Transition newTransition;
                if (tempSketch.getToken().getType().equals("nterm")) {
                    newTransition = new Transition(
                            tempSketch.getSource() + stateCounter,
                            tempSketch.getToken().getValue(),
                            tempSketch.getTarget() + stateCounter
                    );
                    newTransition.setSubmachineToken(tempSketch.getToken());
                }
                else if (tempSketch.getToken().getType().equals("ε")) {
                    Token tempToken = null;
                    newTransition = new Transition(
                            tempSketch.getSource() + stateCounter,
                            tempToken,
                            tempSketch.getTarget() + stateCounter
                    );
                    newTransition.setSubmachineToken(tempSketch.getToken());
                }
                else {
                    newTransition = new Transition(
                            tempSketch.getSource() + stateCounter,
                            tempSketch.getToken(),
                            tempSketch.getTarget() + stateCounter
                    );
                }
                newTransition.addPreAction(semanticAction);

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

    }


    Action semanticAction = new Action("semanticAction") {
        @Override
        public void execute(Token token) {



        }

        @Override
        public List execute(int state, List tree) {
            return null;
        }
    };


    private <T> Set<T> getSet(T... elements) {
        Set<T> result = new HashSet<>();
        result.addAll(Arrays.asList(elements));
        return result;
    }

}
