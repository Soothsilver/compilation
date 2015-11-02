package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.analysis.OverloadResolution;

public class UnaryExpression extends CallExpression {
    private String operator;
    private UnaryExpressionSide side;

    public static UnaryExpression create(String operator, UnaryExpressionSide side, Expression expression, int line, int column, Compilation compilation) {
        UnaryExpression ex = new UnaryExpression();
        ex.operator = operator;
        ex.line = line;
        ex.column = column;
        ex.arguments = new Expressions();
        ex.side = side;
        ex.arguments.add(expression);
        ex.group = SubroutineGroup.create((side == UnaryExpressionSide.Prefix ? "PRE" : "POST") + operator, line, column, compilation);
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

