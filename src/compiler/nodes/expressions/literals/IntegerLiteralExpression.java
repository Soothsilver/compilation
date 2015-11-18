package compiler.nodes.expressions.literals;

import compiler.intermediate.*;
import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.Set;

public class IntegerLiteralExpression extends LiteralExpression {
    int data;

    public IntegerLiteralExpression(int data, int line, int column, Compilation compilation) {
        super(line, column, compilation);
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
    public Operand generateOperand(Executable executable) {
    	return new Operand(data, OperandKind.Immediate);
    }
}
