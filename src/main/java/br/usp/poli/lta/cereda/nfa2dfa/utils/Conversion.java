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
package br.usp.poli.lta.cereda.nfa2dfa.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Conversion {

    private List<SimpleTransition> transitions;
    private int initial;
    private Set<Integer> accepting;

    public Conversion(List<SimpleTransition> transitions,
            int initial, Set<Integer> accepting) {
        this.transitions = transitions;
        this.initial = initial;
        this.accepting = accepting;
    }
    
    private Set<Integer> eclosure(int i) {
        Set<Integer> result = new HashSet<>();
        Set<Integer> temp;
        result.add(i);
        int size;
        List<SimpleTransition> query;
        do {
            size = result.size();
            temp = new HashSet<>(result);

            for (int element : temp) {
                query = emptyTransitionsFromSource(element);
                for (SimpleTransition transition : query) {
                    result.add(transition.getTarget());
                }
            }

        } while (size != result.size());
        return result;
    }

    private Set<Integer> eclosure(Set<Integer> i) {
        Set<Integer> result = new HashSet<>();
        for (int j : i) {
            result.addAll(eclosure(j));
        }
        return result;
    }

    private HashSet<Token> alphabet() {
        HashSet<Token> result = new HashSet<>();
        for (SimpleTransition transition : transitions) {
            if (!transition.epsilon()) {
                result.add(transition.getSymbol());
            }
        }
        return result;
    }

    private Set<Set<Integer>> acceptingStates(Set<Set<Integer>> Q) {
        Set<Set<Integer>> F = new HashSet<>();
        for (int i : accepting) {
            for (Set<Integer> q : Q) {
                if (q.contains(i)) {
                    F.add(q);
                }
            }
        }
        return F;
    }

    private Set<Integer> delta(Set<Integer> q, Token token) {
        Set<Integer> result = new HashSet<>();
        List<SimpleTransition> query = consumeToken(q, token);
        for (SimpleTransition transition : query) {
            result.add(transition.getTarget());
        }
        return result;
    }

    private List<SimpleTransition> consumeToken(Set<Integer> q, Token token) {
        List<SimpleTransition> result = new ArrayList<>();
        for (SimpleTransition transition : transitions) {
            if (!transition.epsilon()) {
                if (transition.getSymbol().equals(token)) {
                    if (q.contains(transition.getSource())) {
                        result.add(transition);
                    }
                }
            }
        }
        return result;
    }

    private List<SimpleTransition> consumeToken(int q, Token token) {
        List<SimpleTransition> result = new ArrayList<>();
        for (SimpleTransition transition : transitions) {
            if (!transition.epsilon()) {
                if (transition.getSymbol().equals(token)) {
                    if (transition.getSource() == q) {
                        result.add(transition);
                    }
                }
            }
        }
        return result;
    }

    private List<SimpleTransition> emptyTransitionsFromSource(int element) {
        List<SimpleTransition> result = new ArrayList<>();
        for (SimpleTransition transition : transitions) {
            if (transition.getSource() == element) {
                if (transition.epsilon()) {
                    result.add(transition);
                }
            }
        }
        return result;
    }

    private class ETransition {

        private Set<Integer> source;
        private Token symbol;
        private Set<Integer> target;

        public ETransition() {
        }

        public ETransition(Set<Integer> source,
                Token symbol, Set<Integer> target) {
            this.source = source;
            this.symbol = symbol;
            this.target = target;
        }

        public Set<Integer> getSource() {
            return source;
        }

        public Token getSymbol() {
            return symbol;
        }

        public Set<Integer> getTarget() {
            return target;
        }

        public void setSource(Set<Integer> source) {
            this.source = source;
        }

        public void setSymbol(Token symbol) {
            this.symbol = symbol;
        }

        public void setTarget(Set<Integer> target) {
            this.target = target;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Transição estendida: (");
            sb.append("origem: (").append(
                    StringUtils.join(source, ", ")).append("), ");
            sb.append("token: ").append(symbol).append(", ");
            sb.append("destino: (").append(
                    StringUtils.join(target, ",")).append(")");
            sb.append(")");
            return sb.toString();
        }

    }

    public Triple<Integer, Set<Integer>, List<SimpleTransition>> convert() {

        List<ETransition> T = new ArrayList<>();
        Set<Integer> q0 = eclosure(initial);
        Set<Set<Integer>> Q = new HashSet<>();
        Set<Set<Integer>> F;
        Q.add(q0);

        List<Set<Integer>> worklist = new ArrayList<>();
        Set<Integer> q;
        Set<Integer> t;

        worklist.add(q0);

        Set<Token> sigma = alphabet();

        while (!worklist.isEmpty()) {
            q = worklist.remove(0);

            for (Token c : sigma) {
                t = eclosure(delta(q, c));
                if (!t.isEmpty()) {
                    T.add(new ETransition(q, c, t));
                    if (!Q.contains(t)) {
                        Q.add(t);
                        worklist.add(t);
                    }
                }
            }
        }

        F = acceptingStates(Q);

        Map<Set<Integer>, Integer> names = new HashMap<>();
        int counter = 0;

        names.put(q0, counter);
        for (Set<Integer> e : Q) {
            if (!e.equals(q0)) {
                counter++;
                names.put(e, counter);
            }
        }

        List<SimpleTransition> converted = new ArrayList<>();
        for (ETransition etransition : T) {
            SimpleTransition transition = new SimpleTransition(
                    names.get(etransition.getSource()),
                    etransition.getSymbol(),
                    names.get(etransition.getTarget()));
            converted.add(transition);
        }

        Triple<
                Integer,
                Set<Integer>,
                List<SimpleTransition>
                > result = new Triple<>();
        result.setFirst(names.get(q0));
        result.setThird(converted);

        Set<Integer> FF = new HashSet<>();
        for (Set<Integer> f : F) {
            FF.add(names.get(f));
        }
        result.setSecond(FF);

        return result;

    }

    public Triple<Integer, Set<Integer>, List<SimpleTransition>> minimize() {

        Set<Integer> Q = new HashSet<>();
        Set<Integer> A = new HashSet<>(accepting);
        Set<Integer> R = new HashSet<>();
        for (SimpleTransition transition : transitions) {
            Q.add(transition.getSource());
            Q.add(transition.getTarget());
        }

        for (int q : Q) {
            if (!A.contains(q)) {
                R.add(q);
            }
        }

        Set<Set<Integer>> T = new HashSet<>();
        T.add(A);
        T.add(R);

        Set<Set<Integer>> P = new HashSet<>();

        while (!T.equals(P)) {
            P = new HashSet<>(T);
            T = new HashSet<>();
            for (Set<Integer> p : P) {
                for (Set<Integer> i : split(p, P)) {
                    if (!i.isEmpty()) {
                        T.add(i);
                    }
                }
            }
        }

        Map<Integer, Integer> names = new HashMap<>();
        int counter = 0;
        for (Set<Integer> t : T) {
            if (t.contains(initial)) {
                for (int i : t) {
                    names.put(i, counter);
                }
            }
        }

        for (Set<Integer> t : T) {
            if (!t.contains(initial)) {
                counter++;
                for (int i : t) {
                    names.put(i, counter);
                }
            }
        }

        List<SimpleTransition> converted = new ArrayList<>();

        for (SimpleTransition transition : transitions) {
            SimpleTransition t = new SimpleTransition(
                    names.get(transition.getSource()),
                    transition.getSymbol(),
                    names.get(transition.getTarget()));
            if (!hasTransition(converted, t)) {
                converted.add(t);
            }
        }

        Triple<
                Integer,
                Set<Integer>,
                List<SimpleTransition>
                > result = new Triple<>();

        result.setFirst(names.get(initial));
        result.setThird(converted);

        HashSet<Integer> FF = new HashSet<>();
        for (int i : accepting) {
            FF.add(names.get(i));
        }

        result.setSecond(FF);

        return result;

    }

    private boolean hasTransition(List<SimpleTransition> transitions,
            SimpleTransition transition) {
        for (SimpleTransition t : transitions) {
            if ((t.getSource() == transition.getSource())
                    && (t.getTarget() == transition.getTarget())
                    && (t.getSymbol().equals(transition.getSymbol()))) {
                return true;
            }
        }
        return false;
    }

    private Set<Set<Integer>> split(Set<Integer> S, Set<Set<Integer>> p) {
        Set<Set<Integer>> result = new HashSet<>();
        Set<Token> sigma = alphabet();
        Set<Integer> temp = null;

        HashSet<Integer> s2 = new HashSet<>();
        for (Token c : sigma) {
            for (int s : S) {
                if (temp == null) {
                    temp = getSet(s, c, p);
                } else {
                    if (!temp.equals(getSet(s, c, p))) {
                        s2.add(s);
                    }
                }
            }
            temp = null;
        }

        if (!s2.isEmpty()) {
            Set<Integer> s1 = new HashSet<>(S);
            s1.removeAll(s2);
            result.add(s1);
            result.add(s2);
        } else {
            result.add(S);
        }

        return result;
    }

    private Set<Integer> getSet(final int s,
            final Token c, Set<Set<Integer>> p) {
        List<SimpleTransition> query = consumeToken(s, c);
        if (!query.isEmpty()) {
            for (Set<Integer> i : p) {
                if (i.contains(query.get(0).getTarget())) {
                    return i;
                }
            }
        }
        return new HashSet<>();
    }

}
