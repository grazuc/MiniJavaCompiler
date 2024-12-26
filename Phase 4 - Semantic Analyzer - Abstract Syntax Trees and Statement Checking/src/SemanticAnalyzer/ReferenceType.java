package SemanticAnalyzer;

import LexicalAnalyzer.Token;
import java.util.Arrays;

public class ReferenceType extends Type {

    public ReferenceType(Token tokenType) {
        super(tokenType);
    }

    @Override
    public String getClassName() {
        return this.tokenType.getLexeme();
    }

    public boolean isPrimitive() {
        return false;
    }

    public boolean isCompatibleWithOperator(String operator) {
        return Arrays.asList("=", "==", "!=").contains(operator);
    }

    @Override
    public void setClassName(Token tokenType) {

    }

    public boolean isCompatibleWithType(Type typeToCompareWith) {
        if (typeToCompareWith.isPrimitive())
            return false;
        if (this.tokenType.getLexeme().equals("null") || typeToCompareWith.getClassName().equals("null"))
            return true;
        if (this.tokenType.getLexeme().equals(typeToCompareWith.getClassName()))
            return true;
        ConcreteClass concreteClass = SymbolTable.getInstance().getConcreteClass(this.getClassName());
        Interface comparingInterface = SymbolTable.getInstance().getInterface(typeToCompareWith.getClassName());
        //comparo clase con interface
        if (concreteClass != null && comparingInterface != null) {
            if (concreteClass.hasAncestorInterface(comparingInterface.getClassName()))
                return true;
            while (concreteClass.getAncestorClass() != null) {
                if (concreteClass.getAncestorClass() != null) {
                    if (concreteClass.getAncestorClass().hasAncestorInterface(comparingInterface.getClassName()))
                        return true;
                    concreteClass = concreteClass.getAncestorClass();
                }
            }
        }
        else {
            if (concreteClass == null) {
                Interface thisClassInterface = SymbolTable.getInstance().getInterface(this.getClassName());
                if (comparingInterface != null) {
                    //son dos interfaces las que comparo
                    if (thisClassInterface.hasAncestorInterface(comparingInterface.getClassName()))
                        return true;
                }
                else {
                    //comparo interface con clase
                    ConcreteClass comparingConcreteClass = SymbolTable.getInstance().getConcreteClass(typeToCompareWith.getClassName());
                    if (comparingConcreteClass.getClassName().equals("Object"))
                        return true;
                    if (comparingConcreteClass.hasAncestorInterface(thisClassInterface.getClassName()))
                        return true;
                }
            }
            //comparo dos clases
            else {
                while (concreteClass.getAncestorClass() != null) {
                    if (concreteClass.getAncestorClass().getClassName().equals(typeToCompareWith.getClassName()))
                        return true;
                    concreteClass = concreteClass.getAncestorClass();
                }
            }
        }
        return false;
    }

}