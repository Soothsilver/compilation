package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.intermediate.Operand;
import compiler.intermediate.OperandKind;
import compiler.nodes.declarations.Type;

/**
 * Represents the literal "true" or the literal "false".
 */
public class BooleanLiteralExpression extends LiteralExpression {
    boolean data;

    /**
     * Initializes a new boolean literal expression. Cannot fail.
     */
    public BooleanLiteralExpression(boolean data, int line, int column, Compilation compilation) {
        super(line, column);
        this.data = data;
        this.type = Type.booleanType;
        this.possibleTypes.add(this.type);
    }

    @Override
    public String toString() {
        return (data ? "true" : "false");
    }


    @Override
    public Operand generateOperand() {
        return new Operand(data ? 1 : 0, OperandKind.Immediate);
    }
}
