package br.usp.poli.lta.nlpdep.execute.NLP.output;

public class NLPTree {
    private Node<NLPOutputToken> rootNode;
    private Node<NLPOutputToken> currentNode;

    public NLPTree() {
        this.rootNode = null;
        this.currentNode = null;
    }

    public NLPTree(Node<NLPOutputToken> rootNode) {
        this.rootNode = rootNode;
        this.currentNode = rootNode;
    }

    public Node<NLPOutputToken> getRootNode() {
        return rootNode;
    }

    public void setRootNode(Node<NLPOutputToken> rootNode) {
        this.rootNode = rootNode;
    }

    public Node<NLPOutputToken> getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node<NLPOutputToken> currentNode) {
        this.currentNode = currentNode;
    }

    public NLPTree newClone() {
        Node<NLPOutputToken> newRootNode = cloneChild(this.rootNode);
        NLPTree clonedNLPTree = new NLPTree(newRootNode);

        clonedNLPTree.setCurrentNode(this.currentNode);
        return clonedNLPTree;
    }

    private Node<NLPOutputToken> cloneChild(Node<NLPOutputToken> sourceNode) {
        NLPOutputToken newNLPOutputToken = sourceNode.getData().newClone();
        Node<NLPOutputToken> targetNode = new Node<>(newNLPOutputToken);

        sourceNode.getChildren().forEach(each -> {
            Node<NLPOutputToken> newChildNode = cloneChild(each);
            targetNode.addChild(newChildNode);
        });

        return targetNode;
    }

}
