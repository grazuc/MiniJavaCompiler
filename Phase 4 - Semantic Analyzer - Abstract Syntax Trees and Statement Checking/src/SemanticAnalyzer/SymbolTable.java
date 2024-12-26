package SemanticAnalyzer;

import AST.Access.ConstructorAccess;
import LexicalAnalyzer.Token;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;
import AST.Sentence.BlockNode;

public class SymbolTable {

    private Class currentClass;
    private MethodOrConstructor currentMethod;
    private boolean mainMethodIsDeclared;
    private Token EOFToken;
    private static SymbolTable instance = null;
    private boolean hasConstructor;
    private Constructor currentClassConstructor;
    private Hashtable<String,ConcreteClass> concreteClassesTable;
    private Hashtable<String,Interface> interfacesTable;
    private ArrayList<SemanticError> semanticErrorsList;
    private BlockNode currentBlock;

    public static SymbolTable getInstance(){
        if (instance == null){
            instance = new SymbolTable();
        }
        return instance;
    }

    public SymbolTable(){
        mainMethodIsDeclared = false;
        semanticErrorsList = new ArrayList<>();
        concreteClassesTable = new Hashtable<String,ConcreteClass>();
        interfacesTable = new Hashtable <String,Interface>();
        hasConstructor=false;
        initPredefinedClasses();
    }

    public ArrayList<SemanticError> getSemanticErrorsList(){
        return semanticErrorsList;
    }

    public void insertConcreteClass(ConcreteClass classToInsert) throws SemanticException {
        if (!concreteClassesTable.containsKey(classToInsert.getClassName()) && !interfacesTable.containsKey(classToInsert.getClassName())) {
            concreteClassesTable.put(classToInsert.getClassName(), classToInsert);
        } else
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(classToInsert.getToken(), "El nombre " + classToInsert.getClassName() + " ya esta declarado"));
    }

    public void insertInterface(Interface interfaceToInsert) throws SemanticException {
        if (!concreteClassesTable.containsKey(interfaceToInsert.getClassName()) && !interfacesTable.containsKey(interfaceToInsert.getClassName())) {
            interfacesTable.put(interfaceToInsert.getClassName(), interfaceToInsert);
        } else
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(interfaceToInsert.getToken(), "El nombre " + interfaceToInsert.getClassName() + " ya esta declarado"));
    }

    public void insertConstructor(Class currentclass, Constructor constructorToInsert) throws SemanticException {
        if (!currentclass.hasConstructor() && Objects.equals(currentclass.getToken().getLexeme(), constructorToInsert.token.getLexeme())){
            currentClassConstructor = constructorToInsert;
            currentclass.insertConstructor(constructorToInsert);
            currentclass.setHasConstructorTrue();
        } else {
            if (!Objects.equals(currentclass.getToken().getLexeme(), constructorToInsert.token.getLexeme())){
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(constructorToInsert.token,"El nombre de la clase " +currentclass.getToken().getLexeme() +" no coincide con el nombre del constructor "+constructorToInsert.token.getLexeme()));
            } else {
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(constructorToInsert.token,"La clase "+currentclass.getToken().getLexeme()+" ya tiene un constructor definido"));
            }
        }
    }

    public Class getCurrentClass() {
        return currentClass;
    }

    public void setActualClass(Class currentclass) {
        currentClass = currentclass;
    }

    public void setCurrentMethod(MethodOrConstructor currentmethod) {
        currentMethod = currentmethod;
    }

    public MethodOrConstructor getCurrentMethod() {
        return currentMethod;
    }

    public Constructor getCurrentClassConstructor(){
        return currentClassConstructor;
    }

    public Hashtable<String, ConcreteClass> getConcreteClassesTable() {
        return concreteClassesTable;
    }

    public Hashtable<String, Interface> getInterfacesTable() {
        return interfacesTable;
    }

    public boolean concreteClassIsDeclared(String className) {
        return SymbolTable.getInstance().getConcreteClassesTable().containsKey(className);
    }

    public boolean interfaceIsDeclared(String interfaceName) {
        return SymbolTable.getInstance().getInterfacesTable().containsKey(interfaceName);
    }

    public ConcreteClass getConcreteClass(String concreteClassName) {
        return SymbolTable.getInstance().getConcreteClassesTable().get(concreteClassName);
    }

    public Interface getInterface(String interfaceName) {
        return SymbolTable.getInstance().getInterfacesTable().get(interfaceName);
    }

    public void setCurrentBlock(BlockNode currentBlock) {
        this.currentBlock = currentBlock;
    }

    public BlockNode getCurrentBlock() {
        return this.currentBlock;
    }

    public void checkSentences() throws SemanticExceptionSimple {
        for (ConcreteClass concreteClass: this.concreteClassesTable.values()) {
            this.currentClass = concreteClass;
            for (MethodOrConstructor method : concreteClass.getMethods().values()) {
                this.currentMethod = method;
                if (!method.isChecked()) {
                    if (method.getPrincipalBlock() != null) {
                        this.setCurrentBlock(method.getPrincipalBlock());
                        method.getPrincipalBlock().check();
                    }
                    method.setChecked();
                }
            }
            if (concreteClass.hasConstructor()){
                MethodOrConstructor constructor = concreteClass.getClassConstructor();
                this.currentMethod = constructor;
                if (!constructor.isChecked()){
                    if (constructor.getPrincipalBlock() != null){
                        setCurrentBlock(constructor.getPrincipalBlock());
                        constructor.getPrincipalBlock().check();
                    }
                }
                constructor.setChecked();
            }
        }
    }

    public boolean isMethodParameter(String varName, MethodOrConstructor method){
        for (Parameter parameter: method.getParametersList())
            if (parameter.getParameterName().equals(varName))
                return true;
        return false;
    }

    public Type retrieveParameterType(String varName, MethodOrConstructor method) {
        boolean foundParameter = false;
        ArrayList<Parameter> methodParameterList = method.getParametersList();
        int listIndex = 0;
        Type typeToReturn = null;
        while (!foundParameter) {
            Parameter parameter = methodParameterList.get(listIndex);
            if (parameter.getParameterName().equals(varName)) {
                typeToReturn =  parameter.getParameterType();
                foundParameter = true;
            }
            listIndex += 1;
        }
        return typeToReturn;
    }

    public boolean isAttribute(String varName, ConcreteClass concreteClass) {
        if (concreteClass.getAttributes().containsKey(varName))
            return true;
        return false;
    }

    public boolean isStaticAttribute(String varName, ConcreteClass concreteClass) {
        if (concreteClass.getAttributes().containsKey(varName)) {
            Attribute attribute = concreteClass.getAttributes().get(varName);
            return attribute.getStaticOptional().equals("static");
        }
        return false;
    }

    public Type retrieveAttribute(String varName, ConcreteClass concreteClass) {
        return concreteClass.getAttributes().get(varName).getAttributeType();
    }

    public boolean isCurrentBlockLocalVar(String varName) {
        BlockNode currentBlockAncestor = this.currentBlock;
        if (!currentBlockAncestor.getLocalVarTable().containsKey(varName))
            while (currentBlockAncestor.getAncestorBlock() != null) {
                currentBlockAncestor = currentBlockAncestor.getAncestorBlock();
                if (currentBlockAncestor.getLocalVarTable().containsKey(varName))
                    return true;
            }
        else
            return true;
        return false;
    }

    public Type retrieveLocalVarType(String varName) {
        BlockNode currentBlockAncestor = this.currentBlock;
        if (!currentBlockAncestor.getLocalVarTable().containsKey(varName))
            while (currentBlockAncestor.getAncestorBlock() != null) {
                currentBlockAncestor = currentBlockAncestor.getAncestorBlock();
                if (currentBlockAncestor.getLocalVarTable().containsKey(varName))
                    return currentBlockAncestor.getLocalVarTable().get(varName).getLocalVarType();
            }
        else
            return currentBlockAncestor.getLocalVarTable().get(varName).getLocalVarType();
        return null;
    }

    public void checkDeclarations() throws SemanticException {
        for (ConcreteClass classToCheck : concreteClassesTable.values()) {
            classToCheck.checkDeclarations();
            checkMainMethod(classToCheck);
        }
        for (Interface interfaceToCheck : interfacesTable.values())
            interfaceToCheck.checkDeclarations();
    }

    private void checkMainMethod(ConcreteClass classToCheck) {
        for (MethodOrConstructor methodToCheck : classToCheck.getMethods().values()) {
            if (methodToCheck.getMethodName().equals("main")){
                if (methodToCheck.getStaticHeader().equals("static") && methodToCheck.getReturnType().getClassName().equals("void") && !methodToCheck.hasParameters()) {
                    if (mainMethodIsDeclared) {
                        SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(methodToCheck.getToken(), "Ya existe un metodo main estatico y sin parametros"));
                    } else {
                        mainMethodIsDeclared = true;
                    }
                } else {
                    //System.out.println("Entre en la tabla de Simbolos");
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(methodToCheck.getToken(), "El metodo main esta mal declarado"));
                }
            }
        }
    }

    public void consolidate() throws SemanticException {
        for (Interface interfaceToConsolidate : interfacesTable.values())
            interfaceToConsolidate.consolidate();
        for (ConcreteClass classToConsolidate : concreteClassesTable.values())
            classToConsolidate.consolidate();
        if (!mainMethodIsDeclared)
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(EOFToken, "No se encontro el metodo estatico main sin parametros declarado dentro de ninguna clase"));
    }

    private void initPredefinedClasses() {
        initObjectClass();
        initStringClass();
        initSystemClass();
    }

    public void emptySymbolTable() {
        concreteClassesTable = new Hashtable<String, ConcreteClass>();
        interfacesTable = new Hashtable<String, Interface>();
        mainMethodIsDeclared = false;
        semanticErrorsList = new ArrayList<>();
        initPredefinedClasses();
    }

    public void setEOFToken(Token token) {
        EOFToken = token;
    }

    private void initObjectClass() {

        Token objectClassToken = new Token("idClase", "Object", 0);
        Token debugPrintMethodToken = new Token("idMetVar", "debugPrint", 0);
        Token voidToken = new Token("pr_void", "void", 0);
        Token intToken = new Token("pr_int", "int", 0);
        Token parameterToken = new Token("idMetVar", "i", 0);

        Type debugPrintMethodType = new PrimitiveType(voidToken);
        Type debugPrintMethodParameterType = new PrimitiveType(intToken);

        Parameter methodParameter = new Parameter(parameterToken, debugPrintMethodParameterType);
        ConcreteClass objectClass = new ConcreteClass(objectClassToken, null);
        objectClass.setConsolidated();
        Method debugPrintMethod = new Method(debugPrintMethodToken, "static", debugPrintMethodType, objectClass.getClassName());


        debugPrintMethod.insertParameter(methodParameter);
        objectClass.insertMethod(debugPrintMethod);
        concreteClassesTable.put(objectClass.getClassName(), objectClass);

    }

    private void initStringClass() {
        Token stringClassToken = new Token("idClase", "String", 0);
        ConcreteClass ancestorClass = concreteClassesTable.get("Object");
        Token ancestorClassToken = ancestorClass.getToken();

        ConcreteClass stringConcreteClass = new ConcreteClass(stringClassToken, ancestorClassToken);
        concreteClassesTable.put(stringConcreteClass.getClassName(), stringConcreteClass);
    }

    private void initSystemClass() {
        Token systemClassToken = new Token("idClase", "System", 0);
        ConcreteClass ancestorClass = concreteClassesTable.get("Object");
        Token ancestorClassToken = ancestorClass.getToken();
        ConcreteClass systemConcreteClass = new ConcreteClass(systemClassToken, ancestorClassToken);
        concreteClassesTable.put(systemConcreteClass.getClassName(), systemConcreteClass);
        insertReadMethod(systemConcreteClass);
        insertPrintBMethod(systemConcreteClass);
        insertPrintCMethod(systemConcreteClass);
        insertPrintIMethod(systemConcreteClass);
        insertPrintSMethod(systemConcreteClass);
        insertPrintlnMethod(systemConcreteClass);
        insertPrintBlnMethod(systemConcreteClass);
        insertPrintClnMethod(systemConcreteClass);
        insertPrintIlnMethod(systemConcreteClass);
        insertPrintSlnMethod(systemConcreteClass);
    }

    private void insertReadMethod(ConcreteClass concreteClass) {
        Token intToken = new Token("pr_int", "int", 0);
        Type readMethodType = new PrimitiveType(intToken);
        Token readMethodToken = new Token("idMetVar", "read", 0);
        Method readMethod = new Method(readMethodToken, "static", readMethodType,concreteClass.getClassName());

        concreteClass.insertMethod(readMethod);
    }

    private void insertPrintBMethod(ConcreteClass concreteClass) {
        Token voidToken = new Token("pr_void", "void", 0);
        Token booleanToken = new Token("pr_boolean", "boolean", 0);
        Token parameterBToken = new Token("idMetVar", "b", 0);
        Type printBMethodType = new PrimitiveType(voidToken);
        Token printBMethodToken = new Token("idMetVar", "printB", 0);
        Method printBMethod = new Method(printBMethodToken, "static", printBMethodType,concreteClass.getClassName());
        Type printBMethodParameterType = new PrimitiveType(booleanToken);
        Parameter parameterB = new Parameter(parameterBToken, printBMethodParameterType);

        printBMethod.insertParameter(parameterB);
        concreteClass.insertMethod(printBMethod);
    }

    private void insertPrintCMethod(ConcreteClass concreteClass) {
        Token voidToken = new Token("pr_void", "void", 0);
        Token charToken = new Token("pr_char", "char", 0);
        Token parameterCToken = new Token("idMetVar", "c", 0);
        Type printCMethodType = new PrimitiveType(voidToken);
        Token printCMethodToken = new Token("idMetVar", "printC", 0);
        Method printCMethod = new Method(printCMethodToken, "static", printCMethodType,concreteClass.getClassName());
        Type printCMethodParameterType = new PrimitiveType(charToken);
        Parameter parameterC = new Parameter(parameterCToken, printCMethodParameterType);

        printCMethod.insertParameter(parameterC);
        concreteClass.insertMethod(printCMethod);
    }

    private void insertPrintIMethod(ConcreteClass concreteClass) {
        Token voidToken = new Token("pr_void", "void", 0);
        Token intToken = new Token("pr_int", "int", 0);
        Token parameterIToken = new Token("idMetVar", "i", 0);
        Type printIMethodType = new PrimitiveType(voidToken);
        Token printIMethodToken = new Token("idMetVar", "printI", 0);
        Method printIMethod = new Method(printIMethodToken, "static", printIMethodType,concreteClass.getClassName());
        Type printIMethodParameterType = new PrimitiveType(intToken);
        Parameter parameterI = new Parameter(parameterIToken, printIMethodParameterType);

        printIMethod.insertParameter(parameterI);
        concreteClass.insertMethod(printIMethod);
    }

    private void insertPrintSMethod(ConcreteClass concreteClass) {
        Token voidToken = new Token("pr_void", "void", 0);
        Token stringToken = new Token("idClase", "String", 0);
        Token parameterSToken = new Token("idMetVar", "s", 0);
        Type printSMethodType = new PrimitiveType(voidToken);
        Token printSMethodToken = new Token("idMetVar", "printS", 0);
        Method printSMethod = new Method(printSMethodToken, "static", printSMethodType,concreteClass.getClassName());
        Type printIMethodParameterType = new PrimitiveType(stringToken);
        Parameter parameterS = new Parameter(parameterSToken, printIMethodParameterType);

        printSMethod.insertParameter(parameterS);
        concreteClass.insertMethod(printSMethod);
    }

    private void insertPrintlnMethod(ConcreteClass concreteClass) {
        Token voidToken = new Token("pr_void", "void", 0);
        Type printlnMethodType = new PrimitiveType(voidToken);
        Token printlnMethodToken = new Token("idMetVar", "println", 0);
        Method printlnMethod = new Method(printlnMethodToken, "static", printlnMethodType,concreteClass.getClassName());

        concreteClass.insertMethod(printlnMethod);
    }

    private void insertPrintBlnMethod(ConcreteClass concreteClass) {
        Token voidToken = new Token("pr_void", "void", 0);
        Token booleanToken = new Token("pr_boolean", "boolean", 0);
        Token parameterBToken = new Token("idMetVar", "b", 0);
        Type printBlnMethodType = new PrimitiveType(voidToken);
        Token printBlnMethodToken = new Token("idMetVar", "printBln", 0);
        Method printBlnMethod = new Method(printBlnMethodToken, "static", printBlnMethodType,concreteClass.getClassName());
        Type printBlnMethodParameterType = new PrimitiveType(booleanToken);
        Parameter parameterB = new Parameter(parameterBToken, printBlnMethodParameterType);

        printBlnMethod.insertParameter(parameterB);
        concreteClass.insertMethod(printBlnMethod);
    }

    private void insertPrintClnMethod(ConcreteClass concreteClass) {
        Token voidToken = new Token("pr_void", "void", 0);
        Token charToken = new Token("pr_char", "char", 0);
        Token parameterCToken = new Token("idMetVar", "c", 0);
        Type printClnMethodType = new PrimitiveType(voidToken);
        Token printClnMethodToken = new Token("idMetVar", "printCln", 0);
        Method printClnMethod = new Method(printClnMethodToken, "static", printClnMethodType,concreteClass.getClassName());
        Type printClnMethodParameterType = new PrimitiveType(charToken);
        Parameter parameterB = new Parameter(parameterCToken, printClnMethodParameterType);

        printClnMethod.insertParameter(parameterB);
        concreteClass.insertMethod(printClnMethod);
    }

    private void insertPrintIlnMethod(ConcreteClass concreteClass) {
        Token voidToken = new Token("pr_void", "void", 0);
        Token intToken = new Token("pr_int", "int", 0);
        Token parameterIToken = new Token("idMetVar", "i", 0);
        Type printIlnMethodType = new PrimitiveType(voidToken);
        Token printIlnMethodToken = new Token("idMetVar", "printIln", 0);
        Method printIlnMethod = new Method(printIlnMethodToken, "static", printIlnMethodType,concreteClass.getClassName());
        Type printIlnMethodParameterType = new PrimitiveType(intToken);
        Parameter parameterI = new Parameter(parameterIToken, printIlnMethodParameterType);

        printIlnMethod.insertParameter(parameterI);
        concreteClass.insertMethod(printIlnMethod);
    }

    private void insertPrintSlnMethod(ConcreteClass concreteClass) {
        Token voidToken = new Token("pr_void", "void", 0);
        Token stringToken = new Token("idClase", "String", 0);
        Token parameterSToken = new Token("idMetVar", "s", 0);
        Type printSlnMethodType = new PrimitiveType(voidToken);
        Token printSlnMethodToken = new Token("idMetVar", "printSln", 0);
        Method printSlnMethod = new Method(printSlnMethodToken, "static", printSlnMethodType,concreteClass.getClassName());
        Type printSlnMethodParameterType = new PrimitiveType(stringToken);
        Parameter parameterS = new Parameter(parameterSToken, printSlnMethodParameterType);

        printSlnMethod.insertParameter(parameterS);
        concreteClass.insertMethod(printSlnMethod);
    }

    public Class getClass(String name) {
        for (ConcreteClass concreteClass: this.concreteClassesTable.values())
            if (concreteClass.getClassName().equals(name))
                return concreteClass;
        for (Interface i: this.interfacesTable.values())
            if (i.getClassName().equals(name))
                return i;
        return null;
    }

}

