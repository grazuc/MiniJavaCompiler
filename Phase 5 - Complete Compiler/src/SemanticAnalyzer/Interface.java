package SemanticAnalyzer;

import LexicalAnalyzer.Token;
import java.util.ArrayList;
import java.util.HashSet;

public class Interface extends Class {

    Token ancestorToken;
    private boolean offsetsGenerated;

    public Interface(Token interfaceToken, Token ancestortoken) {
        super(interfaceToken);
        ancestorToken = ancestortoken;
        this.offsetsGenerated = false;
    }

    public void insertMethod(MethodOrConstructor methodToInsert) {
        if (methodToInsert.getStaticHeader().equals("static"))
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(methodToInsert.getToken(), "Una interface no puede tener metodos estaticos"));
        if (!methodAlreadyExists(methodToInsert))
            classMethods.put(methodToInsert.getMethodName(), methodToInsert);
        else{
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

    public void consolidate(){
        if (!consolidated) {
            if (!hasCyclicInheritance){
                for (Interface interfaceToCheck : this.ancestorInterfaces){
                    Interface ancestorInterface= SymbolTable.getInstance().getInterface(interfaceToCheck.getClassName());
                    if (ancestorInterface!=null){
                        if (!ancestorInterface.isConsolidated()){
                            ancestorInterface.consolidate();
                        }
                        consolidateMethods(ancestorInterface);
                        ancestorInterface.setConsolidated();
                    }
                }
            }
        }
    }


    private void checkCyclicInheritance() {
        ArrayList<String> ancestorsList = new ArrayList<>();
        ancestorsList.add(getClassName());
        for (Interface ancestorInterface : ancestorInterfaces){
            Token ancestorToken = ancestorInterface.getToken();
            Interface interfaceInSymbolTable = SymbolTable.getInstance().getInterface(ancestorInterface.getClassName());
            if (interfaceInSymbolTable != null){
                if (interfaceInSymbolTable.hasCyclicInheritance(ancestorsList, ancestorToken)){
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(ancestorToken, "Herencia circular: la interface " + "\"" + getClassName() + "\"" + " se extiende a si misma"));
                    hasCyclicInheritance = true;
                }
            }
        }
    }

    public boolean hasCyclicInheritance(ArrayList<String> ancestorsList, Token interfaceToken) {
        if (!ancestorsList.contains(getClassName())) {
            ancestorsList.add(interfaceToken.getLexeme());
            for (Interface ancestorInterface : this.ancestorInterfaces) {
                Token ancestorToken = ancestorInterface.getToken();
                Interface interfaceToCheck = SymbolTable.getInstance().getInterface(ancestorToken.getLexeme());
                if (interfaceToCheck != null) {
                    if (interfaceToCheck.hasCyclicInheritance(ancestorsList, ancestorToken)) {
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
        for (MethodOrConstructor ancestorMethod: interfaceToConsolidateWith.classMethods.values()) {
            String methodName = ancestorMethod.getMethodName();
            if (!classMethods.containsKey(methodName))
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
                MethodOrConstructor concreteClassMethod = concreteClassToCheck.getMethod(methodName);
                if (!method.methodsHeadersAreEquals(concreteClassMethod)) {
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(concreteClassToCheck.getMethod(method.getMethodName()).getToken(), "El metodo " + "\"" + method.getMethodName() + "\"" + " no respeta el encabezado del metodo definido en la interface " + getClassName()));
                }
                concreteClassMethod.setAsInterfaceMethod();
                concreteClassMethod.setInterface(this);
            }
            else {
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(interfaceToken, "La clase " + concreteClassToCheck.getClassName() + " no implementa todos los metodos de la interface " + getClassName()));
                break;
            }
        }
    }

    public void addAncestorInterface(Interface interfaceToAdd) {

        String interfaceToAddName = interfaceToAdd.getClassName();
        String interfaceNameToCompare;
        boolean nameExists = false;
        for (Interface ancestorInterface: this.ancestorInterfaces) {
            interfaceNameToCompare = ancestorInterface.getClassName();
            if (interfaceToAddName.equals(interfaceNameToCompare)) {
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(interfaceToAdd.getToken(), "La interface " + "\"" + this.getClassName() + "\"" + " ya extiende a la interface " + interfaceToAdd.getClassName()));
                nameExists = true;
                break;
            }
        }
        if (!nameExists) {
            this.ancestorInterfaces.add(interfaceToAdd);

        }
    }


    public boolean hasAncestorInterface(String interfaceName) {
        boolean toReturn;
        for (Interface i: SymbolTable.getInstance().getInterface(this.getClassName()).getAncestorsInterfaces()){
            if (i.getClassName().equals(interfaceName)){
                return true;
            }
        }
        for (Interface i: SymbolTable.getInstance().getInterface(this.getClassName()).getAncestorsInterfaces()){
            toReturn = SymbolTable.getInstance().getInterface(i.getClassName()).hasAncestorInterface(interfaceName);
            if (toReturn){
                return true;
            }
        }
        return false;
    }

    private boolean isConsolidated() {
        return consolidated;
    }

    public boolean hasOffsetsGenerated() {
        return this.offsetsGenerated;
    }

    public void setOffsetsAsSet() {
        this.offsetsGenerated = true;
    }

    public boolean hasAncestors() {
        return ancestorInterfaces.size() >0;
    }

    public int getGreaterOffset() {
        int greatestOffsetNumber = -1;
        for (MethodOrConstructor method: this.classMethods.values()) {
            if (method.hasOffset())
                if (method.getOffset() > greatestOffsetNumber)
                    greatestOffsetNumber = method.getOffset();
        }
        return greatestOffsetNumber + 1;
    }

}