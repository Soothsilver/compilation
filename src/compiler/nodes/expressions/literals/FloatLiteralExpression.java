package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.intermediate.Operand;
import compiler.intermediate.OperandKind;
import compiler.nodes.declarations.Type;

/**
 * Represents float literals such as "2.0" or "1e5".
 */
public class FloatLiteralExpression extends LiteralExpression {
    float data;

    /**
     * Initializes a new FloatLiteralExpression.
     * @param data Float value from lexical analysis.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
    public FloatLiteralExpression(float data, int line, int column, Compilation compilation) {
        super(line, column);
        this.data = data;
        this.type = Type.floatType;
        this.possibleTypes.add(this.type);
    }

    @Override
    public String toString() {
        return Float.toString(data);
    }
    
    @Override
    public Operand generateOperand() {
    	return new Operand(Float.floatToRawIntBits(data), OperandKind.Immediate);
    }

}
