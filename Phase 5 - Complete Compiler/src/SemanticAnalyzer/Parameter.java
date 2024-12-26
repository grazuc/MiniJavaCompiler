package SemanticAnalyzer;

import LexicalAnalyzer.Token;

public class Parameter {
    private Token parameterToken;
    private Type parameterType;
    private int offset;

    public Parameter(Token token, Type parameter) {
        parameterToken = token;
        parameterType=parameter;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getParameterName() {
        return parameterToken.getLexeme();
    }

    public Type getParameterType() {
        return parameterType;
    }

    public Token getParameterToken() {
        return parameterToken;
    }

}