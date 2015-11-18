package compiler.intermediate.instructions;

public class LabelInstruction extends Instruction {
	private String name;
	public LabelInstruction(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "LABEL(" + name + ")";
	}
}
