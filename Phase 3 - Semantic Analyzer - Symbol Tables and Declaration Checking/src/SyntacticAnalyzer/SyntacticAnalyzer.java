package SyntacticAnalyzer;

import LexicalAnalyzer.*;
import SemanticAnalyzer.*;
import SemanticAnalyzer.Class;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class SyntacticAnalyzer {
    private LexicalAnalyzer lexicalAnalyzer;
    private Token currentToken;
    private boolean isConstructor;

    public SyntacticAnalyzer(LexicalAnalyzer lexicalanalyzer) throws LexicalException, IOException, SyntacticException, SemanticException {
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

    private void inicial() throws LexicalException, IOException, SyntacticException, SemanticException {
        listaClases();
        Token EOFToken = currentToken;
        SymbolTable.getInstance().setEOFToken(EOFToken);
        match("EOF");
    }

    private void listaClases() throws LexicalException, IOException, SyntacticException, SemanticException {
        if (Arrays.asList("pr_class","pr_interface").contains(currentToken.getTokenId())){
            clase();
            listaClases();
        } else {

        }
    }

    private void clase() throws LexicalException, IOException, SyntacticException, SemanticException {
        if (currentToken.getTokenId().equals("pr_class")){
            claseConcreta();
        } else if (currentToken.getTokenId().equals("pr_interface")){
            interface_();
        } else {
            throw new SyntacticException(currentToken,"class o interface");
        }
    }

    private void claseConcreta() throws LexicalException, IOException, SyntacticException, SemanticException {
        if (currentToken.getTokenId().equals("pr_class")){
            match("pr_class");
            Token currentTokenClass = currentToken;
            match("idClase");
            Token ancestorToken = herenciaOpcional();
            ConcreteClass currentClass = new ConcreteClass(currentTokenClass, ancestorToken);
            SymbolTable.getInstance().setActualClass(currentClass);
            SymbolTable.getInstance().insertConcreteClass(currentClass);
            match("{");
            listaMiembros();
            match("}");
        } else {
            throw new SyntacticException(currentToken,"class");
        }
    }

    private void interface_() throws LexicalException, IOException, SyntacticException, SemanticException {
        if (currentToken.getTokenId().equals("pr_interface")){
            match("pr_interface");
            Token interfaceToken = currentToken;
            match("idClase");
            Token extendsToken = extiendeOpcional();
            Interface currentInterface = new Interface(interfaceToken,extendsToken);
            SymbolTable.getInstance().setActualClass(currentInterface);
            SymbolTable.getInstance().insertInterface(currentInterface);
            match("{");
            listaEncabezados();
            match("}");
        } else {
            throw new SyntacticException(currentToken,"interface");
        }
    }

    private Token herenciaOpcional() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("pr_extends")){
            return heredaDe();
        } else if (currentToken.getTokenId().equals("pr_implements")){
            return implementaA();
        } else {
            return SymbolTable.getInstance().getConcreteClass("Object").getToken();
        }
    }

    private Token heredaDe() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("pr_extends")){
            match("pr_extends");
            Token ancestorToken = currentToken;
            match("idClase");
            return ancestorToken;
        } else {
            return SymbolTable.getInstance().getConcreteClass("Object").getToken();
        }
    }

    private Token implementaA() throws LexicalException, IOException, SyntacticException {
        if (currentToken.getTokenId().equals("pr_implements")){
            match("pr_implements");
            Token ancestorToken = currentToken;
            match("idClase");
            return ancestorToken;
        } else {
            return SymbolTable.getInstance().getConcreteClass("Object").getToken();
        }
    }

    private Token extiendeOpcional() throws LexicalException, IOException, SyntacticException{
        Token extendsToken = null;
        if (currentToken.getTokenId().equals("pr_extends")){
            match("pr_extends");
            extendsToken = currentToken;
            match("idClase");
        } else {

        }
        return extendsToken;
    }

    private void listaMiembros() throws LexicalException, IOException, SyntacticException, SemanticException {
        if (Arrays.asList("pr_public", "pr_static", "pr_void", "idClase", "pr_boolean", "pr_char", "pr_int").contains(currentToken.getTokenId())){
            miembro();
            listaMiembros();
        } else {

        }
    }

    private void listaEncabezados() throws LexicalException, IOException, SyntacticException, SemanticException {
        if (Arrays.asList("pr_static", "pr_void", "idClase", "pr_boolean", "pr_char", "pr_int").contains(currentToken.getTokenId())) {
            encabezadoMetodo();
            listaEncabezados();
        } else {

        }
    }

    private void encabezadoMetodo() throws LexicalException, SyntacticException, IOException, SemanticException {
        if (Arrays.asList("pr_static", "pr_void", "idClase", "pr_boolean", "pr_char", "pr_int").contains(currentToken.getTokenId())){
            String staticMethod = estaticoOpcional();
            Type methodType = tipoMiembro();
            Token methodToken = currentToken;
            Method method = new Method(methodToken,staticMethod,methodType);
            SymbolTable.getInstance().setCurrentMethod(method);
            SymbolTable.getInstance().getCurrentClass().insertMethod(method);
            match("idMetVar");
            argsFormales();
            match(";");
        } else throw new SyntacticException(currentToken,"static, void, idClase, boolean, char o int");
    }

    private void miembro() throws LexicalException, IOException, SyntacticException, SemanticException {
        if (Arrays.asList("pr_static","pr_void","idClase","pr_boolean","pr_char","pr_int").contains(currentToken.getTokenId())){
            String staticOptional = estaticoOpcional();
            Type possiblyMethodOrAttributeType = tipoMiembro();
            Token possiblyMethodOrAttributeToken = currentToken;
            match("idMetVar");
            atributoOMetodo(staticOptional,possiblyMethodOrAttributeType,possiblyMethodOrAttributeToken);
        } else if (currentToken.getTokenId().equals("pr_public")){
            constructor();
        } else {
            throw new SyntacticException(currentToken,"public, static, void, idClase, boolean, char o int");
        }
    }

    private void atributoOMetodo(String staticOptional, Type possiblyType, Token possiblyToken) throws LexicalException, IOException, SyntacticException, SemanticException {
        if (currentToken.getTokenId().equals(";")){
            if (Objects.equals(possiblyType.getToken().getLexeme(), "void")){
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(possiblyToken, "El tipo de atributo de " + possiblyToken.getLexeme() + " no puede ser void"));
            }else {
                Attribute attribute = new Attribute(possiblyToken,staticOptional,possiblyType);
                ConcreteClass concreteCurrentClass = (ConcreteClass) SymbolTable.getInstance().getCurrentClass();
                concreteCurrentClass.insertAttribute(attribute);
            }
            match(";");
        } else if (currentToken.getTokenId().equals("(")){
            Method method = new Method(possiblyToken,staticOptional,possiblyType);
            SymbolTable.getInstance().setCurrentMethod(method);
            SymbolTable.getInstance().getCurrentClass().insertMethod(method);
            argsFormales();
            bloque();
        } else throw new SyntacticException(currentToken,"; o (");
    }

    private void constructor() throws LexicalException, IOException, SyntacticException, SemanticException {
        if (currentToken.getTokenId().equals("pr_public")){
            match("pr_public");
            Token currentClassToken = currentToken;
            Constructor constructor = new Constructor(currentClassToken);
            isConstructor = true;
            SymbolTable.getInstance().insertConstructor(SymbolTable.getInstance().getCurrentClass(),constructor);
            match("idClase");
            argsFormales();
            bloque();
            isConstructor= false;
        } else throw new SyntacticException(currentToken,"public");
    }

    private String estaticoOpcional() throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("pr_static")){
            match("pr_static");
            return "static";
        } else {
            return "";
        }
    }

    private Type tipoMiembro() throws LexicalException, IOException, SyntacticException {
        if (currentToken.getTokenId().equals("pr_void")){
            Type typeToReturn = new PrimitiveType(currentToken);
            match("pr_void");
            return typeToReturn;
        } else if (Arrays.asList("idClase","pr_boolean","pr_char","pr_int").contains(currentToken.getTokenId())){
            return tipo();
        } else {
            throw new SyntacticException(currentToken,"void, idClase, boolean, char o int");
        }
    }

    private Type tipo() throws LexicalException, IOException, SyntacticException {
        if (Arrays.asList("pr_boolean", "pr_char", "pr_int").contains(currentToken.getTokenId())) {
            return tipoPrimitivo();
        } else
        if (currentToken.getTokenId().equals("idClase")) {
            Type typeToReturn = new ReferenceType(currentToken);
            match("idClase");
            return typeToReturn;
        }
        else
            throw new SyntacticException(currentToken, "boolean, char, int o idClase");
    }

    private Type tipoPrimitivo() throws LexicalException, IOException, SyntacticException {
        if (currentToken.getTokenId().equals("pr_boolean")) {
            Type typeToReturn = new PrimitiveType(currentToken);
            match("pr_boolean");
            return typeToReturn;
        }
        else if (currentToken.getTokenId().equals("pr_char")) {
            Type typeToReturn = new PrimitiveType(currentToken);
            match("pr_char");
            return typeToReturn;
        }
        else if (currentToken.getTokenId().equals("pr_int")) {
            Type typeToReturn = new PrimitiveType(currentToken);
            match("pr_int");
            return typeToReturn;
        }
        else
            throw new SyntacticException(currentToken, "boolean, char o int");
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
            Type parameterType = tipo();
            Parameter parameter = new Parameter(currentToken,parameterType);
            if (isConstructor && SymbolTable.getInstance().getCurrentClassConstructor()!=null){
                //System.out.println("Entre a constructor");
                //System.out.println("Voy a insertar: "+parameter.getParameterName()+" en: "+SymbolTable.getInstance().getCurrentClassConstructor().getConstructorToken().getLexeme()+" estoy parado en: "+SymbolTable.getInstance().getCurrentClass().getToken().getLexeme());
                SymbolTable.getInstance().getCurrentClassConstructor().insertParameter(parameter);
            } else if (SymbolTable.getInstance().getCurrentMethod()!=null){
                //System.out.println("Entre a metodo");
                SymbolTable.getInstance().getCurrentMethod().insertParameter(parameter);
            }
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
        if (Arrays.asList("||", "&&", "==", "!=", "<", ">", "<=", ">=", "+", "-", "*", "/", "%").contains(currentToken.getTokenId())){
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
