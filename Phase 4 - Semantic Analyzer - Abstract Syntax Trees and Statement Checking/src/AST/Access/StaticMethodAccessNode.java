package AST.Access;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;

import java.util.ArrayList;

public class StaticMethodAccessNode extends AccessNode {

    protected Token methodNameToken;
    protected ArrayList<ExpressionNode> expressionNodesList;

    public StaticMethodAccessNode(Token classNameToken, Token methodNameToken, ArrayList<ExpressionNode> expressionNodesList) {
        super(classNameToken);
        this.methodNameToken = methodNameToken;
        this.expressionNodesList = expressionNodesList;
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        ConcreteClass concreteClass = SymbolTable.getInstance().getConcreteClass(this.token.getLexeme());
        if (concreteClass == null)
            throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es una clase concreta declarada");
        MethodOrConstructor staticMethod = concreteClass.getMethod(this.methodNameToken.getLexeme());
        if (staticMethod == null)
            throw new SemanticExceptionSimple(this.methodNameToken, "El metodo " + this.methodNameToken.getLexeme() + " no esta declarado en la clase " + concreteClass.getClassName());
        if (!staticMethod.getStaticHeader().equals("static"))
            throw new SemanticExceptionSimple(this.methodNameToken, "El metodo " + this.methodNameToken.getLexeme() + " no tiene alcance estatico");
        Type staticMethodType = staticMethod.getReturnType();
        if (staticMethod.getParametersList().size() > 0 || this.expressionNodesList != null)
            this.checkArguments(staticMethod);
        if (this.encadenado != null) {
            if (staticMethodType.isPrimitive())
                throw new SemanticExceptionSimple(this.methodNameToken, "El metodo " + this.methodNameToken.getLexeme() + " retorna un tipo primitivo y un tiene encadenado");
            else
                return this.encadenado.check(staticMethodType);
        }
        return staticMethodType;
    }

    private void checkArguments(MethodOrConstructor method) throws SemanticExceptionSimple {
        if (this.expressionNodesList == null || this.expressionNodesList.size() != method.getParametersList().size())
            throw new SemanticExceptionSimple(this.methodNameToken, "metodo mal invocado, la cantidad de parametros es incorrecta");
        ArrayList<Parameter> parametersList = method.getParametersList();
        Type parameterType;
        Type expressionType;
        int index = 0;
        for (ExpressionNode expressionNode: this.expressionNodesList) {
            parameterType = parametersList.get(index).getParameterType();
            expressionType = expressionNode.check();
            index += 1;
            if (!parameterType.isCompatibleWithType(expressionType))
                throw new SemanticExceptionSimple(this.methodNameToken, "tipos incompatibles en el pasaje de parametros");
        }
    }

    @Override
    public boolean isAssignable() {
        return false;
    }

    @Override
    public boolean isCallable() {
        return true;
    }
}
