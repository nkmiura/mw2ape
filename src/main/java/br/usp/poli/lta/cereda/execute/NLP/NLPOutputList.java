package br.usp.poli.lta.cereda.execute.NLP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;

public class NLPOutputList {

    private static final Logger logger = LoggerFactory.getLogger(NLPOutputList.class);

    public class OutputResult {
        LinkedList<String> outputList;
        Boolean parseResult;
    }

    private HashMap<Long, OutputResult> outputResults;

    public NLPOutputList() {
        this.outputResults = new HashMap<>();
    }

    public void incrementOutputList (long threadID)
    {
        if (!this.outputResults.containsKey(threadID)) {
            OutputResult newOutputResult = new OutputResult();
            newOutputResult.outputList = new LinkedList<>();
            newOutputResult.parseResult = false;

            this.outputResults.put(threadID, newOutputResult);
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
