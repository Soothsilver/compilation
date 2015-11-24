package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.intermediate.Operand;
import compiler.intermediate.OperandKind;
import compiler.nodes.declarations.Type;

import java.util.Set;

/**
 * Represents the expression "null" which returns a null reference (a nil pointer).
 */
public class NullExpression extends LiteralExpression {
    /**
     * Initializes a new NullExpression.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
    public NullExpression(int line, int column, Compilation compilation) {
        super(line, column);
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

    @Override
    public Operand generateOperand() {
        return new Operand(0, OperandKind.Immediate);
    }
}
