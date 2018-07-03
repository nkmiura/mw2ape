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

    public void incrementTransducerStackList(long threadID) {
        Stack <String> newTransducerStack = new Stack<>();
        this.transducerStackList.put(threadID, newTransducerStack);
    }

    public void cloneTransducerStackList(long originalThreadID, long newThreadID) {
        if (this.transducerStackList.containsKey(originalThreadID)) {
            if (!this.transducerStackList.containsKey(newThreadID)) {
                Stack<String> newTransducerStack = this.transducerStackList.get(originalThreadID).clone();
                this.transducerStackList.put(newThreadID, newTransducerStack);
                logger.debug("Clonando transducer stack do thread {} para thread {}.",
                        originalThreadID, newThreadID);
            }
        } else {
            logger.debug("Resultados não existentes para o thread {}.",
                    String.valueOf(originalThreadID));
        }
    }
}
