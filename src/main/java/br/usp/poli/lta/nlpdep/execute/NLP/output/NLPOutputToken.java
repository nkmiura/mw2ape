package br.usp.poli.lta.nlpdep.execute.NLP.output;

import br.usp.poli.lta.nlpdep.execute.NLP.NLPDictionaryEntry;
import br.usp.poli.lta.nlpdep.execute.NLP.dependency.DepPattern;
import br.usp.poli.lta.nlpdep.execute.NLP.dependency.DepPatternConstituent;

import java.util.ArrayList;
import java.util.List;

public class NLPOutputToken {
    // propriedades comuns
    private String value;
    private String type;
    // propriedades term
    private Integer idSentence;
    private String nlpWord;
    private NLPDictionaryEntry nlpDictionaryEntry;
    // propriedades nterm
    private Integer mainConstituent;
    private String headDirection;
    private ArrayList<DepPattern> depPatternArrayList;
    // propriedades depPaternConstituent
    private Integer id;
    private Integer head;
    private String depRel;
    private List<String> leftDeps = new ArrayList<>();
    private List<String> rightDeps = new ArrayList<>();
    //


    public NLPOutputToken(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getIdSentence() {
        return idSentence;
    }

    public void setIdSentence(Integer idSentence) {
        this.idSentence = idSentence;
    }

    public String getNlpWord() {
        return nlpWord;
    }

    public void setNlpWord(String nlpWord) {
        this.nlpWord = nlpWord;
    }

    public NLPDictionaryEntry getNlpDictionaryEntry() {
        return nlpDictionaryEntry;
    }

    public void setNlpDictionaryEntry(NLPDictionaryEntry nlpDictionaryEntry) {
        this.nlpDictionaryEntry = nlpDictionaryEntry;
    }

    public Integer getMainConstituent() {
        return mainConstituent;
    }

    public void setMainConstituent(Integer mainConstituent) {
        this.mainConstituent = mainConstituent;
    }

    public String getHeadDirection() {
        return headDirection;
    }

    public void setHeadDirection(String headDirection) {
        this.headDirection = headDirection;
    }

    public Integer getHead() {
        return head;
    }

    public void setHead(Integer head) {
        this.head = head;
    }

    public String getDepRel() {
        return depRel;
    }

    public void setDepRel(String depRel) {
        this.depRel = depRel;
    }

    public ArrayList<DepPattern> getDepPatternArrayList() {
        return depPatternArrayList;
    }

    public void setDepPatternArrayList(ArrayList<DepPattern> depPatternArrayList) {
        this.depPatternArrayList = depPatternArrayList;
    }

    public void copyFromDepPatternConstituent(DepPatternConstituent depPatternConstituent) {
        this.id = depPatternConstituent.getId();
        this.head = depPatternConstituent.getHead();
        this.depRel = depPatternConstituent.getDepRel();
        this.leftDeps = depPatternConstituent.getLeftDeps();
        this.rightDeps = depPatternConstituent.getRightDeps();
    }

    public NLPOutputToken newClone() {
        NLPOutputToken target = new NLPOutputToken(this.value, this.type);
        return target;
    }
}
