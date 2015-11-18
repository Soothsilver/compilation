package compiler.intermediate.instructions;

import compiler.analysis.SubroutineToken;
import compiler.intermediate.*;
import compiler.nodes.declarations.SystemCall;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents an intermediate code instruction to call a subroutine.
 * It is not a real 3-address instruction, more of a pseudoinstruction. It's more useful this way.
 * Different assembly languages will have different calling conventions so a single instruction in the intermediate
 * code is more useful than dozens.
 *
 * By the time this instruction is reached, all expressions in arguments should already be calculated.
 * For MIPS, this instruction does the following:
 * - Pushes arguments into registers or on stack.
 * - Pushes return value register on the stack.
 * - Calls the callee.
 * - Pops the return value back into $ra.
 * - Clears arguments from stack.
 */
public class CallInstruction  extends Instruction {
    SubroutineToken function;
    ArrayList<Operand> operands;
    IntermediateRegister returnRegisterIndex;

    /**
     * Initializes a new CallInstruction object.
     * @param callee The subroutine to call.
     * @param operands A list of operands. Their code was already generated.
     * @param returnRegisterIndex The index of the register where the call's return value should be stored.
     */
    public CallInstruction(SubroutineToken callee, ArrayList<Operand> operands, IntermediateRegister returnRegisterIndex) {
        this.function = callee;
        this.operands = operands;
        this.returnRegisterIndex = returnRegisterIndex;
    }

    @Override
    public String toString() {
        // Yes, this is not a three-address instruction, but so what.
        // It's more useful this way. Different assembly languages will have different calling conventions so a single
        // instruction in the intermediate code is more useful than dozens.
        return "REG(" + returnRegisterIndex + ") = CALL " + function.subroutine.name + " WITH " + operands.stream().map(Operand::toString).collect(Collectors.joining(","));
    }

    @Override
    public String toMipsAssembler() {
        if (function.subroutine instanceof SystemCall) {
            String assembly = "";
            assembly += "\tli $v0," + ((SystemCall)function.subroutine).systemCallCode + "\n";
            if (operands.size() == 1) {
                // TODO non-integer arguments
                // TODO non-immediate arguments
                assembly += "\tli $a0," + operands.get(0).integerValue + "\n";
            }
            assembly += "\tsyscall # " + ((SystemCall) function.subroutine).name + "\n";
            return assembly;
        }
        else {
            String assembly = "\t # calling " + function.subroutine.getHumanSig() + "\n";
            /* For MIPS, this instruction does the following:
             * - Pushes return address register on the stack.
             * - Pushes arguments into registers or on stack.
             * - Calls the callee.
             * - Clears arguments from stack.
             * - Pops the return value back into $ra.
             */

            assembly += MipsMacros.pushOntoStack(MipsRegisters.RETURN_ADDRESS);
            for (Operand operand : operands) {
                String operandCode = "";
                operandCode += operand.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0);
                operandCode += MipsMacros.pushOntoStack(MipsRegisters.TEMPORARY_VALUE_0);
                assembly += operandCode;
            }
            assembly += "\tjal " + function.subroutine.getUniqueLabel() + "\n";
            assembly += returnRegisterIndex.mipsAcquireValueFromRegister(MipsRegisters.RETURN_VALUE);
            assembly += MipsMacros.clearStackItems(operands.size());
            assembly += MipsMacros.popIntoRegister(MipsRegisters.RETURN_ADDRESS);
            assembly += "\t # end calling\n";
            return assembly;
        }
    }
}
