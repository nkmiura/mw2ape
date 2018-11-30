package br.usp.poli.lta.nlpdep.execute.NLP.output;

import br.usp.poli.lta.nlpdep.execute.NLP.dependency.DepPatternConstituent;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class NLPOutputToken {
    /*
    @SerializedName("_comment")
    @Expose
    private String comment;
    */
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("mainConstituent")
    @Expose
    private Integer mainConstituent;
    @SerializedName("headDirection")
    @Expose
    private String headDirection;
    @SerializedName("depPatternConstituents")
    @Expose
    private List<DepPatternConstituent> depPatternConstituents = null;



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

    public NLPOutputToken newClone() {
        NLPOutputToken target = new NLPOutputToken();
        target.value = this.value;
        target.type = this.type;
        return target;
    }

    public Boolean copy(NLPOutputToken target) {
        if (target == null) {
            return false;
        } else {
            target.value = this.value;
            target.type = this.type;
            return true;
        }
    }

    @Override
    public String toString() {
        return "NLPOutputToken{" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
