package SemanticAnalyzer;

import LexicalAnalyzer.Token;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;

public class ConcreteClass extends Class {

    private Token ancestorClassToken;
    private Hashtable<String, Attribute> attributes;
    private Constructor classConstructor;

    private boolean hasConstructor;
    private boolean hasRepeatedInterfaces;

    public ConcreteClass(Token classToken, Token ancestorToken) {
        super(classToken);
        attributes = new Hashtable<>();
        ancestorClassToken = ancestorToken;
        hasRepeatedInterfaces = false;
        hasConstructor = false;
        classConstructor=null;
    }

    public Hashtable<String, Attribute> getAttributes() {
        return attributes;
    }

    public void insertMethod(Method methodToInsert) {
        if ((methodToInsert.getMethodName().equals("main")) && ( !methodToInsert.getStaticHeader().equals("static") || !methodToInsert.getReturnType().equals("void") || methodToInsert.hasParameters())){
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(methodToInsert.getMethodToken(),"El metodo main esta mal declarado"));
        }
        else {
            if (!methodAlreadyExists(methodToInsert))
                classMethods.put(methodToInsert.getMethodName(), methodToInsert);
            else{
                methodToInsert.checkDeclaration();
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(methodToInsert.getMethodToken(), "Ya existe un metodo con nombre " + "\"" + methodToInsert.getMethodName() + "\"" + " en la clase " + getClassName()));
            }
        }
    }

    public void insertConstructor(Constructor constructor){
        if ((!hasConstructor) && (Objects.equals(classToken.getLexeme(), constructor.constructorToken.getLexeme()))){
                classConstructor = constructor;
                hasConstructor= true;
        } else {
            if (!Objects.equals(classToken.getLexeme(), constructor.constructorToken.getLexeme())){
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(constructor.constructorToken,"El nombre de la clase " +classToken.getLexeme() +" no coincide con el nombre del constructor "+constructor.constructorToken.getLexeme()));
            } else {
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(constructor.constructorToken,"La clase "+classToken.getLexeme()+" ya tiene un constructor definido"));
            }
        }
    }

    @Override
    public boolean hasConstructor() {
        return hasConstructor;
    }

    @Override
    public void setHasConstructorTrue() {
        hasConstructor=true;
    }

    public void insertAttribute(Attribute attribute) {
        if (!attributes.containsKey(attribute.getAttributeName()))
            attributes.put(attribute.getAttributeName(), attribute);
        else
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(attribute.getAttributeToken(), "El atributo " + attribute.getAttributeToken().getLexeme() + " ya esta declarado en la clase " + classToken.getLexeme()));
    }

    public void checkDeclarations() {
        checkCyclicInheritance();
        checkAncestorClass();
        checkAttributesDeclaration();
        checkMethodsDeclaration();
        insertConstructor();
    }

    private void insertConstructor() {
        if (!hasConstructor){
            classConstructor = new Constructor(new Token("idClase", getClassName(), 0));
        } else {
            classConstructor.checkDeclaration();
        }
    }

    private void checkAncestorClass() {
        if (ancestorClassToken!= null){
            String ancestorClassOrInterfaceName = ancestorClassToken.getLexeme();
            if (!interfaceIsDeclared(ancestorClassOrInterfaceName) && !concreteClassIsDeclared(ancestorClassOrInterfaceName)){
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(ancestorClassToken,"La clase u interfaz "+ ancestorClassToken.getLexeme()+ " no esta declarada"));
            }
        }
    }



    private void checkCyclicInheritance() {
        ArrayList<String> ancestorsList = new ArrayList<>();
        if (hasCyclicInheritance(ancestorsList)) {
            hasCyclicInheritance = true;
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(ancestorClassToken, "Herencia circular: la clase " + "\"" + getClassName() + "\"" + " se extiende a si misma"));
        }
    }

    public boolean hasCyclicInheritance(ArrayList<String> ancestorsList) {
        if (getAncestorClass() != null) {
            if (!ancestorsList.contains(getAncestorClass().getClassName())) {
                ancestorsList.add(ancestorClassToken.getLexeme());
                return getAncestorClass().hasCyclicInheritance(ancestorsList);
            }
            else
                return true;
        }
        return false;
    }


    private void checkAttributesDeclaration() {
        for (Attribute attributeToCheck: attributes.values())
            attributeToCheck.checkDeclaration();
    }

    private void checkMethodsDeclaration() {
        for (Method methodToCheck: classMethods.values())
            methodToCheck.checkDeclaration();
    }

    public void consolidate() {
        if (!consolidated)
            if (!hasCyclicInheritance)
                if (getAncestorClass() != null) {
                    ConcreteClass ancestorClass = getAncestorClass();
                    if (!ancestorClass.isConsolidated())
                        ancestorClass.consolidate();
                    consolidateAttributes(ancestorClass);
                    consolidateMethods(ancestorClass);
                    verifyInterfacesMethods();
                    consolidated = true;
                } else {
                    if (SymbolTable.getInstance().getInterface(ancestorClassToken.getLexeme())!=null){
                        Interface ancestorInterface = SymbolTable.getInstance().getInterface(ancestorClassToken.getLexeme());
                        if (!ancestorInterface.consolidated){
                            ancestorInterface.consolidate();
                        }
                        consolidateMethods(SymbolTable.getInstance().getConcreteClass("Object"));
                        verifyInterfacesMethods();
                        consolidated = true;
                    }
                }
    }

    private void consolidateAttributes(ConcreteClass ancestorClass) {
        for (Attribute ancestorAttribute: ancestorClass.getAttributes().values()) {
            String ancestorAttributeName = ancestorAttribute.getAttributeName();
            if (!getAttributes().containsKey(ancestorAttributeName))
                insertAttribute(ancestorAttribute);
            else {
                Attribute thisClassAttribute = getAttributes().get(ancestorAttributeName);
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(thisClassAttribute.getAttributeToken(), "El atributo " + "\"" + thisClassAttribute.getAttributeName() + "\"" + " ya fue declarado en una clase ancestra"));
            }
        }
    }

    public void consolidateMethods(Class classToConsolidateWith) {
        for (Method ancestorMethod: classToConsolidateWith.getMethods().values()) {
            String methodName = ancestorMethod.getMethodName();
            if (!getMethods().containsKey(methodName))
                insertMethod(ancestorMethod);
            else {
                Method thisClassMethod = getMethod(methodName);
                if (!thisClassMethod.correctRedefinedMethodHeader(ancestorMethod))
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(thisClassMethod.getMethodToken(), "El metodo " + "\"" + thisClassMethod.getMethodName() + "\"" + " esta incorrectamente redefinido"));
            }
        }
    }

    public ConcreteClass getAncestorClass() {
        if (ancestorClassToken != null)
            return SymbolTable.getInstance().getConcreteClass(ancestorClassToken.getLexeme());
        return null;
    }

    private boolean isConsolidated() {
        return consolidated;
    }

    private void verifyInterfacesMethods() {
        if (ancestorClassToken!=null){
            Token interfaceToken = ancestorClassToken;
            String interfaceName = interfaceToken.getLexeme();
            Interface interfaceToVerifyMethodsImplementations = SymbolTable.getInstance().getInterface(interfaceName);
            if (interfaceToVerifyMethodsImplementations != null){
                interfaceToVerifyMethodsImplementations.verifyMethodsImplementation(interfaceToken, this);
            }
        }
    }

    private boolean concreteClassIsDeclared(String concreteClassName) {
        return SymbolTable.getInstance().concreteClassIsDeclared(concreteClassName);
    }


    private boolean interfaceIsDeclared(String interfaceName) {
        return SymbolTable.getInstance().interfaceIsDeclared(interfaceName);
    }

}
