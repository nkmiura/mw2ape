package br.usp.poli.lta.cereda.execute.NLP;

import br.usp.poli.lta.cereda.mwirth2ape.lexer.Lexer;
import br.usp.poli.lta.cereda.mwirth2ape.model.Token;
import java.util.LinkedList;

public class NLPLexer extends Lexer {

    NLPDictionary dictionary;
    LinkedList<NLPToken> nlpTokens;

    public NLPLexer(String input, String yamlDictionaryFileName) {
        super(input);
        dictionary = new NLPDictionary(yamlDictionaryFileName);
        nlpTokens = new LinkedList<>();
        splitPrepDetUnion(input);
    }

    public boolean hasNext() {
        return !this.nlpTokens.isEmpty() || !bufferIsEmpty();
    }

    @Override
    public Token recognize() {
        Token newToken = new Token();
        newToken.setNlpToken(this.nlpTokens.pop());
        return newToken;
    }

    private void splitPrepDetUnion(String sentence) {
        String processedSentence = sentence;
        processedSentence = processedSentence.replaceAll(" à ", " a a ");
        processedSentence = processedSentence.replaceAll("À ", "A a ");
        processedSentence = processedSentence.replaceAll(" ao ", " a o ");
        processedSentence = processedSentence.replaceAll("Ao ", "A o ");
        processedSentence = processedSentence.replaceAll(" do ", " de o ");
        processedSentence = processedSentence.replaceAll(" dos ", " de os ");
        processedSentence = processedSentence.replaceAll("Do ", "De o ");
        processedSentence = processedSentence.replaceAll("Dos ", "De os ");
        processedSentence = processedSentence.replaceAll(" da ", " de a ");
        processedSentence = processedSentence.replaceAll(" das ", " de as ");
        processedSentence = processedSentence.replaceAll("Da ", "De a ");
        processedSentence = processedSentence.replaceAll("Das ", "De as ");
        processedSentence = processedSentence.replaceAll(" no ", " em+ o ");
        processedSentence = processedSentence.replaceAll(" nos ", " em+ os ");
        processedSentence = processedSentence.replaceAll("No ", "Em+ o ");
        processedSentence = processedSentence.replaceAll("Nos ", "Em+ os ");
        processedSentence = processedSentence.replaceAll(" na ", " em+ a ");
        processedSentence = processedSentence.replaceAll(" numa ", " em+ uma ");
        processedSentence = processedSentence.replaceAll(" numas ", " em+ umas ");
        processedSentence = processedSentence.replaceAll(" nas ", " em+ as ");
        processedSentence = processedSentence.replaceAll("Na ", "Em+ a ");
        processedSentence = processedSentence.replaceAll("Nas ", "Em+ as ");
        processedSentence = processedSentence.replaceAll("Numa ", "Em+ uma ");
        processedSentence = processedSentence.replaceAll("Numas ", "Em+ umas ");
        processedSentence = processedSentence.replaceAll(" àquela ", " a_  aquela ");
   }
}
