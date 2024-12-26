package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.PrimitiveType;
import SemanticAnalyzer.Type;

public class CharNode extends LiteralOperandNode {

    public CharNode(Token currentToken) {
        super(currentToken);
    }

    @Override
    public Type check() {
        return new PrimitiveType(new Token("pr_char","char",0));
    }

}
