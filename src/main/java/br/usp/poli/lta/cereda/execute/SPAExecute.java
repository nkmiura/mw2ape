package br.usp.poli.lta.cereda.execute;

import br.usp.poli.lta.cereda.mwirth2ape.ape.StructuredPushdownAutomaton;
import br.usp.poli.lta.cereda.mwirth2ape.ape.Transition;
import br.usp.poli.lta.cereda.mwirth2ape.ape.conversion.Sketch;
import br.usp.poli.lta.cereda.mwirth2ape.mwirth.Generator;
import br.usp.poli.lta.cereda.mwirth2ape.mwirth.MWirthLexer;
import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
import br.usp.poli.lta.cereda.mwirth2ape.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SPAExecute {
    private static final Logger logger = LoggerFactory.
            getLogger(SPAExecute.class);
    // APE Generator
    private MWirthLexer lexer;
    private Generator lmwg;
    private Stack<Pair<Integer, Integer>> helper;
    private List<Sketch> transitions;
    private HashSet<Transition> spaTransitions;
    private String main;
    private String machine;

    public SPAExecute (MWirthLexer mWirthLexer, Generator lmwg) {
        this.lexer = mWirthLexer;
        this.lmwg = lmwg;
        this.helper = new Stack<>();
        this.transitions = lmwg.getTransitions();
        this.spaTransitions = new HashSet<>();
    }

    public void parseInput() throws Exception {
        // Construir automato
        logger.debug("Started building SPA parser.");
        StructuredPushdownAutomaton spa = new StructuredPushdownAutomaton();
        spa.setSubmachine(this.lmwg.getMain());  // set main machine

        // get machines and states
        Set<String> machines = new HashSet<>();
        Set<String> states;

        for (Sketch transition : transitions) {
            if (!machines.contains(transition.getName())) {
                machines.add(transition.getName());
            }
        }

        for (String machine : machines) {
//            states = new HashSet<>();
            for (Sketch transition : transitions) {
                if (transition.getName().equals(machine)) {
                    Transition newTransition;
                    newTransition = new Transition(
                        transition.getSource(), "" ,transition.getTarget());
                        spa.addTransition(newTransition);
                }
            }
        }

        logger.debug("Finished building SPA parser.");

        logger.debug("Started parsing.");

        logger.debug("Finished parsing.");
    }


    // Configurar saida (arquivo)

    // Executar automato
/*
    public Sketch generateSPAMap( )
            throws Exception {
        Map<String, List<Sketch>> map = new HashMap<>();
        this.transitions.stream().map((sketch) -> {
            if (!map.containsKey(sketch.getName())) {
                map.put(sketch.getName(), new ArrayList<>());
            }
            return sketch;
        }).forEach((sketch) -> {
            map.get(sketch.getName()).add(sketch);
        });
        Sketch sketch = new Sketch();   // fake
        return sketch;  // fake

    }
*/

}
