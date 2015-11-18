package compiler.intermediate;

public class Operand {
	public int integerValue;
	public OperandKind kind;
	
	public Operand(int integerValue, OperandKind kind) {
		this.integerValue = integerValue;
		this.kind = kind;
	}
}

