package br.usp.poli.lta.nlpdep.execute.NLP;

import br.usp.poli.lta.nlpdep.execute.NLP.dependency.DepStackList;
import br.usp.poli.lta.nlpdep.execute.NLP.output.NLPOutputList;
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
    private DepStackList depStackList;

    public NLPSpaThread(StructuredPushdownAutomatonNLP spaNLP, NLPOutputList nlpOutputList,
                        NLPTransducerStackList nlpTransducerStackList, DepStackList depStackList, long parentThreadId)
    {
        this.spaNLP = spaNLP;
        this.nlpOutputList = nlpOutputList;
        this.nlpTransducerStackList = nlpTransducerStackList;
        this.depStackList = depStackList; // 2018.11.28
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
            this.depStackList.cloneDepStackList(this.threadId,this.spaNLP.getTempDepStack());
        } else {
            this.nlpOutputList.incrementOutputList(this.threadId, Thread.currentThread());
            this.nlpTransducerStackList.incrementTransducerStackList(this.threadId);
            this.depStackList.incrementDepStackList(this.threadId);
        }
        logger.info("ThreadId {} ThreadName {} Thread Qty {} Thread run - iniciando reconhecimento.",
                String.valueOf(this.threadId), this.threadName, this.nlpOutputList.getSize());
        this.nlpOutputList.setParseResult(this.threadId,this.spaNLP.parse(this.isClone));

        logger.info("ThreadId {} ThreadName {} Thread Qty {} Thread run - finalizando reconhecimento - resultado {} ", String.valueOf(this.threadId),
                this.threadName, this.nlpOutputList.getSize(), String.valueOf(this.nlpOutputList.getParseResult(this.threadId)));
    }
}
