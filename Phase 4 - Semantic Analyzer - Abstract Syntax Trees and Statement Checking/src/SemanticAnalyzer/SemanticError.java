package SemanticAnalyzer;

import LexicalAnalyzer.Token;

public class SemanticError {

    private Token errorToken;
    private String errorMessage;

    public SemanticError(Token token, String error) {
        errorToken = token;
        errorMessage = error;
    }

    public Token getErrorToken() {
        return errorToken;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}