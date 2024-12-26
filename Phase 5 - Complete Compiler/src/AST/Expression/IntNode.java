package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.PrimitiveType;
import SemanticAnalyzer.Type;
import InstructionGenerator.*;

import java.io.IOException;

public class IntNode extends LiteralOperandNode {

    public IntNode(Token token) {
        super(token);
    }

    public Type check() {
        return new PrimitiveType(new Token("pr_int","int",0));
    }
    public void generateCode() throws IOException {
        InstructionGenerator.getInstance().generateInstruction("PUSH " + this.token.getLexeme());
    }
}
