package compiler.intermediate;

/**
 * Represents an operand of an intermediate 3-address instruction.
 */
public class Operand {
    /**
     * The operand's 4-byte value. Not necessarily a "real" integer.
     */
    public int integerValue;
    /**
     * The operand's addressing mode.
     */
    public OperandKind kind;

    /**
     * Initializes a new instance of the Operand class.
     * @param integerValue The operand's value.
     * @param kind The operand's addressing mode.
     */
    public Operand(int integerValue, OperandKind kind) {
		this.integerValue = integerValue;
		this.kind = kind;
	}

    @Override
    public String toString() {
        switch (kind){
            case Immediate:
                return Integer.toString(integerValue);
            case MemoryDirect:
                return "MEM(" + integerValue + ")";
            case MemoryIndirect:
                return "MEM(REG(" + integerValue + "))";
            case Register:
                return "REG(" + integerValue + ")";
        }
        throw new EnumConstantNotPresentException(OperandKind.class, "kind");
    }

    /**
     * Generates MIPS instructions that load the value of this operand into the specified register. The instructions are terminated by a newline character, if any are generated at all.
     * @param registerName Mnemonic for the register.
     * @return MIPS instructions.
     */
    public String toMipsLoadIntoRegister(String registerName) {
        switch (kind) {
            case Immediate:
                return "\tli " + registerName + "," + integerValue + "\n";
            default:
                    return "!!ERROR This addressing mode is not yet supported.!!";
        }
    }

    /**
     * Creates an operand that should never be used. This is useful for example for procedures that don't return any
     * value but syntactically must return an Operand. The value '77' is unusual enough that it should arouse suspicion
     * if seen in generated code.
     * @return An operand that should never be used.
     */
    public static Operand createOperandWithoutValue() {
        return new Operand(77, OperandKind.Immediate);
    }
}



