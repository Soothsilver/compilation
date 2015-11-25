package compiler.intermediate;


/**
 * Static class that contains function that generate MIPS assembly text.
 */
public final class MipsAssembly {
    private MipsAssembly() { }

    public static String beqz(String register, String label) {
        return "\tbeqz " + register + "," + label + "\n";
    }

    public static String li(String register, int immediateValue) {
        return "\tli " + register + "," + Integer.toString(immediateValue) + "\n";
    }

    public static String jmp(String label) {
        return "\tj " + label + "\n";
    }

    public static String label(String label) {
        return label + ": \n";
    }

	public static String or(String target, String left,	String right) {
		return "\tor " + target + "," + left + "," + right + "\n";
	}

	public static String xor(String target, String left, String right) {
		return "\txor " + target + "," + left + "," + right + "\n";
	}
}
