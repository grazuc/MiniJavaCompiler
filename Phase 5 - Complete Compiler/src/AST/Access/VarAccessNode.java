package AST.Access;

import AST.Sentence.LocalVarDeclarationNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.*;
import InstructionGenerator.*;
import java.io.IOException;
import java.util.Hashtable;

public class VarAccessNode extends AccessNode {

    private LocalVarDeclarationNode localVar;
    private Attribute attribute;
    private Parameter parameter;
    private Hashtable<String,Attribute> table;

    public VarAccessNode(Token token) {
        super(token);
        this.isAssignable = true;
        table = new Hashtable<>();
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public boolean isCallable() {
        return false;
    }

    @Override
    public Type check() throws SemanticExceptionSimple {
        Type varType;
        String varName = this.token.getLexeme();
        MethodOrConstructor currentMethod = SymbolTable.getInstance().getCurrentMethod();
        //System.out.println("Metodo actual: "+SymbolTable.getInstance().getCurrentMethod().getToken());
        if (SymbolTable.getInstance().isMethodParameter(varName, currentMethod)){
            varType = SymbolTable.getInstance().retrieveParameterType(varName, currentMethod);
            parameter=SymbolTable.getInstance().retrieveParameter(varName,currentMethod);
        }
        else
            if (SymbolTable.getInstance().isCurrentBlockLocalVar(varName)) {
                this.localVar = SymbolTable.getInstance().retrieveLocalVar(varName);
                varType = localVar.getLocalVarType();
            }
            else {
                ConcreteClass methodClass = currentMethod.getMethodClass();
                    if (SymbolTable.getInstance().isAttribute(varName, methodClass)) {
                        this.attribute = methodClass.getAttributes().get(this.token.getLexeme());
                        if (!SymbolTable.getInstance().getCurrentMethod().isConstructor()){
                        if (!SymbolTable.getInstance().getCurrentMethod().getStaticHeader().equals("static"))
                            varType = SymbolTable.getInstance().retrieveAttribute(varName, methodClass);
                        else
                            if (SymbolTable.getInstance().getCurrentMethod().getStaticHeader().equals("static") && methodClass.getAttributes().get(this.token.getLexeme()).getStaticOptional().equals("static"))
                                varType = SymbolTable.getInstance().retrieveAttribute(varName, methodClass);
                            else
                                throw new SemanticExceptionSimple(this.token, "un metodo estatico no puede acceder a un atributo");
                        }
                        varType = SymbolTable.getInstance().retrieveAttribute(varName, methodClass);
                    }
                    else{
                        if (!SymbolTable.getInstance().getCurrentMethod().getStaticHeader().equals("static")){
                            if (SymbolTable.getInstance().getCurrentMethod().isConstructor()){
                                throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es una variable local ni un parametro del constructor " + "\"" + currentMethod.getMethodName() + "\"" + " ni un atributo de la clase " + methodClass.getClassName());
                            }
                            else
                                throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es una variable local ni un parametro del metodo " + "\"" + currentMethod.getMethodName() + "\"" + " ni un atributo de la clase " + methodClass.getClassName());
                        }
                        else{
                                throw new SemanticExceptionSimple(this.token, this.token.getLexeme() + " no es una variable local ni un parametro del metodo " + "\"" + currentMethod.getMethodName() + "\"" );
                        }
                    }
            }
        if (this.encadenado != null) {
            if (!varType.isPrimitive())
                return this.encadenado.check(varType);
            else
                throw new SemanticExceptionSimple(this.token, "el lado izquierdo del encadenado es un tipo primitivo");
        }
        return varType;
    }

    @Override
    public void generateCode() throws IOException {
        if (attribute != null) {
            if (attribute.getStaticOptional().equals("static")) {
                generateCodeStaticAccess();
            } else {
                generateCodeDynamicAccess();
            }
        } else generateLocalVarAndParameterCode();
    }

    public void generateCodeStaticAccess() throws IOException{
        boolean access = !isLeftSide() || encadenado != null;
        String label = attribute.getAttributeLabel();
        InstructionGenerator.getInstance().generateInstruction("PUSH "+label);
        attribute.setStatic();
        if (access){
            InstructionGenerator.getInstance().generateInstruction("LOADREF 0");
        } else {
            InstructionGenerator.getInstance().generateInstruction("SWAP");
            InstructionGenerator.getInstance().generateInstruction("STOREREF 0");
        }
    }

    public void generateCodeDynamicAccess() throws IOException{
        generateLocalVarAndParameterCode();
    }

    public void generateLocalVarAndParameterCode() throws IOException{
        //genero codigo para una variable local
        if (this.localVar != null) {
            if (!this.isLeftSide() || this.encadenado != null) //si el acceso a var es lado derecho..
                InstructionGenerator.getInstance().generateInstruction("LOAD " + this.localVar.getVarOffset() + " ; Se apila el valor de la variable local " + this.localVar.getVarName());
            else
                InstructionGenerator.getInstance().generateInstruction("STORE " + this.localVar.getVarOffset());
        }

        //genero codigo para un parametro
        if (this.parameter != null) {
            if (!this.isLeftSide() || this.encadenado != null)
                InstructionGenerator.getInstance().generateInstruction("LOAD " + this.parameter.getOffset() + " ; Se apila el valor del parametro " + this.parameter.getParameterName());
            else
                InstructionGenerator.getInstance().generateInstruction("STORE " + this.parameter.getOffset());
        }

        if (attribute!= null){
            if (!attribute.getStaticOptional().equals("static")){
                InstructionGenerator.getInstance().generateInstruction("LOAD 3");
                if (!isLeftSide() || encadenado!=null){
                    InstructionGenerator.getInstance().generateInstruction("LOADREF "+attribute.getOffset()+"              ; Se apila el valor del atributo "+attribute.getAttributeName());
                } else {
                    InstructionGenerator.getInstance().generateInstruction("SWAP");
                    InstructionGenerator.getInstance().generateInstruction("STOREREF "+attribute.getOffset());
                }
            }

        }

        if (this.encadenado != null)
            encadenado.generateCode();
    }


}
