package compiler.intermediate.instructions;

/**
 * Represents the intermediate code instruction "LABEL(name)" that represents a label to be generated to assembly.
 */
public class LabelInstruction extends Instruction {
	private String name;

	/**
	 * Initializes a new LabelInstruction.
	 * @param name Name of the label. Jumps will refer to this name.
	 */
	public LabelInstruction(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "LABEL(" + name + ")";
	}

	public String getName() {
		return name;
	}

    @Override
    public String toMipsAssembler() {
        return getName() + ": \n";
    }
}
