package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.ReferenceType;
import SemanticAnalyzer.Type;
import InstructionGenerator.*;

import java.io.IOException;

public class NullNode extends LiteralOperandNode {

    public NullNode(Token currentToken) {
        super(currentToken);
    }

    @Override
    public Type check() {
        return new ReferenceType(new Token("idClase", "null", 0));
    }

    @Override
    public void generateCode() throws IOException {
        InstructionGenerator.getInstance().generateInstruction("PUSH 0           ; Se apila un null");
    }

}
