package SemanticAnalyzer;

import LexicalAnalyzer.Token;

import java.util.Objects;

public class Attribute {

    private String staticOptional;
    private Token attributeToken;
    private Type attributeType;
    private int offset;
    private boolean isStatic;
    static boolean isInVt;
    private boolean isInherited;
    public Attribute(Token token, String staticoptional, Type Attributetype){
            attributeToken=token;
            staticOptional=staticoptional;
            attributeType=Attributetype;
            isStatic = false;
            isInVt = false;
            isInherited = false;
    }


    public boolean isInherited(){
        return isInherited;
    }

    public void setIsInherited(){
        isInherited = true;
    }
    public void setIsInVt(){
        isInVt=true;
    }

    public boolean isInVt(){
        return isInVt;
    }
    public String getAttributeLabel(){
        return "Attr_"+this.getAttributeName();
    }

    public void checkDeclaration() {
        if (!attributeType.isPrimitive() && !referenceTypeExists(attributeType.getClassName()))
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(attributeType.getToken(), "El tipo " + attributeType.getClassName() + " no esta declarado"));
    }

    public boolean isStatic(){
        return isStatic;
    }

    public void setStatic(){
        isStatic = true;
    }

    private boolean referenceTypeExists(String className) {
        return SymbolTable.getInstance().concreteClassIsDeclared(className) || SymbolTable.getInstance().interfaceIsDeclared(className);
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

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
