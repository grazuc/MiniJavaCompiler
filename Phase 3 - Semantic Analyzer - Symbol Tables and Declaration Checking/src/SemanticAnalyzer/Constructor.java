package SemanticAnalyzer;

import LexicalAnalyzer.Token;

import java.util.ArrayList;
import java.util.Hashtable;

public class Constructor {

    Token constructorToken;
    private ArrayList<Parameter> parametersList;
    private Hashtable<String, Parameter> parametersTable;

    public Constructor(Token token) {
        constructorToken = token;
        parametersList = new ArrayList<>();
        parametersTable = new Hashtable<>();
    }

    public void insertParameter(Parameter parameterToInsert) {
        if (!parametersTable.containsKey(parameterToInsert.getParameterName())) {
            //System.out.println("Entre al if con: "+parameterToInsert.getParameterToken().getLexeme());
            parametersTable.put(parameterToInsert.getParameterName(), parameterToInsert);
            parametersList.add(parameterToInsert);
        }
        else{
            //System.out.println("Entre al else con: "+parameterToInsert.getParameterToken().getLexeme());
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(parameterToInsert.getParameterToken(), "El parametro " + parameterToInsert.getParameterName() + " ya esta declarado en el constructor " + "\"" + constructorToken.getLexeme() + "\""));
        }
    }

    public void checkDeclaration() {
        checkNoPrimitiveParameters();
    }

    private void checkNoPrimitiveParameters() {
        for (Parameter parameter: parametersTable.values()) {
            if (!parameter.getParameterType().isPrimitive())
                if (!parameterTypeIsDeclared(parameter)) {
                    Token parameterTypeToken = parameter.getParameterType().getToken();
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(parameterTypeToken, "El tipo del parametro " + "\"" + parameter.getParameterName() + "\"" + " del constructor " + "\"" + constructorToken.getLexeme() + "\"" + " no esta declarado"));
                }
        }
    }

    private boolean parameterTypeIsDeclared(Parameter parameter) {
        Type parameterType = parameter.getParameterType();
        String parameterClass = parameterType.getClassName();
        return SymbolTable.getInstance().concreteClassIsDeclared(parameterClass) || SymbolTable.getInstance().interfaceIsDeclared(parameterClass);
    }

    public Token getConstructorToken(){
        return constructorToken;
    }


}