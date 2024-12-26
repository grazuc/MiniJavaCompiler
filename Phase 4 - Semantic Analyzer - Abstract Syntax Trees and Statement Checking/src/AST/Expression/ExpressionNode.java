package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;

public abstract class ExpressionNode {

    protected Token token;

    public ExpressionNode(Token token){
        this.token = token;
    }

    public abstract Type check() throws SemanticExceptionSimple;

    public Token getToken() {
        return this.token;
    }

}
