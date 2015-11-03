package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.Set;

public class NullExpression extends LiteralExpression {
    public NullExpression(int line, int column, Compilation compilation) {
        super(line, column, compilation);
        this.type = Type.nullType;
        this.possibleTypes.add(this.type);
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        if (types == null) return;
        for (Type setType : types) {
            if (setType.canBeNulled()) return;
        }
        compilation.semanticError("None of the following types is nullable: " + types, line, column);
    }

    @Override
    public String toString() {
        return "null";
    }
}
