package br.usp.poli.lta.nlpdep.execute.NLP.dependency;
// http://www.jsonschema2pojo.org/
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DepPattern {

    @SerializedName("_comment")
    @Expose
    private String comment;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("mainConstituent")
    @Expose
    private Integer mainConstituent;
    @SerializedName("headDirection")
    @Expose
    private String headDirection;
    @SerializedName("depPatternConstituents")
    @Expose
    private List<DepPatternConstituent> depPatternConstituents = null;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public List<DepPatternConstituent> getDepPatternConstituents() {
        return depPatternConstituents;
    }

    public void setDepPatternConstituents(List<DepPatternConstituent> depPatternConstituents) {
        this.depPatternConstituents = depPatternConstituents;
    }

}
