package br.usp.poli.lta.cereda.execute.NLP;

import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;

public class NLPTransducerStackList {

    private static final Logger logger = LoggerFactory.getLogger(NLPTransducerStackList.class);
    private HashMap<Long, Stack> transducerStackList;

    public NLPTransducerStackList() {
        this.transducerStackList = new HashMap<>();
    }

    public Stack<String> getTransducerStackList(Long threadID) {
        return transducerStackList.get(threadID);
    }

    public synchronized void incrementTransducerStackList(long threadID) {
        Stack <String> newTransducerStack = new Stack<>();
        this.transducerStackList.put(threadID, newTransducerStack);
    }

    public synchronized void cloneTransducerStackList(long newThreadID, Stack<String> newTransducerStack) {
        if (!this.transducerStackList.containsKey(newThreadID)) {
            this.transducerStackList.put(newThreadID, newTransducerStack);
            logger.debug("Clonando transducer stack para thread {}.", newThreadID);

        } else {
            logger.debug("Transducer stack não existente para o thread {}.",
                    String.valueOf(newThreadID));
        }
    }

}
