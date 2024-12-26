package SemanticAnalyzer;

import LexicalAnalyzer.Token;

import java.util.Objects;

public class Attribute {

    private String staticOptional;
    private Token attributeToken;
    private Type attributeType;

    public Attribute(Token token, String staticoptional, Type Attributetype){
            attributeToken=token;
            staticOptional=staticoptional;
            attributeType=Attributetype;
    }

    public void checkDeclaration() {
        if (!attributeType.isPrimitive() && !referenceTypeExists(attributeType.getClassName()))
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(attributeType.getToken(), "El tipo " + attributeType.getClassName() + " no esta declarado"));
    }

    private boolean referenceTypeExists(String className) {
        return SymbolTable.getInstance().concreteClassIsDeclared(className) || SymbolTable.getInstance().interfaceIsDeclared(className);
    }

    public String getAttributeName() {
        return attributeToken.getLexeme();
    }

    public Token getAttributeToken() {
        return attributeToken;
    }

}
