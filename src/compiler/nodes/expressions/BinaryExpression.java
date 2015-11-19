package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.analysis.OverloadResolution;
import compiler.intermediate.*;
import compiler.intermediate.instructions.BinaryOperatorInstruction;
import compiler.intermediate.instructions.Instructions;
import compiler.nodes.declarations.Variable;

import java.util.ArrayList;

/**
 * Represents a binary expression with an operator, such as "2 + 2" or "i = 3".
 *
 * Note that it inherits from CallExpression, because during semantic analysis, we act towards operators as if they were functions.
 */
public class BinaryExpression extends CallExpression {
    /**
     * Operator (for example, "++", "<<=" or "&").
     */
    public String operator;

    /**
     * Initializes a new BinaryExpression and launches phase 1 resolution for it. Also triggers an error if the
     * expression is an assignment and yet the first operand is not assignable.
     * @param left The operand to the left of the operator.
     * @param operator The operator (for example, "++", "<<=" or "&").
     * @param right The operand to the right of the operator.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     * @return The created BinaryExpression, perhaps with error type set.
     */
    public static BinaryExpression create(Expression left, String operator, Expression right, int line, int column, Compilation compilation) {
        BinaryExpression ex = new BinaryExpression();
        ex.operator = operator;
        ex.line = line;
        ex.column = column;
        ex.arguments = new Expressions();
        ex.arguments.add(left);
        ex.arguments.add(right);
        ex.group = SubroutineGroup.create(operator, line, column, compilation);
        ex.kind = ExpressionKind.ArithmeticExpression;
        switch (operator) {
            case "=":
            case "+=":
            case "-=":
            case "/=":
            case "*=":
            case "%=":
            case "<<=":
            case ">>=":
            case "&=":
            case "|=":
            case "^=":
                ex.kind = ExpressionKind.Assignment;
                if (left.kind == ExpressionKind.Variable) {
                    Variable v = ((VariableExpression)left).variable;
                    if (v != null && v.readonly) {
                        compilation.semanticError("It is illegal to assign a value to a foreach iteration variable.", line, column);
                        ex.setErrorType();
                        return ex;
                    }
                }
                if (!left.isAssignable()) {
                    compilation.semanticError("The left-hand side of an assignment must be a variable, array member or a class member.", line, column);
                    ex.setErrorType();
                    return ex;
                }
                break;
        }
        OverloadResolution.phaseOne(ex, compilation);
        return ex;
    }


    @Override
    public String toString() {
        return arguments.get(0) + operator + arguments.get(1);
    }

    @Override
    public String getErrorMessageTypeMismatch() {
    	if (this.kind == ExpressionKind.Assignment) {
        	
            return "The expression '" + arguments.get(1) + "' is not convertible to type '" + arguments.get(0).type + "'.";
        }
        else {
            return "The operator '" + group.name + "' does not accept operands of type '" + arguments.get(0).type + "' and '" + arguments.get(1).type + "'.";
        }
    }

    @Override
    public ExpressionEvaluationResult generateIntermediateCode(Executable executable) {
        Instructions instructions = new Instructions();
        ArrayList<Operand> operands = new ArrayList<>();
        for (Expression argument : arguments) {
            ExpressionEvaluationResult eer = argument.generateIntermediateCode(executable);
            operands.add(eer.operand);
            instructions.addAll(eer.code);
        }
        IntermediateRegister returnRegisterIndex = executable.summonNewRegister();

        instructions.add(new BinaryOperatorInstruction(this.operator, this.callee.formalTypes.get(0), this.callee.formalTypes.get(1), operands.get(0), operands.get(1), returnRegisterIndex));

        return new ExpressionEvaluationResult(instructions, new Operand(returnRegisterIndex, OperandKind.Register));
    }
}
