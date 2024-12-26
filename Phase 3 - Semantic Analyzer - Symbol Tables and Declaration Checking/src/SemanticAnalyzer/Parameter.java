package SemanticAnalyzer;

import LexicalAnalyzer.Token;

public class Parameter {
    private Token parameterToken;
    private Type parameterType;

    public Parameter(Token token, Type parameter) {
        parameterToken = token;
        parameterType=parameter;
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