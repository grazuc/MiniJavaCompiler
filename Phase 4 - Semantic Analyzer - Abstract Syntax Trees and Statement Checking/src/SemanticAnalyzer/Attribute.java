package SemanticAnalyzer;

import LexicalAnalyzer.Token;

import java.util.Objects;

public class Attribute {

    private String staticOptional;
    private Token attributeToken;
    private Type attributeType;
    private boolean isInherited;

    public Attribute(Token token, String staticoptional, Type Attributetype){
            attributeToken=token;
            staticOptional=staticoptional;
            attributeType=Attributetype;
            this.isInherited = false;
    }

    public void checkDeclaration() {
        if (!attributeType.isPrimitive() && !referenceTypeExists(attributeType.getClassName()))
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(attributeType.getToken(), "El tipo " + attributeType.getClassName() + " no esta declarado"));
    }

    private boolean referenceTypeExists(String className) {
        return SymbolTable.getInstance().concreteClassIsDeclared(className) || SymbolTable.getInstance().interfaceIsDeclared(className);
    }

    public void setInherited() {
        this.isInherited = true;
    }

    public boolean isInherited() {
        return this.isInherited;
    }

    public Type getAttributeType() {
        return this.attributeType;
    }

    public String getStaticOptional(){
        return staticOptional;
    }

    public String getAttributeName() {
        return attributeToken.getLexeme();
    }

    public Token getAttributeToken() {
        return attributeToken;
    }

}
