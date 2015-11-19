package compiler.intermediate;

public class MipsRegisters {
    /**
     * This register is always zero and cannot be written to.
     */
    public static final String HARDWIRED_ZERO = "$zero";
    /**
     * This register contains the return value of a call.
     */
    public static final String RETURN_VALUE = "$v0";
    public static final String EXPRESSION_EVALUATION = "$v1";
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
    public static final String TEMPORARY_VALUE_2 = "$t2";

    public static final String GLOBAL_POINTER = "$gp";
    public static final String STACK_POINTER = "$sp";
    public static final String FRAME_POINTER = "$fp";
    public static final String RETURN_ADDRESS = "$ra";
    public static final String ARGUMENT_REGISTER_0 = "$a0";
    public static final String ARGUMENT_REGISTER_1 = "$a1";
    public static final String ARGUMENT_REGISTER_2 = "$a2";
    public static final String ARGUMENT_REGISTER_3 = "$a3";
}
