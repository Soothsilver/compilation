package compiler.intermediate.instructions;

import compiler.intermediate.MipsRegisters;
import compiler.intermediate.Operand;

import java.util.Objects;

/**
 * Represents the intermediate code RETURN(expression) instruction.
 */
public class ReturnInstruction extends Instruction {
    private Operand operand;

    /**
     * Initializes the ReturnInstruction.
     * @param operand The operand to be converted to a 4-byte value and returned to the caller.
     */
    public ReturnInstruction(Operand operand) {
        this.operand = operand;
    }
    @Override
    public String toString() {
        if (Objects.equals(operand, Operand.nullOperand)) {
            return "RETURN";
        }
        else {
            return "RETURN(" + operand + ")";
        }
    }

    @Override
    public String toMipsAssembler() {
        if (Objects.equals(operand, Operand.nullOperand)) {
            return "\tjr $ra # stop;\n";
        } else {
            return
                    operand.toMipsLoadIntoRegister(MipsRegisters.RETURN_VALUE) +
                    "\tjr $ra # return " +  operand + ";\n";
        }
    }
}
