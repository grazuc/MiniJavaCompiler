package AST.Sentence;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;

public class LocalVarDeclarationNode extends SentenceNode {

    private ExpressionNode expressionNode;
    private Type localVarType;
    private Token operatorToken;

    public LocalVarDeclarationNode(Token nodeToken, ExpressionNode expressionNode, Token operatorToken) {
        super(nodeToken);
        this.expressionNode = expressionNode;
        this.operatorToken = operatorToken;
    }

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
            SymbolTable.getInstance().getCurrentBlock().insertLocalVar(this);
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
}
