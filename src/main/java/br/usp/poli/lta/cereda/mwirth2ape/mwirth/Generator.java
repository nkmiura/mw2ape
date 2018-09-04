/**
* ------------------------------------------------------
*    Laboratório de Linguagens e Técnicas Adaptativas
*       Escola Politécnica, Universidade São Paulo
* ------------------------------------------------------
* 
* This program is free software: you can redistribute it
* and/or modify  it under the  terms of the  GNU General
* Public  License  as  published by  the  Free  Software
* Foundation, either  version 3  of the License,  or (at
* your option) any later version.
* 
* This program is  distributed in the hope  that it will
* be useful, but WITHOUT  ANY WARRANTY; without even the
* implied warranty  of MERCHANTABILITY or FITNESS  FOR A
* PARTICULAR PURPOSE. See the GNU General Public License
* for more details.
* 
**/
package br.usp.poli.lta.cereda.mwirth2ape.mwirth;

import br.usp.poli.lta.cereda.mwirth2ape.ape.Action;
import br.usp.poli.lta.cereda.mwirth2ape.ape.StructuredPushdownAutomaton;
import br.usp.poli.lta.cereda.mwirth2ape.ape.Transition;
import br.usp.poli.lta.cereda.mwirth2ape.ape.conversion.Sketch;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.*;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
import br.usp.poli.lta.cereda.mwirth2ape.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.2
 * @since 1.0
 */
public class Generator {

    private static final Logger logger = LoggerFactory.
            getLogger(Generator.class);

    int type;

    // APE Generator
    private final MWirthLexer lexer;
    private final Stack<Pair<Integer, Integer>> helperPairStack;
    private final List<Sketch> transitions;
    private int current;
    private int counter;
    private String main;
    private String machine;

    // Production analysis for label insertion
    private LabelGrammar labelGrammar;
    private NTerm currentNterm;
    private Production currentProduction;
    // Generate APE with labels
    private HashMap<String, HashMap<Integer, LinkedList<LabelElement>>> mapMachineStatesLabels;
    private HashMap<Integer, LinkedList<LabelElement>> currentStatesLabels;

    public Generator(MWirthLexer mWirthLexer, int type) {

        this.type = type;
        this.lexer = mWirthLexer;
        this.helperPairStack = new Stack<>();
        this.transitions = new ArrayList<>();
        this.current = 0;
        this.counter = 1;

        // Newton
        this.labelGrammar = new LabelGrammar();
    }

    public Generator(LMWirthLexer lmWirthLexer, int type) {

        this.type = type;
        this.lexer = lmWirthLexer;
        this.helperPairStack = new Stack<>();
        this.transitions = new ArrayList<>();
        this.current = 0;
        this.counter = 0;
        this.mapMachineStatesLabels = new HashMap<>();
    }

    // Newton
    public void registerExpressionToken(Token token) {
        ProductionToken newProductionToken = new ProductionToken(token);
        currentProduction.addExpressionToken(newProductionToken);
        currentProduction.addAllToken(newProductionToken);
    }
    public void registerLabelToken(String value) {
        ProductionToken newProductionToken = new ProductionToken("label","label");
        if (value != null) {
            newProductionToken.pushLabel(value);
        }
        currentProduction.addLabelsToken(newProductionToken);
        currentProduction.addAllToken(newProductionToken);
    }
    
    public void generateAutomaton() throws Exception {
        StructuredPushdownAutomaton spa = new StructuredPushdownAutomaton();
        spa.setSubmachine("GRAM");
        spa.addSubmachine("GRAM", 1, getSet(5));
        spa.addSubmachine("EXPR", 6, getSet(7));

        Action criarNovaSubmaquina = new Action("criarNovaSubmaquina") {
            @Override
            public void execute(Token token) {
                switch(type) {
                    case 0:
                        helperPairStack.clear();
                        current = 0;
                        counter = 1;
                        machine = token.getValue();
                        if (main == null) {
                            main = machine;
                        }
                        break;
                    case 1:
                        currentNterm = labelGrammar.newNterm(token.getValue());
                        currentProduction = currentNterm.addProduction(token.getValue());
                        break;
                    case 2:
                        helperPairStack.clear();
                        current = 0;
                        counter = 1;
                        machine = token.getValue();

                        currentStatesLabels = new HashMap<>();
                        mapMachineStatesLabels.put(machine, currentStatesLabels);
                        if (main == null) {
                            main = machine;
                        }
                        break;
                }
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        //novaTransicaoNterm
        Action novaTransicaoNterm = new Action("novaTransicaoNterm") {
            @Override
            public void execute(Token token) {
                Sketch transition;
                switch(type) {
                    case 0:
                        transition = new Sketch(machine, current, token, counter);
                        transitions.add(transition);
                        current = counter;
                        counter++;
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
                        break;
                    case 2:
                        transition = new Sketch(machine, current, token, counter);
                        transitions.add(transition);
                        putStateLabels(counter,token.getProductionToken().getNextLabels());
                        //currentStatesLabels.put(counter,token.getProductionToken().getNextLabels());
                        current = counter;
                        counter++;
                        break;
                    default:
                        break;
                }
            }
            
            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        //novaTransicaoTerm
        Action novaTransicaoTerm = new Action("novaTransicaoTerm") {
            @Override
            public void execute(Token token) {
                Sketch transition;
                switch(type) {
                    case 0:
                        transition = new Sketch(machine, current, token, counter);
                        transitions.add(transition);
                        current = counter;
                        counter++;
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(token.getValue());
                        break;
                    case 2:
                        transition = new Sketch(machine, current, token, counter);
                        transitions.add(transition);
                        putStateLabels(counter,token.getProductionToken().getNextLabels());
                        //currentStatesLabels.put(counter,token.getProductionToken().getNextLabels());
                        current = counter;
                        counter++;
                        break;
                    default:
                        break;
                }
           }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        //novaTransicaoEpsilon
        Action novaTransicaoEpsilon = new Action("novaTransicaoEpsilon") {
            @Override
            public void execute(Token token) {
                Sketch transition;
                switch(type) {
                    case 0:
                        transition = new Sketch(machine, current, token, counter);
                        transitions.add(transition);
                        current = counter;
                        counter++;
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken("ε");
                        break;
                    case 2:
                        transition = new Sketch(machine, current, token, counter);
                        transitions.add(transition);
                        putStateLabels(counter,token.getProductionToken().getNextLabels());
                        //currentStatesLabels.put(counter,token.getProductionToken().getNextLabels());
                        current = counter;
                        counter++;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };


        Action novaProducao = new Action("novaProducao") {
            @Override
            public void execute(Token token) {
                switch(type) {
                    case 0:
                        helperPairStack.push(new Pair<>(current, counter));
                        counter++;
                        break;
                    case 1:
                        registerLabelToken("[");
                        break;
                    case 2:
                        helperPairStack.push(new Pair<>(current, counter));
                        putStateLabels(current,token.getProductionToken().getNextLabels());
                        //currentStatesLabels.put(current,token.getProductionToken().getNextLabels());
                        counter++;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        Action novoEscopo = new Action("novoEscopo") {
            @Override
            public void execute(Token token) {
                switch(type) {
                    case 0:
                        helperPairStack.push(new Pair<>(current, counter));
                        counter++;
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
                        break;
                    case 2:
                        helperPairStack.push(new Pair<>(current, counter));
                        putStateLabels(current,token.getProductionToken().getNextLabels());
                        //currentStatesLabels.put(current,token.getProductionToken().getNextLabels());
                        counter++;
                        break;
                    default:
                        break;
                }
            }
            
            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        Action fechaEscopo = new Action("fechaEscopo") {
            @Override
            public void execute(Token token) {
                switch(type) {
                    case 0:
                        Sketch transition = new Sketch(machine, current,
                                helperPairStack.top().getSecond());
                        transitions.add(transition);
                        current = helperPairStack.top().getSecond();
                        helperPairStack.pop();
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
                        break;
                    case 2:
                        token.setType("ε");
                        token.setValue("ε");
                        Sketch transition2 = new Sketch(machine, current, token,
                                helperPairStack.top().getSecond());
                        transitions.add(transition2);
                        putStateLabels(helperPairStack.top().getSecond(),token.getProductionToken().getNextLabels());
                        //currentStatesLabels.put(helperPairStack.top().getSecond(),token.getProductionToken().getNextLabels());
                        current = helperPairStack.top().getSecond();
                        helperPairStack.pop();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        Action fechaProducao = new Action("fechaProducao") {
            @Override
            public void execute(Token token) {

                switch(type) {
                    case 0:
                        Sketch transition = new Sketch(machine, current,
                                helperPairStack.top().getSecond());
                        transitions.add(transition);
                        current = helperPairStack.top().getSecond();
                        helperPairStack.pop();
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken("]");
                        currentProduction.labels.getLast().pushLabel(currentProduction.getIdentifier(),currentProduction);
                        break;
                    case 2:
                        token.setType("ε");
                        token.setValue("ε");
                        if (token.getProductionToken() != null) {
                            token.getProductionToken().setType("ε");
                            token.getProductionToken().setValue("ε");
                        }
                        Sketch transition2 = new Sketch(machine, current,
                                token, helperPairStack.top().getSecond());
                        transitions.add(transition2);
                        putStateLabels(helperPairStack.top().getSecond(),token.getProductionToken().getNextLabels());
                        //currentStatesLabels.put(helperPairStack.top().getSecond(),token.getProductionToken().getNextLabels());
                        current = helperPairStack.top().getSecond();
                        helperPairStack.pop();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };


        Action adicionaOpcao = new Action("adicionaOpcao") {
            @Override
            public void execute(Token token) {
                switch(type) {
                    case 0:
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
                        break;
                    case 2:
                        token.setType("ε");
                        token.setValue("ε");
                        Sketch transition = new Sketch(machine, current, token,
                                helperPairStack.top().getSecond());
                        transitions.add(transition);
                        current = helperPairStack.top().getFirst();
                        break;
                    default:
                        break;
                }
            }
            
            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        Action adicionaReverso = new Action("adicionaReverso") {
            @Override
            public void execute(Token token) {
                // Newton
                switch(type) {
                    case 0:
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
                        break;
                    case 2:
                        token.setType("ε");
                        token.setValue("ε");
                        Sketch transition = new Sketch(machine, current, token,
                                helperPairStack.top().getSecond());
                        transitions.add(transition);
                        current = helperPairStack.top().getSecond();

                        helperPairStack.push(new Pair<>(helperPairStack.top().getSecond(), helperPairStack.top().getFirst()));
                        break;
                }
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };

        Action fechaEscopoReverso = new Action("fechaEscopoReverso") {
            @Override
            public void execute(Token token) {
                switch(type) {
                    case 0:
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
                        break;
                    case 2:
                        token.setType("ε");
                        token.setValue("ε");
                        Sketch transition1 = new Sketch(machine, current, token,
                                helperPairStack.top().getSecond());
                        transitions.add(transition1);
                        current = helperPairStack.top().getSecond();
                        helperPairStack.pop();

                        current = helperPairStack.top().getSecond();
                        helperPairStack.pop();
                        break;
                }
            }

            @Override
            public List execute(int state, List tree) {
                return null;
            }
        };


        Transition t1 = new Transition(1, new Token("nterm", "nterm"), 2);
        t1.addPreAction(criarNovaSubmaquina);
        spa.addTransition(t1);
        
        Transition t2 = new Transition(2, new Token("=", "="), 3);
        t2.addPreAction(novaProducao);
        spa.addTransition(t2);
        
        Transition t3 = new Transition(3, "EXPR", 4);
        spa.addTransition(t3);
        
        Transition t4 = new Transition(4, new Token(".", "."), 5);
        t4.addPreAction(fechaProducao);
        spa.addTransition(t4);
        
        Transition t5 = new Transition(5, new Token("nterm", "nterm"), 2);
        t5.addPreAction(criarNovaSubmaquina);
        spa.addTransition(t5);
        
        Transition t6 = new Transition(6, new Token("nterm", "nterm"), 7);
        t6.addPreAction(novaTransicaoNterm);
        spa.addTransition(t6);
        
        Transition t7 = new Transition(6, new Token("term", "term"), 7);
        t7.addPreAction(novaTransicaoTerm);
        spa.addTransition(t7);
        
        Transition t8 = new Transition(6, new Token("ε", "ε"), 7);
        t8.addPreAction(novaTransicaoEpsilon);
        spa.addTransition(t8);
        
        Transition t9 = new Transition(7, new Token("nterm", "nterm"), 7);
        t9.addPreAction(novaTransicaoNterm);
        spa.addTransition(t9);
        
        Transition t10 = new Transition(7, new Token("term", "term"), 7);
        t10.addPreAction(novaTransicaoTerm);
        spa.addTransition(t10);
        
        Transition t11 = new Transition(7, new Token("ε", "ε"), 7);
        t11.addPreAction(novaTransicaoEpsilon);
        spa.addTransition(t11);

        Transition t12 = new Transition(7, new Token("|", "|"), 6);
        t12.addPreAction(adicionaOpcao);
        spa.addTransition(t12);
        
        Transition t13 = new Transition(6, new Token("(", "("), 8);
        t13.addPreAction(novoEscopo);
        spa.addTransition(t13);

        Transition t14 = new Transition(7, new Token("(", "("), 8);
        t14.addPreAction(novoEscopo);
        spa.addTransition(t14);

        Transition t15 = new Transition(8, "EXPR", 9);
        spa.addTransition(t15);

        Transition t16 = new Transition(9, new Token("|", "|"), 8);
        t16.addPreAction(adicionaOpcao);
        spa.addTransition(t16);

        Transition t17 = new Transition(9, new Token(")", ")"), 7);
        t17.addPreAction(fechaEscopo);
        spa.addTransition(t17);

        Transition t18 = new Transition(9, new Token("\\", "\\"), 10);
        t18.addPreAction(adicionaReverso);
        spa.addTransition(t18);

        Transition t19 = new Transition(10, "EXPR", 11);
        spa.addTransition(t19);

        Transition t20 = new Transition(11, new Token(")", ")"), 7);
        t20.addPreAction(fechaEscopoReverso);
        spa.addTransition(t20);

        Transition t21 = new Transition(11, new Token("|", "|"), 10);
        t21.addPreAction(adicionaOpcao);
        spa.addTransition(t21);


        spa.setup();
        boolean result = spa.parse(lexer);

        if (!result) {
            throw new Exception("There were errors while trying to perform "
                    + "lexical analysis on the provided file. Make sure the "
                    + "grammar is correct (valid Wirth syntax notation) and "
                    + "try again.");
        }

        switch (this.type) {
            case 1:
                if (! labelGrammar.fillNTermInProductions()) {
                    logger.debug("Error! Grammar incomplete. Not all nterms are defined.");
                }

                logger.debug("# Newton: " + labelGrammar.toString());
                break;
            case 2:
                reduceDeterministicEmptyTransitions();
                break;
            default:
                break;
        }
    }

    private void reduceDeterministicEmptyTransitions ()
    {

        logger.debug("Executando eliminação de transições determinísticas em vazio.");
        //Boolean done = false;

        List<Sketch> emptyTransitionList = new ArrayList<>();

        while (true) {

            for (Sketch tempTransition : transitions) { // Monta lista de transições em vazio
                if (tempTransition.getToken().getType().equals("ε")) {
                    emptyTransitionList.add(tempTransition);
                }
            }
            if (emptyTransitionList.isEmpty()) { break; }

            Boolean emptyTransitionEliminated = false;
            for (Sketch tempEmptyTransition : emptyTransitionList) {  // Processa cada transição em vazio
                Integer source = tempEmptyTransition.getSource();
                Integer target = tempEmptyTransition.getTarget();
                String submachine = tempEmptyTransition.getName();
                if (target != 1) {  // do nothing if target is the accepting state of the submachine
                    List<Sketch> sourceInTransitions = new ArrayList<>();
                    for (Sketch tempSourceInTransition: transitions) { // verifica se há multiplas transiçoes entrando do estado source
                        if ((tempSourceInTransition.getName().equals(submachine)) &&
                                (tempSourceInTransition.getTarget() == source) &&
                                (! tempSourceInTransition.equals(tempEmptyTransition))) {
                            sourceInTransitions.add(tempSourceInTransition);
                        }
                    }
                    List<Sketch> sourceOutTransitions = new ArrayList<>();
                    for (Sketch tempSourceOutTransition: transitions) { // verifica se há multiplas transiçoes saindo do estado source
                        if ((tempSourceOutTransition.getName().equals(submachine)) &&
                                (tempSourceOutTransition.getSource() == source) &&
                                (! tempSourceOutTransition.equals(tempEmptyTransition))) {
                            sourceOutTransitions.add(tempSourceOutTransition);
                        }
                    }
                    List<Sketch> targetInTransitions = new ArrayList<>();
                    for (Sketch tempTargetInTransition: transitions) { // verifica se há multiplas transiçoes entrando do estado target
                        if ((tempTargetInTransition.getName().equals(submachine)) &&
                                (tempTargetInTransition.getTarget() == target) &&
                                (! tempTargetInTransition.equals(tempEmptyTransition))) {
                            targetInTransitions.add(tempTargetInTransition);
                        }
                    }
                    /*
                    List<Sketch> targetOutTransitions = new ArrayList<>();
                    for (Sketch tempTargetOutTransition: transitions) { // verifica se há multiplas transiçoes saindo do estado target
                        if ((tempTargetOutTransition.getName().equals(submachine)) &&
                                (tempTargetOutTransition.getSource() == target) &&
                                (! tempTargetOutTransition.equals(tempEmptyTransition))) {
                            targetOutTransitions.add(tempTargetOutTransition);
                        }
                    }
                    */

                    if (targetInTransitions.isEmpty()) {  // se não há outras transições entrando no target

                        if (sourceOutTransitions.isEmpty()) {
                            // A transicao é unica entre source e target
                            // Junta labels no estado source
                            logger.debug("Eliminando transição em vazio tipo 1: {}.",tempEmptyTransition);
                            if (mapMachineStatesLabels.get(submachine).get(source) != null) {
                                if (this.mapMachineStatesLabels.get(submachine).get(target) != null) {
                                    this.mapMachineStatesLabels.get(submachine).get(source).addAll(
                                            this.mapMachineStatesLabels.get(submachine).get(target));
                                }
                            }
                            else {
                                if (this.mapMachineStatesLabels.get(submachine).get(target) != null) {
                                    this.mapMachineStatesLabels.get(submachine).put(source, this.mapMachineStatesLabels.get(submachine).get(target));
                                }
                            }
                            this.mapMachineStatesLabels.get(submachine).remove(target);
                            transitions.remove(tempEmptyTransition);
                            emptyTransitionList.remove(tempEmptyTransition);
                            // Ajusta source das demais transicoes
                            for (Sketch tempAdjustTransition: transitions) {
                                if (tempAdjustTransition.getName().equals(submachine) &&
                                        (tempAdjustTransition.getSource() == target)) {
                                    tempAdjustTransition.setSource(source);
                                }
                            }
                            emptyTransitionList.remove(tempEmptyTransition);
                            emptyTransitionEliminated = true;
                            break;
                        }
                        else {
                            if (this.mapMachineStatesLabels.get(submachine).get(target) == null) {
                                logger.debug("Eliminando transição em vazio tipo 2: {}.",tempEmptyTransition);
                                this.mapMachineStatesLabels.get(submachine).remove(target);
                                transitions.remove(tempEmptyTransition);
                                // Ajusta source das demais transicoes
                                for (Sketch tempAdjustTransition: transitions) {
                                    if (tempAdjustTransition.getName().equals(submachine) &&
                                            (tempAdjustTransition.getSource() == target)) {
                                        tempAdjustTransition.setSource(source);
                                    }
                                }
                                emptyTransitionList.remove(tempEmptyTransition);
                                emptyTransitionEliminated = true;
                                break;
                            }
                        }
                    }
                    else {
                        if ((sourceInTransitions.size() == 0) && (sourceOutTransitions.size() == 0)) {
                            if (this.mapMachineStatesLabels.get(submachine).get(source) == null) {
                                logger.debug("Eliminando transição em vazio tipo 3: {}.",tempEmptyTransition);
                                // Deleta transição e estado source qdo estado source tem só 1 entrada e saída e não tem label.
                                this.mapMachineStatesLabels.get(submachine).remove(source);
                                transitions.remove(tempEmptyTransition);
                                for (Sketch tempAdjustTransition: transitions) {
                                    if (tempAdjustTransition.getName().equals(submachine) &&
                                            (tempAdjustTransition.getTarget() == source)) {
                                        tempAdjustTransition.setTarget(target);
                                    }
                                }
                                emptyTransitionList.remove(tempEmptyTransition);
                                emptyTransitionEliminated = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (!emptyTransitionEliminated) { break; }
        }
    }

    public LabelGrammar getLabelGrammar()
    {
        return this.labelGrammar;
    }

    private void putStateLabels (Integer state, LinkedList<LabelElement> labels)
    {
        //currentStatesLabels.put(helperPairStack.top().getSecond(),token.getProductionToken().getNextLabels());
        if (this.currentStatesLabels.get(state) == null) {
            this.currentStatesLabels.put(state, labels);
            logger.debug("Estado {}. Rotulo {} adicionado.", state, labels);
        }
        else {
            logger.debug("Estado {}. Rotulo {} não adicionado. Rotulo pre-existente: {}.",
                    state, labels, this.currentStatesLabels.get(state).toString());
        }

    }
    
    private <T> Set<T> getSet(T... elements) {
        Set<T> result = new HashSet<>();
        result.addAll(Arrays.asList(elements));
        return result;
    }

    public List<Sketch> getTransitions() {
        return transitions;
    }

    public HashMap<String, HashMap<Integer, LinkedList<LabelElement>>> getMapMachineStatesLabels() {
        return mapMachineStatesLabels;
    }

    public String getMain() {
        return main;
    }
}
