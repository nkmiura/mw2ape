package br.usp.poli.lta.nlpdep.execute.NLP.dependency;

import br.usp.poli.lta.nlpdep.execute.NLP.output.NLPOutputToken;
import br.usp.poli.lta.nlpdep.execute.NLP.output.Node;

public class DepStackElementNterm extends DepStackElement {
    private Node<NLPOutputToken> node;

    public DepStackElementNterm(String value) {
        this.value = value;
        this.type = "nterm";
    }

    public Node<NLPOutputToken> getNode() {
        return node;
    }

    public void setNode(Node<NLPOutputToken> node) {
        this.node = node;
    }
}
