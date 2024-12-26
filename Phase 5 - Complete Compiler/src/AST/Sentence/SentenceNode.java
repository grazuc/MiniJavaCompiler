package AST.Sentence;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;

import java.io.IOException;

public abstract class SentenceNode {

    protected Token token;

    public SentenceNode(Token token) {
        this.token = token;
    }

    public abstract void check() throws SemanticExceptionSimple;

    protected abstract void generateCode() throws IOException;

    protected abstract boolean isVariableDeclaration();

}
