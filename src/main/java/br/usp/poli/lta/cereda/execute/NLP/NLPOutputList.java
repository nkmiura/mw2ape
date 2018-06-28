package br.usp.poli.lta.cereda.execute.NLP;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.LinkedList;

public class NLPOutputList {

    private static final Logger logger = LoggerFactory.getLogger(NLPOutputList.class);

    private HashMap<Long, NLPOutputResult> outputResults;

    public NLPOutputList() {
        this.outputResults = new HashMap<>();
    }

    public void incrementOutputList (long threadID, Thread thread)
    {
        if (!this.outputResults.containsKey(threadID)) {
            NLPOutputResult newOutputResult = new NLPOutputResult();
            //newOutputResult.outputList = new LinkedList<>();
            //newOutputResult.parseResult = false;
            newOutputResult.setThread(thread);
            this.outputResults.put(threadID, newOutputResult);
            logger.debug("Iniciando resultados para thread {}",
                    String.valueOf(threadID));
        } else {
            logger.debug("Resultados já iniciados para o thread {}.",
                    String.valueOf(threadID));
        }
    }

    public void cloneOutputResult (long originalThreadID, long newThreadID) {
        if (this.outputResults.containsKey(originalThreadID)) {
            if (!this.outputResults.containsKey(newThreadID)) {
                NLPOutputResult newOutputResult = new NLPOutputResult();
                LinkedList<String> newOutputList = new LinkedList<>();
                newOutputList.addAll(this.outputResults.get(originalThreadID).getOutputList());
                newOutputResult.setOutputList(newOutputList);
                this.outputResults.put(newThreadID, newOutputResult);
                logger.debug("Clonando resultados do thread {} para thread {}.",
                        originalThreadID, newThreadID);
            }
        } else {
            logger.debug("Resultados não existentes para o thread {}.",
                    String.valueOf(originalThreadID));
        }
    }

    public boolean insertOutputResult (long threadId, String partialResult) {
        if (this.outputResults.containsKey(threadId)) {
            this.outputResults.get(threadId).outputList.addLast(partialResult);
            return true;
        }
        else { return false; }
    }

    public LinkedList<String> getOutputResult (long threadID) {
        if (this.outputResults.containsKey(threadID)) {
            return this.outputResults.get(threadID).outputList;
        }
        else { return null; }
    }

    public boolean setParseResult(long threadId, boolean result) {
        if (this.outputResults.containsKey(threadId)) {
            this.outputResults.get(threadId).parseResult = result;
            return true;
        }
        else { return false; }
    }

    public boolean getParseResult(long threadId) {
        if (this.outputResults.containsKey(threadId)) {
            return this.outputResults.get(threadId).parseResult.booleanValue();

        } else {
            return false;
        }
    }

    public Boolean isAnyThreadAlive() {
        Boolean result = false;

        for (NLPOutputResult tempNLPOutputResult: this.outputResults.values()) {
            if (tempNLPOutputResult.getThread().isAlive()) {
                result = true;
                break;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ Qtd: ");
        sb.append(String.valueOf(outputResults.size() + "\n"));
        this.outputResults.forEach(
                (threadID, outputResult) ->
                    { if (outputResult.parseResult) {
                        sb.append("ThreadID " + String.valueOf(threadID) + ": ");
                        sb.append(outputResult.outputList.toString() + "\n");
                    }
                }
        );
        sb.append('}');
        return sb.toString();
    }
}
