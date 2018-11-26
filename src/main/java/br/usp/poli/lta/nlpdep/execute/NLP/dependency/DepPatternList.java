package br.usp.poli.lta.nlpdep.execute.NLP.dependency;
// http://www.jsonschema2pojo.org/
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DepPatternList {

    @SerializedName("depPatterns")
    @Expose
    private List<DepPattern> depPatterns = null;

    public List<DepPattern> getDepPatterns() {
        return depPatterns;
    }

    public void setDepPatterns(List<DepPattern> depPatterns) {
        this.depPatterns = depPatterns;
    }

}
