package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArrayAccessExpression extends Expression {
    public Expression array;
    public Expression index;
    public static ArrayAccessExpression create(Expression array, Expression index, Compilation compilation) {
        ArrayAccessExpression expression = new ArrayAccessExpression();
        expression.line = array.line;
        expression.column = array.column;
        expression.array = array;
        expression.index = index;
        expression.kind = ExpressionKind.ArrayAccess;
        // We would prefer to get only arrays here, but the Overload Resolution Process does not permit it. So we have to accept everything.
        expression.array.propagateTypes(null, compilation);
        if (expression.array.type.kind == Type.TypeKind.ArrayType) {
            index.propagateTypes(new HashSet<>(Arrays.asList(Type.integerType)), compilation);
            expression.type = expression.array.type.typeArguments.get(0);
            expression.possibleTypes.add(expression.type);
            return  expression;
        } else {
            compilation.semanticError("The expression '" + array + "' does not evaluate into an array.", array.line, array.column);
            expression.type = Type.errorType;
            return  expression;
        }
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        if (types == null) return;
        for (Type t : types) {
            if (this.type.convertibleTo(t)) return;
        }
        compilation.semanticError("An array member of type '" + this.type + "' cannot be converted to any of the following types: " + types, line, column);
        // TODO unification
        // TODO check validity, perhaps?
    }

    @Override
    public String toString() {
        return array + "[" + index + "]";
    }

    @Override
    public boolean isAssignable() {
        return true;
    }
}
