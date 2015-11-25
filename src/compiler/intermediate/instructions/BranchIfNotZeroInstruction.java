package compiler.intermediate.instructions;

import compiler.intermediate.MipsRegisters;
import compiler.intermediate.Operand;

/**
 * Represents an intermediate code instruction that jumps conditionally if its operand is not zero.
 */
public class BranchIfNotZeroInstruction extends Instruction {
    private Operand operand;
    private LabelInstruction target;

    /**
     * Initializes the BranchIfNotZero instruction.
     * @param operand The operand to compare to zero.
     * @param targetLabel Jump to this label if the operand is not zero.
     */
    public BranchIfNotZeroInstruction(Operand operand, LabelInstruction targetLabel) {
       this.operand = operand;
       this.target = targetLabel;
    }

    @Override
    public String toString() {
        return "IF " + operand.toString() + " != 0 JUMP " + target.getName();
    }

    @Override
    public String toMipsAssembler() {
        String loadIntoRegister =
                operand.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0);


        return loadIntoRegister +
                "\tbnez " + MipsRegisters.TEMPORARY_VALUE_0 + "," + target.getName()+ "\n";
    }
}