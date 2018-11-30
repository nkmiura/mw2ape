package br.usp.poli.lta.nlpdep.execute.NLP.output;

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

    public synchronized void incrementOutputList (long threadID, Thread thread)
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

    public synchronized void cloneOutputResult (long originalThreadID, long newThreadID) {
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

    public synchronized void cloneOutputResult (long newThreadID, NLPOutputResult nlpOutputResult) {
        if (!this.outputResults.containsKey(newThreadID)) { // a lista ainda não contem dados para a thread
            logger.debug("Clonando resultados para ThreadID {} # Dados: {}", newThreadID, String.valueOf(nlpOutputResult.getOutputList()));
            NLPOutputResult newOutputResult = new NLPOutputResult();
            LinkedList<String> newOutputList = new LinkedList<>();

            /*
            for (String listElement: nlpOutputResult.getOutputList()) {
                String newListElement = listElement;
                newOutputList.push(newListElement);
            } */

            Thread thread = Thread.currentThread();
            //newOutputList = (LinkedList) (nlpOutputResult.getOutputList()).clone();
            //newOutputList.addAll(nlpOutputResult.getOutputList());

            newOutputResult.setOutputList(nlpOutputResult.getOutputList());
            newOutputResult.setThread(thread);
            newOutputResult.setParseResult(false);
            this.outputResults.put(newThreadID, newOutputResult);
            logger.debug("Resultados clonados para ThreadId {} - Output list: {}", newThreadID,nlpOutputResult.getOutputList());

        } else {
            logger.debug("Erro na clonagem de resultados. Já existem dados para a Thread Id {}.", newThreadID,
                    String.valueOf(nlpOutputResult.getOutputList()));
        }
    }

    public synchronized void setOutputResults(HashMap<Long, NLPOutputResult> outputResults) {
        this.outputResults = outputResults;
    }

    public synchronized boolean insertOutputResult (long threadId, String partialResult) {
        if (this.outputResults.containsKey(threadId)) {
            //logger.debug(" ### Insert output: ThreadId {} # Before: {} # String add: {}", threadId,
            //        outputResults.get(threadId).getOutputList(), partialResult);
            logger.debug(" ### Insert output: ThreadId {} # String to add: {}", threadId, partialResult);
            this.outputResults.get(threadId).getOutputList().addLast(partialResult);
            //logger.debug(" ### Insert output: ThreadId {} # Ater: {}", threadId,
            //        outputResults.get(threadId).getOutputList());

            return true;
        }
        else { return false; }
    }

    public synchronized LinkedList<String> getOutputResult (long threadID) {
        if (this.outputResults.containsKey(threadID)) {
            return this.outputResults.get(threadID).getOutputList();
        }
        else { return null; }
    }

    public synchronized long getSize()
    {
        return this.outputResults.size();
    }

    public synchronized boolean setOutputResult (long threadID, NLPOutputResult nlpOutputResult) {
        if (this.outputResults.containsKey(threadID)) {
            this.outputResults.put(threadID, nlpOutputResult);
            return true;
        }
        else { return false; }
    }

    public synchronized boolean setParseResult(long threadId, boolean result) {
        if (this.outputResults.containsKey(threadId)) {
            this.outputResults.get(threadId).parseResult = result;
            return true;
        }
        else { return false; }
    }

    public synchronized boolean getParseResult(long threadId) {
        if (this.outputResults.containsKey(threadId)) {
            return this.outputResults.get(threadId).parseResult.booleanValue();

        } else {
            return false;
        }
    }

    public synchronized Boolean isAnyThreadAlive() {
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
