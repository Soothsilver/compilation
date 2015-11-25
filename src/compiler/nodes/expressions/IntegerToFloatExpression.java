package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.OperandWithCode;
import compiler.nodes.declarations.Type;

import java.util.Set;

/**
 * Represents a compiler-generated expression that envelops an Integer-type expression when it needs to be
 * converted to a float. This expression is generated during the second phase of overload resolution.
 */
public class IntegerToFloatExpression extends Expression {
    private Expression integerExpression;

    /**
     * Initializes a new IntegerToFloat expression. Always succeeds.
     * @param integerExpression The Integer-type expression to envelop.
     */
    public IntegerToFloatExpression(Expression integerExpression) {
        this.integerExpression = integerExpression;
        this.line = this.integerExpression.line;
        this.column = this.integerExpression.column;
        this.type = Type.floatType;
    }

    @Override
    public String toString() {
        return "!float(" + integerExpression + ")";
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        throw new RuntimeException("This should never be called.");
    }

    /**
     * This method for IntegerToFloatExpression merely passes the enclosed expression. This is because we don't care about whether the operand
     * is a float or an integer. Everything is stored as 4-byte integers in MIPS, and float-int disambiguation for overloading was already handled
     * during semantic analysis.
     *
     * Perhaps I'm wrong here. This may change later.
     */
    @Override
    public OperandWithCode generateIntermediateCode(Executable executable) {
    	return integerExpression.generateIntermediateCode(executable);
    }
    
    
}
