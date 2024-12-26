package AST.Sentence;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;

public abstract class SentenceNode {

    protected Token token;

    public SentenceNode(Token token) {
        this.token = token;
    }

    public abstract void check() throws SemanticExceptionSimple;

}
