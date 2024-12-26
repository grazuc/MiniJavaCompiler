package SemanticAnalyzer;

import LexicalAnalyzer.Token;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;
import InstructionGenerator.*;
import org.w3c.dom.Attr;

public class ConcreteClass extends Class {

    private Token ancestorClassToken;
    private Hashtable<String, Attribute> attributes;
    private MethodOrConstructor classConstructor;

    private boolean hasConstructor;
    private boolean hasRepeatedInterfaces;
    private boolean hasImplements;
    private boolean methodOffsetsGenerated;
    private boolean attributeOffsetsGenerated;
    private int cirSize;
    private int vtSize;
    private Hashtable<Integer, MethodOrConstructor> dynamicMethodsOffsetsMap;
    private boolean vtSizeIsSet;

    public ConcreteClass(Token classToken, Token ancestorToken) {
        super(classToken);
        attributes = new Hashtable<>();
        ancestorClassToken = ancestorToken;
        hasRepeatedInterfaces = false;
        hasConstructor = false;
        classConstructor=null;
        hasImplements = false;
        this.attributeOffsetsGenerated = false;
        this.methodOffsetsGenerated = false;
        this.cirSize = 1; //El 0 es para la VT
        this.dynamicMethodsOffsetsMap = new Hashtable<>();
        this.vtSizeIsSet = false;
        this.vtSize = 0;
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

    public int getTotalOfDynamicMethods() {
        int totalOfDynamicMethods = 0;
        for (MethodOrConstructor method: this.classMethods.values()) {
            if (!method.getStaticHeader().equals("static"))
                totalOfDynamicMethods++;
        }
        return totalOfDynamicMethods;
    }

    public void setHasImplements(){
        hasImplements = true;
    }

    public Hashtable<String, Attribute> getAttributes() {
        return attributes;
    }

    public boolean hasAncestorInterface(String interfaceToCheckName){
        boolean toReturn = false;
        for (Interface i: this.ancestorInterfaces) {
            if (i.getClassName().equals(interfaceToCheckName))
                return true;
            if (i.hasAncestorInterface(interfaceToCheckName))
                toReturn =  true;
        }
        return toReturn;
    }


    public void insertMethod(MethodOrConstructor methodToInsert) {
        if ((methodToInsert.getMethodName().equals("main")) && ( !methodToInsert.getStaticHeader().equals("static") || !methodToInsert.getReturnType().getClassName().equals("void") || methodToInsert.hasParameters())){
            //System.out.println("Entre en clase concreta");
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(methodToInsert.getToken(),"El metodo main esta mal declarado"));
        }
        else {
            if (!methodAlreadyExists(methodToInsert))
                classMethods.put(methodToInsert.getMethodName(), methodToInsert);
            else{
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(methodToInsert.getToken(), "Ya existe un metodo con nombre " + "\"" + methodToInsert.getMethodName() + "\"" + " en la clase " + getClassName()));
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
        if (!attributes.containsKey(attribute.getAttributeName())){
            Attribute attributeToInsert = new Attribute(attribute.getAttributeToken(),attribute.getStaticOptional(),attribute.getAttributeType());
            attributes.put(attributeToInsert.getAttributeName(), attributeToInsert);
        }
        else
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(attribute.getAttributeToken(), "El atributo " + attribute.getAttributeToken().getLexeme() + " ya esta declarado en la clase " + classToken.getLexeme()));
    }


    public void checkDeclarations() {
        checkCyclicInheritance();
        insertConstructor();
        checkAncestorClass();
        //checkInterfacesDeclarations();
        checkAttributesDeclaration();
        checkMethodsDeclaration();
    }

    private void checkInterfacesDeclarations(){
        for (Interface interfaceToCheck : this.ancestorInterfaces) {
            Token interfaceToken = interfaceToCheck.getToken();
            String interfaceName = interfaceToken.getLexeme();
            if (!this.interfaceIsDeclared(interfaceName))
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(interfaceToken, "La interface " + interfaceName + " no esta declarada"));
        }
    }

    private void insertConstructor() {
        if (!hasConstructor){
            classConstructor = new Constructor(new Token("idClase", getClassName(), 0));
            hasConstructor = true;
        } else {
            classConstructor.checkDeclaration();
        }
    }

    public void insertConstructor(MethodOrConstructor constructor){
        if ((!hasConstructor) && (constructor.getToken().getLexeme().equals(classToken.getLexeme()))){
            classConstructor = constructor;
            hasConstructor= true;
        } else {
            if (hasConstructor){
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(constructor.token,"La clase "+classToken.getLexeme()+" ya tiene un constructor definido"));
            } else if (!constructor.getToken().getLexeme().equals(classToken.getLexeme())){
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(constructor.token,"El nombre de la clase " +classToken.getLexeme() +" no coincide con el nombre del constructor "+constructor.token.getLexeme()));
            }
        }
    }

    private void checkAncestorClass() {
        if (ancestorClassToken!= null){
            String ancestorClassOrInterfaceName = ancestorClassToken.getLexeme();
            if (hasImplements){
                if (concreteClassIsDeclared(ancestorClassOrInterfaceName)) {
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(ancestorClassToken, "Una clase concreta no puede implementar a otra clase concreta"));
                }
            } else {
                if (interfaceIsDeclared(ancestorClassOrInterfaceName)){
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(ancestorClassToken, "Una clase concreta no puede extender una interfaz"));
                }
            }
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
        for (MethodOrConstructor methodToCheck: classMethods.values())
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
            if (!getAttributes().containsKey(ancestorAttributeName)){
                insertAttribute(ancestorAttribute);
                setInheritedAttribute(ancestorAttribute.getAttributeName());
            }
            else {
                Attribute thisClassAttribute = getAttributes().get(ancestorAttributeName);
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(thisClassAttribute.getAttributeToken(), "El atributo " + "\"" + thisClassAttribute.getAttributeName() + "\"" + " ya fue declarado en una clase ancestra"));
            }
        }
    }

    public void consolidateMethods(Class classToConsolidateWith) {
        //System.out.println("Entre a consolidateMethods de ConcreteClass con clase: "+classToConsolidateWith.getClassName());
        for (MethodOrConstructor ancestorMethod: classToConsolidateWith.getMethods().values()) {
            String methodName = ancestorMethod.getMethodName();
            if (!getMethods().containsKey(methodName))
                insertMethod(ancestorMethod);
            else {
                MethodOrConstructor thisClassMethod = this.getMethods().get(methodName);
                if (!ancestorMethod.correctRedefinedMethodHeader(thisClassMethod))
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(thisClassMethod.getToken(), "El metodo" + "\"" + thisClassMethod.getMethodName() + "\"" + " esta incorrectamente redefinido"));
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
        //System.out.println("Holiwis");
        for (Interface interfaceThatImplements: this.ancestorInterfaces) {
            Token interfaceToken = interfaceThatImplements.getToken();
            String interfaceName = interfaceToken.getLexeme();
            Interface interfaceToVerifyMethodsImplementations = SymbolTable.getInstance().getInterface(interfaceName);
            if (interfaceToVerifyMethodsImplementations != null)
                interfaceToVerifyMethodsImplementations.verifyMethodsImplementation(interfaceToken, this);
        }
    }

    private boolean concreteClassIsDeclared(String concreteClassName) {
        return SymbolTable.getInstance().concreteClassIsDeclared(concreteClassName);
    }


    private boolean interfaceIsDeclared(String interfaceName) {
        return SymbolTable.getInstance().interfaceIsDeclared(interfaceName);
    }

    public MethodOrConstructor getClassConstructor() {
        return this.classConstructor;
    }

    public boolean hasAttributeOffsetsGenerated() {
        return this.attributeOffsetsGenerated;
    }

    public boolean hasMethodOffsetsGenerated() {
        return this.methodOffsetsGenerated;
    }

    public int getCirSize() {
        return this.cirSize;
    }

    public int getVtSize() {
        return this.vtSize;
    }

    public String getVTLabel() {
        return "VT_Clase" + this.getClassName();
    }

    public void generateOffsets() {
        this.generateAttributesOffsets();
        this.generateClassMethodsOffsets();
    }

    public void generateAttributesOffsets() {
        if (this.getAncestorClass() != null)
            if (!this.getAncestorClass().getClassName().equals("Object"))
                if (!this.getAncestorClass().hasAttributeOffsetsGenerated())
                    this.getAncestorClass().generateOffsets();

        if (this.getAncestorClass() != null) {
            for (Attribute ancestorAttribute: this.getAncestorClass().getAttributes().values()) {
                for (Attribute attribute: this.attributes.values())
                    if (ancestorAttribute.getAttributeName().equals(attribute.getAttributeName())) {
                        attribute.setOffset(ancestorAttribute.getOffset());
                    }
            }
            this.cirSize = this.getAncestorClass().getCirSize();
        }
        for (Attribute attribute: this.attributes.values()) {
            if (!attribute.isInherited()){
                //System.out.println("Hola");
                attribute.setOffset(this.getCirSize());
                this.cirSize += 1;
                //System.out.println("Tamaño CIR: "+cirSize);
            }
        }
        this.attributeOffsetsGenerated = true;
    }

    public void generateClassMethodsOffsets() {
        if (classConstructor!=null)
            classConstructor.setParametersOffset();
        if (this.getAncestorClass() != null)
            if (!this.getAncestorClass().hasMethodOffsetsGenerated())
                this.getAncestorClass().generateClassMethodsOffsets();

        if (this.getAncestorClass() != null)
            if (!this.getAncestorClass().getClassName().equals("Object")) {
                for (MethodOrConstructor ancestorMethod: this.getAncestorClass().getMethods().values())
                    for (MethodOrConstructor method: this.classMethods.values())
                        if (!method.isInterfaceMethod())
                            if (ancestorMethod.getMethodName().equals(method.getMethodName())) {
                                //System.out.println("Voy a setear el offset: "+ancestorMethod.getOffset()+" al metodo: "+ancestorMethod.getMethodName());
                                method.setOffset(ancestorMethod.getOffset());
                                method.setOffsetIsSet();
                            }
                this.vtSize = this.getAncestorClass().getVtSize();
                //System.out.println("Tamaño vt: "+vtSize);
            }

        for (MethodOrConstructor method: this.classMethods.values()) {
            //System.out.println("Entre con metodo: "+method.getMethodName());
            if (!method.getStaticHeader().equals("static")){
                if (!method.isInterfaceMethod()) {
                    //System.out.println("Hola");
                    if (!method.hasOffset()) {
                        //System.out.println("Voy a setear el offset: "+vtSize+" al metodo: "+method.getMethodName());
                        method.setOffset(this.vtSize);
                        method.setOffsetIsSet();
                        this.vtSize += 1;
                        //System.out.println("Tamaño vt: "+vtSize);
                    }
                    this.dynamicMethodsOffsetsMap.put(method.getOffset(), method);
                }}
        }
        this.methodOffsetsGenerated = true;
    }

    public void generateInterfaceMethodsOffsets() {
        for (MethodOrConstructor interfaceMethodToSetOffset : this.classMethods.values()) {
            //System.out.println("Metodo: "+interfaceMethodToSetOffset.getMethodName());
            if (interfaceMethodToSetOffset.isInterfaceMethod()) {
                Interface interfaceMethod = interfaceMethodToSetOffset.getInterfaceMethod();
                MethodOrConstructor methodInInterface = interfaceMethod.getMethod(interfaceMethodToSetOffset.getMethodName());
                //System.out.println("Voy a setear el offset: "+methodInInterface.getOffset()+" al metodo: "+methodInInterface.getMethodName());
                interfaceMethodToSetOffset.setOffset(methodInInterface.getOffset());
                this.dynamicMethodsOffsetsMap.put(interfaceMethodToSetOffset.getOffset(), interfaceMethodToSetOffset);
                interfaceMethodToSetOffset.setOffsetIsSet();
            }
        }
        this.vtSize = this.getGreatestOffset();
        //System.out.println("Tamaño vt: "+vtSize);
    }

    //genera offsets de aquellos metodos heredados de una clase
    //con igual nombre que uno de una interface
    //para poder usar en la VT
    public void generateInheritedMethodsOffsetsForVt() {
        if (this.getAncestorClass() != null && this.getAncestorClass().getClassName().equals("Object"))
            for (MethodOrConstructor method: this.getAncestorClass().getMethods().values()) {
                if (!method.getStaticHeader().equals("static")) {
                    int ancestorMethodOffset = method.getOffset();
                    if (!this.dynamicMethodsOffsetsMap.containsKey(ancestorMethodOffset)) {
                        MethodOrConstructor thisClassMethod = SymbolTable.getInstance().getConcreteClass(this.getClassName()).getMethod(method.getMethodName());
                        this.dynamicMethodsOffsetsMap.put(ancestorMethodOffset, thisClassMethod);
                    }
                }
            }
    }

    private int getGreatestOffset() {
        int greatestMethodOffset = 0;
        for (MethodOrConstructor method: this.classMethods.values()) {
            //System.out.println("Entre con metodo: "+method.getMethodName()+" con offset: "+method.getOffset());
            if (method.getOffset() > greatestMethodOffset) {
                //System.out.println("Hola");
                greatestMethodOffset = method.getOffset();
            }
        }
        return greatestMethodOffset;
    }

    public void generateVT() throws IOException {
        InstructionGenerator.getInstance().setDataMode();
        InstructionGenerator.getInstance().generateInstruction("VT_Clase" + this.getClassName() + ":");
        String VTInstruction = "DW";
        if (this.dynamicMethodsOffsetsMap.size() > 0) {
            //System.out.println("Tamaño vt: "+vtSize);
            //la clase tiene metodos dinamicos

            for (int offset = 0; offset <= this.vtSize; offset++) {
                //System.out.println("Entre");
                MethodOrConstructor method = this.dynamicMethodsOffsetsMap.get(offset);
                //System.out.println("(generateVT) Current vtSize: "+vtSize);
                //System.out.println("(generateVT) Current size dynamicmethodsoffsetsmap: "+dynamicMethodsOffsetsMap.size());
                if (method != null)
                    VTInstruction += " " + method.getMethodLabel() + ",";
                else
                    VTInstruction += " 0,";
            }
            VTInstruction = VTInstruction.substring(0, VTInstruction.length() - 1);  //elimino la , dps del ultimo metodo
            InstructionGenerator.getInstance().generateInstruction(VTInstruction);
        }
        else
            //la clase no tiene metodos dinamicos
            InstructionGenerator.getInstance().generateInstruction("NOP ; No se realiza ninguna operación ya que la clase en cuestión no tiene métodos dinámicos");

        ArrayList<Attribute> staticAttributes = new ArrayList<>();
        for (Attribute a : attributes.values()){
                if (a.getStaticOptional().equals("static")){
                    staticAttributes.add(a);
                }
            }
        if (staticAttributes.size() >0 ){
            //System.out.println("Hola");
            InstructionGenerator.getInstance().setDataMode();
        }
        for (Attribute a : staticAttributes){
            if (!a.isInVt()){
                String instruction = a.getAttributeLabel() +": DW 0";
                InstructionGenerator.getInstance().generateInstruction(instruction);
                a.setIsInVt();
            }
        }
    }

    private void setInheritedAttribute(String attributeName){
        for(Attribute atributo : attributes.values()) {
            if (atributo.getAttributeName().equals(attributeName)) {
                atributo.setIsInherited();
                break;
            }
        }
    }

    public void generateCode() throws IOException {
        InstructionGenerator.getInstance().setCodeMode();
        for (MethodOrConstructor method: this.classMethods.values())
            if (!method.codeIsGenerated()) {
                method.generateCode();
                method.setCodeGenerated();
            }
        InstructionGenerator.getInstance().setCodeMode();
        if (hasConstructor){
            this.classConstructor.generateCode();
        }
    }

}
