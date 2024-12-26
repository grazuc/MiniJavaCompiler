package SemanticAnalyzer;

import AST.Sentence.BlockNode;
import LexicalAnalyzer.Token;
import InstructionGenerator.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Constructor extends MethodOrConstructor{
    private ArrayList<Parameter> parametersList;
    private Hashtable<String, Parameter> parametersTable;
    private BlockNode currentBlock;
    private BlockNode principalBlock;
    private boolean principalBlockIsChecked;
    private String className;
    private int offset;
    private boolean codeIsGenerated;
    private boolean hasOffset;
    private boolean isInterfaceMethod;
    public Constructor(Token token) {
        super(token);
        parametersList = new ArrayList<>();
        parametersTable = new Hashtable<>();
        principalBlockIsChecked = false;
        this.codeIsGenerated = false;
        this.hasOffset = false;
        isInterfaceMethod = false;
    }

    public String getStaticHeader() {
        return "public";
    }
    public void setClassName(String name){
        className = name;
    }

    public void setChecked() {
        this.principalBlockIsChecked = true;
    }

    @Override
    public ConcreteClass getMethodClass() {
        return SymbolTable.getInstance().getConcreteClass(this.className);
    }


    @Override
    public void generateCode() throws IOException{
        int memToFree = parametersList.size() + 1;
        InstructionGenerator.getInstance().generateInstruction("Constructor_"+this.token.getLexeme()+":");
        InstructionGenerator.getInstance().generateInstruction("LOADFP");
        InstructionGenerator.getInstance().generateInstruction("LOADSP");
        InstructionGenerator.getInstance().generateInstruction("STOREFP");
        if (principalBlock != null){
            principalBlock.generateCode();
        }
        InstructionGenerator.getInstance().generateInstruction("STOREFP");
        InstructionGenerator.getInstance().generateInstruction("RET "+memToFree+" ; Libera los parametros y retorna de la unidad");
    }

    @Override
    public boolean codeIsGenerated() {
        return this.codeIsGenerated;
    }

    @Override
    public String getMethodLabel() {
        return this.getMethodName() + "_Clase" + this.className;
    }

    @Override
    public void setCodeGenerated() {
        this.codeIsGenerated = true;
    }

    @Override
    public void setOffset(int offset) {
        //System.out.println("Entraste aca");
        this.offset = offset;
    }

    @Override
    public int getOffset() {
        //System.out.println("Entraste aca");
        return offset;
    }

    @Override
    public void setOffsetIsSet() {
        //System.out.println("Entraste aca");
        hasOffset = true;
    }

    @Override
    public boolean hasOffset() {
        //System.out.println("Entraste aca");
        return hasOffset;
    }

    @Override
    public boolean isInterfaceMethod() {
        return isInterfaceMethod;
    }

    @Override
    public void setAsInterfaceMethod() {
        isInterfaceMethod = true;
    }

    @Override
    public void setInterface(Interface interfaceMethod) {

    }

    @Override
    public Interface getInterfaceMethod() {
        return null;
    }

    public boolean isChecked() {
        return this.principalBlockIsChecked;
    }

    public boolean hasParameters() {
        return parametersList.size() != 0;
    }

    public ArrayList<Parameter> getParametersList() {
        return parametersList;
    }

    public void insertParameter(Parameter parameterToInsert) {
        if (!parametersTable.containsKey(parameterToInsert.getParameterName())) {
            //System.out.println("Entre al if con: "+parameterToInsert.getParameterToken().getLexeme());
            parametersTable.put(parameterToInsert.getParameterName(), parameterToInsert);
            parametersList.add(parameterToInsert);
        }
        else{
            //System.out.println("Entre al else con: "+parameterToInsert.getParameterToken().getLexeme());
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(parameterToInsert.getParameterToken(), "El parametro " + parameterToInsert.getParameterName() + " ya esta declarado en el constructor " + "\"" + token.getLexeme() + "\""));
        }
    }
    public void setParametersOffset(){
        int i = this.getStaticHeader().equals("static") ? parametersList.size() + 2 : parametersList.size() + 3;
        for(Parameter p : parametersList)
            p.setOffset(i--);
    }

    @Override
    public String getMethodName() {
        return this.className;
    }

    @Override
    public Type getReturnType() {
        return null;
    }

    public void checkDeclaration() {
        checkNoPrimitiveParameters();
    }

    @Override
    public boolean correctRedefinedMethodHeader(MethodOrConstructor ancestor) {
        return false;
    }

    @Override
    public boolean methodsHeadersAreEquals(MethodOrConstructor m) {
        return false;
    }

    private void checkNoPrimitiveParameters() {
        for (Parameter parameter: parametersTable.values()) {
            if (!parameter.getParameterType().isPrimitive())
                if (!parameterTypeIsDeclared(parameter)) {
                    Token parameterTypeToken = parameter.getParameterType().getToken();
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(parameterTypeToken, "El tipo del parametro " + "\"" + parameter.getParameterName() + "\"" + " del constructor " + "\"" + token.getLexeme() + "\"" + " no esta declarado"));
                }
        }
    }

    private boolean parameterTypeIsDeclared(Parameter parameter) {
        Type parameterType = parameter.getParameterType();
        String parameterClass = parameterType.getClassName();
        return SymbolTable.getInstance().concreteClassIsDeclared(parameterClass) || SymbolTable.getInstance().interfaceIsDeclared(parameterClass);
    }

    public void setPrincipalBlock(BlockNode blockNode) {
        this.principalBlock = blockNode;
    }

    public BlockNode getPrincipalBlock() {
        return this.principalBlock;
    }

    public void setCurrentBlock(BlockNode blockNode) {
        this.currentBlock = blockNode;
    }

    public BlockNode getCurrentBlock() {
        return this.currentBlock;
    }


}