package LexicalAnalyzer;

public class LexicalException extends Exception {

    private String lexemeWithError;
    private int lineNumber;
    private int columnNumber;
    private String lexicalErrorLine;
    private String errorType;

    public LexicalException(String lexemewitherror, int linenumber, int columnnumber, String errordetail, String lexicalerrorline) {
        lexemeWithError = lexemewitherror;
        lineNumber = linenumber;
        columnNumber = columnnumber;
        errorType = errordetail;
        lexicalErrorLine = lexicalerrorline;
    }

    public String getMessage() {
        return generateStringError();
    }

    public String generateStringError() {
        return "Error Lexico en linea " +lineNumber + ", columna " +columnNumber + ": " /*+lexema */ +errorType+ "\n" + generateErrorDetail() + "\n[Error:"+lexemeWithError +"|"+lineNumber + "]\n\n";
    }

    public String generateErrorDetail() {
        String errorToShow = "Detalle: ";
        int initStringLength = errorToShow.length();
        errorToShow += lexicalErrorLine;
        String errorPointer = "";
        for (int totalPointerDisplacement = 1; totalPointerDisplacement < (columnNumber + initStringLength); totalPointerDisplacement++)
            errorPointer+= " ";
        errorPointer += "^";
        return errorToShow + "\n" + errorPointer;
    }

}
