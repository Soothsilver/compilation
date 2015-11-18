package compiler.intermediate;

import java.util.ArrayList;

import compiler.Compilation;
import compiler.nodes.*;
import compiler.nodes.declarations.*;

/**
 * Represents a program that passed semantic analysis and is being converted to assembler format.
 */
public class Executable {

	/**
	 * Instantiates a new Executable instance.
	 * @param compilation The compilation object that already must have passed, without errors, semantic analysis.
	 */
	public Executable(Compilation compilation) {
		ProgramNode programNode = compilation.abstractSyntaxTree;
		for (Subroutine subroutine : programNode.Subroutines) {
			functions.add(IntermediateFunction.create(subroutine, this));
		}
	}
	
	// storing global variables here 
	public ArrayList<Object> types;
	public ArrayList<IntermediateFunction> functions = new ArrayList<>();
	
	@Override
	public String toString() {
		String intermediateCode = "";
		for (IntermediateFunction function : functions) {
			intermediateCode += "SUBROUTINE " + function.getName() + ": \n";
			intermediateCode += function.toString() + "\n";
		}
		return intermediateCode;
	}

    /**
     * Creates MIPS assembly from the entire program and returns it as a string. This string can be saved to a file and
     * is ready to be interpreted by an MIPS emulator.
     * @return MIPS assembly code.
     */
    public String toMipsAssembler() {
        String s = "";
        s += ".text\n";
        for (IntermediateFunction func : functions) {
            if (func.getUniqueLabel().equals("main_procedure")) {
                s += "\tj main_procedure # Jump to entry point.\n";
                break;
            }
            if (func.getUniqueLabel().equals("main_integer_list_of_string_procedure")) {
                s += "\tj main_integer_list_of_string_procedure # Jump to entry point.\n";
                break;
            }
        }
        // TODO add entry point
        for (IntermediateFunction func : functions) {
            s += func.toMipsAssembler();
        }
        return s;
    }
}
