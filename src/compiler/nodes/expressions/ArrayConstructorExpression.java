package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArrayConstructorExpression extends Expression {
    private Type innerType;
    private Expression size;

    public ArrayConstructorExpression(Type innerType, Expression size, int line, int column, Compilation compilation) {
        this.line = line;
        this.column = column;
        this.size = size;
        this.innerType = innerType;
        this.type = Type.createArray(innerType, line, column);
        this.size.propagateTypes(new HashSet<>(Arrays.asList(Type.integerType)), compilation);
        this.possibleTypes.add(this.type);
        this.kind = ExpressionKind.ArrayConstructor;
    }


    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        if (!types.contains(this.type)) {
            compilation.semanticError("An array of '" + this.innerType + "' cannot be converted to any of the following types: " + types, line, column);
        }
    }

    @Override
    public String toString() {
        return "new " + innerType + "[" + size + "]";
    }
}
