package SyntacticAnalyzer;

import AST.Access.*;
import AST.Encadenado.Encadenado;
import AST.Encadenado.LlamadaEncadenada;
import AST.Encadenado.VarEncadenada;
import AST.Expression.*;
import AST.Sentence.*;
import LexicalAnalyzer.*;
import SemanticAnalyzer.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SyntacticAnalyzer {
    private LexicalAnalyzer lexicalAnalyzer;
    private Token currentToken;
    private boolean isImplements;

    public SyntacticAnalyzer(LexicalAnalyzer lexicalanalyzer) throws LexicalException, IOException, SyntacticException, SemanticException, SemanticExceptionSimple {
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

    private void inicial() throws LexicalException, IOException, SyntacticException, SemanticException, SemanticExceptionSimple {
        listaClases();
        Token EOFToken = currentToken;
        SymbolTable.getInstance().setEOFToken(EOFToken);
        match("EOF");
    }

    private void listaClases() throws LexicalException, IOException, SyntacticException, SemanticException, SemanticExceptionSimple {
        if (Arrays.asList("pr_class","pr_interface").contains(currentToken.getTokenId())){
            clase();
            listaClases();
        } else {

        }
    }

    private void clase() throws LexicalException, IOException, SyntacticException, SemanticException, SemanticExceptionSimple {
        if (currentToken.getTokenId().equals("pr_class")){
            claseConcreta();
        } else if (currentToken.getTokenId().equals("pr_interface")){
            interface_();
        } else {
            throw new SyntacticException(currentToken,"class o interface");
        }
    }

    private void claseConcreta() throws LexicalException, IOException, SyntacticException, SemanticException, SemanticExceptionSimple {
        if (currentToken.getTokenId().equals("pr_class")){
            match("pr_class");
            Token currentTokenClass = currentToken;
            match("idClase");
            Token ancestorToken = herenciaOpcional();
            ConcreteClass currentClass = new ConcreteClass(currentTokenClass, ancestorToken);
            SymbolTable.getInstance().setActualClass(currentClass);
            SymbolTable.getInstance().insertConcreteClass(currentClass);
            if (isImplements){
                currentClass.setHasImplements();
            }
            isImplements = false;
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
            isImplements = true;
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

    private void listaMiembros() throws LexicalException, IOException, SyntacticException, SemanticException, SemanticExceptionSimple {
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
            MethodOrConstructor method = new Method(methodToken,staticMethod,methodType,SymbolTable.getInstance().getCurrentClass().getClassName());
            SymbolTable.getInstance().setCurrentMethod(method);
            SymbolTable.getInstance().getCurrentClass().insertMethod(method);
            match("idMetVar");
            argsFormales();
            match(";");
        } else throw new SyntacticException(currentToken,"static, void, idClase, boolean, char o int");
    }

    private void miembro() throws LexicalException, IOException, SyntacticException, SemanticException, SemanticExceptionSimple {
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

    private void atributoOMetodo(String staticOptional, Type possiblyType, Token possiblyToken) throws LexicalException, IOException, SyntacticException, SemanticException, SemanticExceptionSimple {
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
            MethodOrConstructor method = new Method(possiblyToken,staticOptional,possiblyType,SymbolTable.getInstance().getCurrentClass().getClassName());
            SymbolTable.getInstance().setCurrentMethod(method);
            SymbolTable.getInstance().getCurrentClass().insertMethod(method);
            argsFormales();
            BlockNode principalBlock = new BlockNode(null, null);
            SymbolTable.getInstance().getCurrentMethod().setPrincipalBlock(principalBlock);
            this.bloque(principalBlock);
        } else throw new SyntacticException(currentToken,"; o (");
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
            //System.out.println("Metodo actual: "+SymbolTable.getInstance().getCurrentMethod().getToken().getLexeme());
            SymbolTable.getInstance().getCurrentMethod().insertParameter(parameter);
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

    private void listaSentencias() throws LexicalException, IOException, SyntacticException, SemanticExceptionSimple {
        if (Arrays.asList(";","pr_var","pr_return","pr_if","pr_while","{","+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            SentenceNode sentence = sentencia();
            SymbolTable.getInstance().getCurrentMethod().getCurrentBlock().addSentence(sentence);
            listaSentencias();
        } else {

        }
    }

    private SentenceNode sentencia() throws LexicalException, IOException, SyntacticException, SemanticExceptionSimple {
        SentenceNode sentenceNode = null;
        if (currentToken.getTokenId().equals(";")){
            sentenceNode = new EmptySentence(this.currentToken);
            match(";");
        } else if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            ExpressionAssignmentNode expression = (ExpressionAssignmentNode) expresion();
            //System.out.println("Expresion left: "+expression.getLeftSide());
            //System.out.println("Expresion right: "+expression.getRightSide());
            if (expression.getRightSide() != null){
                if ((expression.getLeftSide()!= null) && expression.getLeftSide() instanceof BinaryExpressionNode){
                    throw new SemanticExceptionSimple(expression.getToken(),"Una asignacion no puede tener como lado izquierdo una expresion binaria");
                } else {
                    if (expression.getLeftSide() instanceof ParenthesizedExpressionNode){
                        ((ParenthesizedExpressionNode) expression.getLeftSide()).setIsAssignable();
                    }
                    sentenceNode = new AssignmentNode(expression.getToken(), (AccessNode) expression.getLeftSide(), expression.getRightSide());
                }
            } else {
                if (expression.getLeftSide() instanceof AccessNode && !(expression.getLeftSide() instanceof ParenthesizedExpressionNode)){
                    sentenceNode = new CallNode(expression.getToken(), (AccessNode) expression.getLeftSide());
                } else {
                    if (expression.getLeftSide().getToken() != null){
                        throw new SemanticExceptionSimple(expression.getLeftSide().getToken(),"Expresion incorrecta");
                    }
                }
            }
            match(";");
        } else if (currentToken.getTokenId().equals("pr_var")){
            sentenceNode = varLocal();
            match(";");
        } else if (currentToken.getTokenId().equals("pr_return")){
            sentenceNode = noTerminalReturn();
            match(";");
        } else if(currentToken.getTokenId().equals("pr_if")){
            sentenceNode = noTerminalIf();
        } else if(currentToken.getTokenId().equals("pr_while")){
            sentenceNode = noTerminalWhile();
        } else if(currentToken.getTokenId().equals("{")){
            BlockNode currentBlock = SymbolTable.getInstance().getCurrentMethod().getCurrentBlock();
            BlockNode newBlock = new BlockNode(SymbolTable.getInstance().getCurrentMethod().getToken(),currentBlock);
            sentenceNode = this.bloque(newBlock);
        } else throw new SyntacticException(currentToken, "+,-,!,null,true,false,intLiteral,charLiteral,stringLiteral,idMetVar,this,new,idClase,(,var,return,if,while o {");
        return sentenceNode;
    }

    private void constructor() throws LexicalException, IOException, SyntacticException, SemanticException, SemanticExceptionSimple {
        if (currentToken.getTokenId().equals("pr_public")){
            match("pr_public");
            Token currentClassToken = currentToken;
            MethodOrConstructor constructor = new Constructor(currentClassToken);
            constructor.setIsConstructor();
            SymbolTable.getInstance().setCurrentMethod(constructor);
            //System.out.println("Nombre clase: "+SymbolTable.getInstance().getCurrentClass().getClassName());
            constructor.setClassName(SymbolTable.getInstance().getCurrentClass().getClassName());
            //SymbolTable.getInstance().getCurrentClass().insertConstructor((Constructor) constructor);
            match("idClase");
            argsFormales();
            BlockNode principalBlock = new BlockNode(null, null);
            SymbolTable.getInstance().getCurrentMethod().setPrincipalBlock(principalBlock);
            bloque(principalBlock);
            ConcreteClass classToInsertConstructor = (ConcreteClass) SymbolTable.getInstance().getCurrentClass();
            classToInsertConstructor.insertConstructor(constructor);
            //System.out.println("Constructor con nombre: "+constructor.getToken().getLexeme()+" insertado en clase "+classToInsertConstructor.getToken().getLexeme());
            //System.out.println("Constructor con nombre "+SymbolTable.getInstance().getCurrentMethod().getToken().getLexeme()+" Es constructor? "+SymbolTable.getInstance().getCurrentMethod().isConstructor());
        } else throw new SyntacticException(currentToken,"public");
    }
   /* public void a1(){
        MethodOrConstructor method = new Method(possiblyToken,staticOptional,possiblyType,SymbolTable.getInstance().getCurrentClass().getClassName());
        SymbolTable.getInstance().setCurrentMethod(method);
        SymbolTable.getInstance().getCurrentClass().insertMethod(method);
        argsFormales();
        BlockNode principalBlock = new BlockNode(null, null);
        SymbolTable.getInstance().getCurrentMethod().setPrincipalBlock(principalBlock);
        this.bloque(principalBlock);
    }*/

    private BlockNode bloque(BlockNode blocknode) throws SyntacticException, LexicalException, IOException, SemanticExceptionSimple {
        BlockNode returnBlock = blocknode;
        SymbolTable.getInstance().getCurrentMethod().setCurrentBlock(blocknode);
        if (currentToken.getLexeme().equals("{")) {
            match("{");
            listaSentencias();
            match("}");
            if (blocknode.getAncestorBlock() != null) {
                SymbolTable.getInstance().getCurrentMethod().setCurrentBlock(blocknode.getAncestorBlock());
            }
        } else
            throw new SyntacticException(currentToken, "{");
        return returnBlock;
    }
    private WhileNode noTerminalWhile() throws LexicalException, SyntacticException, IOException, SemanticExceptionSimple {
        WhileNode whileNode;
        if (currentToken.getTokenId().equals("pr_while")){
            Token whileToken = currentToken;
            match("pr_while");
            match("(");
            ExpressionNode condition = expresion();
            ExpressionAssignmentNode expressionAssignmentNode = (ExpressionAssignmentNode) condition;
            if (expressionAssignmentNode.getRightSide()!= null){
                System.out.println("El while no puede tener una asignacion en la condicion");
            }
            match(")");
            SentenceNode sentence = sentencia();
            whileNode = new WhileNode(whileToken,expressionAssignmentNode.getLeftSide(),sentence);
        } else throw new SyntacticException(currentToken,"while");
        return whileNode;
    }

    private IfNode noTerminalIf() throws LexicalException, SyntacticException, IOException, SemanticExceptionSimple {
        IfNode ifNode;
        if (currentToken.getTokenId().equals("pr_if")){
            Token ifToken = currentToken;
            match("pr_if");
            match("(");
            ExpressionNode condition = expresion();
            ExpressionAssignmentNode expressionAssignmentNode = (ExpressionAssignmentNode) condition;
            if (expressionAssignmentNode.getRightSide()!= null){
                throw new SemanticExceptionSimple(expressionAssignmentNode.getRightSide().getToken(),"If mal definido");
            }
            match(")");
            SentenceNode sentenceNode = sentencia();
            ifNode = new IfNode(ifToken,expressionAssignmentNode.getLeftSide(),sentenceNode);
            SentenceNode elseSentence = elseOpcional();
            ifNode.setElseSentence(elseSentence);
        } else throw new SyntacticException(currentToken,"if");
        return ifNode;
    }

    private SentenceNode elseOpcional() throws LexicalException, SyntacticException, IOException, SemanticExceptionSimple {
        if (currentToken.getTokenId().equals("pr_else")){
            match("pr_else");
            return sentencia();
        } else {
            return null;
        }
    }

    private ReturnNode noTerminalReturn() throws LexicalException, SyntacticException, IOException, SemanticExceptionSimple{
        ExpressionNode expressionNode;
        if (currentToken.getTokenId().equals("pr_return")){
            Token returnToken = currentToken;
            match("pr_return");
            expressionNode = expresionOpcional();
            return new ReturnNode(returnToken, expressionNode);
        } else throw new SyntacticException(currentToken,"return");
    }

    private LocalVarDeclarationNode varLocal() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("pr_var")){
            match("pr_var");
            Token varNodeToken = currentToken;
            match("idMetVar");
            Token operatorToken = currentToken;
            match("=");
            ExpressionNode expressionNode = expresionCompuesta();
            LocalVarDeclarationNode varNode = new LocalVarDeclarationNode(varNodeToken, expressionNode, operatorToken);
            return varNode;
        } else throw new SyntacticException(currentToken,"var");
    }

    private ExpressionNode expresion() throws LexicalException, IOException, SyntacticException{
        ExpressionAssignmentNode expressionAssignmentNode = new ExpressionAssignmentNode(null,null,null);
        if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            expressionAssignmentNode.setLeftSide(expresionCompuesta());
            expressionAssignmentNode.setRightSide(expresionPrima(expressionAssignmentNode));
        } else throw new SyntacticException(currentToken,"+,-,!,null,true,false,intLiteral,charLiteral,stringLiteral,idMetVar,this,new,idClase o (");
        return expressionAssignmentNode;
    }

    private ExpressionNode expresionCompuesta() throws LexicalException, IOException, SyntacticException{
        ExpressionNode expressionNode = null;
        if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            ExpressionNode expression = expresionBasica();
            expressionNode = expresionCompuestaPrima(expression);
        } else throw new SyntacticException(currentToken,"+,-,!,null,true,false,intLiteral,charLiteral,stringLiteral,idMetVar,this,new,idClase o (");
        return expressionNode;
    }

    private ExpressionNode expresionBasica() throws LexicalException, IOException, SyntacticException{
        ExpressionNode expressionNode;
        if (Arrays.asList("+","-","!").contains(currentToken.getTokenId())){
            Token operatorToken = operadorUnario();
            OperandNode operandNode = operando();
            expressionNode = new UnaryExpressionNode(operatorToken,operandNode);
        } else if (Arrays.asList("pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            expressionNode = operando();
        } else throw new SyntacticException(currentToken, "+, -, !, null, true, false, intLiteral, charLiteral, stringLiteral, idMetVar, this, new, idClase o (");
        return expressionNode;
    }

    private ExpressionNode expresionPrima(ExpressionAssignmentNode expressionAssignmentNode) throws LexicalException, IOException, SyntacticException{
        if (currentToken.getTokenId().equals("=")){
            expressionAssignmentNode.setToken(currentToken);
            match("=");
            return expresion();
        } else {

        }
        return null;
    }

    private Token operadorUnario() throws LexicalException, SyntacticException, IOException {
        Token toReturn;
        if (currentToken.getTokenId().equals("+")){
            toReturn = currentToken;
            match("+");}
        else if (currentToken.getTokenId().equals("-")){
            toReturn = currentToken;
            match("-");}
        else if (currentToken.getTokenId().equals("!")){
            toReturn = currentToken;
            match("!");}
        else
            throw new SyntacticException(currentToken, "+, - o !");
        return toReturn;
    }

    private OperandNode operando() throws LexicalException, SyntacticException, IOException{
        if (Arrays.asList("pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral").contains(currentToken.getTokenId())){
            return literal();
        } else if (Arrays.asList("idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            return acceso();
        } else throw new SyntacticException(currentToken,"null, true, false, intLiteral, charLiteral, stringLiteral,idMetVar,this,new,idClase o (");
    }

    private LiteralOperandNode literal() throws SyntacticException, LexicalException, IOException {
        LiteralOperandNode literalOperandNode = null;
        if (currentToken.getTokenId().equals("pr_null")){
            literalOperandNode = new NullNode(this.currentToken);
            match("pr_null");}
        else if (currentToken.getTokenId().equals("pr_true")){
            literalOperandNode = new BooleanNode(this.currentToken);
            match("pr_true");}
        else if (currentToken.getTokenId().equals("pr_false")){
            literalOperandNode = new BooleanNode(this.currentToken);
            match("pr_false");}
        else if (currentToken.getTokenId().equals("intLiteral")){
            literalOperandNode = new IntNode(this.currentToken);
            match("intLiteral");}
        else if (currentToken.getTokenId().equals("charLiteral")){
            literalOperandNode = new CharNode(this.currentToken);
            match("charLiteral");}
        else if (currentToken.getTokenId().equals("stringLiteral")){
            literalOperandNode = new StringNode(this.currentToken);
            match("stringLiteral");}
        else
            throw new SyntacticException(currentToken, "null, true, false, intLiteral, charLiteral o stringLiteral");
        return literalOperandNode;
    }

    private AccessNode acceso() throws LexicalException, SyntacticException, IOException {
        AccessNode accessNode;
        if (Arrays.asList("idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            accessNode = primario();
            Encadenado encadenado = encadenadoOpcional(null);
            accessNode.setEncadenado(encadenado);
        } else throw new SyntacticException(currentToken,"idMetVar,this,new,idClase o (");
        return accessNode;
    }

    private Encadenado encadenadoOpcional(Encadenado encadenadoParametro) throws LexicalException, SyntacticException, IOException{
        Encadenado encadenado;
        if (currentToken.getTokenId().equals(".")){
            match(".");
            Token encadenadoToken = currentToken;
            match("idMetVar");
            encadenado = encadenadoOpcionalPrima(encadenadoToken);
            if (encadenado == null){
                encadenado = new VarEncadenada(encadenadoToken);
            }
            if (encadenadoParametro!= null){
                encadenadoParametro.setEncadenado(encadenado);
            }
        } else {
            return null;
        }
        return encadenado;
    }

    private Encadenado encadenadoOpcionalPrima(Token encadenadoToken) throws LexicalException, SyntacticException, IOException{
        Encadenado encadenado;
        if (currentToken.getTokenId().equals("(")){
            ArrayList<ExpressionNode> expressionNodeArrayList = argsActuales();
            encadenado = new LlamadaEncadenada(encadenadoToken, expressionNodeArrayList);
            encadenado.setIsNotAssignable();
            encadenadoOpcional(encadenado);
            return encadenado;
        } else {
            encadenado = new VarEncadenada(encadenadoToken);
            encadenadoOpcional(encadenado);
            return encadenado;
        }
    }

    private AccessNode primario() throws LexicalException, SyntacticException, IOException {
        if (currentToken.getTokenId().equals("idMetVar")){
            Token idMetVarToken = currentToken;
            match("idMetVar");
            return primarioOpcional(idMetVarToken);
        } else if(currentToken.getTokenId().equals("pr_this")){
            return accesoThis();
        } else if(currentToken.getTokenId().equals("pr_new")){
            return accesoConstructor();
        } else if(currentToken.getTokenId().equals("idClase")){
            return accesoMetodoEstatico();
        } else if(currentToken.getTokenId().equals("(")){
            return expresionParentizada();
        } else throw new SyntacticException(currentToken,"idMetVar,this,new,idClase o (");
    }

    private AccessNode primarioOpcional(Token idMetVarToken) throws LexicalException, SyntacticException, IOException{
        AccessNode accessNode = null;
        if (currentToken.getTokenId().equals("(")){
            ArrayList<ExpressionNode> expressionNodeArrayList = argsActuales();
            accessNode = new MethodAccess(idMetVarToken,expressionNodeArrayList);
        } else {
            accessNode = new VarAccessNode(idMetVarToken);
        }
        return accessNode;
    }

    private ArrayList<ExpressionNode> argsActuales() throws LexicalException, SyntacticException, IOException{
        ArrayList<ExpressionNode> expressionNodesList = new ArrayList<>();
        if (currentToken.getTokenId().equals("(")){
            match("(");
            expressionNodesList = listaExpsOpcional(expressionNodesList);
            match(")");
            return expressionNodesList;
        } else throw new SyntacticException(currentToken,"(");
    }

    private ArrayList<ExpressionNode> listaExpsOpcional(ArrayList<ExpressionNode> expressionNodesList) throws LexicalException, SyntacticException, IOException{
        if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            return listaExps(expressionNodesList);
        } else {
            return null;
        }
    }

    private ArrayList<ExpressionNode> listaExps(ArrayList<ExpressionNode> expressionNodesList) throws LexicalException, SyntacticException, IOException{
        if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            ExpressionNode expressionNode = expresion();
            ExpressionAssignmentNode expressionAssignmentNode = (ExpressionAssignmentNode) expressionNode;
            expressionNodesList.add(expressionAssignmentNode.getLeftSide());
            return listaExpresionesPrimas(expressionNodesList);
        } else throw new SyntacticException(currentToken,"+,-,!,null,true,false,intLiteral,charLiteral,stringLiteral,idMetVar,this,new,idClase o (");
    }

    private ArrayList<ExpressionNode> listaExpresionesPrimas(ArrayList<ExpressionNode> expressionNodesList) throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals(",")){
            match(",");
            return listaExps(expressionNodesList);
        } else {
            return expressionNodesList;
        }
    }

    private ThisAccessNode accesoThis() throws LexicalException, SyntacticException, IOException {
        ThisAccessNode thisAccessNode;
        if (currentToken.getTokenId().equals("pr_this")){
            thisAccessNode = new ThisAccessNode(currentToken,SymbolTable.getInstance().getCurrentClass().getClassName());
            match("pr_this");
        }
        else throw new SyntacticException(currentToken, "this");
        return thisAccessNode;
    }

    private ConstructorAccess accesoConstructor() throws LexicalException, SyntacticException, IOException{
        ConstructorAccess constructorAccess;
        if (currentToken.getTokenId().equals("pr_new")){
            match("pr_new");
            Token classNameToken = currentToken;
            match("idClase");
            ArrayList <ExpressionNode> expressionNodeArrayList = argsActuales();
            constructorAccess = new ConstructorAccess(classNameToken,expressionNodeArrayList);
            constructorAccess.setIsNotAssignable();
            return constructorAccess;
        } else throw new SyntacticException(currentToken,"new");
    }

    private StaticMethodAccessNode accesoMetodoEstatico() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("idClase")){
            Token classNameToken = currentToken;
            match("idClase");
            match(".");
            Token methodNameToken = currentToken;
            match("idMetVar");
            ArrayList <ExpressionNode> expressionNodeArrayList = argsActuales();
            StaticMethodAccessNode staticMethodAccessNode = new StaticMethodAccessNode(classNameToken,methodNameToken,expressionNodeArrayList);
            staticMethodAccessNode.setIsNotAssignable();
            return staticMethodAccessNode;
        } else throw new SyntacticException(currentToken,"idClase");
    }

    private ParenthesizedExpressionNode expresionParentizada() throws LexicalException, SyntacticException, IOException{
        if (currentToken.getTokenId().equals("(")){
            match("(");
            ExpressionAssignmentNode expressionAssignmentNode = (ExpressionAssignmentNode) expresion();
            ExpressionNode expressionNode = expressionAssignmentNode.getLeftSide();
            match(")");
            ParenthesizedExpressionNode parenthesizedExpressionNode = new ParenthesizedExpressionNode(expressionAssignmentNode.getLeftSide().getToken(),expressionNode);
            parenthesizedExpressionNode.setIsNotAssignable();
            return parenthesizedExpressionNode;
        } else throw new SyntacticException(currentToken,"(");
    }

    private ExpressionNode expresionCompuestaPrima(ExpressionNode leftSide) throws LexicalException, IOException, SyntacticException{
        if (Arrays.asList("||", "&&", "==", "!=", "<", ">", "<=", ">=", "+", "-", "*", "/", "%").contains(currentToken.getTokenId())){
            Token binaryOperatorToken = operadorBinario();
            ExpressionNode rightSide = expresionBasica();
            BinaryExpressionNode binaryExpressionNode = new BinaryExpressionNode(binaryOperatorToken,leftSide,rightSide);
            return expresionCompuestaPrima(binaryExpressionNode);
        } else {
            return leftSide;
        }
    }

    /* a1 == 5 && a1 == 5*/

    private Token operadorBinario() throws LexicalException, SyntacticException, IOException {
        Token toReturn;
        if (currentToken.getTokenId().equals("||")){
            toReturn = currentToken;
            match("||");}
        else if (currentToken.getTokenId().equals("&&")){
            toReturn = currentToken;
            match("&&");}
        else if (currentToken.getTokenId().equals("==")){
            toReturn = currentToken;
            match("==");}
        else if (currentToken.getTokenId().equals("!=")){
            toReturn = currentToken;
            match("!=");}
        else if (currentToken.getTokenId().equals("<")){
            toReturn = currentToken;
            match("<");}
        else if (currentToken.getTokenId().equals(">")){
            toReturn = currentToken;
            match(">");}
        else if (currentToken.getTokenId().equals("<=")){
            toReturn = currentToken;
            match("<=");}
        else if (currentToken.getTokenId().equals(">=")){
            toReturn = currentToken;
            match(">=");}
        else if (currentToken.getTokenId().equals("+")){
            toReturn = currentToken;
            match("+");}
        else if (currentToken.getTokenId().equals("-")){
            toReturn = currentToken;
            match("-");}
        else if (currentToken.getTokenId().equals("*")){
            toReturn = currentToken;
            match("*");}
        else if (currentToken.getTokenId().equals("/")){
            toReturn = currentToken;
            match("/");}
        else if (currentToken.getTokenId().equals("%")){
            toReturn = currentToken;
            match("%");}
        else
            throw new SyntacticException(currentToken, "+, -, *, /, %, >=, <=, >, <, !=, ==, && o ||");
        return toReturn;
    }

    private ExpressionNode expresionOpcional() throws LexicalException, SyntacticException, IOException{
        ExpressionNode expressionNode = null;
        if (Arrays.asList("+","-","!","pr_null","pr_true","pr_false","intLiteral","charLiteral","stringLiteral","idMetVar","pr_this","pr_new","idClase","(").contains(currentToken.getTokenId())){
            expressionNode = expresion();
            ExpressionAssignmentNode expressionAssignmentNode = (ExpressionAssignmentNode) expressionNode;
            expressionNode = expressionAssignmentNode.getLeftSide();
        } else {
            if (this.currentToken.getTokenId().equals(";")){
                expressionNode = new EmptyExpressionNode(this.currentToken);
            }
        }
        return expressionNode;
    }

}
