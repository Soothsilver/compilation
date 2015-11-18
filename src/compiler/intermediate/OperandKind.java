package compiler.intermediate;

/**
 * Represents the addressing mode of an operand.
 */
public enum OperandKind {
	/**
	 * The operand is a 4-byte literal value stored directly in assembler, such as "2"  or "2.3" or "'a'" or "true".
	 */
	Immediate,
	/**
	 * The operand is an integer index of a register. 
	 */
	Register,
	/**
	 * The operand is an integer index of a register that contains a memory address.
	 */
	MemoryIndirect,
	/**
	 * The operand is an integer memory address.
	 */
	MemoryDirect
}
