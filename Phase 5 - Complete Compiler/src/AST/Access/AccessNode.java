package AST.Access;

import AST.Encadenado.Encadenado;
import AST.Expression.OperandNode;
import LexicalAnalyzer.Token;

public abstract class AccessNode extends OperandNode {

    protected Encadenado encadenado;
    protected boolean isAssignable;
    private boolean isLeftSide;
    public AccessNode(Token token) {
        super(token);
        this.isAssignable = true;
        isLeftSide = false;
    }

    public void setEncadenado(Encadenado encadenado) {
        this.encadenado = encadenado;
    }

    public Encadenado getEncadenado() {
        return this.encadenado;
    }

    public abstract boolean isAssignable();

    public void setIsNotAssignable() {
        this.isAssignable = false;
    }

    public abstract boolean isCallable();

    public void setAsLeftSide() {
        this.isLeftSide = true;
    }

    public boolean isLeftSide() {
        if (this.encadenado != null)
            return this.encadenado.isLeftSide();
        else
            return this.isLeftSide;
    }
}
