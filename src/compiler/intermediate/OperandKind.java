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
	 * The operand is an IntermediateRegister.
	 */
	Register,
    /**
     * The operand is a global variable, identified by name.
     */
    GlobalVariable,
	/**
	 * This operand's IntermediateRegister holds a memory address. This operand represents whatever is stored at that memory address.
	 */
	RegisterContainsHeapAddress,
	Parameter, LocalVariable
}
