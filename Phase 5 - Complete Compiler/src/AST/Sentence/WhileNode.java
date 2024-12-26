package AST.Sentence;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;
import InstructionGenerator.*;
import java.io.IOException;

public class WhileNode extends SentenceNode {

    private ExpressionNode condition;
    private SentenceNode sentence;
    //las etiquetas tienen que ser unicas por se define con alcance estático a la variable
    private static int whileEndLabelNumber = 0;
    private static int whileBeginLabelNumber = 0;

    public WhileNode(Token token, ExpressionNode condition, SentenceNode sentence) {
        super(token);
        this.condition = condition;
        this.sentence = sentence;
    }

    public boolean isVariableDeclaration() {return false;}

    @Override
    public void check() throws SemanticExceptionSimple {
        Type conditionType = this.condition.check();
        if (conditionType != null)
            if (conditionType.isPrimitive() && conditionType.getClassName().equals("boolean"))
                this.sentence.check();
            else
                throw new SemanticExceptionSimple(condition.getToken(), "La condicion del while debe ser de tipo primitivo boolean");
    }

    @Override
    protected void generateCode() throws IOException {
        String whileEndLabel = this.newWhileEndLabel();
        String whileBeginLabel = this.newWhileBeginLabel();

        //etiqueta de comienzo del while
        InstructionGenerator.getInstance().generateInstruction(whileBeginLabel + ":");

        //se genera el codigo de la condicion
        this.condition.generateCode();

        //si la condicion es falsa, se produce un salto a la etiqueta de fin del while
        InstructionGenerator.getInstance().generateInstruction("BF " + whileEndLabel + "               ; Si el tope de la fila es falso, se salta a " + whileEndLabel);

        //se genera el codigo de la sentencia del while
        this.sentence.generateCode();

        //se vuelve a saltar al comienzo del while donde se evaluará otra vez la condicion
        InstructionGenerator.getInstance().generateInstruction("JUMP " + whileBeginLabel);
        InstructionGenerator.getInstance().generateInstruction(whileEndLabel + ":");
    }

    private String newWhileEndLabel() {
        String labelName = "while_end_label_" + this.whileEndLabelNumber;
        this.whileEndLabelNumber += 1;
        return labelName;
    }

    private String newWhileBeginLabel() {
        String labelName = "while_begin_label_" + this.whileBeginLabelNumber;
        this.whileBeginLabelNumber += 1;
        return labelName;
    }
}
