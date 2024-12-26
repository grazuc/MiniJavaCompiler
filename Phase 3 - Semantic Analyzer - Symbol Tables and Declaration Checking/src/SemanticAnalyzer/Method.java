package SemanticAnalyzer;

import LexicalAnalyzer.Token;
import java.util.ArrayList;
import java.util.Hashtable;

public class Method {

    private Token methodToken;
    private String staticScope;
    private Type methodReturnType;
    private ArrayList<Parameter> parametersList;
    private Hashtable<String, Parameter> parametersTable;

    public Method(Token methodtoken, String staticscope, Type methodreturntype) {
        staticScope = staticscope;
        methodToken = methodtoken;
        methodReturnType = methodreturntype;
        parametersList = new ArrayList<>();
        parametersTable = new Hashtable<>();
    }

    public void insertParameter(Parameter parameterToInsert) {
        if (!parametersTable.containsKey(parameterToInsert.getParameterName())) {
            parametersTable.put(parameterToInsert.getParameterName(), parameterToInsert);
            parametersList.add(parameterToInsert);
        }
        else
            SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(parameterToInsert.getParameterToken(), "El parametro " + parameterToInsert.getParameterName() + " ya esta declarado en el metodo " + "\"" + methodToken.getLexeme() + "\""));
    }

    public String getMethodName() {
        return methodToken.getLexeme();
    }

    public String getStaticHeader() {
        return staticScope;
    }

    public String getReturnType() {
        return methodReturnType.getClassName();
    }

    public ArrayList<Parameter> getParametersList() {
        return parametersList;
    }

    public void checkDeclaration() {
        checkNoPrimitiveParameters();
        checkNoPrimitiveReturnType();
    }

    private void checkNoPrimitiveParameters() {
        for (Parameter parameter: parametersTable.values()) {
            if (!parameter.getParameterType().isPrimitive())
                if (!parameterTypeIsDeclared(parameter)) {
                    Token parameterTypeToken = parameter.getParameterType().getToken();
                    SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(parameterTypeToken, "El tipo del parametro " + "\"" + parameter.getParameterName() + "\"" + " del metodo " + "\"" + methodToken.getLexeme() + "\"" + " no esta declarado"));
                }
        }
    }

    private void checkNoPrimitiveReturnType() {
        if (!methodReturnType.isPrimitive())
            if (!returnTypeClassIsDeclared())
                SymbolTable.getInstance().getSemanticErrorsList().add(new SemanticError(methodReturnType.getToken(), "El tipo de retorno del metodo " + "\"" + methodToken.getLexeme() + "\"" + " no esta declarado"));
    }

    private boolean parameterTypeIsDeclared(Parameter parameter) {
        Type parameterType = parameter.getParameterType();
        String parameterClass = parameterType.getClassName();
        return SymbolTable.getInstance().concreteClassIsDeclared(parameterClass) || SymbolTable.getInstance().interfaceIsDeclared(parameterClass);
    }

    public boolean correctRedefinedMethodHeader(Method ancestorMethod) {
        return methodsHeadersAreEquals(ancestorMethod);
    }

    public boolean methodsHeadersAreEquals(Method ancestorMethod) {
        if (!ancestorMethod.getStaticHeader().equals(staticScope) || !ancestorMethod.getReturnType().equals(methodReturnType.getClassName()) || !hasEqualsParameters(ancestorMethod))
            return false;
        return true;
    }

    private boolean hasEqualsParameters(Method ancestorMethod) {
        boolean parametersAreEquals;
        if (ancestorMethod.getParametersList().size() == parametersList.size()) {
            parametersAreEquals = true;
            int parameterIndex = 0;
            while (parametersAreEquals && (parameterIndex < parametersList.size())) {
                Parameter ancestorParameter = ancestorMethod.getParametersList().get(parameterIndex);
                if (!hasEqualsParameters(ancestorParameter, parameterIndex))
                    parametersAreEquals = false;
                parameterIndex = parameterIndex + 1;
            }
        }
        else
            parametersAreEquals = false;
        return parametersAreEquals;
    }

    private boolean hasEqualsParameters(Parameter parameterToCompareWith, int parameterIndex) {
        Parameter parameterOfThisMethod = parametersList.get(parameterIndex);
        if (!parameterToCompareWith.getParameterType().getClassName().equals(parameterOfThisMethod.getParameterType().getClassName()))
            return false;
        return true;
    }

    private boolean returnTypeClassIsDeclared() {
        return SymbolTable.getInstance().concreteClassIsDeclared(methodReturnType.getClassName()) || SymbolTable.getInstance().interfaceIsDeclared(methodReturnType.getClassName());
    }

    public Token getMethodToken() {
        return methodToken;
    }

    public boolean hasParameters() {
        return parametersList.size() != 0;
    }
}