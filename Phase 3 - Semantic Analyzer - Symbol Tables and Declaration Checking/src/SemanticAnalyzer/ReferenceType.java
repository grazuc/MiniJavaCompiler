package SemanticAnalyzer;

import LexicalAnalyzer.Token;

public class ReferenceType extends Type {

    public ReferenceType(Token token) {
        super(token);
    }

    public boolean isPrimitive() {
        return false;
    }

}