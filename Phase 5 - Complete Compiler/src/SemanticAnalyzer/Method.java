package SemanticAnalyzer;

import AST.Sentence.BlockNode;
import LexicalAnalyzer.Token;
import InstructionGenerator.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Method extends MethodOrConstructor{

    private String staticScope;
    private Type methodReturnType;
    private ArrayList<Parameter> parametersList;
    private Hashtable<String, Parameter> parametersTable;
    private BlockNode currentBlock;
    private BlockNode principalBlock;
    private boolean principalBlockIsChecked;
    private String className;
    private boolean codeIsGenerated;
    private int offset;
    private boolean hasOffset;
    private boolean isInterfaceMethod;
    private Interface interfaceMethod;

    public Method(Token token, String staticscope, Type methodreturntype, String className) {
        super(token);
        staticScope = staticscope;
        methodReturnType = methodreturntype;
        parametersList = new ArrayList<>();
        parametersTable = new Hashtable<>();
        this.principalBlockIsChecked = false;
        this.className = className;
        this.codeIsGenerated = false;
        this.hasOffset = false;
        this.isInterfaceMethod = false;
    }

    public void insertParameter(Parameter parameterToInsert) {
        if (!parametersTable.containsKey(parameterToInsert.getParameterName())) {
            parametersTable.put(parameterToInsert.getParameterName(), parameterToInsert);
            parametersList.add(parameterToInsert);
        }
        else
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(parameterToInsert.getParameterToken(), "El parametro " + parameterToInsert.getParameterName() + " ya esta declarado en el metodo " + "\"" + token.getLexeme() + "\""));
    }

    public String getMethodName() {
        return token.getLexeme();
    }

    public String getStaticHeader() {
        return staticScope;
    }



    @Override
    public void setClassName(String name) {

    }

    public Type getReturnType() {
        return methodReturnType;
    }

    public ArrayList<Parameter> getParametersList() {
        return parametersList;
    }

    public void checkDeclaration() {
        checkNoPrimitiveParameters();
        checkNoPrimitiveReturnType();
    }

    private void checkNoPrimitiveParameters() {
        for (Parameter parameter: parametersTable.values()) {
            if (!parameter.getParameterType().isPrimitive())
                if (!parameterTypeIsDeclared(parameter)) {
                    Token parameterTypeToken = parameter.getParameterType().getToken();
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(parameterTypeToken, "El tipo del parametro " + "\"" + parameter.getParameterName() + "\"" + " del metodo " + "\"" + token.getLexeme() + "\"" + " no esta declarado"));
                }
        }
    }

    private void checkNoPrimitiveReturnType() {
        if (!methodReturnType.isPrimitive())
            if (!returnTypeClassIsDeclared())
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(methodReturnType.getToken(), "El tipo de retorno del metodo " + "\"" + token.getLexeme() + "\"" + " no esta declarado"));
    }

    private boolean parameterTypeIsDeclared(Parameter parameter) {
        Type parameterType = parameter.getParameterType();
        String parameterClass = parameterType.getClassName();
        return SymbolTable.getInstance().concreteClassIsDeclared(parameterClass) || SymbolTable.getInstance().interfaceIsDeclared(parameterClass);
    }

    public boolean correctRedefinedMethodHeader(MethodOrConstructor ancestorMethod) {
        return methodsHeadersAreEquals(ancestorMethod);
    }

    public boolean methodsHeadersAreEquals(MethodOrConstructor ancestorMethod) {
        if (!ancestorMethod.getStaticHeader().equals(staticScope) || !ancestorMethod.getReturnType().getClassName().equals(methodReturnType.getClassName()) || !hasEqualsParameters(ancestorMethod))
            return false;
        return true;
    }

    private boolean hasEqualsParameters(MethodOrConstructor ancestorMethod) {
        boolean parametersAreEquals;
        if (ancestorMethod.getParametersList().size() == parametersList.size()) {
            parametersAreEquals = true;
            int parameterIndex = 0;
            while (parametersAreEquals && (parameterIndex < parametersList.size())) {
                Parameter ancestorParameter = ancestorMethod.getParametersList().get(parameterIndex);
                if (!hasEqualsParameters(ancestorParameter, parameterIndex))
                    parametersAreEquals = false;
                parameterIndex = parameterIndex + 1;
            }
        }
        else
            parametersAreEquals = false;
        return parametersAreEquals;
    }

    private boolean hasEqualsParameters(Parameter parameterToCompareWith, int parameterIndex) {
        Parameter parameterOfThisMethod = parametersList.get(parameterIndex);
        if (!parameterToCompareWith.getParameterType().getClassName().equals(parameterOfThisMethod.getParameterType().getClassName()))
            return false;
        return true;
    }

    private boolean returnTypeClassIsDeclared() {
        return SymbolTable.getInstance().concreteClassIsDeclared(methodReturnType.getClassName()) || SymbolTable.getInstance().interfaceIsDeclared(methodReturnType.getClassName());
    }

    public boolean hasParameters() {
        return parametersList.size() != 0;
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

    public void setChecked() {
        this.principalBlockIsChecked = true;
    }

    public boolean isChecked() {
        return this.principalBlockIsChecked;
    }

    public ConcreteClass getMethodClass() {
        return SymbolTable.getInstance().getConcreteClass(this.className);
    }

    public void generateCode() throws IOException {
        //System.out.println("Entre a generar codigo con el metodo: "+token.getLexeme());
        InstructionGenerator.getInstance().generateInstruction(this.getMethodLabel() + ":");
        InstructionGenerator.getInstance().generateInstruction("LOADFP ; Se guarda el enlace dinámico del registro de activación del llamador");
        InstructionGenerator.getInstance().generateInstruction("LOADSP ; Se apila el comienzo del registro de activación de la unidad llamada");
        InstructionGenerator.getInstance().generateInstruction("STOREFP ; Se actualiza el frame pointer para que indicar que el RA que estamos armando es el actual (llamado)");

        if (this.parametersList.size() > 0) {
            setParametersOffset();
        }

        //Si no tiene un bloque principal entonces se trata de un método predefinido
        if (this.principalBlock != null) {
            this.principalBlock.generateCode();
            this.codeIsGenerated = true;
        } else{
            generateCodeForPredefinedMethod();
        }

        InstructionGenerator.getInstance().generateInstruction("STOREFP ; Se actualiza el frame pointer");
        InstructionGenerator.getInstance().generateInstruction("RET "+ this.getReturnOffset() + " ; Retorna el retorno de la unidad y libera " + this.getReturnOffset() + " lugares de la pila");
    }

    public void setParametersOffset(){
        int i = this.getStaticHeader().equals("static") ? parametersList.size() + 2 : parametersList.size() + 3;
        for(Parameter p : parametersList)
            p.setOffset(i--);
    }

    private void generateCodeForPredefinedMethod() throws IOException {
        if (this.getMethodName().equals("debugPrint")) {
            InstructionGenerator.getInstance().generateInstruction("LOAD 3");   //LOAD 3 porque tiene un solo parametro
            InstructionGenerator.getInstance().generateInstruction("IPRINT");
            InstructionGenerator.getInstance().generateInstruction("PRNLN");
        }
        if (this.getMethodName().equals("read")) {
            //lee el próximo byte del stream de entrada estándar
            InstructionGenerator.getInstance().generateInstruction("READ");
            InstructionGenerator.getInstance().generateInstruction("STORE 3");
        }
        if (this.getMethodName().equals("printB")) {
            InstructionGenerator.getInstance().generateInstruction("LOAD 3");   //LOAD 3 porque tiene un solo parametro
            InstructionGenerator.getInstance().generateInstruction("BPRINT");
        }
        if (this.getMethodName().equals("printC")) {
            InstructionGenerator.getInstance().generateInstruction("LOAD 3");
            InstructionGenerator.getInstance().generateInstruction("CPRINT");
        }
        if (this.getMethodName().equals("printI")) {
            InstructionGenerator.getInstance().generateInstruction("LOAD 3");
            InstructionGenerator.getInstance().generateInstruction("IPRINT");
        }
        if (this.getMethodName().equals("printS")) {
            InstructionGenerator.getInstance().generateInstruction("LOAD 3");
            InstructionGenerator.getInstance().generateInstruction("SPRINT");
        }
        if (this.getMethodName().equals("println")) {
            InstructionGenerator.getInstance().generateInstruction("PRNLN");
        }
        if (this.getMethodName().equals("printBln")) {
            InstructionGenerator.getInstance().generateInstruction("LOAD 3");   //LOAD 3 porque tiene un solo parametro
            InstructionGenerator.getInstance().generateInstruction("BPRINT");
            InstructionGenerator.getInstance().generateInstruction("PRNLN");
        }
        if (this.getMethodName().equals("printCln")) {
            InstructionGenerator.getInstance().generateInstruction("LOAD 3");   //LOAD 3 porque tiene un solo parametro
            InstructionGenerator.getInstance().generateInstruction("CPRINT");
            InstructionGenerator.getInstance().generateInstruction("PRNLN");
        }
        if (this.getMethodName().equals("printIln")) {
            InstructionGenerator.getInstance().generateInstruction("LOAD 3");   //LOAD 3 porque tiene un solo parametro
            InstructionGenerator.getInstance().generateInstruction("IPRINT");
            InstructionGenerator.getInstance().generateInstruction("PRNLN");
        }
        if (this.getMethodName().equals("printSln")) {
            InstructionGenerator.getInstance().generateInstruction("LOAD 3");   //LOAD 3 porque tiene un solo parametro
            InstructionGenerator.getInstance().generateInstruction("SPRINT");
            InstructionGenerator.getInstance().generateInstruction("PRNLN");
        }
    }
    public int getReturnOffset(){
        if(staticScope.equals("static")){
            if(parametersList != null)
                return parametersList.size();
            else
                return 0;
        }
        else
        if(parametersList!= null)
            return parametersList.size() + 1;
        else
            return 1;
    }

    public boolean codeIsGenerated() {
        return this.codeIsGenerated;
    }

    public String getMethodLabel() {
        return this.getMethodName() + "_Clase" + this.className;
    }

    public void setCodeGenerated() {
        this.codeIsGenerated = true;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        //System.out.println("Entraste aca");
        return this.offset;
    }

    public void setOffsetIsSet() {
        this.hasOffset = true;
    }

    public boolean hasOffset() {
        return this.hasOffset;
    }

    public boolean isInterfaceMethod() {
        return this.isInterfaceMethod;
    }

    public void setAsInterfaceMethod() {
        this.isInterfaceMethod = true;
    }
    public void setInterface(Interface interfaceMethod) {
        this.interfaceMethod = interfaceMethod;
    }

    public Interface getInterfaceMethod() {
        return this.interfaceMethod;
    }
}