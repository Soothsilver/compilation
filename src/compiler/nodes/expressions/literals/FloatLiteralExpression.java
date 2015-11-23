package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.intermediate.Operand;
import compiler.intermediate.OperandKind;
import compiler.nodes.declarations.Type;

public class FloatLiteralExpression extends LiteralExpression {
    float data;

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
