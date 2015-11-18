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
     *
     * The stack contains:
     * - old return value
     * - arguments
     * Now, we should put on stack:
     * - local variables
     *
     *
     * At the end of the subroutine, we should:
     * - remove from the stack local variable
     * - jump back
     * These should happen at any call of stop/return, or if we reach the end of a procedure.
     * We will never reach the end of a function because semantic analysis prevents it.
     *
     */
    public String toMipsAssembler() {
		String s = "";
        s += "\n";
        s += this.getUniqueLabel() + ": \n";
        for (Instruction instruction : instructions) {
            s += instruction.toMipsAssembler();
        }
        s += "\tjr $ra # return from procedure\n";
		return s;
	}

    /**
     * Returns an assembler-friendly name that uniquely identifies this overload of the subroutine. Use this name as a label
     * to jump to when calling this subroutine.
     * @return A label identifier.
     */
    public String getUniqueLabel() {
        return subroutine.getUniqueLabel();
    }

}
