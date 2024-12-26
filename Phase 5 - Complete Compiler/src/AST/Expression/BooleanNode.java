package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.PrimitiveType;
import SemanticAnalyzer.Type;
import InstructionGenerator.*;

import java.io.IOException;

public class BooleanNode extends LiteralOperandNode {

    public BooleanNode(Token token) {
        super(token);
    }

    @Override
    public Type check() {
        return new PrimitiveType(new Token("pr_boolean","boolean",0));
    }

    @Override
    public void generateCode() throws IOException {
        if (this.token.getLexeme().equals("true"))
            InstructionGenerator.getInstance().generateInstruction("PUSH 1");
        else
            InstructionGenerator.getInstance().generateInstruction("PUSH 0");
    }

}
