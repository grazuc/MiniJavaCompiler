package SemanticAnalyzer;

import LexicalAnalyzer.Token;
import java.util.ArrayList;

public class Interface extends Class {

    Token ancestorToken;

    public Interface(Token interfaceToken, Token ancestortoken) {
        super(interfaceToken);
        ancestorToken = ancestortoken;
    }

    public void insertMethod(MethodOrConstructor methodToInsert) {
        if (methodToInsert.getStaticHeader().equals("static"))
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(methodToInsert.getToken(), "Una interface no puede tener metodos estaticos"));
        if (!methodAlreadyExists(methodToInsert))
            classMethods.put(methodToInsert.getMethodName(), methodToInsert);
        else{
            methodToInsert.checkDeclaration();
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(methodToInsert.getToken(), "El metodo " + "\"" + methodToInsert.getMethodName() + "\"" + " ya esta declarado" + " en la interface " + getClassName()));
        }
    }

    public void checkDeclarations() {
        if (ancestorToken!=null){
            Token interfaceToken = ancestorToken;
            String interfaceToCheckName = interfaceToken.getLexeme();
            if (!interfaceIsDeclared(interfaceToCheckName))
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(interfaceToken, "La interface " + interfaceToCheckName + " no esta declarada"));
            checkCyclicInheritance();
            checkMethodsDeclaration();
        } else {
            checkCyclicInheritance();
            checkMethodsDeclaration();
        }
    }

    private void checkMethodsDeclaration() {
        for (MethodOrConstructor methodToCheck: classMethods.values())
            methodToCheck.checkDeclaration();
    }

    public void consolidate() {
        if (!consolidated) {
            if (!hasCyclicInheritance && ancestorToken!=null){
                Interface interfaceInSymbolTable = SymbolTable.getInstance().getInterface(ancestorToken.getLexeme());
                if (interfaceInSymbolTable != null) {
                    if (!interfaceInSymbolTable.isConsolidated()){
                        interfaceInSymbolTable.consolidate();
                    }
                    consolidateMethods(interfaceInSymbolTable);
                    interfaceInSymbolTable.setConsolidated();
                }
            }
        }
    }

    private void checkCyclicInheritance() {
        ArrayList<String> ancestorsList = new ArrayList<>();
        ancestorsList.add(getClassName());
        if (ancestorToken!=null){
            Interface interfaceInSymbolTable = SymbolTable.getInstance().getInterface(ancestorToken.getLexeme());
            if (interfaceInSymbolTable != null){
                if (interfaceInSymbolTable.hasCyclicInheritance(ancestorsList, ancestorToken)){
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(ancestorToken, "Herencia circular: la interface " + "\"" + getClassName() + "\"" + " se extiende a si misma"));
                    hasCyclicInheritance = true;
                }
            }
        }
    }

    public boolean hasCyclicInheritance(ArrayList<String> ancestorsList, Token interfaceToken){
        if (!ancestorsList.contains(getClassName())) {
            ancestorsList.add(interfaceToken.getLexeme());
            if (ancestorToken!=null){
                Interface interfaceInSymbolTable = SymbolTable.getInstance().getInterface(ancestorToken.getLexeme());
                if (interfaceInSymbolTable != null){
                    if (interfaceInSymbolTable.hasCyclicInheritance(ancestorsList,ancestorToken)){
                        return true;
                    }
                }
                ancestorsList.remove(interfaceToken.getLexeme());
            }
        }
        else
            return true;
        return false;
    }

    private void consolidateMethods(Interface interfaceToConsolidateWith) {
        for (MethodOrConstructor ancestorMethod: interfaceToConsolidateWith.getMethods().values()) {
            String methodName = ancestorMethod.getMethodName();
            if (!getMethods().containsKey(methodName))
                insertMethod(ancestorMethod);
            else {
                MethodOrConstructor thisClassMethod = getMethod(methodName);
                if (!thisClassMethod.correctRedefinedMethodHeader(ancestorMethod))
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(thisClassMethod.getToken(), "El metodo " + "\"" + thisClassMethod.getMethodName() + "\"" + " esta incorrectamente redefinido"));
            }
        }
    }

    private boolean interfaceIsDeclared(String interfaceName) {
        return SymbolTable.getInstance().interfaceIsDeclared(interfaceName);
    }

    public void verifyMethodsImplementation(Token interfaceToken, ConcreteClass concreteClassToCheck) {
        for (MethodOrConstructor method : classMethods.values()) {
            if (concreteClassToCheck.getMethods().containsKey(method.getMethodName())) {
                String methodName = method.getMethodName();
                if (!method.methodsHeadersAreEquals(concreteClassToCheck.getMethod(methodName)))
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(concreteClassToCheck.getMethod(method.getMethodName()).getToken(), "El metodo " + "\"" + method.getMethodName() + "\"" + " no respeta el encabezado del metodo definido en la interface " +getClassName()));
            }
            else {
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(interfaceToken, "La clase " + concreteClassToCheck.getClassName() + " no implementa todos los metodos de la interface " + getClassName()));
                break;
            }
        }

    }

    public boolean hasAncestorInterface(String interfaceName) {
        boolean toReturn = false;
        if (ancestorToken != null){
            Interface i = SymbolTable.getInstance().getInterface(ancestorToken.getLexeme());
            if (i!=null){
                if (i.getClassName().equals(interfaceName)){
                    return true;
                }
                if (i.hasAncestorInterface(interfaceName)){
                    toReturn = true;
                }
            }
        }
        return toReturn;
    }

    private boolean isConsolidated() {
        return consolidated;
    }



}