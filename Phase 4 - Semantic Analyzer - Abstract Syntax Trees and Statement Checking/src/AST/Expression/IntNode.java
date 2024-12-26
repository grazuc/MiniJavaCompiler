package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.PrimitiveType;
import SemanticAnalyzer.Type;

public class IntNode extends LiteralOperandNode {

    public IntNode(Token token) {
        super(token);
    }

    public Type check() {
        return new PrimitiveType(new Token("pr_int","int",0));
    }
}
