package AST.Sentence;

import AST.Access.AccessNode;
import AST.Encadenado.Encadenado;
import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;

public class AssignmentNode extends SentenceNode {

    private AccessNode leftSide;
    private ExpressionNode rightSide;

    public AssignmentNode(Token token, AccessNode leftSideNode, ExpressionNode rightSideNode) {
        super(token);
        this.leftSide = leftSideNode;
        this.rightSide = rightSideNode;
    }

    @Override
    public void check() throws SemanticExceptionSimple {
        //System.out.println("LI: chequear nodo asignacion: "+leftSide);
        //System.out.println("LD: chequear nodo asignacion: "+rightSide);
        Type leftSideType;
        if (this.leftSideIsAssignable())
            leftSideType = this.leftSide.check();
        else
            throw new SemanticExceptionSimple(this.token, "El lado izquierdo de la asignación no es asignable");
        Type rightSideAssignmentType = this.rightSide.check();
        if (rightSideAssignmentType != null){
            if (!rightSideAssignmentType.isCompatibleWithType(leftSideType))
                throw new SemanticExceptionSimple(this.token, "el tipo del lado izquierdo de la asignacion " + leftSideType.getClassName() +  " no conforma con el tipo " + rightSideAssignmentType.getClassName());
            if (!bothSidesAreCompatibleWithOperator(leftSideType, rightSideAssignmentType))
                throw new SemanticExceptionSimple(this.token, "el tipo del lado izquierdo y del lado derecho de la asignación no son compatibles con el operador de asignacion " + this.token.getLexeme());
        }
    }

    private boolean leftSideIsAssignable() {
        Encadenado leftSideCad = leftSide.getEncadenado();
        if (leftSideCad != null) {
            boolean isLastCad = false;
            while (!isLastCad) {
                if (leftSideCad.getEncadenado() == null)
                    isLastCad = true;
                else
                    leftSideCad = leftSideCad.getEncadenado();
            }
            return leftSideCad.isAssignable();
        }
        else
            return leftSide.isAssignable();
    }

    private boolean bothSidesAreCompatibleWithOperator(Type leftSideAssignmentType, Type rightSideAssignmentType) {
        String operator = this.token.getLexeme();
        return leftSideAssignmentType.isCompatibleWithOperator(operator) && rightSideAssignmentType.isCompatibleWithOperator(operator);
    }
}
