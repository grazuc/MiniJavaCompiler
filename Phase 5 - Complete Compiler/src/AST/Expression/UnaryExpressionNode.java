package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import InstructionGenerator.*;
import java.io.IOException;

public class UnaryExpressionNode extends ExpressionNode {

    private ExpressionNode operandNode;

    public UnaryExpressionNode(Token token, OperandNode operandNode) {
        super(token);
        this.operandNode = operandNode;
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        Type operandType = operandNode.check();
        String operator =  this.token.getLexeme();
        if (operandType.isCompatibleWithOperator(operator))
            return operandType;
        else
            throw new SemanticExceptionSimple(this.token, "El operador " + this.token.getLexeme() + " no es compatible con el tipo " + operandType.getClassName());
    }

    @Override
    public void generateCode() throws IOException {
        this.operandNode.generateCode();
        if (this.token.getLexeme().equals("!"))
            InstructionGenerator.getInstance().generateInstruction("NOT");
        if (this.token.getLexeme().equals("-"))
            InstructionGenerator.getInstance().generateInstruction("NEG");
    }

}
