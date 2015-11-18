package compiler.intermediate;

public class Operand {
	public int integerValue;
	public OperandKind kind;
	public static enum OperandKind {
		Immediate,
		Register,
		MemoryIndirect,
		MemoryDirect
	}
}

