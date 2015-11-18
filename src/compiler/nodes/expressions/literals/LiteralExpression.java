package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.intermediate.Operand;
import compiler.intermediate.OperandKind;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;
import compiler.nodes.expressions.ExpressionKind;

import java.util.Set;

public abstract class LiteralExpression extends Expression {

    protected LiteralExpression( int line, int column, Compilation compilation) {
        this.line = line;
        this.column = column;
        this.kind = ExpressionKind.Literal;
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        if (types == null) return;
        if (!types.contains(this.type)) {
            compilation.semanticError("A " + this.type.name + " cannot be converted to any of the following types: " + types, line, column);
        }
    }
    
    public Operand generateOperand() {
    	return new Operand(1, OperandKind.Immediate);
    }
}
