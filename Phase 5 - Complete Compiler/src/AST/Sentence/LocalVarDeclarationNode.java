package AST.Sentence;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import InstructionGenerator.*;
import java.io.IOException;

public class LocalVarDeclarationNode extends SentenceNode {

    private ExpressionNode expressionNode;
    private Type localVarType;
    private Token operatorToken;
    private int varOffset;
    private BlockNode blockNode;

    public LocalVarDeclarationNode(Token nodeToken, ExpressionNode expressionNode, Token operatorToken) {
        super(nodeToken);
        this.expressionNode = expressionNode;
        this.operatorToken = operatorToken;
    }

    public boolean isVariableDeclaration() {return true;}

    @Override
    public void check() throws SemanticExceptionSimple {
        MethodOrConstructor currentMethod = SymbolTable.getInstance().getCurrentMethod();
        if (!SymbolTable.getInstance().isMethodParameter(this.token.getLexeme(), currentMethod)) {
            Type localVarType = this.expressionNode.check();
            if (localVarType.getClassName().equals("null") || localVarType.getClassName().equals("void"))
                throw new SemanticExceptionSimple(this.operatorToken, "no se puede inferir el tipo de la variable");
            if (isDeclaredInMainBlock(SymbolTable.getInstance().getCurrentMethod().getCurrentBlock(),token.getLexeme())){
                throw new SemanticExceptionSimple(token,"La variable "+token.getLexeme()+" ya fue previamente declarada");
            }
            this.setType(localVarType);
            this.blockNode = SymbolTable.getInstance().getCurrentBlock();
            this.blockNode.insertLocalVar(this);
        }
        else
            throw new SemanticExceptionSimple(this.token, "el nombre para la variable ya esta utilizado en un parametro");
    }

    private boolean isDeclaredInMainBlock(BlockNode actualBlock, String varName){
        if(actualBlock != null) {
            if (actualBlock.isLocalVariable(varName))
                return true;
            else
                return isDeclaredInMainBlock(actualBlock.getAncestorBlock(), varName);
        }
        return false;
    }

    public Type getLocalVarType() {
        return this.localVarType;
    }

    public void setType(Type localVarType) {
        this.localVarType = localVarType;
    }

    public String getVarName() {
        return this.token.getLexeme();
    }

    public Token getVarToken() {
        return this.token;
    }

    public void setVarOffset(int varOffset) {
        this.varOffset = varOffset;
    }

    public int getVarOffset() {
        return this.varOffset;
    }

    @Override
    protected void generateCode() throws IOException {
        if (expressionNode!= null){
            this.expressionNode.generateCode();
            InstructionGenerator.getInstance().generateInstruction("STORE " + this.varOffset + " ; Se almacena el valor de la expresion en la variable local " + this.token.getLexeme());
        }
        //InstructionGenerator.getInstance().generateInstruction("RMEM 1 ; Se reserva espacio para una variable local");
        //incremento la cantidad de variables del bloque asociado a la declaracion de la variable, para saber cuanta memoria liberar luego
        this.blockNode.increaseTotalBlockVars();
    }

}
