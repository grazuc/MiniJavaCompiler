package SyntacticAnalyzer;

import LexicalAnalyzer.Token;

public class SyntacticException extends Exception{

    private Token currentToken;
    private String IDToken;

    public SyntacticException (Token currenttoken, String tokenID){
        currentToken= currenttoken;
        IDToken = tokenID;
    }

    public String getMessage() {
        return "Error Sintactico en linea "
                + currentToken.getLineNumber()
                + ": se esperaba "
                + IDToken
                + " y se encontro "
                + currentToken.getTokenId()
                + generateStringError();
    }

    private String generateStringError() {
        return "\n\n[Error:" +
                currentToken.getLexeme()
                + "|"
                + currentToken.getLineNumber()
                + "]\n\n";
    }

}
