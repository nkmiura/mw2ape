package br.usp.poli.lta.cereda.execute.NLP;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


import org.yaml.snakeyaml.Yaml;

public class NLPDictionary {

    HashMap<String, ArrayList<ArrayList<String>>> dictionaryHashMap;
    String yamlDictionaryFileName;

    public NLPDictionary(String yamlDictionaryFileName) {
        this.yamlDictionaryFileName = yamlDictionaryFileName;
        LoadYamlDictionary();
    }

    public Boolean LoadYamlDictionary()
    {
        Boolean result = false;
        File inputFile = null;
        Yaml yamlDictionary = new Yaml();
        try {
            inputFile = new File(this.yamlDictionaryFileName);

            if (!inputFile.exists()) {
                throw new Exception("The provided nlpDictionary file " +
                        yamlDictionaryFileName + " does not exist. " +
                        "Make sure the location is correct and try again.");
            } else {
                if (inputFile != null) {
                    InputStream yamlStream = new FileInputStream(inputFile);
                    dictionaryHashMap = (HashMap<String, ArrayList<ArrayList<String>>>)yamlDictionary.load(yamlStream);
                    result = true;
                }
            }
        }
        catch (Exception exception) {
            System.out.println("An exception was thrown while loading the NLP nlpDictionary.");
            System.out.println(exception.toString());
        }

        return result;
    }

    public ArrayList<NLPDictionaryEntry> getEntry(String word) {
        ArrayList<NLPDictionaryEntry> nlpDictionaryEntries = new ArrayList<>();
        if (word != "") {
            ArrayList<ArrayList<String>> result = this.dictionaryHashMap.get(word);
            for (ArrayList<String> tempEntry: result) {
                NLPDictionaryEntry nlpDictionaryEntry = new NLPDictionaryEntry(
                    tempEntry.get(0),tempEntry.get(1),tempEntry.get(2),
                    tempEntry.get(3),tempEntry.get(4),tempEntry.get(5)
                );
                nlpDictionaryEntries.add(nlpDictionaryEntry);
            }
        }
        return nlpDictionaryEntries;
    }

    public HashMap<String, ArrayList<ArrayList<String>>> getDictionaryHashMap() {
        return dictionaryHashMap;
    }
}
