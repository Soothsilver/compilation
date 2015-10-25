package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.List;
import java.util.Set;

public class ArithmeticExpression extends Expression {
    Expression left;
    Expression right;

    private ArithmeticExpression(Expression left, Expression right, int line, int column) {
        this.line = line;
        this.column = column;
        this.left = left;
        this.right = right;
    }

    public static ArithmeticExpression create(Expression left, Expression right, int line, int column) {
        return new ArithmeticExpression(left, right, line, column);
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        compilation.semanticError("TO BE IMPLEMENTED: ARITHMETIC EXPRESSION", line, column);
    }
}
