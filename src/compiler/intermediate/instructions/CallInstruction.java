package compiler.intermediate.instructions;

import compiler.analysis.SubroutineToken;
import compiler.intermediate.Operand;
import compiler.nodes.declarations.SystemCall;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents an intermediate code instruction to call a subroutine.
 */
public class CallInstruction  extends Instruction {
    SubroutineToken function;
    ArrayList<Operand> operands;
    int returnRegisterIndex;

    /**
     * Initializes a new CallInstruction object.
     * @param callee The subroutine to call.
     * @param operands A list of operands. Their code was already generated.
     * @param returnRegisterIndex The index of the register where the call's return value should be stored.
     */
    public CallInstruction(SubroutineToken callee, ArrayList<Operand> operands, int returnRegisterIndex) {
        this.function = callee;
        this.operands = operands;
        this.returnRegisterIndex = returnRegisterIndex;
    }

    @Override
    public String toString() {
        // Yes, this is not a three-address instruction, but so what.
        // It's more useful this way. Different assembly languages will have different calling conventions so a single
        // instruction in the intermediate code is more useful than dozens.
        return "CALL " + function.subroutine.name + " WITH " + operands.stream().map(Operand::toString).collect(Collectors.joining(","));
    }

    @Override
    public String toMipsAssembler() {
        if (function.subroutine instanceof SystemCall) {
            /*        li $v0, 4        # system call code for print_str
        la $a0, str      # address of string to print
        syscall          # print the string

        li $v0, 1        # system call code for print_int
        li $a0, 5        # integer to print
        syscall          # print it*/
            String assembly = "";
            assembly += "\tli $v0," + ((SystemCall)function.subroutine).systemCallCode + "\n";
            if (operands.size() == 1) {
                // TODO non-integer arguments
                // TODO non-immediate arugments
                assembly += "\tli $a0," + operands.get(0).integerValue + "\n";
            }
            assembly += "\tsyscall # " + ((SystemCall) function.subroutine).name + "\n";
            return assembly;
        }
        else {
            return "!!ERROR - ONLY SYSTEM CALLS SUPPORTED FOR NOW.";
        }
    }
}
