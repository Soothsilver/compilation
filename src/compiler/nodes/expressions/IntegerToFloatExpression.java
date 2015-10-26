package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.Set;

public class IntegerToFloatExpression extends Expression {
    public Expression integerExpression;
    public IntegerToFloatExpression(Expression integerExpression) {
        this.integerExpression = integerExpression;
        this.line = this.integerExpression.line;
        this.column = this.integerExpression.column;
        this.type = Type.floatType;
    }

    @Override
    public String toString() {
        return "!float(" + integerExpression + ")";
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        throw new RuntimeException("This should never be called.");
    }
}
