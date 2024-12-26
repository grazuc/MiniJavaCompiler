package AST.Access;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;

public class ThisAccessNode extends AccessNode {

    private String className;

    public ThisAccessNode(Token token, String className) {
        super(token);
        this.className = className;
        this.isAssignable = true;
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        if (SymbolTable.getInstance().getCurrentMethod().getStaticHeader().equals("static"))
            throw new SemanticExceptionSimple(this.token, "un acceso dentro de un bloque de un metodo estatico no puede comenzar con this");
        ConcreteClass currentClass = (ConcreteClass) SymbolTable.getInstance().getCurrentClass();
        if (this.encadenado != null)
            return encadenado.check(new ReferenceType(new Token("idClase", this.className, 0)));
        else
            return new ReferenceType(currentClass.getToken());
    }

    @Override
    public boolean isAssignable() {
        return this.encadenado != null;
    }

    @Override
    public boolean isCallable() {
        return false;
    }
}
