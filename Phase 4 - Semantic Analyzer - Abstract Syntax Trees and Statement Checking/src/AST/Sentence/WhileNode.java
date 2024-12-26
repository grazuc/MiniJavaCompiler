package AST.Sentence;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;

public class WhileNode extends SentenceNode {

    private ExpressionNode condition;
    private SentenceNode sentence;

    public WhileNode(Token token, ExpressionNode condition, SentenceNode sentence) {
        super(token);
        this.condition = condition;
        this.sentence = sentence;
    }

    @Override
    public void check() throws SemanticExceptionSimple {
        Type conditionType = this.condition.check();
        if (conditionType != null)
            if (conditionType.isPrimitive() && conditionType.getClassName().equals("boolean"))
                this.sentence.check();
            else
                throw new SemanticExceptionSimple(condition.getToken(), "La condicion del while debe ser de tipo primitivo boolean");
    }
}
