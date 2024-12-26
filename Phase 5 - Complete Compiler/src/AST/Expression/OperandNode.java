package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;

public abstract class OperandNode extends ExpressionNode {

    public OperandNode(Token token) {
        super(token);
    }

    public Token getToken() {
        return this.token;
    }

    public abstract Type check() throws SemanticExceptionSimple;
}
