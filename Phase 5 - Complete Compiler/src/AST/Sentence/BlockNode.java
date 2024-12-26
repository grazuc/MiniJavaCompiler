package AST.Sentence;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import InstructionGenerator.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class BlockNode extends SentenceNode {

    private ArrayList<SentenceNode> sentencesList;
    private Hashtable<String, LocalVarDeclarationNode> localVarTable;
    private BlockNode ancestorBlock;
    private int availableLocalVarOffset;
    private int totalVars;
    private int lastVariableOffset;

    public BlockNode(Token token, BlockNode ancestorBlock) {
        super(token);
        this.sentencesList = new ArrayList<>();
        this.localVarTable = new Hashtable<>();
        this.ancestorBlock = ancestorBlock;
        this.availableLocalVarOffset = 1;
        this.totalVars = 0;
    }

    public Hashtable<String, LocalVarDeclarationNode> getLocalVarTable() {
        return this.localVarTable;
    }

    private int getAvailableLocalVarOffset() {
        return this.availableLocalVarOffset;
    }

    public void insertLocalVar(LocalVarDeclarationNode localVarNode) throws SemanticExceptionSimple {
        MethodOrConstructor currentMethod = SymbolTable.getInstance().getCurrentMethod();
        boolean isParameter = false;
        for (Parameter p : currentMethod.getParametersList()){
            if (localVarNode.getVarName().equals(p.getParameterName())){
                isParameter = true;
                break;
            }
        }
        if (isParameter){
            throw new SemanticExceptionSimple(localVarNode.getVarToken(), "Ya existe una variable local con nombre " + localVarNode.getVarName() + " dentro del alcance");
        }
        if (isLocalVariablePrivate(localVarNode.getVarName())){
            throw new SemanticExceptionSimple(localVarNode.getVarToken(), "Ya existe una variable local con nombre " + localVarNode.getVarName() + " dentro del alcance");
        }
        localVarTable.put(localVarNode.getVarName(),localVarNode);
    }

    public void increaseTotalBlockVars() {
        this.totalVars += 1;
    }

    private int getAncestorAvailableOffset() {
        BlockNode ancBlock = this.ancestorBlock;
        while (ancBlock != null) {
            if (ancBlock.getAvailableLocalVarOffset() != 1)
                return ancBlock.getAvailableLocalVarOffset();
            ancBlock = ancBlock.getAncestorBlock();
        }
        return 1;
    }


    @Override
    public void check() throws SemanticExceptionSimple {
        SymbolTable.getInstance().setCurrentBlock(this);
        for (SentenceNode sentenceNode: this.sentencesList) {
            if (sentenceNode != null){
                sentenceNode.check();
            }
        }
        if (this.getAncestorBlock() != null)
            SymbolTable.getInstance().setCurrentBlock(this.ancestorBlock);
    }

    private boolean isLocalVariablePrivate(String localVarName){
        if (localVarTable.get(localVarName) == null){
            if (ancestorBlock != null)
                return ancestorBlock.isLocalVariablePrivate(localVarName);
            else return false;
        } else return true;
    }

    public boolean isLocalVariable(String varName){
        if(localVarTable.containsKey(varName))
            return true;
        else
            return false;
    }


    public void generateCode() throws IOException{
        if (ancestorBlock!= null){
            lastVariableOffset = ancestorBlock.lastVariableOffset;
        }
        for (SentenceNode s : sentencesList){
            if (s.isVariableDeclaration()){
                ((LocalVarDeclarationNode) s).setVarOffset(lastVariableOffset--);
                InstructionGenerator.getInstance().generateInstruction("RMEM 1 ; Reservo espacio para una variable local");
                s.generateCode();
            } else{
                s.generateCode();
            }
        }
        if (localVarTable.size() != 0){
            InstructionGenerator.getInstance().generateInstruction("FMEM "+localVarTable.size()+" ; Libero las variables locales");
        }
    }

    public boolean isVariableDeclaration() {return false;}

    public int getTotalVars() {
        int total;
        if (this.ancestorBlock != null) {
            total = this.ancestorBlock.getTotalVars() + this.totalVars;
        }
        else
            total = this.totalVars;
        return total;
    }

    public void addSentence(SentenceNode sentenceNode) {
        this.sentencesList.add(sentenceNode);
    }
    public BlockNode getAncestorBlock() {
        return this.ancestorBlock;
    }
}
