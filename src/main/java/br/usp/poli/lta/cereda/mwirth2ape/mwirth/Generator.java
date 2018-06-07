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
import br.usp.poli.lta.cereda.mwirth2ape.labeling.LabelGrammar;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.NTerm;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.Production;
import br.usp.poli.lta.cereda.mwirth2ape.labeling.ProductionToken;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
import br.usp.poli.lta.cereda.mwirth2ape.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * @author Paulo Roberto Massa Cereda
 * @modified by Newton Kiyotaka Miura
 * @version 1.2
 * @since 1.0
 */
public class Generator {

    private static final Logger logger = LoggerFactory.
            getLogger(Generator.class);

    int type;

    // APE Generator
    private final MWirthLexer lexer;
    private final Stack<Pair<Integer, Integer>> helper;
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

    public Generator(MWirthLexer mWirthLexer, int type) {

        this.type = type;
        this.lexer = mWirthLexer;
        this.helper = new Stack<>();
        this.transitions = new ArrayList<>();
        this.current = 0;
        this.counter = 1;

        // Newton
        this.labelGrammar = new LabelGrammar();
    }

    public Generator(LMWirthLexer lmWirthLexer, int type) {

        this.type = type;
        this.lexer = lmWirthLexer;
        this.helper = new Stack<>();
        this.transitions = new ArrayList<>();
        this.current = 0;
        this.counter = 0;

        // Newton
        //this.labelGrammar = new LabelGrammar();
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
                        helper.clear();
                        current = 0;
                        counter = 1;
                        //current = counter;
                        //counter ++;
                        machine = token.getValue();
                        if (main == null) {
                            main = machine;
                        }
                        break;
                    case 2:
                        helper.clear();
                        current = 0;
                        counter = 1;
                        //current = counter;
                        //counter ++;
                        machine = token.getValue();
                        if (main == null) {
                            main = machine;
                        }
                        break;
                    case 1:
                        currentNterm = labelGrammar.newNterm(token.getValue());
                        currentProduction = currentNterm.addProduction(token.getValue());
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
                    case 2:
                        transition = new Sketch(machine, current, token, counter);
                        transitions.add(transition);
                        current = counter;
                        counter++;
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
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
                    case 2:
                        transition = new Sketch(machine, current, token, counter);
                        transitions.add(transition);
                        current = counter;
                        counter++;
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(token.getValue());
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
                    case 2:
                        transition = new Sketch(machine, current, token, counter);
                        transitions.add(transition);
                        current = counter;
                        counter++;
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken("ε");
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
                        helper.push(new Pair<>(current, counter));
                        counter++;
                        break;
                    case 1:
                        registerLabelToken("[");
                        break;
                    case 2:
                        helper.push(new Pair<>(current, counter));
                        counter++;
/*
                        Sketch transition;
                        Token newToken = new Token("ε","ε");
                        ProductionToken newProductionToken = new ProductionToken("ε","ε");
                        newProductionToken.pushLabel("[");
                        newToken.setProductionToken(newProductionToken);
                        transition = new Sketch(machine, current, newToken, counter);
                        transitions.add(transition);
                        current = counter;
                        counter++; */
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
                        helper.push(new Pair<>(current, counter));
                        counter++;
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
                        break;
                    case 2:
                        helper.push(new Pair<>(current, counter));
                        counter++;
/*
                        token.setType("ε");
                        token.setValue("ε");
                        Sketch transition;
                        transition = new Sketch(machine, current, token, counter);
                        transitions.add(transition);
                        current = counter;
                        counter++; */
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
                                helper.top().getSecond());
                        transitions.add(transition);
                        current = helper.top().getSecond();
                        helper.pop();
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
                        break;
                    case 2:
                        token.setType("ε");
                        token.setValue("ε");
                        Sketch transition2 = new Sketch(machine, current, token,
                                helper.top().getSecond());
                        transitions.add(transition2);
                        current = helper.top().getSecond();
                        helper.pop();
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
                                helper.top().getSecond());
                        transitions.add(transition);
                        current = helper.top().getSecond();
                        helper.pop();
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
                                token, helper.top().getSecond());
                        transitions.add(transition2);
                        current = helper.top().getSecond();
                        helper.pop();
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
                    case 2:
                        token.setType("ε");
                        token.setValue("ε");
                        Sketch transition = new Sketch(machine, current, token,
                                helper.top().getSecond());
                        transitions.add(transition);
                        current = helper.top().getFirst();
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
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
                    case 2:
                        token.setType("ε");
                        token.setValue("ε");
                        Sketch transition = new Sketch(machine, current, token,
                                helper.top().getSecond());
                        transitions.add(transition);
                        current = helper.top().getSecond();

                        helper.push(new Pair<>(helper.top().getSecond(), helper.top().getFirst()));
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
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

        Action fechaEscopoReverso = new Action("fechaEscopoReverso") {
            @Override
            public void execute(Token token) {
                switch(type) {
                    case 0:
                    case 2:
                        token.setType("ε");
                        token.setValue("ε");
                        Sketch transition1 = new Sketch(machine, current, token,
                                helper.top().getSecond());
                        transitions.add(transition1);
                        current = helper.top().getSecond();
                        helper.pop();

                        current = helper.top().getSecond();
                        helper.pop();
                        break;
                    case 1:
                        registerExpressionToken(token);
                        registerLabelToken(null);
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

                break;
            default:
                break;
        }
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

    public String getMain() {
        return main;
    }
}
