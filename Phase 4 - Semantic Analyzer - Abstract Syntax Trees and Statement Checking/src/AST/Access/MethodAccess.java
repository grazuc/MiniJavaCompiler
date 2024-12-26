package AST.Access;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import java.util.ArrayList;

public class MethodAccess extends AccessNode {

    ArrayList<ExpressionNode> expressionNodesList;

    public MethodAccess(Token token, ArrayList<ExpressionNode> expressionNodesList) {
        super(token);
        this.expressionNodesList = expressionNodesList;
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        ConcreteClass concreteClass = (ConcreteClass) SymbolTable.getInstance().getCurrentClass();
        if (!concreteClass.getMethods().containsKey(this.token.getLexeme()))
            throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es un de la clase " + concreteClass.getClassName());
        MethodOrConstructor method = concreteClass.getMethods().get(this.token.getLexeme());
        if (SymbolTable.getInstance().getCurrentMethod().getStaticHeader().equals("static") && !method.getStaticHeader().equals("static"))
            throw new SemanticExceptionSimple(this.token, "no se puede llamar a un metodo dinamico dentro de un metodo con alcance estatico");
        if (method.getParametersList().size() > 0)
            this.checkArguments(method);
        if (this.encadenado == null)
            return method.getReturnType();
        else
            if (!concreteClass.getMethods().get(this.token.getLexeme()).getReturnType().isPrimitive())
                return this.encadenado.check(concreteClass.getMethods().get(this.token.getLexeme()).getReturnType());
            else
                throw new SemanticExceptionSimple(this.token, "el metodo " + this.token.getLexeme() + " retorna un tipo primitivo y tiene un encadenado");
    }

    private void checkArguments(MethodOrConstructor method) throws SemanticExceptionSimple {
        if (this.expressionNodesList == null || this.expressionNodesList.size() != method.getParametersList().size())
            throw new SemanticExceptionSimple(this.token, "metodo mal invocado, la cantidad de parametros es incorrecta");
        ArrayList<Parameter> parametersList = method.getParametersList();
        Type parameterType;
        Type expressionType;
        int index = 0;
        for (ExpressionNode expressionNode: this.expressionNodesList) {
            parameterType = parametersList.get(index).getParameterType();
            expressionType = expressionNode.check();
            index += 1;
            if (!expressionType.isCompatibleWithType(parameterType))
                throw new SemanticExceptionSimple(this.token, "tipos incompatibles en el pasaje de parametros");
        }
    }


    @Override
    public boolean isAssignable() {
        return this.encadenado != null;
    }

    @Override
    public boolean isCallable() {
        return true;
    }
}
