package compiler.intermediate;

import java.util.ArrayList;

import compiler.Compilation;
import compiler.nodes.*;
import compiler.nodes.declarations.*;

public class Executable {
	public Executable(Compilation compilation) {
		ProgramNode programNode = compilation.abstractSyntaxTree;
		for (Subroutine subroutine : programNode.Subroutines) {
			functions.add(IntermediateFunction.create(subroutine, this));
		}
	}
	
	// storing global variables here 
	public ArrayList<Object> types;
	public ArrayList<IntermediateFunction> functions = new ArrayList<IntermediateFunction>();
	
	@Override
	public String toString() {
		String intermediateCode = "";
		for (IntermediateFunction function : functions) {
			intermediateCode += "SUBROUTINE " + function.getName() + ": \n";
			intermediateCode += function.toString() + "\n";
		}
		return intermediateCode;
	}
	
}
