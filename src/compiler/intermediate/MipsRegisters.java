package compiler.intermediate;

/**
 * This static class contains names of various MIPS registers.
 */
public final class MipsRegisters {
    /**
     * This register is always zero and cannot be written to.
     */
    public static final String HARDWIRED_ZERO = "$zero";
    /**
     * This register contains the return value of a call.
     */
    public static final String RETURN_VALUE = "$v0";
    /**
     * This register contains the second 32 bits of a 64-bit return value of a call.
     */
    public static final String SECOND_RETURN_VALUE = "$v1";
    /**
     * This temporary value register ($t0) is reserved for use by MIPS code snippets generated from instructions.
     * Each instruction must assume that the value will not survive the insertion of another code snippet,
     * unless that code snippet's documentation explicitly says that it is prohibited from using the value.
     */
    public static final String TEMPORARY_VALUE_0 = "$t0";
    /**
     * This temporary value register ($t1) is reserved for use by MIPS code snippets generated from instructions.
     * Each instruction must assume that the value will not survive the insertion of another code snippet,
     * unless that code snippet's documentation explicitly says that it is prohibited from using the value.
     */
    public static final String TEMPORARY_VALUE_1 = "$t1";

    /**
     * Unused register.
     */
    public static final String GLOBAL_POINTER = "$gp";
    /**
     * Stack pointer register points at the topmost object on the stack, not above it.
     */
    public static final String STACK_POINTER = "$sp";
    /**
     * I have no idea what this register is for and it might be useful, because it might help with local variables.
     */
    public static final String FRAME_POINTER = "$fp";
    /**
     * This register is filled with the address to return to when the instruction "jal" (jump and link) is executed.
     */
    public static final String RETURN_ADDRESS = "$ra";
    /**
     * Normally, MIPS convention is that this register should be used to pass the first argument to a function.
     * We ignore this convention, except for system calls.
     */
    public static final String ARGUMENT_REGISTER_0 = "$a0";
    /**
     * Normally, MIPS convention is that this register should be used to pass the second argument to a function.
     * We ignore this convention.
     */
    public static final String ARGUMENT_REGISTER_1 = "$a1";
    /**
     * Normally, MIPS convention is that this register should be used to pass the third argument to a function.
     * We ignore this convention.
     */
    public static final String ARGUMENT_REGISTER_2 = "$a2";
    /**
     * Normally, MIPS convention is that this register should be used to pass the fourth argument to a function.
     * We ignore this convention.
     */
    public static final String ARGUMENT_REGISTER_3 = "$a3";
}
