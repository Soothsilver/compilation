package compiler.intermediate;

import compiler.Compilation;
import compiler.analysis.Uniqueness;
import compiler.intermediate.instructions.LabelInstruction;
import compiler.nodes.ProgramNode;
import compiler.nodes.declarations.*;

import java.util.ArrayList;

/**
 * Represents a program that passed semantic analysis and is being converted to assembler format.
 */
public class Executable {
    /**
     * The MIPS label into static data portion of the assembler code, where excess intermediate registers are stored.
     */
    public static final String REGISTERS_SPACE = "____registers";

    /**
     * The number of local variables defined up to this point in this subroutine.
     * This number increases during each block's declaration and decreases as a scope closes.
     * The number is reinitialized to zero at the beginning of each subroutine.
     * It is used to determine where on the stack local variables are, and how should the stack pointer be moved
     * during subroutine calls.
     */
    public int localVariableMaximum = 0;


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
	 * Instantiates a new Executable instance. This will immediately create intermediate code for the entire source file.
	 * @param compilation The compilation object that already must have passed, without errors, semantic analysis.
	 */
	public Executable(Compilation compilation) {
		ProgramNode programNode = compilation.abstractSyntaxTree;
        for (Declaration declaration : programNode.Declarations) {
            if (declaration instanceof TypeOrTypeTemplate) {
                continue;
            }
            if (declaration instanceof Variable) {
                Variable vGlobal = (Variable)declaration;
                vGlobal.kind = VariableKind.Global;
                globalVariables.add(vGlobal);
            }
        }
		for (Subroutine subroutine : programNode.Subroutines) {
            for (int i = 0; i < subroutine.parameters.size(); i++) {
                subroutine.parameters.get(i).variable.index = i;
                subroutine.parameters.get(i).variable.reverseIndex = subroutine.parameters.size() - i - 1;
            }
			functions.add(IntermediateFunction.create(subroutine, this));
		}
	}

    // storing global variables here
    /**
     * Types not yet done. Maybe later.
     */
	public ArrayList<Object> types;
    /**
     * Ordered list of global variables, with kind set.
     */
    public ArrayList<Variable> globalVariables = new ArrayList<>();
    /**
     * List of string literals to put in code.
     */
    public ArrayList<IntermediateStringLiteral> stringLiterals = new ArrayList<>();
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
        for (IntermediateStringLiteral isl : stringLiterals) {
            intermediateCode += "LITERAL " + isl.getLabel() + ": " + isl.getData() + "\n";
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
            s += "\t" + global.name + ": .word 0 # global 32-bit variable with default value '0'\n";
        }
        for (IntermediateStringLiteral isl : stringLiterals) {
            s += "\t" + isl.getLabel() +": .asciiz \"" + isl.getData() + "\"\n";
        }
        s += ".text\n";
        s += ".globl main\n";
        s += "main: \n";
        for (IntermediateFunction func : functions) {
            if (func.getUniqueLabel().equals("main__procedure")) {
                s += "\tjal main__procedure # Jump to entry point.\n";
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

    /**
     * The number of registers currently in use.
     */
    public int registerCount = 0;

    /**
     * List of intermediate registers currently in use. This is cleared whenever a new function is being converted to intermediate code.
     */
    public ArrayList<IntermediateRegister> registers = new ArrayList<>();

    /**
     * Creates a new unique intermediate register.
     */
    public IntermediateRegister summonNewRegister() {
        registerCount++;
        IntermediateRegister newRegister = new IntermediateRegister(registerCount);
        registers.add(newRegister);
        return newRegister;
    }

    /**
     * Creates a new IntermediateStringLiteral from the specified textual data and adds the literal
     * to the compilation's list of string literals.
     * @param data The textual data that will be contained by the literal.
     * @return Reference to the created literal.
     */
    public IntermediateStringLiteral createStringLiteral(String data) {
        IntermediateStringLiteral isl = new IntermediateStringLiteral("____str" + Uniqueness.getUniqueId(), data);
        stringLiterals.add(isl);
        return isl;
    }
}
