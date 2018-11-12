package br.usp.poli.lta.cereda.execute.NLP;

import java.util.LinkedList;

public class NLPOutputResult {
    LinkedList<String> outputList;
    Thread thread;
    Boolean parseResult;

    public NLPOutputResult () {
        outputList = new LinkedList<>();
        parseResult = new Boolean(false);
        thread = new Thread();
    }

    public LinkedList<String> getOutputList() {
        return outputList;
    }

    public synchronized void setOutputList(LinkedList<String> outputList) {
        this.outputList = (LinkedList) outputList.clone();
    }

    public Boolean getParseResult() {
        return parseResult;
    }

    public void setParseResult(Boolean parseResult) {
        this.parseResult = parseResult;
    }


    public Thread getThread() {
        return thread;
    }

    public synchronized void setThread(Thread thread) {
        this.thread = thread;
    }


    @Override
    public String toString() {
        return "{" +
                "outputList=" + outputList +
                ", parseResult=" + parseResult +
                '}';
    }
}
