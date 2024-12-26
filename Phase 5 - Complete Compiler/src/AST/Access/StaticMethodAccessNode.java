package AST.Access;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import InstructionGenerator.*;
import java.io.IOException;
import java.util.ArrayList;

public class StaticMethodAccessNode extends AccessNode {

    protected Token methodNameToken;
    protected ArrayList<ExpressionNode> expressionNodesList;
    private MethodOrConstructor staticMethod;

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
        staticMethod = concreteClass.getMethod(this.methodNameToken.getLexeme());
        if (staticMethod == null)
            throw new SemanticExceptionSimple(this.methodNameToken, "El metodo " + this.methodNameToken.getLexeme() + " no esta declarado en la clase " + concreteClass.getClassName());
        if (!staticMethod.getStaticHeader().equals("static"))
            throw new SemanticExceptionSimple(this.methodNameToken, "El metodo " + this.methodNameToken.getLexeme() + " no tiene alcance estatico");
        Type staticMethodType = staticMethod.getReturnType();
        if (staticMethod.getParametersList().size() > 0 || this.expressionNodesList != null)
            this.checkArguments(staticMethod);
        if (this.encadenado != null) {
            if (staticMethodType.isPrimitive())
                throw new SemanticExceptionSimple(this.methodNameToken, "El metodo " + this.methodNameToken.getLexeme() + " retorna un tipo primitivo y tiene un encadenado");
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
            if (!parameterType.getClassName().equals(expressionType.getClassName())){
                if (!parameterType.isCompatibleWithType(expressionType))
                    throw new SemanticExceptionSimple(this.methodNameToken, "tipos incompatibles en el pasaje de parametros");
            }
        }
    }

    public void generateCode() throws IOException {
        if (!this.staticMethod.getReturnType().getClassName().equals("void"))
            InstructionGenerator.getInstance().generateInstruction("RMEM 1 ; Se reserva lugar para el retorno");

        this.generateParametersCode();

        InstructionGenerator.getInstance().generateInstruction("PUSH " + this.staticMethod.getMethodLabel());
        InstructionGenerator.getInstance().generateInstruction("CALL");

        if (this.encadenado != null)
            encadenado.generateCode();
    }

    private void generateParametersCode2() throws IOException {
        if (this.expressionNodesList != null)
            for (int index = this.expressionNodesList.size() - 1; index >= 0; index--)
                this.expressionNodesList.get(index).generateCode();  //genero codigo de cada parametro
    }

    private void generateParametersCode() throws IOException{
        if (expressionNodesList != null){
            for (ExpressionNode p : expressionNodesList){
                p.generateCode();
            }
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
