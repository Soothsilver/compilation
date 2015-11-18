package compiler.intermediate;

import java.util.*;

import compiler.intermediate.instructions.Instruction;
import compiler.nodes.declarations.Subroutine;

public class IntermediateFunction {
	public int address;
	private String name;
	public String getName() {
		return name;
	}
	public List<Instruction> instructions = new ArrayList<Instruction>();
	public static IntermediateFunction create(Subroutine subroutine,
			Executable executable) {
		
		IntermediateFunction function = new IntermediateFunction();
		function.name = subroutine.name;
		return function;
	}
	
	public String toString() {
		return "";
	};
}
