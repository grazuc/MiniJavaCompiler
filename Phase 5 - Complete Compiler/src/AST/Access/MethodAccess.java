package AST.Access;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import InstructionGenerator.*;
import java.io.IOException;
import java.util.ArrayList;

public class MethodAccess extends AccessNode {

    ArrayList<ExpressionNode> expressionNodesList;
    private MethodOrConstructor method;

    public MethodAccess(Token token, ArrayList<ExpressionNode> expressionNodesList) {
        super(token);
        this.expressionNodesList = expressionNodesList;
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        ConcreteClass concreteClass = (ConcreteClass) SymbolTable.getInstance().getCurrentClass();
        if (!concreteClass.getMethods().containsKey(this.token.getLexeme()))
            throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es un de la clase " + concreteClass.getClassName());
        method = concreteClass.getMethods().get(this.token.getLexeme());
        //System.out.println("Metodo: "+method.getMethodName());
        //System.out.println("Tipo de retorno: "+method.getReturnType());
        //System.out.println("Es primitivo su retorno?"+method.getReturnType().isPrimitive());
        if (SymbolTable.getInstance().getCurrentMethod().getStaticHeader().equals("static") && !method.getStaticHeader().equals("static"))
            throw new SemanticExceptionSimple(this.token, "no se puede llamar a un metodo dinamico dentro de un metodo con alcance estatico");
        if (method.getParametersList().size() > 0)
            this.checkArguments(method);
        if (this.encadenado == null)
            return method.getReturnType();
        else
            if (!method.getReturnType().isPrimitive())
                return this.encadenado.check(method.getReturnType());
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
    public void generateCode() throws IOException {
        if (this.method.getStaticHeader().equals("static")) {
            this.generateCodeForStaticMethod();
        } else
            this.generateCodeForDynamicMethod();

        if (this.encadenado != null)
            encadenado.generateCode();
    }

    private void generateCodeForStaticMethod() throws IOException {
        if (!this.method.getReturnType().getClassName().equals("void"))
            InstructionGenerator.getInstance().generateInstruction("RMEM 1 ; Se reserva lugar para el valor de retorno del metodo");
        this.generateParametersCode();
        InstructionGenerator.getInstance().generateInstruction("PUSH " + this.method.getMethodLabel());
        InstructionGenerator.getInstance().generateInstruction("CALL");
    }

    private void generateCodeForDynamicMethod() throws IOException {
        InstructionGenerator.getInstance().generateInstruction("LOAD 3        ; Se apila el this");

        if (!this.method.getReturnType().getClassName().equals("void")) {
            InstructionGenerator.getInstance().generateInstruction("RMEM 1 ; Se reserva lugar para el valor de retorno del metodo");
            InstructionGenerator.getInstance().generateInstruction("SWAP ; Muevo this");
        }

        this.generateParametersCode();

        InstructionGenerator.getInstance().generateInstruction("DUP ; Se duplica el this porque al hacer LOADREF lo pierdo");
        InstructionGenerator.getInstance().generateInstruction("LOADREF 0 ; Se carga la VT");
        InstructionGenerator.getInstance().generateInstruction("LOADREF " + method.getOffset());
        InstructionGenerator.getInstance().generateInstruction("CALL");
    }

    private void generateParametersCode() throws IOException{
        if (expressionNodesList != null){
            for (ExpressionNode p : expressionNodesList){
                p.generateCode();
                if (!method.getStaticHeader().equals("static")){
                    InstructionGenerator.getInstance().generateInstruction("SWAP ; Muevo this");  //para que el this quede en el tope
                }
            }
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
