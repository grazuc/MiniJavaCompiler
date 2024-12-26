package AST.Access;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;

import java.io.IOException;

public class ParenthesizedExpressionNode extends AccessNode {

    protected ExpressionNode expression;

    public ParenthesizedExpressionNode(Token token, ExpressionNode expression) {
        super(token);
        this.expression = expression;
    }

    public Type check() throws SemanticExceptionSimple {
        Type expressionType = this.expression.check();
        if (this.encadenado != null){
            if (expressionType.isPrimitive()){
                throw new SemanticExceptionSimple(expression.getToken(), "El lado izquierdo del encadenado es un tipo primitivo");
            }
            return this.encadenado.check(expressionType);
        }
        return expressionType;
    }

    @Override
    public void generateCode() throws IOException {
        this.expression.generateCode();
        if (this.encadenado != null)
            this.encadenado.generateCode();
    }

    public void setIsAssignable(){
        isAssignable = true;
    }
    @Override
    public boolean isAssignable() {
        return isAssignable;
    }

    @Override
    public boolean isCallable() {
        return false;
    }
}
