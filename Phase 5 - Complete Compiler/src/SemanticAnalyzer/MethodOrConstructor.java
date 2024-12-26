package SemanticAnalyzer;

import AST.Sentence.BlockNode;
import LexicalAnalyzer.Token;

import java.io.IOException;
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
    public abstract void setParametersOffset();
    public abstract void generateCode()throws IOException;
    public abstract boolean codeIsGenerated();
    public abstract String getMethodLabel();
    public abstract void setCodeGenerated();
    public abstract void setOffset(int offset);
    public abstract int getOffset();
    public abstract void setOffsetIsSet();
    public abstract boolean hasOffset();
    public abstract boolean isInterfaceMethod();
    public abstract void setAsInterfaceMethod();
    public abstract void setInterface(Interface interfaceMethod);
    public abstract Interface getInterfaceMethod();
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
