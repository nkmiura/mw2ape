package br.usp.poli.lta.nlpdep.execute.NLP.dependency;

import br.usp.poli.lta.nlpdep.mwirth2ape.structure.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class DepStackList {
    private static final Logger logger = LoggerFactory.getLogger(DepStackList.class);
    private HashMap<Long, Stack> depStackList;

    public DepStackList() {
        this.depStackList = new HashMap<>();
    }

    public Stack<DepStackElement> getDepStackFromThreadID(Long threadID) {
        return depStackList.get(threadID);
    }

    public synchronized void incrementDepStackList(long threadID) {
        Stack <DepStackElement> newDepStack = new Stack<>();
        //NLPOutputToken3 newRootToken = new NLPOutputToken3("root", "root");
        //DepStackElementRoot newDepStackElementRoot = new DepStackElementRoot(newRootToken);
        //newDepStack.push(newDepStackElementRoot);

        this.depStackList.put(threadID, newDepStack);
    }


    public synchronized void addStack(long newThreadID, Stack<DepStackElement> newDepStack) {
        if (!this.depStackList.containsKey(newThreadID)) {
            this.depStackList.put(newThreadID, newDepStack);
            logger.debug("Clonando dep stack para thread {}.", newThreadID);

        } else {
            logger.debug("dep stack não existente para o thread {}.", newThreadID);
        }
    }

}
