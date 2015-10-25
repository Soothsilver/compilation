package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.ErrorReporter;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;

import java.util.List;
import java.util.Set;

public abstract class LiteralExpression extends Expression {

    protected LiteralExpression( int line, int column, Compilation compilation) {
        this.line = line;
        this.column = column;
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        // Ignore this call.
        // Literal expressions are not vulnerable to overloading or inference. They only have one type.
    }
}
