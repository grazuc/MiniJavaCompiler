package SemanticAnalyzer;

import AST.Sentence.BlockNode;
import LexicalAnalyzer.Token;

import java.util.ArrayList;
import java.util.Hashtable;

public abstract class MethodOrConstructor {
    protected Token token;
    protected boolean isConstructor;


    public MethodOrConstructor(Token token){
        this.token = token;
        isConstructor = false;
    }


    public abstract void setPrincipalBlock(BlockNode blocknode);
    public abstract BlockNode getPrincipalBlock();
    public abstract void setCurrentBlock(BlockNode blocknode);
    public abstract BlockNode getCurrentBlock();

    public abstract void insertParameter(Parameter parameter);
    public abstract String getMethodName();
    public abstract String getStaticHeader();
    public abstract void setClassName(String n);
    public abstract Type getReturnType();
    public abstract ArrayList<Parameter> getParametersList();
    public abstract void checkDeclaration();
    public abstract boolean correctRedefinedMethodHeader(MethodOrConstructor ancestor);
    public abstract boolean methodsHeadersAreEquals(MethodOrConstructor m);
    public abstract boolean hasParameters();
    public abstract boolean isChecked();
    public abstract void setChecked();
    public abstract ConcreteClass getMethodClass();
    public void setIsConstructor(){
        isConstructor = true;
    }

    public Token getToken(){
        return token;
    }

    public boolean isConstructor(){
        return isConstructor;
    }
}
