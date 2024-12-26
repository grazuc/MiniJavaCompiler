package SemanticAnalyzer;

import LexicalAnalyzer.Token;

public class PrimitiveType extends Type {

    public PrimitiveType(Token token) {
        super(token);
    }

    public boolean isPrimitive() {
        return true;
    }

}