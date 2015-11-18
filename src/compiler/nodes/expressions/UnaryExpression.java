package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.analysis.OverloadResolution;

/**
 * Represents a unary expression such as "i++".
 */
public class UnaryExpression extends CallExpression {
    private String operator;
    private UnaryExpressionSide side;

    /**
     * Creates a new UnaryExpression and launches phase 1 resolution for it.
     *
     * This function may fail for a number of reasons:
     * a) The operator does not exist.
     * b) The operator does not accept the argument of the type given.
     * c) The operand is not an assignable expression (i.e. it is not an l-value).
     * @param operator The operator. For example, for increment, use "++".
     * @param side Whether the operator precedes or succeeds the operand. This is only relevant for increment and decrement operators.
     * @param expression The operand.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     * @return A UnaryExpression object, perhaps with error type set.
     */
    public static UnaryExpression create(String operator, UnaryExpressionSide side, Expression expression, int line, int column, Compilation compilation) {
        UnaryExpression ex = new UnaryExpression();
        ex.operator = operator;
        ex.line = line;
        ex.column = column;
        ex.arguments = new Expressions();
        ex.side = side;
        ex.arguments.add(expression);
        ex.group = SubroutineGroup.create( (operator.equals("++") || operator.equals("--") ? (side == UnaryExpressionSide.Prefix ? "PRE" : "POST") : "") + operator, line, column, compilation);
        ex.kind = ExpressionKind.ArithmeticExpression;
        switch (operator) {
            case "++":
            case "--":
                ex.kind = ExpressionKind.Increment;
                if (!expression.isAssignable()) {
                    compilation.semanticError("The operand of an increment or decrement operator must be a variable, array member or a class member.", line, column);
                    ex.setErrorType();
                    return ex;
                }
                break;
        }
        OverloadResolution.phaseOne(ex, compilation);
        return ex;
    }

    @Override
    public String getErrorMessageTypeMismatch() {
        return "The unary operator '" + group.name + "' does not accept an operand of the type '" + arguments.get(0).type + "'.";
    }
}

