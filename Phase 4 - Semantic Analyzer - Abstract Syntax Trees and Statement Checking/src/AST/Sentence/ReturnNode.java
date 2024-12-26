package AST.Sentence;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;

public class ReturnNode extends SentenceNode {

    private ExpressionNode expressionNode;

    public ReturnNode(Token token, ExpressionNode expressionNode) {
        super(token);
        this.expressionNode = expressionNode;
    }

    @Override
    public void check() throws SemanticExceptionSimple {
        Type expressionType = this.expressionNode.check();
        MethodOrConstructor method = SymbolTable.getInstance().getCurrentMethod();
        Type returnMethodType = method.getReturnType();
        if (method.isConstructor()){
            throw new SemanticExceptionSimple(token,"Un constructor no puede tener retorno");
        }
        //System.out.println("Metodo o constructor con nombre: "+method.getToken().getLexeme());
        //System.out.println("Es constructor? "+method.isConstructor());
        if (expressionType == null && !returnMethodType.getClassName().equals("void"))
                throw new SemanticExceptionSimple(this.token, "El metodo debe retornar una expresion de tipo " + returnMethodType.getClassName());
        if (expressionType != null) {
            if (!expressionType.isCompatibleWithType(returnMethodType))
                if (!returnMethodType.getClassName().equals("void"))
                    throw new SemanticExceptionSimple(this.token, "El metodo debe retornar una expresion de tipo " + returnMethodType.getClassName());
                else
                    throw new SemanticExceptionSimple(this.token, "El metodo no tiene retorno");
        }
    }

}
