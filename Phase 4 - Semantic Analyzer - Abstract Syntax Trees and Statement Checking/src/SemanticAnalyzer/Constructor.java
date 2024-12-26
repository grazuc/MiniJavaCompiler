package SemanticAnalyzer;

import AST.Sentence.BlockNode;
import LexicalAnalyzer.Token;

import java.util.ArrayList;
import java.util.Hashtable;

public class Constructor extends MethodOrConstructor{
    private ArrayList<Parameter> parametersList;
    private Hashtable<String, Parameter> parametersTable;
    private BlockNode currentBlock;
    private BlockNode principalBlock;
    private boolean principalBlockIsChecked;
    private String className;
    public Constructor(Token token) {
        super(token);
        parametersList = new ArrayList<>();
        parametersTable = new Hashtable<>();
        principalBlockIsChecked = false;
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