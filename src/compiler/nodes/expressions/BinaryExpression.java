package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.analysis.OverloadResolution;

public class BinaryExpression extends CallExpression {
    public String operator;

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

}
