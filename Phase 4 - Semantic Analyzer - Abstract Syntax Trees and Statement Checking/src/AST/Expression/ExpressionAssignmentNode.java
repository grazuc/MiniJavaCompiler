package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;


public class ExpressionAssignmentNode extends ExpressionNode{

    protected ExpressionNode leftSide;
    protected ExpressionNode rightSide;

    public ExpressionAssignmentNode(Token expressionToken,ExpressionNode leftSideNode, ExpressionNode rightSideNode){
        super(expressionToken);
        leftSide = leftSideNode;
        rightSide = rightSideNode;
    }

    public void setLeftSide(ExpressionNode leftSideNode){
        leftSide = leftSideNode;
    }

    public void setRightSide(ExpressionNode rightSideNode){
        rightSide = rightSideNode;
    }

    public void setToken(Token token){
        this.token = token;
    }

    public ExpressionNode getLeftSide(){
        return leftSide;
    }

    public ExpressionNode getRightSide(){
        return rightSide;
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        //System.out.println("LI: chequear nodo asignacion: "+leftSide);
        //System.out.println("LD: chequear nodo asignacion: "+rightSide);
        Type toReturn = null;
        if (leftSide!= null){
            toReturn = leftSide.check();
            if (rightSide == null){
                return toReturn;
            } else {
                toReturn = rightSide.check();
            }
        }
        return toReturn;
    }
}
