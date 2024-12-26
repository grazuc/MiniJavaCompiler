package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.PrimitiveType;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;

public class EmptyExpressionNode extends ExpressionNode {

    public EmptyExpressionNode(Token token) {
        super(token);
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        return null;
    }

}
