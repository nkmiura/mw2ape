package br.usp.poli.lta.cereda.execute;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;

public class NLPDictionary {

    HashMap<String, ArrayList<NLPDictionaryEntry>> dictionaryHashMap;
    String yamlDictionaryFileName;

    public NLPDictionary(String yamlDictionaryFileName) {
        this.yamlDictionaryFileName = yamlDictionaryFileName;

    }

    public Boolean LoadDictionary ()
    {
        Boolean result = false;
        File inputFile = null;
        Yaml yamlDictionary = new Yaml();
        try {
            inputFile = new File(this.yamlDictionaryFileName);

            if (!inputFile.exists()) {
                throw new Exception("The provided dictionary file " +
                        yamlDictionaryFileName + " does not exist. " +
                        "Make sure the location is correct and try again.");
            } else {
                if (inputFile != null) {
                    InputStream yamlStream = new FileInputStream(inputFile);
                    dictionaryHashMap = (HashMap<String, ArrayList<NLPDictionaryEntry>>)yamlDictionary.load(yamlStream);
                    result = true;
                }
            }
        }
        catch (Exception exception) {
            System.out.println("An exception was thrown while loading the NLP dictionary.");
            System.out.println(exception.toString());
        }

        return result;
    }
}
