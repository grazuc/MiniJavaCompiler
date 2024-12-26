package AST.Sentence;

import AST.Access.AccessNode;
import AST.Encadenado.Encadenado;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;

public class CallNode extends SentenceNode {

    private AccessNode accessNode;

    public CallNode(Token token, AccessNode accessNode) {
        super(token);
        this.accessNode = accessNode;
    }

    @Override
    public void check() throws SemanticExceptionSimple {
        Encadenado accessNodeEncadenado = this.accessNode.getEncadenado();
        if (accessNodeEncadenado != null) {
            while (accessNodeEncadenado.getEncadenado() != null)
                accessNodeEncadenado = accessNodeEncadenado.getEncadenado();
            if (!accessNodeEncadenado.isCallable())
                throw new SemanticExceptionSimple(accessNodeEncadenado.getToken(), "llamada incorrecta");
        }
        else
            if (!accessNode.isCallable())
                throw new SemanticExceptionSimple(accessNode.getToken(), "llamada incorrecta");
        this.accessNode.check();
    }

}
