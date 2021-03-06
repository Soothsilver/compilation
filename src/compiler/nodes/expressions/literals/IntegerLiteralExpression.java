package compiler.nodes.expressions.literals;

import compiler.intermediate.*;
import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.Set;

/**
 * Represents integer literals, such as "245".
 */
public class IntegerLiteralExpression extends LiteralExpression {
    int data;

    /**
     * Initializes a new IntegerLiteral expression.
     * @param data Integer value from lexical analysis.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
    public IntegerLiteralExpression(int data, int line, int column, Compilation compilation) {
        super(line, column);
        this.data = data;
        this.type = Type.integerType;
        this.possibleTypes.add(this.type);
    }

    @Override
    public String toString() {
        return Integer.toString(data);
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        if (types == null) return;
        if (!types.contains(Type.integerType) && !types.contains(Type.floatType)) {
            compilation.semanticError("An integer cannot be converted to any of the following types: " + types, line, column);
        }
    }
    
    @Override
    public Operand generateOperand() {
    	return new Operand(data, OperandKind.Immediate);
    }
}
