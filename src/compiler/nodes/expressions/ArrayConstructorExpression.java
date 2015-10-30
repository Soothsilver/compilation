package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.Set;

public class ArrayConstructorExpression extends Expression {
    private Type innerType;
    private int size;

    public ArrayConstructorExpression(Type innerType, int size, int line, int column, Compilation compilation) {
        this.line = line;
        this.column = column;
        this.size = size;
        this.innerType = innerType;
        this.type = Type.createArray(innerType, line, column);
        this.possibleTypes.add(this.type);
    }
    public static ArrayConstructorExpression infer(Expressions expressions, int line, int column, Compilation compilation) {
        // TODO make this actually work
        ArrayConstructorExpression expression = new ArrayConstructorExpression(Type.errorType, expressions.size(), line, column, compilation);
        return expression;
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        if (!types.contains(this.type)) { //todo unify
            compilation.semanticError("An array of '" + this.innerType + "' cannot be converted to any of the following types: " + types, line, column);
        }
    }

    @Override
    public String toString() {
        return "new " + innerType + "[" + size + "]";
    }
}
