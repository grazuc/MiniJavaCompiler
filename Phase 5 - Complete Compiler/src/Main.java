import FileHandler.FileHandler;
import LexicalAnalyzer.LexicalAnalyzer;
import LexicalAnalyzer.LexicalException;
import SemanticAnalyzer.*;
import SyntacticAnalyzer.SyntacticAnalyzer;
import SyntacticAnalyzer.SyntacticException;
import InstructionGenerator.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Main {
    public static void main(String[] args) {
        File inputFile = new File(args[0]);
        String outputFileName = args[1];
        Map<String, String> keywordsMap = new HashMap<>();
        FileHandler fileHandler = null;
        LexicalAnalyzer lexicalAnalyzer = null;
        SyntacticAnalyzer syntaxAnalyzer = null;

        try {
            fileHandler = new FileHandler(inputFile);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
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
            SymbolTable.getInstance().emptySymbolTable();

            lexicalAnalyzer = new LexicalAnalyzer(fileHandler, keywordsMap);
            syntaxAnalyzer = new SyntacticAnalyzer(lexicalAnalyzer);

            SymbolTable.getInstance().checkDeclarations();
            SymbolTable.getInstance().consolidate();

            if (SymbolTable.getInstance().getSemanticErrorsList().size() > 0)
                throw new SemanticException(SymbolTable.getInstance().getSemanticErrorsList());
        } catch (IOException | LexicalException | SyntacticException |
                 SemanticException | SemanticExceptionSimple exception) {
            System.out.println(exception.getMessage());
        }

        try {
            SymbolTable.getInstance().checkSentences();
            InstructionGenerator.getInstance().setOutputFileName(outputFileName);
            InstructionGenerator.getInstance().generateInstructions();
            System.out.println("Compilación Exitosa\n\n");
            System.out.println("[SinErrores]");
        } catch (SemanticExceptionSimple exceptionSimple) {
            System.out.println(exceptionSimple.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

