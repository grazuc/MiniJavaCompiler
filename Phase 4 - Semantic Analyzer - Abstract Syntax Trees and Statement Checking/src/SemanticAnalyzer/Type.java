package SemanticAnalyzer;

import LexicalAnalyzer.Token;

public abstract class Type {

    protected Token tokenType;
    protected String className;

    public Type(Token tokenType) {
        this.tokenType = tokenType;
        this.className = tokenType.getLexeme();
    }

    public String toString() {
        return this.tokenType.getLexeme();
    }

    public abstract String getClassName();

    public abstract boolean isPrimitive();

    public Token getToken() {
        return this.tokenType;
    }

    public abstract boolean isCompatibleWithOperator(String operator);

    public abstract void setClassName(Token tokenType);

    public abstract boolean isCompatibleWithType(Type typeToCompareWith);
}