package AST.Sentence;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;

import java.io.IOException;

public class EmptySentence extends SentenceNode {

    public EmptySentence(Token token) {
        super(token);
    }

    @Override
    public void check() throws SemanticExceptionSimple {
    }

    public boolean isVariableDeclaration() {return false;}

    @Override
    protected void generateCode() throws IOException {

    }
}
