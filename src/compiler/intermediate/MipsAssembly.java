package compiler.intermediate;


/**
 * Static class that contains function that generate MIPS assembly text.
 */
public final class MipsAssembly {
    private MipsAssembly() { }

    /**
     * Returns MIPS code the branch-if-zero instruction "beqz".
     * @param register The register to compare to zero.
     * @param label MIPS label to jump to if zero.
     * @return MIPS code.
     */
    public static String beqz(String register, String label) {
        return "\tbeqz " + register + "," + label + "\n";
    }

    /**
     * Returns MIPS code for the load-immediate pseudo-instruction "li".
     * @param register The register to load the immediate value into.
     * @param immediateValue The immediate value.
     * @return MIPS code.
     */
    public static String li(String register, int immediateValue) {
        return "\tli " + register + "," + Integer.toString(immediateValue) + "\n";
    }

    /**
     * Returns MIPS code for the unconditional jump "j".
     * @param label MIPS label to jump to.
     * @return MIPS code.
     */
    public static String jmp(String label) {
        return "\tj " + label + "\n";
    }

    /**
     * Returns MIPS code for a label.
     * @param label Name for the MIPS label.
     * @return MIPS code.
     */
    public static String label(String label) {
        return label + ": \n";
    }

    /**
     * Returns MIPS code for the instruction "or".
     * @param target The register to save the result in.
     * @param left The left operand.
     * @param right The right operand.
     * @return MIPS code.
     */
    public static String or(String target, String left,	String right) {
		return "\tor " + target + "," + left + "," + right + "\n";
	}

    /**
     * Returns MIPS code for the instruction "xor".
     * @param target The register to save the result in.
     * @param left The left operand.
     * @param right The right operand.
     * @return MIPS code.
     */
	public static String xor(String target, String left, String right) {
		return "\txor " + target + "," + left + "," + right + "\n";
	}
}
