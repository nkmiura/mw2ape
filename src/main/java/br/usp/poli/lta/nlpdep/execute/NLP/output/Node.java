package br.usp.poli.lta.nlpdep.execute.NLP.output;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author w w w. j a v a g i s t s . c o m
 *
 * https://www.javagists.com/java-tree-data-structure
 */
public class Node<T> {

    private T data = null;

    private List<Node<T>> children = new ArrayList<>();

    private Node<T> parent = null;

    public Node(T data) {
        this.data = data;
    }

    public Node<T> addChild(Node<T> child) {
        child.setParent(this);
        this.children.add(child);
        return child;
    }

    public void addChildren(List<Node<T>> children) {
        children.forEach(each -> each.setParent(this));
        this.children.addAll(children);
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public Node<T> getParent() {
        return parent;
    }

    public Node<T> getRoot() {
        if (parent == null) {
            return this;
        }
        return parent.getRoot();
    }

}