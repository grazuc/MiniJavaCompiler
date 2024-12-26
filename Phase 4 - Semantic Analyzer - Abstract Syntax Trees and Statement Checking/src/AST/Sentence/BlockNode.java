package AST.Sentence;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.Method;
import SemanticAnalyzer.MethodOrConstructor;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.SymbolTable;

import java.util.ArrayList;
import java.util.Hashtable;

public class BlockNode extends SentenceNode {

    private ArrayList<SentenceNode> sentencesList;
    private Hashtable<String, LocalVarDeclarationNode> localVarTable;
    private BlockNode ancestorBlock;

    public BlockNode(Token token, BlockNode ancestorBlock) {
        super(token);
        this.sentencesList = new ArrayList<>();
        this.localVarTable = new Hashtable<>();
        this.ancestorBlock = ancestorBlock;
    }

    public Hashtable<String, LocalVarDeclarationNode> getLocalVarTable() {
        return this.localVarTable;
    }

    public void insertLocalVar(LocalVarDeclarationNode localVarNode) throws SemanticExceptionSimple {
        if (this.ancestorBlock != null) {
            for (LocalVarDeclarationNode localVarInAncestorBlock: ancestorBlock.getLocalVarTable().values())
                this.localVarTable.put(localVarInAncestorBlock.getVarName(), localVarInAncestorBlock);
        }
        MethodOrConstructor currentMethod = SymbolTable.getInstance().getCurrentMethod();
        if (!currentMethod.getParametersList().contains(localVarNode.getVarName())) {
            if (!this.localVarTable.containsKey(localVarNode.getVarName()))
                this.localVarTable.put(localVarNode.getVarName(), localVarNode);
            else
                throw new SemanticExceptionSimple(localVarNode.getVarToken(), "Ya existe una variable local con nombre " + localVarNode.getVarName() + " dentro del alcance");
        }
        else
            throw new SemanticExceptionSimple(localVarNode.getVarToken(), "El nombre " + localVarNode.getVarName() + " ya esta utilizado en un parametro dentro del metodo " + "\"" + currentMethod.getMethodName() + "\"");
    }

    @Override
    public void check() throws SemanticExceptionSimple {
        SymbolTable.getInstance().setCurrentBlock(this);
        for (SentenceNode sentenceNode: this.sentencesList) {
            if (sentenceNode == null) { //sacar

            }
            else
                sentenceNode.check();
        }
        if (this.getAncestorBlock() != null)
            SymbolTable.getInstance().setCurrentBlock(this.ancestorBlock);
    }

    public boolean isLocalVariable(String localVarName){
        if (localVarTable.containsKey(localVarName)){
            return true;
        } else {
            return false;
        }
    }

    public void addSentence(SentenceNode sentenceNode) {
        this.sentencesList.add(sentenceNode);
    }
    public BlockNode getAncestorBlock() {
        return this.ancestorBlock;
    }
}
