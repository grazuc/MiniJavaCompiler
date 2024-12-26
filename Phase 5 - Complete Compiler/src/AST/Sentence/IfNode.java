package AST.Sentence;

import AST.Expression.ExpressionNode;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemanticExceptionSimple;
import SemanticAnalyzer.Type;
import InstructionGenerator.*;
import java.io.IOException;

public class IfNode extends SentenceNode {

    private ExpressionNode condition;
    private SentenceNode sentence;
    private SentenceNode elseSentence;

    //las etiquetas tienen que ser unicas por eso la variable es estatica
    private static int ifLabelNumber = 0;
    private static int elseLabelNumber = 0;

    public IfNode(Token token, ExpressionNode condition, SentenceNode sentence) {
        super(token);
        this.condition = condition;
        this.sentence = sentence;
    }

    public boolean isVariableDeclaration() {return false;}

    public void setElseSentence(SentenceNode elseSentence) {
        this.elseSentence = elseSentence;
    }

    @Override
    public void check() throws SemanticExceptionSimple {
        Type conditionType = this.condition.check();
        if (conditionType != null)
            if (conditionType.isPrimitive() && conditionType.getClassName().equals("boolean"))
                this.sentence.check();
            else
               throw new SemanticExceptionSimple(this.token, "La condicion del if debe ser de tipo primitivo boolean");
        if (this.elseSentence != null)
            elseSentence.check();
    }

    @Override
    public void generateCode() throws IOException {
        if (elseSentence == null){
            generateIfCode();
        } else {
            generateIfElseCode();
        }
    }

    private void generateIfCode() throws IOException {
        String ifLabel = this.newIfEndLabel();
        condition.generateCode();
        InstructionGenerator.getInstance().generateInstruction("    BF "+ifLabel+" ; Si es falso, salto al final de then, sino ejecuto then");
        sentence.generateCode();
        InstructionGenerator.getInstance().generateInstruction(ifLabel+": NOP ; Final del then");
    }

    private void generateIfElseCode() throws IOException {
        String elseLabel = this.newElseLabel();
        String outIfLabel = this.newIfEndLabel();
        condition.generateCode();
        InstructionGenerator.getInstance().generateInstruction("BF "+elseLabel+" ; Si es falso, salta a else, sino ejecuto then");
        sentence.generateCode();
        InstructionGenerator.getInstance().generateInstruction("JUMP "+outIfLabel+" ; Termina de ejecutar then, salta a final de if");
        InstructionGenerator.getInstance().generateInstruction(elseLabel+": NOP ; Principio de else");
        elseSentence.generateCode();
        InstructionGenerator.getInstance().generateInstruction(outIfLabel+": NOP ; Final del if");
    }





    protected void generateCode2() throws IOException {
        String ifEndLabel = this.newIfEndLabel();
        String elseLabel = this.newElseLabel();
        this.condition.generateCode();

        //no hay else
        if (this.elseSentence == null) {
            InstructionGenerator.getInstance().generateInstruction("BF " + ifEndLabel + "       ; Si el tope de la fila es falso, salto a " + ifEndLabel);
            this.sentence.generateCode();
        }
        else {
            InstructionGenerator.getInstance().generateInstruction("BF " + elseLabel + "      ; Si el tope de la pila es falso, salto a " + elseLabel);
            this.sentence.generateCode();
            InstructionGenerator.getInstance().generateInstruction("JUMP " + ifEndLabel);
            InstructionGenerator.getInstance().generateInstruction(elseLabel + ":");
            this.elseSentence.generateCode();
        }
        InstructionGenerator.getInstance().generateInstruction(ifEndLabel + ":");
        InstructionGenerator.getInstance().generateInstruction("NOP");
    }

    private String newIfEndLabel() {
        String labelName = "if_end_label_" + this.ifLabelNumber;
        this.ifLabelNumber += 1;
        return labelName;
    }

    private String newElseLabel() {
        String labelName = "else_label_" + this.elseLabelNumber;
        this.elseLabelNumber += 1;
        return labelName;
    }

}
