package compiler.intermediate.instructions;

/**
 * Instruction of the intermediate 3-address code, not MIPS.
 */
public abstract class Instruction {
	
	@Override
	public abstract String toString();

	/**
	 * Transforms this intermediate code instruction into MIPS assembly code.
	 *
	 * One intermediate code instruction may generate many MIPS assembly lines. This method should be overridden
     * by all intermediate instructions.
	 *
	 * The returned string must either be empty or end with a newline character. All MIPS instructions except labels
     * should start with the tabulator "\t".
	 *
	 * @return MIPS assembly code.
	 */
	public String toMipsAssembler() {
		return "\t!!ERROR(The instruction '" + this + "' does not override toMipsAssembler.)\n";
	}
}
