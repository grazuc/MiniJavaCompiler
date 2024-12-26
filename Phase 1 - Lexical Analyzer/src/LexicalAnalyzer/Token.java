package LexicalAnalyzer;

public class Token {
    private String lexeme;
    private String TokenID;
    private int lineNumber;

    public Token(String idtoken, String lex, int line){
        lexeme = lex;
        lineNumber = line;
        TokenID = idtoken;
    }

    public String getTokenId(){
        return TokenID;
    }

    public String getLexeme(){
        return lexeme;
    }

    public int getLineNumber(){
        return lineNumber;
    }

    public String toString(){
        return "("+TokenID+","+lexeme+","+lineNumber+")";
    }
}
