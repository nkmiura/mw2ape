package br.usp.poli.lta.nlpdep.execute.NLP.dependency;
// http://www.jsonschema2pojo.org/
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import br.usp.poli.lta.nlpdep.mwirth2ape.labeling.LabelGrammar;
import br.usp.poli.lta.nlpdep.mwirth2ape.labeling.NTerm;
import br.usp.poli.lta.nlpdep.mwirth2ape.labeling.Production;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepPatternList {

    @SerializedName("depPatterns")
    @Expose
    private List<DepPattern> depPatterns;

    private static final Logger logger = LoggerFactory.
            getLogger(DepPatternList.class);


    public List<DepPattern> getDepPatterns() {
        return depPatterns;
    }

    public void setDepPatterns(List<DepPattern> depPatterns) {
        this.depPatterns = depPatterns;
    }

    public DepPatternList loadDepPatternsFromJson (String inputNLPDependencyPatternsFileName) {

        Gson depPatternGson = new Gson();
        BufferedReader depPatternBr = null;
        DepPatternList depPatternList = null;
        try {
            depPatternBr = new BufferedReader(new FileReader(inputNLPDependencyPatternsFileName));
            depPatternList = depPatternGson.fromJson(depPatternBr, DepPatternList.class);
            if (depPatternList != null) {
                logger.debug("DepPatterns: {} loaded.", depPatternList.depPatterns.size());
                /* for (DepPattern singleDepPattern : depPatternList.getDepPatterns()) {
                    System.out.println(singleDepPattern.toString());
                } */
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (depPatternBr != null) {
                try {
                    depPatternBr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return depPatternList;
    }



    public Boolean insertDepPatternsToNterms(LabelGrammar labelGrammar){
        if (this.depPatterns != null) {
            for (DepPattern singleDepPattern : this.depPatterns) {
                for (NTerm singleNterm: labelGrammar.nterms) {
                    for (Production singleProduction: singleNterm.productions) {
                        if (singleProduction.getIdentifier().equals(singleDepPattern.getValue())) {
                            singleProduction.getDepPatterns().add(singleDepPattern);
                            logger.debug("# depPattern added to nterm {} - list after insertion: {}",
                                    singleProduction.getIdentifier(),singleProduction.getDepPatterns().toString());
                        }
                    }
                }
                //System.out.println(singleDepPattern.toString());
            }
        }

        return false;
    }

    public void addDepPattern (DepPattern depPattern) {
        if (this.depPatterns == null) {
            this.depPatterns = new ArrayList<>();
        }
        this.depPatterns.add(depPattern);
    }

    @Override
    public String toString() {
        return "DepPatternList{" +
                "depPatterns=" + depPatterns +
                '}';
    }
}
