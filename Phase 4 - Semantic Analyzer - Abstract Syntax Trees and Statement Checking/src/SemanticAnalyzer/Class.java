package SemanticAnalyzer;

import java.util.HashSet;
import java.util.Hashtable;
import LexicalAnalyzer.Token;

public abstract class Class {

    protected Token classToken;
    protected boolean consolidated;
    protected boolean hasCyclicInheritance;
    protected Hashtable<String, MethodOrConstructor> classMethods;

    public Class(Token token){
        classToken = token;
        consolidated=false;
        hasCyclicInheritance=false;
        classMethods = new Hashtable<>();
    }

    protected void setConsolidated(){
        consolidated= true;
    }

    protected boolean methodAlreadyExists(MethodOrConstructor method){
        return classMethods.containsKey(method.getMethodName());
    }

    public Hashtable<String,MethodOrConstructor> getMethods(){
        return classMethods;
    }

    public MethodOrConstructor getMethod(String methodName){
        return classMethods.get(methodName);
    }

    public Token getToken(){
        return classToken;
    }

    public String getClassName(){
        return classToken.getLexeme();
    }

    public abstract void insertMethod(MethodOrConstructor methodToInsert) throws SemanticException;

    public abstract void consolidate() throws SemanticException;

    public abstract void checkDeclarations() throws SemanticException;

    public void insertConstructor(Constructor constructor) {
    }

    public boolean hasConstructor() {
        return false;
    }

    public void setHasConstructorTrue() {
    }
}
