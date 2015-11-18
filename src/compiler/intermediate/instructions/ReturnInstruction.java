package compiler.intermediate.instructions;

import compiler.intermediate.Operand;

public class ReturnInstruction extends Instruction {
    private Operand operand;
    public ReturnInstruction(Operand operand) {
        this.operand = operand;
    }
    @Override
    public String toString() {
        return "RETURN(" + operand + ")";
    }
}
