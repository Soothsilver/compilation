package compiler.intermediate.instructions;

import compiler.intermediate.IntermediateRegister;
import compiler.intermediate.MipsRegisters;
import compiler.intermediate.Operand;

/**
 * Represents an intermediate code instruction that allocates N words of heap memory and stores a pointer to this
 * space into a register.
 */
public class PureAllocateInstruction extends Instruction {
    private IntermediateRegister referenceRegister;
    private Operand size;

    /**
     * Initializes a new AllocateInstruction.
     * @param referenceRegister The register that will contain the address of the allocated data.
     * @param size The operand that gives the number of WORDS that should be allocated.
     */
    public PureAllocateInstruction(IntermediateRegister referenceRegister, Operand size) {
        this.referenceRegister = referenceRegister;
        this.size = size;
    }

    @Override
    public String toString() {
        return referenceRegister + " = ALLOCATE(" + size + " bytes)";
    }

    @Override
    public String toMipsAssembler() {
        return size.toMipsLoadIntoRegister(MipsRegisters.ARGUMENT_REGISTER_0) +
               "\tli $v0,9\n" +
               "\tsyscall # sbrk (memory allocation)\n" +
                referenceRegister.mipsAcquireValueFromRegister(MipsRegisters.RETURN_VALUE)
                ;
    }
}
