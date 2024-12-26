package SyntacticAnalyzer;

import LexicalAnalyzer.*;

import java.io.IOException;
import java.util.Arrays;

public class SyntacticAnalyzer {
    private LexicalAnalyzer lexicalAnalyzer;
    private Token currentToken;

    public SyntacticAnalyzer(LexicalAnalyzer lexicalanalyzer) throws LexicalException, IOException, SyntacticException{
        lexicalAnalyzer=lexicalanalyzer;
        currentToken= lexicalAnalyzer.nextToken();
        inicial();
    }

    private void match(String tokenId) throws SyntacticException, IOException, LexicalException {
        if (tokenId.equals(currentToken.getTokenId())) {
            currentToken = lexicalAnalyzer.nextToken();
        } else
            throw new SyntacticException(currentToken, tokenId);
    }

    private void inicial() throws LexicalException, IOException, SyntacticException {
        listaClases();
        match("EOF");
    }

    private void listaClases() throws LexicalException, IOException, SyntacticException{
        if (Arrays.asList("pr_class","pr_interface").contains(currentToken.getTokenId())){
            clase();
            listaClases();
        } else {

        }
    }

    private void clase() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("pr_class")){
            claseConcreta();
        } else if (currentToken.getTokenId().equals("pr_interface")){
            interface_();
        } else {
            throw new SyntacticException(currentToken,"class o interface");
        }
    }

    private void claseConcreta() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("pr_class")){
            match("pr_class");
            match("idClase");
            herenciaOpcional();
            match("{");
            listaMiembros();
            match("}");
        } else {
            throw new SyntacticException(currentToken,"class");
        }
    }

    private void interface_() throws LexicalException, IOException, SyntacticException {
        if (currentToken.getTokenId().equals("pr_interface")){
            match("pr_interface");
            match("idClase");
            extiendeOpcional();
            match("{");
            listaEncabezados();
            match("}");
        } else {
            throw new SyntacticException(currentToken,"interface");
        }
    }

    private void herenciaOpcional() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("pr_extends")){
            heredaDe();
        } else if (currentToken.getTokenId().equals("pr_implements")){
            implementaA();
        } else {

        }
    }

    private void heredaDe() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("pr_extends")){
            match("pr_extends");
            match("idClase");
        } else {
            throw new SyntacticException(currentToken,"extends");
        }
    }

    private void implementaA() throws LexicalException, IOException, SyntacticException {
        if (currentToken.getTokenId().equals("pr_implements")){
            match("pr_implements");
            match("idClase");
        } else {
            throw new SyntacticException(currentToken,"implements");
        }
    }

    private void extiendeOpcional() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("pr_extends")){
            match("pr_extends");
            match("idClase");
        } else {

        }
    }

    private void listaMiembros() throws LexicalException, IOException, SyntacticException{
        if (Arrays.asList("pr_public", "pr_static", "pr_void", "idClase", "pr_boolean", "pr_char", "pr_int").contains(currentToken.getTokenId())){
            miembro();
            listaMiembros();
        } else {

        }
    }

    private void listaEncabezados() throws LexicalException, IOException, SyntacticException{
        if (Arrays.asList("pr_static", "pr_void", "idClase", "pr_boolean", "pr_char", "pr_int").contains(currentToken.getTokenId())) {
            encabezadoMetodo();
            listaEncabezados();
        } else {

        }
    }

    private void encabezadoMetodo() throws LexicalException, SyntacticException, IOException {
        if (Arrays.asList("pr_static", "pr_void", "idClase", "pr_boolean", "pr_char", "pr_int").contains(currentToken.getTokenId())){
            estaticoOpcional();
            tipoMiembro();
            match("idMetVar");
            argsFormales();
            match(";");
        } else throw new SyntacticException(currentToken,"static, void, idClase, boolean, char o int");
    }

    private void miembro() throws LexicalException, IOException, SyntacticException{
        if (Arrays.asList("pr_static","pr_void","idClase","pr_boolean","pr_char","pr_int").contains(currentToken.getTokenId())){
            estaticoOpcional();
            tipoMiembro();
            match("idMetVar");
            atributoOMetodo();
        } else if (currentToken.getTokenId().equals("pr_public")){
            constructor();
        } else {
            throw new SyntacticException(currentToken,"public, static, void, idClase, boolean, char o int");
        }
    }

    private void constructor() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("pr_public")){
            match("pr_public");
            match("idClase");
            argsFormales();
            bloque();
        } else throw new SyntacticException(currentToken,"public");
    }

    private void estaticoOpcional() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("pr_static")){
            match("pr_static");
        } else {

        }
    }

    private void tipoMiembro() throws LexicalException, IOException, SyntacticException {
        if (currentToken.getTokenId().equals("pr_void")){
            match("pr_void");
        } else if (Arrays.asList("idClase","pr_boolean","pr_char","pr_int").contains(currentToken.getTokenId())){
            tipo();
        } else {
            throw new SyntacticException(currentToken,"void, idClase, boolean, char o int");
        }
    }

    private void tipo() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("idClase")){
            match("idClase");
        } else if (Arrays.asList("pr_boolean","pr_char","pr_int").contains(currentToken.getTokenId())){
            tipoPrimitivo();
        } else {
            throw new SyntacticException(currentToken,"idClase, boolean, char o int");
        }
    }

    private void tipoPrimitivo() throws LexicalException, IOException, SyntacticException {
        if (currentToken.getTokenId().equals("pr_boolean")){
            match("pr_boolean");
        } else if(currentToken.getTokenId().equals("pr_char")){
            match("pr_char");
        } else if (currentToken.getTokenId().equals("pr_int")){
            match("pr_int");
        } else throw new SyntacticException(currentToken,"boolean, char o int");
    }

    private void atributoOMetodo() throws LexicalException, IOException, SyntacticException {
        if (currentToken.getTokenId().equals(";")){
            match(";");
        } else if (currentToken.getTokenId().equals("(")){
            argsFormales();
            bloque();
        } else throw new SyntacticException(currentToken,"; o (");
    }

    private void argsFormales() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("(")){
            match("(");
            listaArgsFormalesOpcional();
            match(")");
        } else throw new SyntacticException(currentToken,"(");
    }

    private void listaArgsFormalesOpcional() throws LexicalException, IOException, SyntacticException {
        if (Arrays.asList("idClase","pr_boolean","pr_char","pr_int").contains(currentToken.getTokenId())){
            listaArgsFormales();
        } else {

        }
    }

    private void listaArgsFormales() throws LexicalException, IOException, SyntacticException {
        if (Arrays.asList("idClase","pr_boolean","pr_char","pr_int").contains(currentToken.getTokenId())){
            argFormal();
            argFormalPrima();
        } else throw new SyntacticException(currentToken,"idClase, boolean, char o int");
    }

    private void argFormal() throws LexicalException, IOException, SyntacticException{
        if (Arrays.asList("idClase","pr_boolean","pr_char","pr_int").contains(currentToken.getTokenId())){
            tipo();
            match("idMetVar");
        } else throw new SyntacticException(currentToken,"idClase, boolean, char o int");
    }

    private void argFormalPrima() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals(",")){
            match(",");
            listaArgsFormales();
        } else {

        }
    }

    private void bloque() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("{")){
            match("{");
            listaSentencias();
            match("}");
        } else throw new SyntacticException(currentToken,"{");
    }

    private void listaSentencias() throws LexicalException, IOException, SyntacticException{
        if (Arrays.asList(";","pr_var","pr_return","pr_if","pr_while","{","+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            sentencia();
            listaSentencias();
        } else {

        }
    }

    private void sentencia() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals(";")){
            match(";");
        } else if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            expresion();
            match(";");
        } else if (currentToken.getTokenId().equals("pr_var")){
            varLocal();
            match(";");
        } else if (currentToken.getTokenId().equals("pr_return")){
            noTerminalReturn();
            match(";");
        } else if(currentToken.getTokenId().equals("pr_if")){
            noTerminalIf();
        } else if(currentToken.getTokenId().equals("pr_while")){
            noTerminalWhile();
        } else if(currentToken.getTokenId().equals("{")){
            bloque();
        } else throw new SyntacticException(currentToken, "+,-,!,null,true,false,intLiteral,charLiteral,stringLiteral,idMetVar,this,new,idClase,(,var,return,if,while o {");
    }
    private void noTerminalWhile() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("pr_while")){
            match("pr_while");
            match("(");
            expresion();
            match(")");
            sentencia();
        } else throw new SyntacticException(currentToken,"while");
    }

    private void noTerminalIf() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("pr_if")){
            match("pr_if");
            match("(");
            expresion();
            match(")");
            sentencia();
            elseOpcional();
        } else throw new SyntacticException(currentToken,"if");
    }

    private void elseOpcional() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("pr_else")){
            match("pr_else");
            sentencia();
        } else {

        }
    }

    private void noTerminalReturn() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("pr_return")){
            match("pr_return");
            expresionOpcional();
        } else throw new SyntacticException(currentToken,"return");
    }

    private void varLocal() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("pr_var")){
            match("pr_var");
            match("idMetVar");
            match("=");
            expresionCompuesta();
        } else throw new SyntacticException(currentToken,"var");
    }

    private void expresion() throws LexicalException, IOException, SyntacticException{
        if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            expresionCompuesta();
            expresionPrima();
        } else throw new SyntacticException(currentToken,"+,-,!,null,true,false,intLiteral,charLiteral,stringLiteral,idMetVar,this,new,idClase o (");
    }

    private void expresionCompuesta() throws LexicalException, IOException, SyntacticException{
        if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            expresionBasica();
            expresionCompuestaPrima();
        } else throw new SyntacticException(currentToken,"+,-,!,null,true,false,intLiteral,charLiteral,stringLiteral,idMetVar,this,new,idClase o (");
    }

    private void expresionBasica() throws LexicalException, IOException, SyntacticException{
        if (Arrays.asList("+","-","!").contains(currentToken.getTokenId())){
            operadorUnario();
            operando();
        } else if (Arrays.asList("pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            operando();
        } else throw new SyntacticException(currentToken, "+, -, !, null, true, false, intLiteral, charLiteral, stringLiteral, idMetVar, this, new, idClase o (");
    }

    private void operadorUnario() throws LexicalException, SyntacticException, IOException {
        if (currentToken.getTokenId().equals("+"))
            match("+");
        else if (currentToken.getTokenId().equals("-"))
            match("-");
        else if (currentToken.getTokenId().equals("!"))
            match("!");
        else
            throw new SyntacticException(currentToken, "+, - o !");
    }

    private void operando() throws LexicalException, SyntacticException, IOException{
        if (Arrays.asList("pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral").contains(currentToken.getTokenId())){
            literal();
        } else if (Arrays.asList("idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            acceso();
        } else throw new SyntacticException(currentToken,"null, true, false, intLiteral, charLiteral, stringLiteral,idMetVar,this,new,idClase o (");
    }

    private void literal() throws SyntacticException, LexicalException, IOException {
        if (currentToken.getTokenId().equals("pr_null"))
            match("pr_null");
        else if (currentToken.getTokenId().equals("pr_true"))
            match("pr_true");
        else if (currentToken.getTokenId().equals("pr_false"))
            match("pr_false");
        else if (currentToken.getTokenId().equals("intLiteral"))
            match("intLiteral");
        else if (currentToken.getTokenId().equals("charLiteral"))
            match("charLiteral");
        else if (currentToken.getTokenId().equals("stringLiteral"))
            match("stringLiteral");
        else
            throw new SyntacticException(currentToken, "null, true, false, intLiteral, charLiteral o stringLiteral");
    }

    private void acceso() throws LexicalException, SyntacticException, IOException {
        if (Arrays.asList("idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            primario();
            encadenadoOpcional();
        } else throw new SyntacticException(currentToken,"idMetVar,this,new,idClase o (");
    }

    private void encadenadoOpcional() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals(".")){
            match(".");
            match("idMetVar");
            encadenadoOpcionalPrima();
        } else {

        }
    }

    private void encadenadoOpcionalPrima() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("(")){
            argsActuales();
            encadenadoOpcional();
        } else {
            encadenadoOpcional();
        }
    }

    private void primario() throws LexicalException, SyntacticException, IOException {
        if (currentToken.getTokenId().equals("idMetVar")){
            match("idMetVar");
            primarioOpcional();
        } else if(currentToken.getTokenId().equals("pr_this")){
            accesoThis();
        } else if(currentToken.getTokenId().equals("pr_new")){
            accesoConstructor();
        } else if(currentToken.getTokenId().equals("idClase")){
            accesoMetodoEstatico();
        } else if(currentToken.getTokenId().equals("(")){
            expresionParentizada();
        } else throw new SyntacticException(currentToken,"idMetVar,this,new,idClase o (");
    }

    private void primarioOpcional() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("(")){
            argsActuales();
        } else {

        }
    }

    private void argsActuales() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("(")){
            match("(");
            listaExpsOpcional();
            match(")");
        } else throw new SyntacticException(currentToken,"(");
    }

    private void listaExpsOpcional() throws LexicalException, SyntacticException, IOException{
        if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            listaExps();
        } else {

        }
    }

    private void listaExps() throws LexicalException, SyntacticException, IOException{
        if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            expresion();
            listaExpresionesPrimas();
        } else throw new SyntacticException(currentToken,"+,-,!,null,true,false,intLiteral,charLiteral,stringLiteral,idMetVar,this,new,idClase o (");
    }

    private void listaExpresionesPrimas() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals(",")){
            match(",");
            listaExps();
        } else {

        }
    }

    private void accesoThis() throws LexicalException, SyntacticException, IOException {
        if (currentToken.getTokenId().equals("pr_this"))
            match("pr_this");
        else throw new SyntacticException(currentToken, "this");
    }

    private void accesoConstructor() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("pr_new")){
            match("pr_new");
            match("idClase");
            argsActuales();
        } else throw new SyntacticException(currentToken,"new");
    }

    private void accesoMetodoEstatico() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("idClase")){
            match("idClase");
            match(".");
            match("idMetVar");
            argsActuales();
        } else throw new SyntacticException(currentToken,"idClase");
    }

    private void expresionParentizada() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("(")){
            match("(");
            expresion();
            match(")");
        } else throw new SyntacticException(currentToken,"(");
    }

    private void expresionPrima() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("=")){
            match("=");
            expresion();
        } else {

        }
    }

    private void expresionCompuestaPrima() throws LexicalException, IOException, SyntacticException{
        if (Arrays.asList("||", "&&", "==", "!=", "<", ">", "<=", ">=", "+", "-", "*", "/", "%").contains(this.currentToken.getTokenId())){
            operadorBinario();
            expresionBasica();
            expresionCompuestaPrima();
        } else {

        }
    }

    private void operadorBinario() throws LexicalException, SyntacticException, IOException {
        if (currentToken.getTokenId().equals("||"))
            match("||");
        else if (currentToken.getTokenId().equals("&&"))
            match("&&");
        else if (currentToken.getTokenId().equals("=="))
            match("==");
        else if (currentToken.getTokenId().equals("!="))
            match("!=");
        else if (currentToken.getTokenId().equals("<"))
            match("<");
        else if (currentToken.getTokenId().equals(">"))
            match(">");
        else if (currentToken.getTokenId().equals("<="))
            match("<=");
        else if (currentToken.getTokenId().equals(">="))
            match(">=");
        else if (currentToken.getTokenId().equals("+"))
            match("+");
        else if (currentToken.getTokenId().equals("-"))
            match("-");
        else if (currentToken.getTokenId().equals("*"))
            match("*");
        else if (currentToken.getTokenId().equals("/"))
            match("/");
        else if (currentToken.getTokenId().equals("%"))
            match("%");
        else
            throw new SyntacticException(currentToken, "+, -, *, /, %, >=, <=, >, <, !=, ==, && o ||");
    }

    private void expresionOpcional() throws LexicalException, SyntacticException, IOException{
        if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            expresion();
        } else {

        }
    }

}
