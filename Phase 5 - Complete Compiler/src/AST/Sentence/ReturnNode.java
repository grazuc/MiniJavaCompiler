package AST.Sentence;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import InstructionGenerator.*;
import java.io.IOException;

public class ReturnNode extends SentenceNode {

    private ExpressionNode expressionNode;
    private MethodOrConstructor method;
    private BlockNode blockOfReturn;
    private int parameterCount;

    public ReturnNode(Token token, ExpressionNode expressionNode) {
        super(token);
        this.expressionNode = expressionNode;
    }

    @Override
    public void check() throws SemanticExceptionSimple {
        Type expressionType = this.expressionNode.check();
        method = SymbolTable.getInstance().getCurrentMethod();
        if (method.isConstructor()){
            throw new SemanticExceptionSimple(token,"Un constructor no puede tener retorno");
        }
        Type returnMethodType = method.getReturnType();
        //System.out.println("Metodo con nombre: "+method.getToken().getLexeme());
        //System.out.println("Tipo de retorno del metodo: "+method.getReturnType());
        if (expressionType == null && !returnMethodType.getClassName().equals("void"))
                throw new SemanticExceptionSimple(this.token, "El metodo debe retornar una expresion de tipo " + returnMethodType.getClassName());
        if (expressionType != null) {
            //System.out.println("Tipo retorno de la expresion "+expressionType);
            //System.out.println("Tipo retorno del metodo: "+returnMethodType);
            if (!expressionType.isCompatibleWithType(returnMethodType))
                if (!returnMethodType.getClassName().equals("void")) {
                    throw new SemanticExceptionSimple(this.token, "El metodo debe retornar una expresion de tipo " + returnMethodType.getClassName());
                }
                else
                    throw new SemanticExceptionSimple(this.token, "El metodo no tiene retorno");
        }
        this.setBlockOfReturn(SymbolTable.getInstance().getCurrentBlock());
    }

    private void setBlockOfReturn(BlockNode blockOfReturn) {
        this.blockOfReturn = blockOfReturn;
    }


    protected void generateCode() throws IOException{
        parameterCount = method.getParametersList().size();
        if (expressionNode!= null && !expressionNode.getToken().getLexeme().equals(";")){
            generateStoreCode();
        }
        generateReturnCode();
    }
    private void generateStoreCode() throws IOException {
        int offsetReturn;
        expressionNode.generateCode();
        offsetReturn = method.getStaticHeader().equals("static") ? parameterCount + 3 : parameterCount + 4; // Estatico: PR, ED, Parametros, Ret || Dinamico: PR, ED, THIS, Parametros, Ret
        InstructionGenerator.getInstance().generateInstruction("    STORE "+offsetReturn+" ; Guarda retorno en su lugar");
    }

    public boolean isVariableDeclaration() {return false;}

    private void generateReturnCode() throws IOException{
        int localVariablesToFree;
        int memToFree;
        memToFree = !method.isConstructor() && method.getStaticHeader().equals("static") ? parameterCount : parameterCount +1;
        localVariablesToFree = blockOfReturn.getTotalVars();
        InstructionGenerator.getInstance().generateInstruction("    FMEM "+localVariablesToFree+" ; Borra variables locales reservadas");
        InstructionGenerator.getInstance().generateInstruction("    STOREFP ; Usa ED para volver a RA llamador");
        InstructionGenerator.getInstance().generateInstruction("    RET "+memToFree+" ; Libera los parametros y retorna de la unidad");
    }

    /*protected void generateCode() throws IOException {
        //libero memoria de las variables locales
        InstructionGenerator.getInstance().generateInstruction("FMEM " + this.blockOfReturn.getTotalVars() + "         ; Se libera memoria de variables locales despues de un return");

        if (this.method.getReturnType().getClassName().equals("void")) {
            InstructionGenerator.getInstance().generateInstruction("STOREFP            ; Nodo return, se actualiza el FP para que ahora apunte al RA llamador");
            InstructionGenerator.getInstance().generateInstruction("RET " + this.method.getReturnOffset() + "       ; Se liberan " + this.method.getReturnOffset() + " lugares de la pila");
        }
        else {
            this.expressionNode.generateCode();
            InstructionGenerator.getInstance().generateInstruction("STORE " + this.method.getStoringValueInReturnOffset() + "       ; Se coloca el valor de la expresion del return en la locacion que fue reservada para el retorno del metodo");
            InstructionGenerator.getInstance().generateInstruction("STOREFP           ; Nodo return, se actualiza el FP para que ahora apunte al RA llamador");
            InstructionGenerator.getInstance().generateInstruction("RET " + this.method.getReturnOffset() + "       ; Se liberan " + this.method.getReturnOffset() + " lugares de la pila");
        }
    }*/

}
