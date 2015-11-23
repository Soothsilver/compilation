package compiler.intermediate;

/**
 * Represents one of the infinity registers available during the intermediate code stage.
 */
public class IntermediateRegister {
    int index;

    /**
     * Creates a new intermediate register. This method should only be called by Executable.
     * @param index Any integer different from all other intermediate registers.
     */
    public IntermediateRegister(int index) {
        this.index = index;
    }

    /**
     * Generates MIPS code that copies the value of the specified MIPS register into whatever MIPS uses to store the
     * value of this intermediate register.
     * @param incomingValueRegister The MIPS register whose value should be copied.
     * @return MIPS code to do this operation.
     */
    public String mipsAcquireValueFromRegister(String incomingValueRegister) {
        /*return "\tla " + MipsRegisters.TEMPORARY_VALUE_0 + "," + Executable.REGISTERS_SPACE + "\n" +
               "\tsw " + incomingValueRegister + "," + (4*index) + "(" + MipsRegisters.TEMPORARY_VALUE_0 + ")\n";
       */ return "\tsw " + incomingValueRegister + "," + Executable.REGISTERS_SPACE + "+" + (4*index) + "\n";
       // return "!!ERROR(Value not acquired from register " + incomingValueRegister + " to intermediate " + toString() + ")\n"; // Let's just... not acquire the value.
    }

    @Override
    public String toString() {
        return "REG(" + index + ")";
    }

    /**
     * Generates MIPS code that saves the value of this intermediate register into the specified MIPS register.
     * This method MUST NOT use the registers TEMPORARY_VALUE_0 nor TEMPORARY_VALUE_1.
     * @param saveTheValueIntoThis The register where the value should be saved.
     * @return MIPS code.
     */
    public String mipsSaveValueToRegister(String saveTheValueIntoThis) {
        return "\tlw " + saveTheValueIntoThis + "," + Executable.REGISTERS_SPACE + "+" + (4*index) + "\n";
    }
}
