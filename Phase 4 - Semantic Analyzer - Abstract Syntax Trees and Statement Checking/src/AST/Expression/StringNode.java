package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.ReferenceType;
import SemanticAnalyzer.Type;

public class StringNode extends LiteralOperandNode {

    public StringNode(Token currentToken) {
        super(currentToken);
    }

    @Override
    public Type check() {
        return new ReferenceType(new Token("idClase", "String", 0));
    }

}
