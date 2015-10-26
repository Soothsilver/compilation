package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.Set;

public class StringLiteralExpression extends LiteralExpression {
    String data;

    public StringLiteralExpression(String data, int line, int column, Compilation compilation) {
        super(line, column, compilation);
        this.data = data;
        this.type = Type.integerType;
        this.possibleTypes.add(this.type);
    }

    @Override
    public String toString() {
        return "\"" + data + "\"";
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        if (!types.contains(Type.stringType)) {
            compilation.semanticError("A string cannot be converted to any of the following types: " + types, line, column);
        }
    }
}
