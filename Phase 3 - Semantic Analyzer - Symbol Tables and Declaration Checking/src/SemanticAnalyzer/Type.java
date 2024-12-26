package SemanticAnalyzer;

import LexicalAnalyzer.Token;

public abstract class Type {
    private Token tokenType;

    public Type(Token token){
        tokenType=token;
    }

    public Token getToken(){
        return tokenType;
    }

    public String toString(){
        return tokenType.getLexeme();
    }

    public String getClassName(){
        return tokenType.getLexeme();
    }

    public abstract boolean isPrimitive();

}
