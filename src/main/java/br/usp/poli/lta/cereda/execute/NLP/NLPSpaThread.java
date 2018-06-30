package br.usp.poli.lta.cereda.execute.NLP;

import br.usp.poli.lta.cereda.mwirth2ape.structure.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Newton Kiyotaka Miura
 * @version 1.0
 * @since 1.0
 */

public class NLPSpaThread implements Runnable {

    private static final Logger logger = LoggerFactory.
            getLogger(NLPSpaThread.class);

    private long threadId;
    private long parentThreadId;
    private String threadName;
    private StructuredPushdownAutomatonNLP spaNLP;
    private NLPOutputList nlpOutputList;
    private boolean isClone;
    private Stack<String> transducerStack;


    public NLPSpaThread(StructuredPushdownAutomatonNLP spaNLP, NLPOutputList nlpOutputList, long parentThreadId)
    {
        this.spaNLP = spaNLP;
        this.nlpOutputList = nlpOutputList;
        if (parentThreadId >= 0) {
            this.parentThreadId = parentThreadId;
            this.isClone = true;
        } else {
            this.isClone = false;
        }
    }


    @Override
    public void run() {
        this.threadId = Thread.currentThread().getId();
        this.threadName = Thread.currentThread().getName();
        if (this.isClone) {
            this.nlpOutputList.cloneOutputResult(this.parentThreadId, this.threadId);
        } else {
            this.nlpOutputList.incrementOutputList(this.threadId, Thread.currentThread());
        }
        logger.debug("Iniciando reconhecimento com thread id: " + String.valueOf(this.threadId) + " - " + this.threadName);
        this.nlpOutputList.setParseResult(this.threadId,this.spaNLP.parse(this.isClone));
        logger.debug("Finalizando reconhecimento com thread: " + String.valueOf(this.threadId) + " - " + this.threadName);
    }
}
