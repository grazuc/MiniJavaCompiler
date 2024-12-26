package AST.Access;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import InstructionGenerator.*;
import java.io.IOException;
import java.util.ArrayList;

public class ConstructorAccess extends AccessNode {

    protected ArrayList<ExpressionNode> expressionNodesList;
    MethodOrConstructor constructor;

    public ConstructorAccess(Token token,ArrayList<ExpressionNode> expressionNodesList) {
        super(token);
        this.expressionNodesList = expressionNodesList;
    }

    @Override
    public boolean isAssignable() {
        return false;
    }

    @Override
    public boolean isCallable() {
        return true;
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        //System.out.println("Entre a check del Acceso a Constructor");
        Type constructorType;
        if (this.encadenado == null) {
            ConcreteClass concreteClass = SymbolTable.getInstance().getConcreteClass(this.token.getLexeme());
            if (concreteClass != null) {
                if (!concreteClass.hasConstructor()){
                    return new ReferenceType(concreteClass.getToken());
                }
                if (!concreteClass.getClassConstructor().getToken().getLexeme().equals(this.token.getLexeme()))
                    throw new SemanticExceptionSimple(this.token, " no es un constructor de la clase " + this.token.getLexeme());
                else{
                    constructorType = new ReferenceType(this.token);
                    ConcreteClass claseConcretaConstructor = SymbolTable.getInstance().getConcreteClass(constructorType.getClassName());
                    constructor = claseConcretaConstructor.getClassConstructor();
                    if (constructor.hasParameters() || expressionNodesList!= null){
                        checkConstructorArguments(constructor);
                    }
                }
            }
            else
                throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es una clase concreta declarada");
        }
        else
            if (SymbolTable.getInstance().concreteClassIsDeclared(this.token.getLexeme()))
                return this.encadenado.check(new ReferenceType(this.token));
            else
                throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es una clase concreta declarada");
        return constructorType;
    }

    private void checkConstructorArguments(MethodOrConstructor constructor) throws SemanticExceptionSimple{
        //System.out.println("Entre a checkConstructorArguments");
        //System.out.println("Tengo esta cantidad de parametros: "+expressionNodesList.size());
        if(this.expressionNodesList == null || this.expressionNodesList.size() != constructor.getParametersList().size())
            throw new SemanticExceptionSimple(this.token, "La cantidad de parametros del constructor invocado es incorrecta");
        ArrayList<Parameter> listaParametros = constructor.getParametersList();
        Type parameterType;
        Type expressionType;
        int index = 0;
        for(ExpressionNode nodoExpresion : this.expressionNodesList){
            parameterType = listaParametros.get(index).getParameterType();
            expressionType = nodoExpresion.check();
            index+=1;
            if(!expressionType.isCompatibleWithType(parameterType))
                throw new SemanticExceptionSimple(this.token, "Los parametros poseen un tipo incompatible");
        }
    }

    @Override
    public void generateCode() throws IOException {
        ConcreteClass concreteClass = SymbolTable.getInstance().getConcreteClass(this.token.getLexeme());
        int CIR_Size = concreteClass.getCirSize();

        InstructionGenerator.getInstance().generateInstruction("RMEM 1 ; Retorno acceso constructor");
        InstructionGenerator.getInstance().generateInstruction("PUSH " + CIR_Size + "; Se apila el tamaño del CIR ");
        InstructionGenerator.getInstance().generateInstruction("PUSH simple_malloc ; Se coloca la dirección de la rutina malloc en el tope de la pila");
        InstructionGenerator.getInstance().generateInstruction("CALL ; Se realiza la llamada a la rutina malloc");
        InstructionGenerator.getInstance().generateInstruction("DUP ; Se duplica el tope de la pila");
        InstructionGenerator.getInstance().generateInstruction("PUSH " + concreteClass.getVTLabel() + "       ; Se apila la dirección del comienzo de la virtual table");
        InstructionGenerator.getInstance().generateInstruction("STOREREF 0 ; Se guarda la referencia a la virtual table en el CIR creado" );
        InstructionGenerator.getInstance().generateInstruction("DUP ; Se duplica el tope de la pila");
        if (constructor!= null){
            if (expressionNodesList!= null){
                for (ExpressionNode p : expressionNodesList) {
                    p.generateCode();
                    InstructionGenerator.getInstance().generateInstruction("SWAP ; Muevo this"); // Tiene this
                }
            }
        }
        InstructionGenerator.getInstance().generateInstruction("PUSH Constructor_" + this.token.getLexeme() + " ; Se apila la dirección del comienzo del constructor de clase " + this.token.getLexeme());
        InstructionGenerator.getInstance().generateInstruction("CALL ; Se invoca la unidad en el tope de la pila (dirección de comienzo de generación de código del constructor de la clase " + this.token.getLexeme() + ") ");

        if (this.encadenado != null)
            encadenado.generateCode();
    }

}
