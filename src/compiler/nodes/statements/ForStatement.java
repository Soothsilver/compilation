package compiler.nodes.statements;

import compiler.Compilation;
import compiler.analysis.Uniqueness;
import compiler.intermediate.Executable;
import compiler.intermediate.IntermediateFunction;
import compiler.intermediate.OperandWithCode;
import compiler.intermediate.instructions.*;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;
import compiler.nodes.expressions.ExpressionKind;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Represents the "for (init;cond;increment) body;" statement.
 */
public class ForStatement extends CycleStatement{
	/**
	 * The expression evaluated at the beginning of each iteration.
	 */
	public Expression test;
	/**
	 * The expression executed at the start of the first iteration.
	 */
	public Expression initialisation;
	/**
	 * The expression executed at the end of each iteration.
	 */
	public Expression incrementation;
	
	 private static List<ExpressionKind> permittedKinds = Arrays.asList(
	            ExpressionKind.Assignment,
	            ExpressionKind.Increment,
	            ExpressionKind.Subroutine,
	            ExpressionKind.MemberSubroutine
	    );


	/**
	 * Initializes a new ForStatement. Launches phase 2 resolution for all three expressions, forcing the boolean type on the test expression.
	 * Also triggers a semantic error if the initialization or incrementation expressions have no side effects.
	 *
	 * @param initialisation The expression to execute before the first iteration.
	 * @param test The expression that is tested for truth at the start of each iteration.
	 * @param incrementation The expression executed at the end of each iteration.
	 * @param line Source line.
	 * @param column Source column.
	 * @param compilation The compilation object.
	 */
	public ForStatement(Expression initialisation, Expression test, Expression incrementation, int line, int column, Compilation compilation) {
		super(line, column);
		this.initialisation = initialisation;
		this.test = test;
		this.incrementation = incrementation;
		this.test.propagateTypes(new HashSet<>(Arrays.asList(Type.booleanType)), compilation);
		this.initialisation.propagateTypes(null, compilation);
		this.incrementation.propagateTypes(null, compilation);
		
		 if (!permittedKinds.contains(incrementation.kind)) {
	            compilation.semanticError("Only assignment, increment, decrement and subroutine call expressions are permitted as statements.", 
	            		incrementation.line, incrementation.column);
	        }
		 
		 if (!permittedKinds.contains(initialisation.kind)) {
	            compilation.semanticError("Only assignment, increment, decrement and subroutine call expressions are permitted as statements.", 
	            		initialisation.line, initialisation.column);
	        }
	}
	
	 public String toString() {
	      	return "for(" + initialisation + ";" + test + ";" + incrementation + ") " + body;
	    }
	 
	 @Override
	 public List<Instruction> generateIntermediateCode (Executable executable, IntermediateFunction function) {
	        Instructions instructions = new Instructions();
	        
	        OperandWithCode eer = test.generateIntermediateCode(executable);
	        LabelInstruction testFor = new LabelInstruction("testfor_" + Uniqueness.getUniqueId());
	        LabelInstruction cycleEnd = new LabelInstruction("endfor_" + Uniqueness.getUniqueId());
	        instructions.addAll(initialisation.generateIntermediateCode(executable).code);
	        instructions.add(testFor);
	        instructions.addAll(eer.code);
	        instructions.add(new BranchIfZeroInstruction(eer.operand, cycleEnd));
	        LabelInstruction previous = executable.enclosingLoopEnd;
	        executable.enclosingLoopEnd = cycleEnd;
	        instructions.addAll(body.generateIntermediateCode(executable, function));
	        executable.enclosingLoopEnd = previous;
	        instructions.addAll(incrementation.generateIntermediateCode(executable).code);
	        instructions.add(new JumpInstruction(testFor));
	        instructions.add(cycleEnd);
	        
	        return instructions;
	 }
}
