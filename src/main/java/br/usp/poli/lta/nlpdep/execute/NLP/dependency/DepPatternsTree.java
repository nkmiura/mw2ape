package br.usp.poli.lta.nlpdep.execute.NLP.dependency;

import br.usp.poli.lta.nlpdep.execute.NLP.StructuredPushdownAutomatonNLP;
import br.usp.poli.lta.nlpdep.execute.NLP.output.NLPOutputToken;
import br.usp.poli.lta.nlpdep.execute.NLP.output.Node;
import br.usp.poli.lta.nlpdep.mwirth2ape.structure.Stack;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;

public class DepPatternsTree {
    private static final Logger logger = LoggerFactory.getLogger(DepParseTree.class);

    private Stack<String> stack = new Stack<>();

    private StructuredPushdownAutomatonNLP spaNLP;

    private JsonObject depJsnObj = new JsonObject();  // Dados gerais
    private JsonArray depPtrnsJsnArray = new JsonArray();  // Relações de dependências
    private JsonObject depSglPtrnJsnObj;  // Uma relação de dependência
    private JsonArray depPtrnCnstntsJsnArray;  // Lista de constituintes de uma relação
    private JsonObject depSglCnstntJsnObj;  // Single constituent

    public DepPatternsTree(StructuredPushdownAutomatonNLP spaNLP) {
        this.spaNLP = spaNLP;
    }

    public JsonObject parsePreorderFromLeaf() {
        boolean result = false;
        long threadId = Thread.currentThread().getId();

        Path path = Paths.get(spaNLP.getAppProperties().getProperty("inputFileName"));

        depJsnObj.addProperty("File", FilenameUtils.removeExtension(path.getFileName().getName(0).toString()) +"_" +
                Thread.currentThread().getName() + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(("yyyyMMdd-HHmmss"))) + ".json");

        depJsnObj.addProperty("ThreadName", Thread.currentThread().getName());
        depJsnObj.addProperty("Date", LocalDateTime.now().format(DateTimeFormatter.ofPattern(("yyyy-MM-dd HH:mm:ss"))));

        /*
        // verifica se o nó é nulo 2019.02.24
        if (((DepStackElementNterm)(this.spaNLP.getDepStackList().getDepStackFromThreadID(threadId).top())).getNode() == null) {
            // Pergunta ao usuário se continua
            Scanner userInput = new Scanner(System.in);

            String input = "y";   // teste forçado, decomentar abaixo e tirar para forçar interação
            while (!(input.equals("y") || input.equals("n"))) {
                System.out.println(Thread.currentThread().getName() + ": Termina thread? (y ou n)");
                input = userInput.nextLine();
            }
            if (input.equals("y")) {
                return null;
            }
        } else {   */
            Node<NLPOutputToken> rootNode =
                    ((DepStackElementNterm) (this.spaNLP.getDepStackList().getDepStackFromThreadID(threadId).top())).getNode();

            result = parsePreorder(rootNode);

            depJsnObj.add("DepPatterns", depPtrnsJsnArray);
            //patternsOutput.append(depJsnObj);
        /*
        } */
        if (result) {
            return depJsnObj;
        } else {
            return null;
        }

    }

    final boolean parsePreorder (Node<NLPOutputToken> node) {
        boolean result = true;

        if (node.getData().getType().equals("nterm")) {
            NLPOutputToken currentNlpOutputToken = node.getData();
            logger.debug("DepGetPattern processando nterm {}",
                    currentNlpOutputToken.getValue());

            depSglPtrnJsnObj  = new JsonObject();
            depSglPtrnJsnObj.addProperty("_comment","");
            depSglPtrnJsnObj.addProperty("value",currentNlpOutputToken.getValue());
            //depSglPtrnJsnObj.addProperty("mainConstituent","");
            depSglPtrnJsnObj.addProperty("headDirection","");

            depPtrnCnstntsJsnArray = new JsonArray();
            int counter = 1;
            for (Node<NLPOutputToken> currentChild: node.getChildren()) {
                depSglCnstntJsnObj = new JsonObject();
                depSglCnstntJsnObj.addProperty("id", counter);
                depSglCnstntJsnObj.addProperty("value", currentChild.getData().getValue());
                depSglCnstntJsnObj.addProperty("type", currentChild.getData().getType());
                depSglCnstntJsnObj.addProperty("head","");
                depSglCnstntJsnObj.addProperty("depRel","");
                JsonArray leftDeps = new JsonArray();
                JsonArray rightDeps = new JsonArray();
                depSglCnstntJsnObj.add("leftDeps", leftDeps);
                depSglCnstntJsnObj.add("rightDeps", rightDeps);

                depPtrnCnstntsJsnArray.add(depSglCnstntJsnObj);
                counter++;
            }
            if (depPtrnCnstntsJsnArray.size() == 1) {
                depSglPtrnJsnObj.addProperty("mainConstituent",1);
            } else {
                depSglPtrnJsnObj.addProperty("mainConstituent","");
            }
            depSglPtrnJsnObj.add("depConstituents",depPtrnCnstntsJsnArray);

        } else {
            logger.debug("DepGetPattern não é nterm! É {}", node.getData().getType());
        }

        depPtrnsJsnArray.add(depSglPtrnJsnObj);
        for (Node<NLPOutputToken> currentChild: node.getChildren()) {
            if (!parsePreorder(currentChild)) {
                result = false;
                break;
            }
        }

        return result;
    }

}
