package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.OperandWithCode;
import compiler.intermediate.Operand;
import compiler.intermediate.OperandKind;
import compiler.intermediate.instructions.Instructions;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;
import compiler.nodes.expressions.ExpressionKind;

import java.util.Set;

/**
 * An abstract class inherited by all literal expressions.
 */
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

    /**
     * Generates an operand from a literal expression. This method should be overridden in all literal expressions.
     * The operand's kind should always be immediate.
     * @return An operand.
     */
    public Operand generateOperand(Executable executable) {
    	return new Operand(1, OperandKind.Immediate);
    }

    @Override
    public OperandWithCode generateIntermediateCode(Executable executable) {
        return new OperandWithCode(new Instructions(), generateOperand(executable));
    }
}
