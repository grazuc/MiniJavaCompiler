package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import InstructionGenerator.*;
import java.io.IOException;

public class BinaryExpressionNode extends ExpressionNode {

    private ExpressionNode leftSide;
    private ExpressionNode rightSide;

    public BinaryExpressionNode(Token expressionOperator, ExpressionNode leftSide, ExpressionNode rightSide) {
        super(expressionOperator);
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        Type leftSideTypeExpression = leftSide.check();
        Type rightSideTypeExpression = rightSide.check();
        String operator = this.token.getLexeme();
        if (leftSideTypeExpression.isCompatibleWithOperator(operator) && rightSideTypeExpression.isCompatibleWithOperator(operator)) {
            if (leftSideTypeExpression.isCompatibleWithType(rightSideTypeExpression) || rightSideTypeExpression.isCompatibleWithType(leftSideTypeExpression)) {
                Type primitiveType = new PrimitiveType(this.token);
                primitiveType.setClassName(this.token);
                return primitiveType;
            }
            else
                throw new SemanticExceptionSimple(this.token, "El tipo " + leftSideTypeExpression.getClassName() + " no es compatible con el tipo " + rightSideTypeExpression.getClassName());
        }
        else
            if (leftSideTypeExpression.isCompatibleWithOperator(operator))
                throw new SemanticExceptionSimple(this.token, "El operador " + this.token.getLexeme() + " no es compatible con el tipo " + rightSideTypeExpression.getClassName());
            else
                throw new SemanticExceptionSimple(this.token, "El operador " + this.token.getLexeme() + " no es compatible con el tipo " + leftSideTypeExpression.getClassName());
    }

    @Override
    public void generateCode() throws IOException {

        this.leftSide.generateCode();
        this.rightSide.generateCode();
        String expressionOperator = this.token.getLexeme();

        switch (expressionOperator) {
            case "==": {
                InstructionGenerator.getInstance().generateInstruction("EQ");
                break;
            }
            case "!=": {
                InstructionGenerator.getInstance().generateInstruction("NE");
                break;
            }
            case "+": {
                InstructionGenerator.getInstance().generateInstruction("ADD");
                break;
            }
            case "-": {
                InstructionGenerator.getInstance().generateInstruction("SUB");
                break;
            }
            case "*": {
                InstructionGenerator.getInstance().generateInstruction("MUL");
                break;
            }
            case "/": {
                InstructionGenerator.getInstance().generateInstruction("DIV");
                break;
            }
            case "%": {
                InstructionGenerator.getInstance().generateInstruction("MOD");
                break;
            }
            case "<=": {
                InstructionGenerator.getInstance().generateInstruction("LE");
                break;
            }
            case ">=": {
                InstructionGenerator.getInstance().generateInstruction("GE");
                break;
            }
            case "<": {
                InstructionGenerator.getInstance().generateInstruction("LT");
                break;
            }
            case ">": {
                InstructionGenerator.getInstance().generateInstruction("GT");
                break;
            }
            case "||": {
                InstructionGenerator.getInstance().generateInstruction("OR");
                break;
            }
            case "&&": {
                InstructionGenerator.getInstance().generateInstruction("AND");
                break;
            }
        }
    }

}
