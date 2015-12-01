package compiler.intermediate.instructions;

import compiler.analysis.SubroutineToken;
import compiler.intermediate.*;
import compiler.nodes.declarations.SystemCall;

import java.util.ArrayList;
import java.util.Objects;
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
    IntermediateRegister returnRegister;
    int localVariableCount;

    /**
     * Initializes a new CallInstruction object.
     * @param callee The subroutine to call.
     * @param operands A list of operands. Their code was already generated.
     * @param returnRegisterIndex The index of the register where the call's return value should be stored.
     */
    public CallInstruction(SubroutineToken callee, ArrayList<Operand> operands, IntermediateRegister returnRegisterIndex, int localVariableCount) {
        this.function = callee;
        this.operands = operands;
        this.returnRegister = returnRegisterIndex;
        this.localVariableCount = localVariableCount;
    }

    @Override
    public String toString() {
        // Yes, this is not a three-address instruction, but so what.
        // It's more useful this way. Different assembly languages will have different calling conventions so a single
        // instruction in the intermediate code is more useful than dozens.
        return returnRegister +
                " = CALL " + function.subroutine.name + " WITH " + operands.stream().map(Operand::toString).collect(Collectors.joining(","));
    }


    public ArrayList<IntermediateRegister> savedRegisters = new ArrayList<>();

    @Override
    public String toMipsAssembler() {
        if (function.subroutine instanceof SystemCall) {
            String assembly = "";
            assembly += "\tli $v0," + ((SystemCall)function.subroutine).systemCallCode + "\n";
            if (operands.size() == 1) {
                assembly += operands.get(0).toMipsLoadIntoRegister(((SystemCall)function.subroutine).parameters.get(0).name);
            }
            assembly += "\tsyscall # " + ((SystemCall) function.subroutine).name + "\n";
            return assembly;
        }
        else {
            String assembly = "\t # calling " + function.subroutine.getHumanSig() + "\n";
            /* For MIPS, this instruction does the following:
             * - Moves the stack pointer beyond local variables.
             * - Pushes intermediate registers on the stack.
             * - Pushes return address register on the stack.
             * - Pushes arguments into registers or on stack.
             * - Calls the callee.
             * - Clears arguments from stack.
             * - Pops the return value back into $ra.
             * - Pops intermediate registers from the stack.
             * - Moves the stack pointer back on the local variables.
             */

            int stackDisplacement = 0;
            assembly += MipsMacros.moveStackPointer(this.localVariableCount);
            stackDisplacement += this.localVariableCount;
            assembly += "\t   # pushing intermediate registers\n";
            for (int i = 0; i < this.savedRegisters.size(); i++) {
                assembly += this.savedRegisters.get(i).mipsSaveValueToRegister(MipsRegisters.TEMPORARY_VALUE_0);
                assembly += MipsMacros.pushOntoStack(MipsRegisters.TEMPORARY_VALUE_0);
                stackDisplacement += 1;
            }
            assembly += "\t   # end pushing intermediate registers\n";


            assembly += MipsMacros.pushOntoStack(MipsRegisters.RETURN_ADDRESS);
            stackDisplacement += 1;
            for (Operand operand : operands) {
                String operandCode = "";
                operandCode += operand.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0, stackDisplacement);
                operandCode += MipsMacros.pushOntoStack(MipsRegisters.TEMPORARY_VALUE_0);
                stackDisplacement++;
                assembly += operandCode;
            }
            assembly += "\tjal " + function.subroutine.getUniqueLabel() + "\n";

            // Acquiring value into intermediate register must not use the stack.
            assembly += returnRegister.mipsAcquireValueFromRegister(MipsRegisters.RETURN_VALUE);
            assembly += MipsMacros.clearStackItems(operands.size());
            assembly += MipsMacros.popIntoRegister(MipsRegisters.RETURN_ADDRESS);

            assembly += "\t   # poping intermediate registers\n";
            for (int i = savedRegisters.size() - 1; i >= 0; i--) {
                if (Objects.equals(returnRegister, savedRegisters.get(i))) {
                    assembly += MipsMacros.clearStackItems(1);
                    continue;
                }
                assembly += MipsMacros.popIntoRegister(MipsRegisters.TEMPORARY_VALUE_0);
                assembly += savedRegisters.get(i).mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0);
            }
            assembly += "\t   # end poping intermediate registers\n";

            assembly += MipsMacros.clearStackItems(this.localVariableCount);
            assembly += "\t # end calling\n";
            return assembly;
        }
    }
}
