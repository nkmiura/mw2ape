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
package br.usp.poli.lta.nlpdep.mwirth2ape.mwirth;

import br.usp.poli.lta.nlpdep.execute.SPAGetStruct;
import br.usp.poli.lta.nlpdep.mwirth2ape.ape.Action;
import br.usp.poli.lta.nlpdep.mwirth2ape.ape.StructuredPushdownAutomaton;
import br.usp.poli.lta.nlpdep.mwirth2ape.ape.Transition;
import br.usp.poli.lta.nlpdep.mwirth2ape.ape.conversion.Sketch;
import br.usp.poli.lta.nlpdep.mwirth2ape.labeling.*;
import br.usp.poli.lta.nlpdep.mwirth2ape.model.Token;
import br.usp.poli.lta.nlpdep.mwirth2ape.structure.Stack;
import br.usp.poli.lta.nlpdep.mwirth2ape.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.3
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
    //private HashMap<String, HashMap<Integer, LinkedList<LabelElement>>> mapMachineStates;
    //private HashMap<String, LinkedList<Integer>> mapMachineStates; // 2018.10.11 *
    //private HashMap<Integer, LinkedList<LabelElement>> currentStatesLabels;   // 2018.10.11
    //private LinkedList<Integer> currentStates; // 2018.10.11 *

    public Generator(MWirthLexer mWirthLexer, int type) {
        this.type = type;
        this.lexer = mWirthLexer;
        this.helperPairStack = new Stack<>();
        this.transitions = new ArrayList<>();
        this.current = 0;
        this.counter = 1;
        this.labelGrammar = new LabelGrammar();
    }

    public Generator(LMWirthLexer lmWirthLexer, int type) {
        this.type = type;
        this.lexer = lmWirthLexer;
        this.helperPairStack = new Stack<>();
        this.transitions = new ArrayList<>();
        this.current = 0;
        this.counter = 0;
        //this.mapMachineStates = new HashMap<>(); // 2018.10.11 *
    }

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
                Sketch transition;
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
                    case 3:
                        helperPairStack.clear();
                        current = 0;
                        counter = 1;
                        machine = token.getValue();
                        token.setType("ε");
                        token.setValue("ε");

                        LinkedList<LabelElement> initialLabelElements;
                        LabelElement initialLabelElement;
                        initialLabelElements = new LinkedList<>();
                        initialLabelElement = new LabelElement();
                        initialLabelElement.setValue("[");
                        initialLabelElements.add(initialLabelElement);
                        token.getProductionToken().addPostLabels(initialLabelElements);

                        transition = new Sketch(machine, current, token, counter);
                        transitions.add(transition);

                        current = counter;
                        counter++;

                        //currentStates = new LinkedList<>();    // 2018.10.11
                        //mapMachineStates.put(machine, currentStates);    // 2018.10.11
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
                        //current = helperPairStack.top().getSecond();    // retirado em 2019.03.06
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
                    throw new Exception("Error! Grammar incomplete. Not all nterms are defined.");
                }
                //logger.debug("# Newton: " + labelGrammar.toString());
                break;
            case 2:

                reduceDeterministicEmptyTransitions();
                simplifyCommonPrefix();
                break;
            default:
                break;
        }
    }


    private void reduceDeterministicEmptyTransitions ()
    {
        logger.debug("Executando eliminação de transições determinísticas em vazio.");

        List<Sketch> emptyTransitionList = new ArrayList<>();

        while (true) {
            emptyTransitionList.clear();
            for (Sketch tempTransition : transitions) { // Monta lista de transições em vazio
                if (tempTransition.getToken().getType().equals("ε")) {
                    emptyTransitionList.add(tempTransition);
                }
            }
            if (emptyTransitionList.isEmpty()) {
                logger.debug("Não existem transicoes em vazio."); break; }
                else {
                    logger.debug("\n# Nova lista de transicoes em vazio: {} elementos.", emptyTransitionList.size());
            }

            Boolean emptyTransitionEliminated = false;
            for (Iterator<Sketch> iterator = emptyTransitionList.iterator(); iterator.hasNext(); ) {
                // Processa cada transição em vazio
                Sketch tempEmptyTransition = iterator.next();

                Integer source = tempEmptyTransition.getSource();
                Integer target = tempEmptyTransition.getTarget();
                String submachine = tempEmptyTransition.getName();
                logger.debug("Verificando transição em vazio: {}, pre {} e pos {}.",tempEmptyTransition,
                        tempEmptyTransition.getToken().getProductionToken().getPreLabels(),
                        tempEmptyTransition.getToken().getProductionToken().getPostLabels());

                List<Sketch> sourceInTransitions = new ArrayList<>();
                for (Sketch tempSourceInTransition: transitions) { // verifica se há multiplas transiçoes entrando do estado source
                    if ((tempSourceInTransition.getName().equals(submachine)) &&
                            (tempSourceInTransition.getTarget() == source)) {
                        sourceInTransitions.add(tempSourceInTransition);
                    }
                }
                Integer sourceInQty = sourceInTransitions.size();

                List<Sketch> sourceOutTransitions = new ArrayList<>();
                for (Sketch tempSourceOutTransition: transitions) { // verifica se há multiplas transiçoes saindo do estado source
                    if ((tempSourceOutTransition.getName().equals(submachine)) &&
                            (tempSourceOutTransition.getSource() == source)) {
                        sourceOutTransitions.add(tempSourceOutTransition);
                    }
                }
                Integer sourceOutQty = sourceOutTransitions.size();

                List<Sketch> targetInTransitions = new ArrayList<>();
                for (Sketch tempTargetInTransition: transitions) { // verifica se há multiplas transiçoes entrando do estado target
                    if ((tempTargetInTransition.getName().equals(submachine)) &&
                            (tempTargetInTransition.getTarget() == target)) {
                        targetInTransitions.add(tempTargetInTransition);
                    }
                }
                Integer targetInQty = targetInTransitions.size();

                List<Sketch> targetOutTransitions = new ArrayList<>();
                for (Sketch tempTargetOutTransition: transitions) { // verifica se há multiplas transiçoes saindo do estado target
                    if ((tempTargetOutTransition.getName().equals(submachine)) &&
                            (tempTargetOutTransition.getSource() == target) ) {
                        targetOutTransitions.add(tempTargetOutTransition);
                    }
                }
                Integer targetOutQty = targetOutTransitions.size();

                logger.debug(" Origem: In: {}, Out: {}; Destino: - In: {}, Out: {}",
                        String.valueOf(sourceInQty),String.valueOf(sourceOutQty),
                        String.valueOf(targetInQty),String.valueOf(targetOutQty));

                if ((sourceOutQty == 0) || (targetInQty == 0)) {
                    logger.debug(" Transicao nao sera removida. Configuracao impossivel Source Out: {}, Target In: {}.",
                            sourceOutQty, targetInQty);
                }
                else if (   (((sourceInQty == 0) && (sourceOutQty >= 1) && (targetInQty == 1) && (targetOutQty >= 1)) ||
                            ((sourceInQty >= 1) && (sourceOutQty >= 2) && (targetInQty == 1) && (targetOutQty >= 1))) &&
                            (target != 2))
                {
                    logger.debug(" Elimina target e transicao em vazio, transicoes posteriores se iniciam no estado source.");
                    // this.mapMachineStates.get(submachine).remove(target); // 2018.10.11 *
                    transitions.remove(tempEmptyTransition);
                    //emptyTransitionList.remove(tempEmptyTransition);
                    //iterator.remove();

                    // Ajusta source das demais transicoes
                    for (Sketch tempAdjustTransition: transitions) {
                        if (tempAdjustTransition.getName().equals(submachine) &&
                                (tempAdjustTransition.getSource() == target)) {
                            if ((tempEmptyTransition.getToken().getProductionToken().getPreLabels() == null) &&
                                    (tempEmptyTransition.getToken().getProductionToken().getPostLabels() == null)) {
                                // Nao tem label na transicao
                                logger.debug("  sem label na transicao em vazio");
                                logger.debug("  Situação após ajuste: source {} target {} value {} pre {} pos {} ",
                                        String.valueOf(source),
                                        String.valueOf(tempAdjustTransition.getTarget()),
                                        tempAdjustTransition.getToken().getValue(),
                                        tempAdjustTransition.getToken().getProductionToken().getPreLabels(),
                                        tempAdjustTransition.getToken().getProductionToken().getPostLabels());
                            }
                            else {
                                // Tem label na transicao
                                logger.debug("  transicao em vazio pre {} e pos {} como prefixo da trans posterior: source {} target {} value {} pre {} e pos {}",
                                        tempEmptyTransition.getToken().getProductionToken().getPreLabels(),
                                        tempEmptyTransition.getToken().getProductionToken().getPostLabels(),
                                        String.valueOf(tempAdjustTransition.getSource()),
                                        String.valueOf(tempAdjustTransition.getTarget()),
                                        tempAdjustTransition.getToken().getValue(),
                                        tempAdjustTransition.getToken().getProductionToken().getPreLabels(),
                                        tempAdjustTransition.getToken().getProductionToken().getPostLabels());
                                tempAdjustTransition.getToken().getProductionToken().pushPreLabels(
                                        tempEmptyTransition.getToken().getProductionToken().getPostLabels());
                                tempAdjustTransition.getToken().getProductionToken().pushPreLabels(
                                        tempEmptyTransition.getToken().getProductionToken().getPreLabels());
                                logger.debug("  Situação após ajuste: source {} target {} value {} pre {} pos {} ",
                                        String.valueOf(source),
                                        String.valueOf(tempAdjustTransition.getTarget()),
                                        tempAdjustTransition.getToken().getValue(),
                                        tempAdjustTransition.getToken().getProductionToken().getPreLabels(),
                                        tempAdjustTransition.getToken().getProductionToken().getPostLabels());
                            }
                            tempAdjustTransition.setSource(source);
                        }
                    }
                    emptyTransitionEliminated = true;
                    break;
                }
                else if (   ((sourceInQty == 1) && (sourceOutQty == 1) && (targetInQty >= 1)) ||
                            ((sourceInQty >= 2) && (sourceOutQty == 1) &&
                                    ((targetInQty == 1)) ||
                                     ((targetInQty >= 2) && (targetOutQty >= 1)) ) &&
                                    (source != 0))
                {
                    logger.debug(" Elimina source e transicao em vazio, transicoes anteriores sao finalizados no estado target");
                    //this.mapMachineStates.get(submachine).remove(source);  // 2018.10.11 *
                    transitions.remove(tempEmptyTransition);
                    //emptyTransitionList.remove(tempEmptyTransition);
                    //iterator.remove();

                    // Ajusta source das demais transicoes
                    for (Sketch tempAdjustTransition: transitions) {
                        if (tempAdjustTransition.getName().equals(submachine) &&
                                (tempAdjustTransition.getTarget() == source)) {
                            if ((tempEmptyTransition.getToken().getProductionToken().getPreLabels() == null) &&
                                    (tempEmptyTransition.getToken().getProductionToken().getPostLabels() == null)) {
                                // Nao tem label na transicao
                                logger.debug("  sem label na transicao em vazio");
                                logger.debug("  Situação após ajuste: source {} target {} value {} pre {} pos {} ",
                                        String.valueOf(tempAdjustTransition.getSource()),
                                        String.valueOf(target),
                                        tempAdjustTransition.getToken().getValue(),
                                        tempAdjustTransition.getToken().getProductionToken().getPreLabels(),
                                        tempAdjustTransition.getToken().getProductionToken().getPostLabels());
                            }
                            else {
                                // Tem label na transicao
                                logger.debug("  transicao em vazio pre {} e pos {} como sufixo da trans anterior: source {} target {} value {} pre {} e pos {}.",
                                        tempEmptyTransition.getToken().getProductionToken().getPreLabels(),
                                        tempEmptyTransition.getToken().getProductionToken().getPostLabels(),
                                        String.valueOf(tempAdjustTransition.getSource()),
                                        String.valueOf(tempAdjustTransition.getTarget()),
                                        tempAdjustTransition.getToken().getValue(),
                                        tempAdjustTransition.getToken().getProductionToken().getPreLabels(),
                                        tempAdjustTransition.getToken().getProductionToken().getPostLabels());
                                tempAdjustTransition.getToken().getProductionToken().addPostLabels(
                                        tempEmptyTransition.getToken().getProductionToken().getPreLabels());
                                tempAdjustTransition.getToken().getProductionToken().addPostLabels(
                                        tempEmptyTransition.getToken().getProductionToken().getPostLabels());
                                logger.debug("  Situação após ajuste: source {} target {} value {} pre {} pos {} ",
                                        String.valueOf(tempAdjustTransition.getSource()),
                                        String.valueOf(target),
                                        tempAdjustTransition.getToken().getValue(),
                                        tempAdjustTransition.getToken().getProductionToken().getPreLabels(),
                                        tempAdjustTransition.getToken().getProductionToken().getPostLabels());
                            }
                            tempAdjustTransition.setTarget(target);
                        }
                    }
                    emptyTransitionEliminated = true;
                    break;
                }
                else {
                    logger.debug(" Transição em vazio não eliminada, retirado da lista de transições em vazio em análise.");
                    //emptyTransitionList.remove(tempEmptyTransition);
                    //iterator.remove();
                }
            }
            if (!emptyTransitionEliminated) { break; }
        }
    }


    private void simplifyCommonPrefix()
    {
        logger.debug("Colocando transições em evidência.");

        SPAGetStruct spaStruct = new SPAGetStruct(this.transitions);
        Map<String, List<Sketch>> machineSketchesMap = spaStruct.getMachinesFromTransitions(); // map list of Sketch (transitions) to a machine
        //Map<String, Integer> machineMaxStateIdMap = spaStruct.getMaxStateIdFromMachineMap(machineSketchesMap);

        for (String machine : machineSketchesMap.keySet()) {
            List<Sketch> tempSketches = machineSketchesMap.get(machine); // Get transitions for a machine
            simplifyBranch(machine,0,tempSketches);
        }
        logger.debug("Finalizada simplificação.");
    }

    private void simplifyBranch(String machine, Integer source, List<Sketch> listSketches)
    {
        List<Sketch> branchSketches = new ArrayList<Sketch>();

        while (true)
        {
            branchSketches.clear();

            for (Sketch tempSketch: listSketches) { // monta lista de transições com origem em source
                if (tempSketch.getSource() == source)
                {
                    branchSketches.add(tempSketch);
                }
            }

            Boolean simplificationDone = false;

            if (branchSketches.size() <= 1) { // se lista é vazia, retorna
                //break;
                return;
            }

            else {
                for (Sketch oneBranch: branchSketches) { // para cada elemento da lista
                    for (Sketch candidateBranch: branchSketches) { // compara com a propria lista
                        logger.debug(" ## Comparando {} com {}", oneBranch.toString(), candidateBranch.toString());
                        if ((isBranchesTokensEqual(oneBranch,candidateBranch)) && (!oneBranch.equals(candidateBranch))) {
                            Boolean preLabelsMatch = false;
                            Boolean postLabelsMatch = false;
                            // Verifica labels pre
                            if (oneBranch.getToken().getProductionToken().getPreLabels() == null) {
                                if (candidateBranch.getToken().getProductionToken().getPreLabels() == null) {
                                    preLabelsMatch = true;
                                }
                            } else {
                                if (candidateBranch.getToken().getProductionToken().getPreLabels() != null) {
                                    preLabelsMatch = compareLabels(oneBranch.getToken().getProductionToken().getPreLabels(), candidateBranch.getToken().getProductionToken().getPreLabels());
                                }
                            }
                            if (preLabelsMatch) {
                                // Se label pre forem iguais, verifica label pos
                                if (oneBranch.getToken().getProductionToken().getPostLabels() == null) {
                                    if (candidateBranch.getToken().getProductionToken().getPostLabels() == null) {
                                        postLabelsMatch = true;
                                    }
                                } else {
                                    if (candidateBranch.getToken().getProductionToken().getPostLabels() != null) {
                                        postLabelsMatch = compareLabels(oneBranch.getToken().getProductionToken().getPostLabels(), candidateBranch.getToken().getProductionToken().getPostLabels());
                                    }
                                }
                                if (postLabelsMatch) {
                                    logger.debug(" ## Encontrei transição eliminavel {}", candidateBranch.toString());
                                    // Ajusta os demais
                                    Integer originalTargetState = candidateBranch.getTarget();
                                    for (Sketch tempAdjustSketch: this.transitions) { // monta lista de transições com origem em source
                                        if ((tempAdjustSketch.getName().equals(machine)) && (tempAdjustSketch.getSource() == originalTargetState))
                                        {
                                            tempAdjustSketch.setSource(oneBranch.getTarget());
                                            logger.debug(" ### Adjusted source from {}: {}", originalTargetState, tempAdjustSketch);
                                        }
                                    }
                                    listSketches.remove(candidateBranch);
                                    branchSketches.remove(candidateBranch);
                                    this.transitions.remove(candidateBranch);
                                    //this.mapMachineStates.get(machine).remove(originalTargetState);
                                    simplificationDone = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (simplificationDone) break;
                }
            }
            if (!simplificationDone) {
                for (Sketch tempBranch : branchSketches) {
                    simplifyBranch(machine, tempBranch.getTarget(), listSketches);
                }
                break;
            }
        }
    }

    Boolean isBranchesTokensEqual(Sketch branch1, Sketch branch2)
    {
        return branch1.getToken().getValue().equals(branch2.getToken().getValue());
    }

    Boolean compareLabels(LinkedList<LabelElement> labels1, LinkedList<LabelElement> labels2) {
        Boolean result = false;
        Boolean partialResult = true;
        if (labels1.size() == labels2.size()) {
            for (int i = 0; i < labels1.size(); i++) {
                LabelElement label1Element = labels1.get(i);
                LabelElement label2Element = labels2.get(i);
                if (! label1Element.getValue().equals(label2Element.getValue())) {
                    partialResult = false;
                }
            }
            result = partialResult;
        }
        return result;
    }

    public LabelGrammar getLabelGrammar()
    {
        return this.labelGrammar;
    }


    private <T> Set<T> getSet(T... elements) {
        Set<T> result = new HashSet<>();
        result.addAll(Arrays.asList(elements));
        return result;
    }

    public List<Sketch> getTransitions() {
        return transitions;
    }
/*
    public HashMap<String, LinkedList<Integer>> getMapMachineStates() {
        return mapMachineStates;
    }
    */
    public String getMain() {
        return main;
    }
}
