package AST.Expression;

import LexicalAnalyzer.Token;
import SemanticAnalyzer.ReferenceType;
import SemanticAnalyzer.Type;
import InstructionGenerator.*;
import java.io.IOException;

public class StringNode extends LiteralOperandNode {

    private static int stringLabelNumber = 0;

    public StringNode(Token currentToken) {
        super(currentToken);
    }

    @Override
    public Type check() {
        return new ReferenceType(new Token("idClase", "String", 0));
    }

    @Override
    public void generateCode() throws IOException {
        //se genera el código en .data
        //no se recomienda alojar los Strings de MiniJava en .data ya que se pueden manejar creando objetos en el .heap

        InstructionGenerator.getInstance().setDataMode();
        String label = this.generateStringLabel();
        String instruction = label + ":";
        InstructionGenerator.getInstance().generateInstruction(instruction);
        InstructionGenerator.getInstance().generateInstruction("DW " + this.token.getLexeme() + ", 0");

        InstructionGenerator.getInstance().setCodeMode();
        InstructionGenerator.getInstance().generateInstruction("PUSH " + label);
    }

    private String generateStringLabel() {
        String label =  "str_label_" + this.stringLabelNumber;
        this.stringLabelNumber += 1;
        return label;
    }

}
