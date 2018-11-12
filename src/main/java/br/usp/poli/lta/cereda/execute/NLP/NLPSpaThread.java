package br.usp.poli.lta.cereda.execute.NLP;

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
    private NLPTransducerStackList nlpTransducerStackList;

    public NLPSpaThread(StructuredPushdownAutomatonNLP spaNLP, NLPOutputList nlpOutputList,
                        NLPTransducerStackList nlpTransducerStackList, long parentThreadId)
    {
        this.spaNLP = spaNLP;
        this.nlpOutputList = nlpOutputList;
        this.nlpTransducerStackList = nlpTransducerStackList;
        if (parentThreadId >= 0) {
            this.parentThreadId = parentThreadId;
            this.isClone = true;
        } else {
            this.isClone = false;
        }
    }

    @Override
    public synchronized void run() {
        this.threadId = Thread.currentThread().getId();
        this.threadName = Thread.currentThread().getName();

        logger.debug("Thread run: thread id: " + String.valueOf(this.threadId) +
                " - name: " + this.threadName + " - clone: " + String.valueOf(isClone));
        if (this.isClone) {
            this.nlpOutputList.cloneOutputResult(this.threadId, this.spaNLP.getTempNLPOutputResult());
            this.nlpTransducerStackList.cloneTransducerStackList(this.threadId, this.spaNLP.getTempNLPTransducerStack());

        } else {
            this.nlpOutputList.incrementOutputList(this.threadId, Thread.currentThread());
            this.nlpTransducerStackList.incrementTransducerStackList(this.threadId);
        }
        logger.info("Iniciando reconhecimento com thread id: " + String.valueOf(this.threadId) +
                " - name: " + this.threadName);
        this.nlpOutputList.setParseResult(this.threadId,this.spaNLP.parse(this.isClone));

        logger.info("Finalizando reconhecimento com thread: " + String.valueOf(this.threadId) +
                " - name: " + this.threadName + " - resultado: " +  String.valueOf(this.nlpOutputList.getParseResult(this.threadId)));
    }
}
