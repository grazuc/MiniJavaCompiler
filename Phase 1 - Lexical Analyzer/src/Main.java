import FileHandler.FileHandler;
import LexicalAnalyzer.*;
import SyntacticAnalyzer.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Main {
    public static void main(String[] args) {
        File file = new File(args[0]);
        FileHandler fileHandler = null;
        Map<String, String> keywordsMap = new HashMap<>();
        LexicalAnalyzer lexicalAnalyzer = null;
        SyntacticAnalyzer syntaxAnalyzer = null;

        try {
            file = new File(args[0]);
        } catch (ArrayIndexOutOfBoundsException exception) {
            exception.printStackTrace();
        }
        try {
            fileHandler = new FileHandler(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        keywordsMap.put("class", "pr_class");
        keywordsMap.put("interface", "pr_interface");
        keywordsMap.put("extends", "pr_extends");
        keywordsMap.put("implements", "pr_implements");
        keywordsMap.put("public", "pr_public");
        keywordsMap.put("static", "pr_static");
        keywordsMap.put("void", "pr_void");
        keywordsMap.put("boolean", "pr_boolean");
        keywordsMap.put("char", "pr_char");
        keywordsMap.put("int", "pr_int");
        keywordsMap.put("if", "pr_if");
        keywordsMap.put("else", "pr_else");
        keywordsMap.put("while", "pr_while");
        keywordsMap.put("return", "pr_return");
        keywordsMap.put("var", "pr_var");
        keywordsMap.put("this", "pr_this");
        keywordsMap.put("new", "pr_new");
        keywordsMap.put("null", "pr_null");
        keywordsMap.put("true", "pr_true");
        keywordsMap.put("false", "pr_false");

        try {
            lexicalAnalyzer = new LexicalAnalyzer(fileHandler, keywordsMap);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        ArrayList<Token> tokensList = new ArrayList<>();
        boolean tokensLeft = true;
        boolean encontreErrores = false;
        while (tokensLeft) {
            try {
                Token token = lexicalAnalyzer.nextToken();
                tokensList.add(token);
                if (token.getTokenId().equals("EOF")) {
                    tokensLeft = false;
                    if (!encontreErrores){
                        for (Token tokenToPrint : tokensList)
                            System.out.println(tokenToPrint.toString());
                        System.out.println("\n[SinErrores]");
                    }
                }
            } catch (IOException | LexicalException exception) {
                System.out.println(exception.getMessage());
                encontreErrores = true;
            }
        }

    }
}