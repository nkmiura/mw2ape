package br.usp.poli.lta.nlpdep.execute.NLP.dependency;

import br.usp.poli.lta.nlpdep.execute.NLP.StructuredPushdownAutomatonNLP;
import br.usp.poli.lta.nlpdep.execute.NLP.output.NLPOutputToken;
import br.usp.poli.lta.nlpdep.execute.NLP.output.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DepParseTree {
    private static final Logger logger = LoggerFactory.getLogger(DepParseTree.class);

    private StructuredPushdownAutomatonNLP spaNLP;

    public DepParseTree(StructuredPushdownAutomatonNLP spaNLP) {
        this.spaNLP = spaNLP;
    }

    public boolean parsePreorderFromLeaf(StringBuilder conlluOutput) {
        boolean result = false;
        long threadId = Thread.currentThread().getId();

        Node<NLPOutputToken> rootNode =
                ((DepStackElementNterm)(this.spaNLP.getDepStackList().getDepStackFromThreadID(threadId).top())).getNode();

        result = parsePreorder(rootNode, conlluOutput);

        return result;
    }


    public boolean parseFromRootDep() {
        boolean result = false;

        return result;
    }

    final boolean parsePreorder (Node<NLPOutputToken> node, StringBuilder conlluOutput) {
        boolean result = true;

        if (node.getData().getType().equals("term")) {
            NLPOutputToken currentNlpOutputToken = node.getData();
            logger.debug("DepParseTree processando term {} ({}) idSentence: {} head: {} depRel: {}",
                    currentNlpOutputToken.getValue(), currentNlpOutputToken.getNlpWord(),currentNlpOutputToken.getIdSentence(),
                    currentNlpOutputToken.getHead(), currentNlpOutputToken.getDepRel());
            // Obtem dados do nterm pai
            NLPOutputToken parentNlpOutputToken = node.getParent().getData();
            // obtem posicao no nterm pai
            int myPosition = node.getParent().getChildren().indexOf(node);


            HashMap<String,ArrayList<Node>> depRelNodesMap = new HashMap<>(); // Nós LCA
            HashMap<String,ArrayList<Node>> headCandidateNodesMap = new HashMap<>(); // Nós candidatos


            // para cada padrao no nterm pai verifica dep e obtem lista de possíveis nós LCA
            for (DepPattern currentDepPattern: parentNlpOutputToken.getDepPatternArrayList()) {
                DepPatternConstituent depPatternConstituent = currentDepPattern.getDepPatternConstituents().get(myPosition);
                String currentDepRel = depPatternConstituent.getDepRel();
                ArrayList<Node> currentLcaNodes = new ArrayList<>(); // nós LCA
                ArrayList<Node> headCandidateNodes = new ArrayList<>(); // Nòs candidatos

                if (depPatternConstituent.getHead() != 0) { // Head está abaixo do nterm pai
                    currentLcaNodes.add(node.getParent());
                    logger.debug("DepParseTree depRel: {} - encontrado nó LCA {}.", currentDepRel, node.getParent().getData().getValue());
                    if (((currentDepPattern.getHeadDirection().equals("left")) && (depPatternConstituent.getHead() < (myPosition + 1))) ||
                            ((currentDepPattern.getHeadDirection().equals("right")) && (depPatternConstituent.getHead() > (myPosition + 1))) ||
                            (currentDepPattern.getHeadDirection().equals(""))) { // verifica direção e posição
                        Node<NLPOutputToken> candidateNode = node.getParent().getChildren().get(depPatternConstituent.getHead()-1);
                        logger.debug("DepParseTree encontrei possível nó candidato {} ({})", depPatternConstituent.getType(),
                                depPatternConstituent.getValue());
                        setOrSearchHeadCandidates(currentDepRel, candidateNode, headCandidateNodes);
                    }
                } else if (currentDepRel.equals("root")) {
                    currentLcaNodes.add(node.getParent());
                    headCandidateNodesMap.put(currentDepRel, headCandidateNodes);  // insere lista vazia para raiz
                } else if (currentDepPattern.getMainConstituent() == (myPosition+1)) { // Head está acima do nterm pai
                    getLCA(currentDepRel,node.getParent(),currentLcaNodes, headCandidateNodes);
                }
                if (currentLcaNodes.size() != 0) {
                    depRelNodesMap.put(currentDepRel, currentLcaNodes);
                    //logger.debug("DepParseTree depRel: {} - encontrados {} nós LCA.", currentDepRel, depRelNodesMap.size());
                }
                if (headCandidateNodes.size() != 0) {
                    headCandidateNodesMap.put(currentDepRel,headCandidateNodes);
                }
            }
            // Define o head

            //ArrayList<Node> headCandidateNodes = new ArrayList<>();
            for (HashMap.Entry<String, ArrayList<Node>> entry :headCandidateNodesMap.entrySet()) {
                logger.debug(" # DepParseTree depRel: {} - Qtd nós candidatos a head {}.", entry.getKey(), entry.getValue().size());
                if (entry.getValue().size() > 1) {
                    HashSet<Node> originalSet = new HashSet<>(entry.getValue());
                    ArrayList<Node> uniqueSet = new ArrayList<>(originalSet);
                    entry.getValue().clear();
                    entry.getValue().addAll(uniqueSet);
                    logger.debug(" # DepParseTree depRel: {} - Qtd nós únicos candidatos a head {}.", entry.getKey(), entry.getValue().size());
                }
            }

            if (headCandidateNodesMap.size() == 1) {
                String depRelUnique = headCandidateNodesMap.keySet().stream().findFirst().get();

                if (headCandidateNodesMap.get(depRelUnique).size() <= 1) { // 0 se root, 1 se for único
                    currentNlpOutputToken.setDepRel(depRelUnique);
                    if (depRelUnique.equals("root")) {
                        currentNlpOutputToken.setHead(0);
                    } else {
                        NLPOutputToken headNLPToken = ((Node<NLPOutputToken>)headCandidateNodesMap.get(depRelUnique).get(0)).getData();
                        currentNlpOutputToken.setHead(headNLPToken.getIdSentence());
                    }
                }
            }

            logger.debug("DepParseTree finalizado term {} ({}) idSentence: {} head: {} depRel: {}\n",
                    currentNlpOutputToken.getValue(), currentNlpOutputToken.getNlpWord(),currentNlpOutputToken.getIdSentence(),
                    currentNlpOutputToken.getHead(), currentNlpOutputToken.getDepRel());
            conlluOutput.append(currentNlpOutputToken.getIdSentence() + "\t" + currentNlpOutputToken.getNlpWord() + "\t" +
                    currentNlpOutputToken.getNlpDictionaryEntry().getCanonical() + "\t" +
                    currentNlpOutputToken.getNlpDictionaryEntry().getPosTag() + "\t" +
                    currentNlpOutputToken.getNlpDictionaryEntry().getAttributes() + "\t" +
                    currentNlpOutputToken.getHead() + "\t" + currentNlpOutputToken.getDepRel() + "\n");

        }

        for (Node<NLPOutputToken> currentChild: node.getChildren()) {
            if (!parsePreorder(currentChild, conlluOutput)) {
                result = false;
                break;
            }
        }


        return result;
    }

    // Obtem o lowest common ancestor para a relação de dependência depRel
    void getLCA (String depRel, Node<NLPOutputToken> node, ArrayList<Node> lcaNodes, ArrayList<Node> headCandidateNodes) {
        //boolean result = true;
        int myPosition = node.getParent().getChildren().indexOf(node);
        for (DepPattern currentDepPattern: node.getParent().getData().getDepPatternArrayList()) {
            DepPatternConstituent depPatternConstituent = currentDepPattern.getDepPatternConstituents().get(myPosition);

            if ((depPatternConstituent.getHead() != 0) && (depPatternConstituent.getDepRel().equals(depRel))) { // Head está abaixo do nterm pai
                lcaNodes.add(node.getParent());
                logger.debug("DepParseTree depRel: {} - encontrado nó LCA {}.", depRel, node.getParent().getData().getValue());
                Node<NLPOutputToken> candidateNode = node.getParent().getChildren().get(depPatternConstituent.getHead()-1);
                setOrSearchHeadCandidates(depRel, candidateNode, headCandidateNodes);


            } else if (currentDepPattern.getMainConstituent() == (myPosition+1)) { // Head está acima do nterm pai
                getLCA(depRel,node,lcaNodes, headCandidateNodes);
            }
        }
        //return result;
    }

    void getHeadCandidateNodes (String depRel, Node<NLPOutputToken> node, ArrayList<Node> headCandidateNodes) {
        for (DepPattern depPattern: node.getData().getDepPatternArrayList()) { // loop para cada DepPattern
            for (DepPatternConstituent depPatternConstituent: depPattern.getDepPatternConstituents()) { // Para cada constituinte
                // para cada leftDeps
                for (String currentDepRel: depPatternConstituent.getLeftDeps()) {
                    if (currentDepRel.equals(depRel)) { // encontrei
                        logger.debug("DepParseTree encontrei possível nó candidato {} ({})", depPatternConstituent.getType(),
                                depPatternConstituent.getValue());
                        //Node<NLPOutputToken> candidateNode = node.getChildren().get(depPatternConstituent.getId()-1);
                        setOrSearchHeadCandidates(currentDepRel,
                                node.getChildren().get(depPatternConstituent.getId()-1), headCandidateNodes);
                    }
                }
                // para cada rightDeps
                for (String currentDepRel: depPatternConstituent.getRightDeps()) {
                    if (currentDepRel.equals(depRel)) { // encontrei
                        logger.debug("DepParseTree encontrei possível nó candidato {} ({})", depPatternConstituent.getType(),
                                depPatternConstituent.getValue());
                        setOrSearchHeadCandidates(currentDepRel,
                                node.getChildren().get(depPatternConstituent.getId()-1), headCandidateNodes);
                    }
                }
            }
        }
    }

    private void setOrSearchHeadCandidates(String currentDepRel, Node<NLPOutputToken> candidateNode, ArrayList<Node> headCandidateNodes) {
        if (candidateNode.getData().getType().equals("term")) { // nó é term
            headCandidateNodes.add(candidateNode);
            logger.debug("DepParseTree nó candidato: {} ({})",candidateNode.getData().getValue(),
                    candidateNode.getData().getNlpWord());
        } else { // nó é nterm
            getHeadCandidateNodes(currentDepRel,candidateNode,headCandidateNodes);
        }
    }

}
