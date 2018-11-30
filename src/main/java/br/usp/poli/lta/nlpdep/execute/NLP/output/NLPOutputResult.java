package br.usp.poli.lta.nlpdep.execute.NLP.output;

import java.util.LinkedList;

public class NLPOutputResult {
    LinkedList<String> outputList;
    NLPTree outputTree;
    Thread thread;
    Boolean parseResult;

    public NLPOutputResult () {
        outputList = new LinkedList<>();
        parseResult = new Boolean(false);
        thread = new Thread();
        outputTree = new NLPTree();
    }

    public LinkedList<String> getOutputList() {
        return outputList;
    }

    public synchronized void setOutputList(LinkedList<String> outputList) {
        this.outputList = (LinkedList) outputList.clone();
    }

    public NLPTree getOutputTree() {
        return outputTree;
    }

    public void setOutputTree(NLPTree outputTree) {
        this.outputTree = outputTree;
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
