package compiler.intermediate;

import java.util.ArrayList;

import compiler.Compilation;
import compiler.intermediate.instructions.LabelInstruction;
import compiler.nodes.*;
import compiler.nodes.declarations.*;

/**
 * Represents a program that passed semantic analysis and is being converted to assembler format.
 */
public class Executable {
    public static final String REGISTERS_SPACE = "____registers";

    /**
     * Set this variable to a LabelInstruction prior to generating intermediate code for a cycle's body.
     * If the cycle's statement contains a break;, it will jump to this label. If the statement contains a cycle itself,
     * it must replace this instruction and then replace it back after its inner statement is done.
     *
     * It's possible that this won't be null even if we're outside a cycle (for example, when a function is called), but
     * that is okay because semantic analysis guarantees that break will only happen inside a cycle.
     */
    public LabelInstruction enclosingLoopEnd = null;

	/**
	 * Instantiates a new Executable instance.
	 * @param compilation The compilation object that already must have passed, without errors, semantic analysis.
	 */
	public Executable(Compilation compilation) {
		ProgramNode programNode = compilation.abstractSyntaxTree;
        for (Declaration declaration : programNode.Declarations) {
            if (declaration instanceof TypeOrTypeTemplate) {
                System.out.println("Type and type template definitions are not supported yet.");
                continue;
            }
            if (declaration instanceof Variable) {
                Variable vGlobal = (Variable)declaration;
                vGlobal.kind = VariableKind.Global;
                globalVariables.add(vGlobal);
            }
        }
		for (Subroutine subroutine : programNode.Subroutines) {
			functions.add(IntermediateFunction.create(subroutine, this));
		}
	}

    // storing global variables here
    /**
     * Types not yet done. Maybe later.
     */
	public ArrayList<Object> types;
    public ArrayList<Variable> globalVariables = new ArrayList<>();
    /**
     * Contains all subroutines of the source code transformed to intermediate code, but no predefined subroutines.
     */
    public ArrayList<IntermediateFunction> functions = new ArrayList<>();
	
	@Override
	public String toString() {
		String intermediateCode = "";
        for (Variable global : globalVariables) {
            intermediateCode += "GLOBAL VARIABLE " + global.name + "\n";
        }
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
        s += ".data\n";
        s += "\t " + REGISTERS_SPACE + ": .space 4000 # Temporary values are stored in this memory.\n";
        for (Variable global : globalVariables) {
            s += "\t" + global.name + ": .word 0\n";
        }
        s += ".text\n";
        s += ".globl main\n";
        s += "main: \n";
        for (IntermediateFunction func : functions) {
            if (func.getUniqueLabel().equals("main_procedure")) {
                s += "\tjal main_procedure # Jump to entry point.\n";
                break;
            }
            if (func.getUniqueLabel().equals("main_integer_list_of_string_procedure")) {
                s += "\tjal main_integer_list_of_string_procedure # Jump to entry point.\n";
                break;
            }
        }
        s += "\tj end_of_program\n";
        for (IntermediateFunction func : functions) {
            s += func.toMipsAssembler();
        }
        s += "end_of_program: \n";
        return s;
    }

    private int registerCount = 0;
    public IntermediateRegister summonNewRegister() {
        registerCount++;
        return new IntermediateRegister(registerCount);
    }
}
