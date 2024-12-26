package LexicalAnalyzer;

import FileHandler.FileHandler;
import java.io.IOException;
import java.util.Map;
public class LexicalAnalyzer {
    private int currentCharacter;
    private String lexeme;
    private String lineError;
    private Map<String,String> keywordsMap;
    private FileHandler fileHandler;
    private int lineNumberForError;
    private int columnNumberForError;
    private int foundEOL;

    public LexicalAnalyzer(FileHandler filehandler,Map<String,String> map) throws IOException{
        fileHandler=filehandler;
        keywordsMap=map;
        updateCurrentCharacter();
    }

    private void updateLexeme() {
        lexeme= lexeme + (char) currentCharacter;
    }

    private void updateCurrentCharacter() throws IOException{
        fileHandler.nextCharacter();
        currentCharacter = fileHandler.getCurrentCharacter();
    }

    public Token nextToken() throws IOException, LexicalException {
        lexeme = "";
        return estado_0();
    }

    private Token estado_0() throws IOException, LexicalException {
        if (Character.isWhitespace(currentCharacter)) {
            updateCurrentCharacter();
            return estado_0();
        } else if (Character.isDigit(currentCharacter)) {
            updateLexeme();
            updateCurrentCharacter();
            return estado_1();
        } else if (Character.isLetter(currentCharacter) && Character.isUpperCase(currentCharacter)) {
            updateLexeme();
            updateCurrentCharacter();
            return estado_10();
        } else if (Character.isLetter(currentCharacter) && Character.isLowerCase(currentCharacter)) {
            updateLexeme();
            updateCurrentCharacter();
            return estado_11();
        } else if (currentCharacter == '>'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_12();
        } else if (currentCharacter == '<'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_14();
        } else if (currentCharacter == '!'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_16();
        } else if (currentCharacter == '='){
            updateLexeme();
            updateCurrentCharacter();
            return estado_18();
        } else if (currentCharacter == '*'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_20();
        } else if (currentCharacter == '-'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_21();
        } else if (currentCharacter == '+'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_23();
        } else if (currentCharacter == '%'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_25();
        } else if (currentCharacter == '&'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_26();
        } else if (currentCharacter == '|'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_28();
        } else if (currentCharacter == '('){
            updateLexeme();
            updateCurrentCharacter();
            return estado_30();
        } else if (currentCharacter == ')'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_31();
        } else if (currentCharacter == '{'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_32();
        } else if (currentCharacter == '}'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_33();
        } else if (currentCharacter == ';'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_34();
        } else if (currentCharacter == '.'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_35();
        } else if (currentCharacter == ','){
            updateLexeme();
            updateCurrentCharacter();
            return estado_36();
        } else if (currentCharacter == '/'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_37();
        } else if (currentCharacter == '"'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_41();
        } else if (currentCharacter == '\''){
            updateLexeme();
            updateCurrentCharacter();
            return estado_44();
        } else if (currentCharacter == -1){
            return estado_54();
        } else {
            updateLexeme();
            updateCurrentCharacter();
            throw new LexicalException(lexeme, fileHandler.getCurrentRow(),fileHandler.getCurrentColumn(),lexeme + " no es un símbolo válido",fileHandler.getLineWithError());
        }
    }

    private Token estado_1() throws IOException, LexicalException {
        if (Character.isDigit(currentCharacter)){
            updateLexeme();
            updateCurrentCharacter();
            return estado_2();
        } else {
            return new Token("intLiteral",lexeme,fileHandler.getCurrentRow());
        }
    }

    private Token estado_2() throws IOException, LexicalException {
        if (Character.isDigit(currentCharacter)){
            updateLexeme();
            updateCurrentCharacter();
            return estado_3();
        } else {
            return new Token("intLiteral",lexeme,fileHandler.getCurrentRow());
        }
    }

    private Token estado_3() throws IOException, LexicalException {
        if (Character.isDigit(currentCharacter)){
            updateLexeme();
            updateCurrentCharacter();
            return estado_4();
        } else {
            return new Token("intLiteral",lexeme,fileHandler.getCurrentRow());
        }
    }

    private Token estado_4() throws IOException, LexicalException {
        if (Character.isDigit(currentCharacter)){
            updateLexeme();
            updateCurrentCharacter();
            return estado_5();
        } else {
            return new Token("intLiteral",lexeme,fileHandler.getCurrentRow());
        }
    }

    private Token estado_5() throws IOException, LexicalException {
        if (Character.isDigit(currentCharacter)){
            updateLexeme();
            updateCurrentCharacter();
            return estado_6();
        } else {
            return new Token("intLiteral",lexeme,fileHandler.getCurrentRow());
        }
    }

    private Token estado_6() throws IOException, LexicalException {
        if (Character.isDigit(currentCharacter)){
            updateLexeme();
            updateCurrentCharacter();
            return estado_7();
        } else {
            return new Token("intLiteral",lexeme,fileHandler.getCurrentRow());
        }
    }

    private Token estado_7() throws IOException, LexicalException {
        if (Character.isDigit(currentCharacter)){
            updateLexeme();
            updateCurrentCharacter();
            return estado_8();
        } else {
            return new Token("intLiteral",lexeme,fileHandler.getCurrentRow());
        }
    }

    private Token estado_8() throws IOException, LexicalException {
        if (Character.isDigit(currentCharacter)){
            updateLexeme();
            updateCurrentCharacter();
            return estado_9();
        } else {
            return new Token("intLiteral",lexeme,fileHandler.getCurrentRow());
        }
    }

    private Token estado_9() throws IOException, LexicalException{
        if (Character.isDigit(currentCharacter)){
            updateLexeme();
            throw new LexicalException(lexeme,fileHandler.getCurrentRow(),fileHandler.getCurrentColumn(),lexeme+ " tiene mas de 9 digitos",fileHandler.getLineWithError());
        } else {
            return new Token("intLiteral",lexeme,fileHandler.getCurrentRow());
        }
    }

    private Token estado_10() throws IOException{
        if(Character.isLetter(currentCharacter) || Character.isDigit(currentCharacter) || currentCharacter == '_'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_10();
        } else {
            return new Token("idClase",lexeme, fileHandler.getCurrentRow());
        }
    }

    private Token estado_11() throws IOException{
        if (Character.isLetter(currentCharacter) || Character.isDigit(currentCharacter) || currentCharacter == '_'){
            updateLexeme();
            updateCurrentCharacter();
            return estado_11();
        } else {
            if (keywordsMap.containsKey(lexeme)){
                return new Token(keywordsMap.get(lexeme),lexeme,fileHandler.getCurrentRow());
            } else {
                return new Token("idMetVar",lexeme,fileHandler.getCurrentRow());
            }
        }
    }

    private Token estado_12() throws IOException {
        if (currentCharacter == '=') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_13();
        }
        else
            return new Token(">",lexeme,fileHandler.getCurrentRow());
    }

    private Token estado_13() {
        return new Token("!=", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_14() throws IOException {
        if (currentCharacter == '=') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_15();
        }
        else
            return new Token("<", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_15() {
        return new Token("<=", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_16() throws IOException {
        if (currentCharacter == '=') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_17();
        }
        else
            return new Token("!", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_17() {
        return new Token("!=", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_18() throws IOException {
        if (currentCharacter == '=') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_19();
        }
        else
            return new Token("=", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_19() {
        return new Token("==", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_20() {
        return new Token("*", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_21() throws IOException {
        if (currentCharacter == '=') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_22();
        }
        else
            return new Token("-", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_22() {
        return new Token("-=", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_23() throws IOException {
        if (currentCharacter == '=') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_24();
        }
        else
            return new Token("+", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_24() {
        return new Token("+=", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_25() {
        return new Token("%", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_26() throws IOException, LexicalException {
        if (currentCharacter == '&') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_27();
        } else
            throw new LexicalException(lexeme, fileHandler.getCurrentRow(), fileHandler.getCurrentColumn(),lexeme + " no es un operador válido", fileHandler.getLineWithError());
    }

    private Token estado_27() {
        return new Token("&&", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_28() throws IOException, LexicalException {
        if (currentCharacter == '|') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_29();
        }
        else {
            throw new LexicalException(lexeme, fileHandler.getCurrentRow(), fileHandler.getCurrentColumn(), lexeme + " no es un operador válido", fileHandler.getLineWithError());        }
    }

    private Token estado_29() {
        return new Token("||", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_30() {
        return new Token("(", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_31() {
        return new Token(")", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_32() {
        return new Token("{", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_33() {
        return new Token("}", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_34() {
        return new Token(";", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_35() {
        return new Token(".", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_36() {
        return new Token(",", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_37() throws IOException, LexicalException {
        if (currentCharacter == '/') {
            updateCurrentCharacter();
            lexeme = "";
            return estado_38();
        }
        else
        if (currentCharacter == '*') {
            lineNumberForError = fileHandler.getCurrentRow();
            columnNumberForError = fileHandler.getCurrentColumn();
            lineError = fileHandler.getLineWithError();
            updateLexeme();
            updateCurrentCharacter();
            return estado_39();
        }
        else
            return new Token("/", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_38() throws IOException, LexicalException {
        if (currentCharacter != '\n' && currentCharacter != -1) {
            updateCurrentCharacter();
            return estado_38();
        }
        else {
            if (currentCharacter == -1)
                return estado_54();
            else
                updateCurrentCharacter();
            return estado_0();
        }
    }

    private Token estado_39() throws IOException, LexicalException {
        if (currentCharacter == '*') {
            if (foundEOL == 0)
                updateLexeme();
            updateCurrentCharacter();
            return estado_40();
        }
        else
        if (currentCharacter == -1) {
            throw new LexicalException(lexeme, lineNumberForError, columnNumberForError, "comentario multilinea sin cerrar ", lineError);
        }
        else
        if (foundEOL == 0 && currentCharacter == '\n') {
            foundEOL = 1;
            updateCurrentCharacter();
            return estado_39();
        }
        else {
            if (foundEOL == 0)
                updateLexeme();
            updateCurrentCharacter();
            return estado_39();
        }
    }

    private Token estado_40() throws IOException, LexicalException {
        if (currentCharacter == '/') {
            updateCurrentCharacter();
            lexeme = "";
            return estado_0();
        }
        else
        if (currentCharacter == '*') {
            if (foundEOL == 0)
                updateLexeme();
            updateCurrentCharacter();
            return estado_40();
        }
        else
        if (currentCharacter == -1)
            throw new LexicalException(lexeme, lineNumberForError, columnNumberForError, "comentario multilinea sin cerrar", lineError);
        else
        if (foundEOL == 0 && currentCharacter == '\n') {
            foundEOL = 1;
            updateCurrentCharacter();
            return estado_39();
        }
        else {
            if (foundEOL == 0)
                updateLexeme();
            updateCurrentCharacter();
            return estado_39();
        }
    }

    private Token estado_41() throws IOException, LexicalException {
        if (currentCharacter == '"') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_42();
        }
        else
        if (currentCharacter == '\n' || currentCharacter == -1)
            throw new LexicalException(lexeme, fileHandler.getCurrentRow(), fileHandler.getCurrentColumn(), lexeme + " no es un String válido", fileHandler.getLineWithError());
        else
        if (currentCharacter == '\\') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_43();
        }
        else {
            updateLexeme();
            updateCurrentCharacter();
            return estado_41();
        }
    }

    private Token estado_42()  {
        return new Token("stringLiteral", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_43() throws IOException, LexicalException {
        if (currentCharacter == '"') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_41();
        }
        else
        if (currentCharacter == '\n' || currentCharacter == -1)
            throw new LexicalException(lexeme, fileHandler.getCurrentRow(), fileHandler.getCurrentColumn(), lexeme + " no es un String válido", fileHandler.getLineWithError());
        else {
            updateLexeme();
            updateCurrentCharacter();
            return estado_41();
        }
    }

    private Token estado_44() throws IOException, LexicalException {
        if (currentCharacter == '\\') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_47();
        }
        else if (currentCharacter == '\n' || currentCharacter == '\'' || currentCharacter == -1) {
            throw new LexicalException(lexeme, fileHandler.getCurrentRow(), fileHandler.getCurrentColumn(), "no es un caracter válido", fileHandler.getLineWithError());
        }
        else {
            updateLexeme();
            updateCurrentCharacter();
            return estado_45();
        }
    }

    private Token estado_45() throws IOException, LexicalException {
        if (currentCharacter == '\'') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_46();
        }
        else {
            if (currentCharacter != -1 && currentCharacter != '\n')
                updateLexeme();
            throw new LexicalException(lexeme, fileHandler.getCurrentRow(), fileHandler.getCurrentColumn(), "no es un caracter válido", fileHandler.getLineWithError());        }
    }

    private Token estado_46()  {
        return new Token("charLiteral", lexeme, fileHandler.getCurrentRow());
    }

    private Token estado_47() throws IOException, LexicalException {
        if (currentCharacter != -1  && currentCharacter != '\n') {
            updateLexeme();
            updateCurrentCharacter();
            return estado_45();
        }
        else
            throw new LexicalException(lexeme, fileHandler.getCurrentRow(), fileHandler.getCurrentColumn(), "no es un caracter válido", fileHandler.getLineWithError());
    }


    private Token estado_54() {
        return new Token("EOF", lexeme, fileHandler.getCurrentRow());
    }

}
