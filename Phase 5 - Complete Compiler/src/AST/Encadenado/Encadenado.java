package AST.Encadenado;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;

import java.io.IOException;

public abstract class Encadenado {

    protected Token token;
    protected Encadenado encadenado;
    protected boolean isAssignable;
    protected boolean isLeftSide;

    public Encadenado(Token token) {
        this.token = token;
        this.isLeftSide = false;
    }

    public void setEncadenado(Encadenado encadenado) {
        this.encadenado = encadenado;
    }

    public abstract Type check(Type type) throws SemanticExceptionSimple;

    public abstract boolean isAssignable();

    public Token getToken() {
        return this.token;
    }

    public void setIsNotAssignable() {
        this.isAssignable = false;
    }

    public Encadenado getEncadenado() {
        return this.encadenado;
    }

    public abstract boolean isCallable();

    public abstract void generateCode() throws IOException;

    public boolean isLeftSide() {
        return this.isLeftSide;
    }

    public void setAsLeftSide() {
        if (this.encadenado != null)
            this.encadenado.setAsLeftSide();
        else
            this.isLeftSide = true;
    }
}
