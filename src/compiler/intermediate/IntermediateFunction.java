package compiler.intermediate;

import java.util.*;
import java.util.stream.Collectors;

import compiler.intermediate.instructions.Instruction;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.SubroutineKind;

/**
 * Represents a subroutine at the stage of intermediate code.
 */
public class IntermediateFunction {
	public int address;
	private String name;
    private Subroutine subroutine;
    /**
     * An ordered list of intermediate code instructions this subroutine consists of.
     */
    public List<Instruction> instructions = new ArrayList<>();
	
	public String getName() {
		return name;
	}

    /**
     * Creates a new instance of IntermediateFunction from an abstract syntax tree Subroutine,
     * and transforms the Subroutine's Statements into intermediate code instructions.
     */
    public static IntermediateFunction create(Subroutine subroutine,
			Executable executable) {
		
		IntermediateFunction function = new IntermediateFunction();
		function.name = subroutine.name;
        function.subroutine = subroutine;
		function.instructions = subroutine.block.generateIntermediateCode(executable, function);
		
		return function;
	}
	
	public String toString() {
		String functionCode = "";
		for (Instruction instruction : instructions) {
			functionCode += instruction.toString() + "\n";
		}
		return functionCode;
	}

    /**
     * Transforms this intermediate-code subroutine into MIPS assembly.
     */
    public String toMipsAssembler() {
		String s = "";
        s += this.getUniqueLabel() + ": \n";
        for (Instruction instruction : instructions) {
            s += instruction.toMipsAssembler();
        }
		return s;
	}

    /**
     * Returns an assembler-friendly name that uniquely identifies this overload of the subroutine. Use this name as a label
     * to jump to when calling this subroutine.
     * @return A label identifier.
     */
    public String getUniqueLabel() {
        // TODO This does not work with generics but so what.
        return subroutine.name + "_" + subroutine.parameters.stream().map(param -> param.type.name).collect(Collectors.joining("_"))
                + (subroutine.kind == SubroutineKind.PROCEDURE ? "procedure" :
                (subroutine.returnType.name + "_function"));
    }

}
