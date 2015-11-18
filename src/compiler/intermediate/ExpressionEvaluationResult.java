package compiler.intermediate;

import compiler.intermediate.instructions.Instruction;

import java.util.List;

/**
 * This helper class contains the result of evaluating an expression during intermediate code generation.
 * An expression must provide a list of instructions to generate its return value (code) and information
 * on how to access this return value (operand).
 */
public class ExpressionEvaluationResult {
	/**
	 * List of instructions to generate the expression's return value.
	 */
	public List<Instruction> code;
	/**
	 * Information on how to access the expression's return value.
	 */
	public Operand operand;

    /**
     * Initializes a new instance of the ExpressionEvaluationResult class.
     */
    public ExpressionEvaluationResult(List<Instruction> code, Operand operand) {
        this.code = code;
        this.operand = operand;
    }
}
