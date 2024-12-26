package SemanticAnalyzer;

import java.util.HashSet;
import java.util.Hashtable;
import LexicalAnalyzer.Token;

public abstract class Class {

    protected Token classToken;
    protected boolean consolidated;
    protected boolean hasCyclicInheritance;
    protected Hashtable<String, Method> classMethods;

    public Class(Token token){
        classToken = token;
        consolidated=false;
        hasCyclicInheritance=false;
        classMethods = new Hashtable<>();
    }

    protected void setConsolidated(){
        consolidated= true;
    }

    protected boolean methodAlreadyExists(Method method){
        return classMethods.containsKey(method.getMethodName());
    }

    public Hashtable<String,Method> getMethods(){
        return classMethods;
    }

    public Method getMethod(String methodName){
        return classMethods.get(methodName);
    }

    public Token getToken(){
        return classToken;
    }

    public String getClassName(){
        return classToken.getLexeme();
    }

    public abstract void insertMethod(Method methodToInsert) throws SemanticException;

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
