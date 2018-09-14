package br.usp.poli.lta.cereda.execute;

import br.usp.poli.lta.cereda.execute.NLP.*;
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
    private NLPTransducerStackList nlpTransducerStackList;
    private NLPAction nlpAction;


    public SPAExecuteNLP(NLPLexer nlpLexer, Generator lmwg, HashSet<String> dictionaryTerm) {
        this.nlpLexer = nlpLexer;
        this.lmwg = lmwg;
        this.transducerStack = new Stack<>();
        this.transitions = lmwg.getTransitions();
        this.mapMachineStates = lmwg.getMapMachineStatesLabels();
        this.spaTransitions = new HashSet<>();
        this.stateCounter = 0;
        this.dictionaryTerm = dictionaryTerm;
        this.outputList = new LinkedList<>();
        this.nlpOutputList = new NLPOutputList();
        this.nlpTransducerStackList = new NLPTransducerStackList();
        this.nlpAction = new NLPAction(this.nlpOutputList, dictionaryTerm, this.nlpTransducerStackList);
    }

    public void parseInput() throws Exception {
        // Build SPA
        logger.debug("Started building SPA parser.");

        //this.nlpOutputList.incrementOutputList(Thread.currentThread().getId());
        //this.nlpOutputList.setParseResult(Thread.currentThread().getId(),true);
        StructuredPushdownAutomatonNLP spa = new StructuredPushdownAutomatonNLP(this.nlpLexer,
                this.nlpOutputList, this.nlpTransducerStackList);
        //Runnable spa = new StructuredPushdownAutomatonNLP(threadIdCounter, this.nlpLexer, this.outputResults);

        spa.setSubmachine(this.lmwg.getMain());  // set main machine

        SPAGetStruct spaStruct = new SPAGetStruct(this.transitions);
        Map<String, List<Sketch>> machineSketchesMap = spaStruct.getMachinesFromTransitions(); // map list of Sketch (transitions) to a machine
        Map<String, Integer> machineMaxStateIdMap = spaStruct.getMaxStateIdFromMachineMap(machineSketchesMap);


        // Construcao do automato
        buildSPA(spa, machineSketchesMap, stateCounter, machineMaxStateIdMap, nlpAction);

        // end Build SPA
        spa.setup();

        // Executar automato
        NLPSpaThread NLPSpaThread = new NLPSpaThread(spa, this.nlpOutputList,
                this.nlpTransducerStackList, -1);
        Thread thread = new Thread(NLPSpaThread);

        logger.debug("Started parsing.");

        thread.start();

        while (this.nlpOutputList.isAnyThreadAlive()) {
            Thread.sleep(10000);
        }

        logger.debug("Finished parsing.");

        // Configurar saida (arquivo)
        //logger.debug("Cadeia de saída (outputlist): ");
        //logger.debug(this.outputList.toString());

        logger.debug("Cadeia de saída (outputResult): ");
        logger.debug(this.nlpOutputList.toString());

    }

    //@Override
    protected void buildSPA (StructuredPushdownAutomaton spa, Map<String, List<Sketch>> machineSketchesMap,
                             Integer stateCounter, Map<String, Integer> machineMaxStateIdMap, NLPAction nlpAction)
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
                        nlpAction.semanticActionState);
                addSPAState(tempTarget, machine, spa, this.mapMachineStates.get(machine).get(tempSketch.getTarget()),
                        nlpAction.semanticActionState);

                //State tempState = new State (tempSketch.getSource(), machine, this.mapMachineStates.get(machine).get(tempSketch.getSource()));

                if (tempSketch.getToken().getType().equals("nterm")) {
                    newTransition = new Transition(
                            tempSource, tempSketch.getToken().getValue(), tempTarget
                    );
                    newTransition.setSubmachineToken(tempSketch.getToken());
                    newTransition.addPostAction(nlpAction.semanticActionNtermTransition);
                }
                else if (tempSketch.getToken().getType().equals("ε")) {
                    Token tempToken = null;
                    newTransition = new Transition(
                            tempSource, tempToken, tempTarget
                    );
                    newTransition.setSubmachineToken(tempSketch.getToken());
                    newTransition.addPostAction(nlpAction.semanticActionEmptyTransition);
                }
                else {
                    newTransition = new Transition(
                            tempSource, tempSketch.getToken(), tempTarget
                    );
                    newTransition.addPostAction(nlpAction.semanticActionTermTransition);
                }
                newTransition.addLabelAction(nlpAction.semanticActionLabels); // 2018.09.14
                spa.addTransition(newTransition);
            }
            stateCounter = stateCounter + machineMaxStateIdMap.get(machine) + 1;
        }
        logger.debug("Finished building SPA parser.");

    }

}
