package InstructionGenerator;

import SemanticAnalyzer.ConcreteClass;
import SemanticAnalyzer.SymbolTable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class InstructionGenerator {

    private BufferedWriter bufferedWriter;
    private String outputFileName;
    private File outputFile;
    private String currentCodeMode;
    private static InstructionGenerator instance = null;

    private InstructionGenerator() {

    }

    public static InstructionGenerator getInstance() {
        if (instance == null)
            instance = new InstructionGenerator();
        return instance;
    }

    public void generateInstructions() throws IOException {
        outputFile = new File(outputFileName);
        FileWriter fileWriter = new FileWriter(outputFile);
        bufferedWriter = new BufferedWriter(fileWriter);
        this.currentCodeMode = ".";
        this.setCodeMode();
        this.generateMainMethodCall();
        this.initSimpleMallocRoutine();
        this.generateClassesCode();
        bufferedWriter.close();
    }

    private void generateMainMethodCall() throws IOException {
        String mainMethodLabel = SymbolTable.getInstance().getMainMethod().getMethodLabel();
        this.generateInstruction("PUSH " + mainMethodLabel);
        this.generateInstruction("CALL");
        this.generateInstruction("HALT");
    }

    private void initSimpleMallocRoutine() throws IOException {
        this.generateInstruction("simple_malloc:");
        this.generateInstruction("LOADFP");
        this.generateInstruction("LOADSP");
        this.generateInstruction("STOREFP");
        this.generateInstruction("LOADHL");
        this.generateInstruction("DUP");
        this.generateInstruction("PUSH 1");
        this.generateInstruction("ADD");
        this.generateInstruction("STORE 4");
        this.generateInstruction("LOAD 3");
        this.generateInstruction("ADD");
        this.generateInstruction("STOREHL");
        this.generateInstruction("STOREFP");
        this.generateInstruction("RET 1");
    }

    private void generateClassesCode() throws IOException {
        for (ConcreteClass concreteClass: SymbolTable.getInstance().getConcreteClassesTable().values())
            concreteClass.generateVT();
        for (ConcreteClass concreteClass: SymbolTable.getInstance().getConcreteClassesTable().values())
            concreteClass.generateCode();
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public void generateInstruction(String instruction) throws IOException {
        if (instruction.contains(":")) {
            bufferedWriter.write(instruction);
            bufferedWriter.newLine();
        }
        else {
            if (!instruction.contains(";")) {
                bufferedWriter.write("                              " + instruction);
                bufferedWriter.newLine();
            }
            else {
                String instructionBeforeSemiColon = generateInstructionBeforeSemiColon(instruction);
                String instructionFromTheSemiColon = instruction.substring(instructionBeforeSemiColon.length());
                String instructionFromTheSemiColonWithWhiteSpaces = this.generateWhiteSpaces(instructionBeforeSemiColon.length(), instructionFromTheSemiColon); //instructionBeforeSemiColon.length());
                bufferedWriter.write("                              " + instructionBeforeSemiColon);
                bufferedWriter.write(instructionFromTheSemiColonWithWhiteSpaces);
                bufferedWriter.newLine();
            }
        }
    }

    private String generateWhiteSpaces(int instructionBeforSemiColonLength, String instruction) {
        int indexWhereTheInstructionStarts = 35;
        String stringWithWhiteSpaces = "";
        while (instructionBeforSemiColonLength < indexWhereTheInstructionStarts) {
            stringWithWhiteSpaces += " ";
            instructionBeforSemiColonLength++;
        }
        return stringWithWhiteSpaces + instruction;
    }

    private String generateInstructionBeforeSemiColon(String instruction) {
        boolean foundSemiColon = false;
        int endStringIndex = 1;

        while (!foundSemiColon) {
            if (instruction.contains(";")) {
                instruction = instruction.substring(0, instruction.length() - endStringIndex);
            }
            else
                foundSemiColon = true;
        }
        return instruction;
    }

    public void setDataMode() throws IOException {
        if (!this.currentCodeMode.equals(".DATA")) {
            this.bufferedWriter.write(".DATA");
            this.bufferedWriter.newLine();
            this.currentCodeMode = ".DATA";
        }
    }

    public void setCodeMode() throws IOException {
        if (!this.currentCodeMode.equals(".CODE")) {
            this.bufferedWriter.write(".CODE");
            this.bufferedWriter.newLine();
            this.currentCodeMode = ".CODE";
        }
    }
}