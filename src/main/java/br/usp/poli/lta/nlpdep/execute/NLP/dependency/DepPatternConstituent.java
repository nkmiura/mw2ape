package br.usp.poli.lta.nlpdep.execute.NLP.dependency;
// http://www.jsonschema2pojo.org/
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DepPatternConstituent {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("head")
    @Expose
    private Integer head;
    @SerializedName("depRel")
    @Expose
    private String depRel;
    @SerializedName("leftDeps")
    @Expose
    private List<String> leftDeps = null;
    @SerializedName("rightDeps")
    @Expose
    private List<String> rightDeps = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<String> getLeftDeps() {
        return leftDeps;
    }

    public void setLeftDeps(List<String> leftDeps) {
        this.leftDeps = leftDeps;
    }

    public List<String> getRightDeps() {
        return rightDeps;
    }

    public void setRightDeps(List<String> rightDeps) {
        this.rightDeps = rightDeps;
    }


    @Override
    public String toString() {
        return "DepPatternConstituent{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", head=" + head +
                ", depRel='" + depRel + '\'' +
                ", leftDeps=" + leftDeps +
                ", rightDeps=" + rightDeps +
                '}';
    }
}
