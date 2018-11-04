package br.usp.poli.lta.cereda.execute.NLP;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author w w w. j a v a g i s t s . c o m
 *
 */
public class NLPTreeNode<T> {

    private T data = null;

    private List<NLPTreeNode<T>> children = new ArrayList<>();

    private NLPTreeNode<T> parent = null;

    public NLPTreeNode(T data) {
        this.data = data;
    }

    public NLPTreeNode<T> addChild(NLPTreeNode<T> child) {
        child.setParent(this);
        this.children.add(child);
        return child;
    }

    public void addChildren(List<NLPTreeNode<T>> children) {
        children.forEach(each -> each.setParent(this));
        this.children.addAll(children);
    }

    public List<NLPTreeNode<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(NLPTreeNode<T> parent) {
        this.parent = parent;
    }

    public NLPTreeNode<T> getParent() {
        return parent;
    }

}