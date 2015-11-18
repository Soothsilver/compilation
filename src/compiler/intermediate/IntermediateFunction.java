package compiler.intermediate;

import java.util.*;

import compiler.intermediate.instructions.Instruction;
import compiler.nodes.declarations.Subroutine;

public class IntermediateFunction {
	public int address;
	private String name;
	public List<Instruction> instructions = new ArrayList<Instruction>();
	
	public String getName() {
		return name;
	}
	
	public static IntermediateFunction create(Subroutine subroutine,
			Executable executable) {
		
		IntermediateFunction function = new IntermediateFunction();
		function.name = subroutine.name;
		function.instructions = subroutine.block.generateIntermediateCode(executable, function);
		
		return function;
	}
	
	public String toString() {
		String functionCode = "";
		for (Instruction instruction : instructions) {
			functionCode += instruction.toString() + "\n";
		}
		return functionCode;
	};
	
}
