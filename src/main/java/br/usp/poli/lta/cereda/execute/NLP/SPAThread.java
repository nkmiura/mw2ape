package br.usp.poli.lta.cereda.execute.NLP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPAThread implements Runnable {

    private static final Logger logger = LoggerFactory.
            getLogger(SPAThread.class);

    private long threadId;
    private String threadName;
    private StructuredPushdownAutomatonNLP spaNLP;
    private NLPOutputList nlpOutputList;


    public SPAThread (StructuredPushdownAutomatonNLP spaNLP, NLPOutputList nlpOutputList)
    {
        this.spaNLP = spaNLP;
        this.nlpOutputList = nlpOutputList;
    }


    @Override
    public void run() {
        this.threadId = Thread.currentThread().getId();
        this.threadName = Thread.currentThread().getName();
        this.nlpOutputList.incrementOutputList(this.threadId);
        logger.debug("Iniciando reconhecimento com thread id: " + String.valueOf(this.threadId) + " - " + this.threadName);
        //outputResults.get(threadId)
        this.nlpOutputList.setParseResult(this.threadId,this.spaNLP.parse());
        logger.debug("Finalizando reconhecimento com thread: " + String.valueOf(this.threadId) + " - " + this.threadName);
    }
}
