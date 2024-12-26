package AST.Encadenado;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import SemanticAnalyzer.Class;

import java.util.ArrayList;

public class LlamadaEncadenada extends Encadenado {

    private ArrayList<ExpressionNode> expressionNodesList;

    public LlamadaEncadenada(Token token, ArrayList<ExpressionNode> expressionNodesList) {
        super(token);
        this.expressionNodesList = expressionNodesList;
    }

    @Override
    public Type check(Type leftSideType) throws SemanticExceptionSimple {
        Type accessMethodType;
        Class classOrInterface = SymbolTable.getInstance().getClass(leftSideType.getClassName());
        if (!classOrInterface.getMethods().containsKey(this.token.getLexeme()))
            throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es metodo un de " + classOrInterface.getClassName());
        else {
            MethodOrConstructor method = classOrInterface.getMethods().get(this.token.getLexeme());
            if (method.getParametersList().size() > 0 || this.expressionNodesList != null)
                this.checkArguments(method);
            accessMethodType = method.getReturnType();
            if (this.encadenado != null) {
                if (accessMethodType.isPrimitive())
                    throw new SemanticExceptionSimple(this.token, "el metodo " + "\"" + this.token.getLexeme() + "\"" + " debe retornar un tipo no primitivo porque tiene un encadenado");
                return this.encadenado.check(accessMethodType);
            }
        }
        return accessMethodType;
    }

    @Override
    public boolean isAssignable() {
        return false;
    }

    @Override
    public boolean isCallable() {
        return true;
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
            if (!parameterType.isCompatibleWithType(expressionType))
                throw new SemanticExceptionSimple(this.token, "tipos incompatibles en el pasaje de parametros");
        }
    }

}
