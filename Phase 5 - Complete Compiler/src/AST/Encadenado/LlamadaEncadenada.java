package AST.Encadenado;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import SemanticAnalyzer.Class;
import InstructionGenerator.*;
import java.io.IOException;
import java.util.ArrayList;

public class LlamadaEncadenada extends Encadenado {

    private ArrayList<ExpressionNode> expressionNodesList;
    private MethodOrConstructor method;
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
            method = classOrInterface.getMethods().get(this.token.getLexeme());
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

    public void generateCode() throws IOException {
        if (this.method.getStaticHeader().equals("static"))
            this.generateStaticMethodCode();
        else
            this.generateDynamicMethodCode();

        if (this.encadenado != null)
            this.encadenado.generateCode();
    }

    private void generateStaticMethodCode() throws IOException {
        //Se descarta la referencia en el tope de la pila ya que no se necesita para la llamada encadenada a un metodo estatico
        InstructionGenerator.getInstance().generateInstruction("POP");
        if (!this.method.getReturnType().getClassName().equals("void"))
            InstructionGenerator.getInstance().generateInstruction("RMEM 1 ; Se reserva lugar para el retorno");

        this.generateParametersCode();

        InstructionGenerator.getInstance().generateInstruction("PUSH " + this.method.getMethodLabel());
        InstructionGenerator.getInstance().generateInstruction("CALL");
    }

    private void generateDynamicMethodCode() throws IOException {
        if (!method.getReturnType().getClassName().equals("void")) {
            InstructionGenerator.getInstance().generateInstruction("RMEM 1 ; Se reserva lugar para el valor de retorno del metodo");
            InstructionGenerator.getInstance().generateInstruction("SWAP");
        }

        this.generateParametersCode();

        InstructionGenerator.getInstance().generateInstruction("DUP ; Se duplica el this porque al hacer LOADREF se pierde");
        InstructionGenerator.getInstance().generateInstruction("LOADREF 0 ; Se carga la VT");
        InstructionGenerator.getInstance().generateInstruction("LOADREF " + method.getOffset() + " ; Se carga la dirección del metodo en la VT");
        InstructionGenerator.getInstance().generateInstruction("CALL");
    }

    private void generateParametersCode() throws IOException{
        if (expressionNodesList != null){
            for (ExpressionNode p : expressionNodesList){
                p.generateCode();
                if (!method.getStaticHeader().equals("static")){
                    InstructionGenerator.getInstance().generateInstruction("SWAP");  //para que el this quede en el tope
                }
            }
        }
    }


}
