package br.usp.poli.lta.nlpdep.execute.NLP.dependency;

import br.usp.poli.lta.nlpdep.execute.NLP.output.NLPOutputToken;
import br.usp.poli.lta.nlpdep.execute.NLP.output.Node;


public class DepStackElementTerm extends DepStackElement {

    private Node<NLPOutputToken> node;

    public DepStackElementTerm(String value) {
        this.value = value;
        this.type = "term";
    }

    public Node<NLPOutputToken> getNode() {
        return node;
    }

    public void setNode(Node<NLPOutputToken> node) {
        this.node = node;
    }
}
