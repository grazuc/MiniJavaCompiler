package AST.Access;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;

public class VarAccessNode extends AccessNode {

    public VarAccessNode(Token token) {
        super(token);
        this.isAssignable = true;
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public boolean isCallable() {
        return false;
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        Type varType;
        String varName = this.token.getLexeme();
        MethodOrConstructor currentMethod = SymbolTable.getInstance().getCurrentMethod();
        //System.out.println("Metodo actual: "+SymbolTable.getInstance().getCurrentMethod().getToken());
        if (SymbolTable.getInstance().isMethodParameter(varName, currentMethod))
            varType = SymbolTable.getInstance().retrieveParameterType(varName, currentMethod);
        else
            if (SymbolTable.getInstance().isCurrentBlockLocalVar(varName))
                varType = SymbolTable.getInstance().retrieveLocalVarType(varName);
            else {
                ConcreteClass methodClass = currentMethod.getMethodClass();
                    if (SymbolTable.getInstance().isAttribute(varName, methodClass)) {
                        if (!SymbolTable.getInstance().getCurrentMethod().isConstructor()){
                        if (!SymbolTable.getInstance().getCurrentMethod().getStaticHeader().equals("static"))
                            varType = SymbolTable.getInstance().retrieveAttribute(varName, methodClass);
                        else
                            if (SymbolTable.getInstance().getCurrentMethod().getStaticHeader().equals("static") && methodClass.getAttributes().get(this.token.getLexeme()).getStaticOptional().equals("static"))
                                varType = SymbolTable.getInstance().retrieveAttribute(varName, methodClass);
                            else
                                throw new SemanticExceptionSimple(this.token, "un metodo estatico no puede acceder a un atributo");
                        }
                        varType = SymbolTable.getInstance().retrieveAttribute(varName, methodClass);
                    }
                    else{
                        if (!SymbolTable.getInstance().getCurrentMethod().getStaticHeader().equals("static")){
                            if (SymbolTable.getInstance().getCurrentMethod().isConstructor()){
                                throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es una variable local ni un parametro del constructor " + "\"" + currentMethod.getMethodName() + "\"" + " ni un atributo de la clase " + methodClass.getClassName());
                            }
                            else
                                throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es una variable local ni un parametro del metodo " + "\"" + currentMethod.getMethodName() + "\"" + " ni un atributo de la clase " + methodClass.getClassName());
                        }
                        else{
                                throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es una variable local ni un parametro del metodo " + "\"" + currentMethod.getMethodName() + "\"" );
                        }
                    }
            }
        if (this.encadenado != null) {
            if (!varType.isPrimitive())
                return this.encadenado.check(varType);
            else
                throw new SemanticExceptionSimple(this.token, "el lado izquierdo del encadenado es un tipo primitivo");
        }
        return varType;
    }

}
