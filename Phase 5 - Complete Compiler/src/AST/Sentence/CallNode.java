package AST.Sentence;

import AST.Access.AccessNode;
import AST.Encadenado.Encadenado;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;
import InstructionGenerator.*;

import java.io.IOException;

public class CallNode extends SentenceNode {

    private AccessNode accessNode;
    private Type callType;

    public CallNode(Token token, AccessNode accessNode) {
        super(token);
        this.accessNode = accessNode;
    }

    public boolean isVariableDeclaration() {return false;}

    @Override
    public void check() throws SemanticExceptionSimple {
        this.callType = this.accessNode.check();
        if (this.accessNode.getEncadenado() != null) {
            Encadenado accessNodeEncadenado = this.accessNode.getEncadenado();
            while (accessNodeEncadenado.getEncadenado() != null)
                accessNodeEncadenado = accessNodeEncadenado.getEncadenado();
            if (!accessNodeEncadenado.isCallable())
                throw new SemanticExceptionSimple(accessNodeEncadenado.getToken(), "llamada incorrecta");
        }
        else {
            if (!accessNode.isCallable())
                throw new SemanticExceptionSimple(accessNode.getToken(), "llamada incorrecta");
        }
    }

    public void generateCode() throws IOException {
        this.accessNode.generateCode();
        if (!this.callType.getClassName().equals("void"))
            InstructionGenerator.getInstance().generateInstruction("POP       ; El retorno del metodo invocado no es void por lo que el valor retornado no es asignado a ninguna variable entonces se descarta");
    }

}
