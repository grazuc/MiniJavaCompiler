package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.PrimitiveType;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;

import java.io.IOException;

public class EmptyExpressionNode extends ExpressionNode {

    public EmptyExpressionNode(Token token) {
        super(token);
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        return null;
    }

    @Override
    public void generateCode() throws IOException {

    }

}
