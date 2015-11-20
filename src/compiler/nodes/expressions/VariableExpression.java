package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.OperandWithCode;
import compiler.intermediate.Operand;
import compiler.intermediate.instructions.Instructions;
import compiler.nodes.declarations.Type;
import compiler.nodes.declarations.Variable;

import java.util.Set;

/**
 * Represents an expression that is just a reference to a variable. For example, in "i = 2;", "i" is a VariableExpression.
 * An expression such as "classInstance.member" is a MemberVariableExpression which is descended from this class.
 */
public class VariableExpression extends Expression {
    /**
     * Name of the global or local variable, or name of the variable member of a type.
     */
    public String name;
    /**
     * Variable symbol found in the symbol tables for this expression.
     */
    public Variable variable;




    protected VariableExpression(int line, int column) {
        super(line, column);
    }

    /**
     * Initializes a new variable expression and performs phase 1 resolution for it. It attempts to find the variable
     * in symbol tables - if it fails, an expression is created as normal, but it will have the error type only.
     * @param identifier Name of the local or global variable.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
    public VariableExpression (String identifier, int line, int column, Compilation compilation) {
        name = identifier;
        this.line = line;
        this.column = column;
        this.kind = ExpressionKind.Variable;
        variable =  compilation.environment.findVariable(identifier);
        if (variable == null) {
            compilation.semanticError("The variable '" + identifier + "' is not defined in this scope.", line, column);
            this.possibleTypes.add(Type.errorType);
            this.type = Type.errorType;
        }
        else {
            this.possibleTypes.add(variable.getType());
            this.type = variable.getType();
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        // Invulnerable to type propagation. Variables have a type strictly defined.
        if (types == null) return;
        for(Type t : types) {
            if (this.type.convertibleTo(t)) {
                return;
            }
        }
        compilation.semanticError("The variable '" + this + "' cannot be converted to any of the types: " + types + ".", line, column);
    }

    @Override
    public boolean isAssignable() {
        return variable != null && !variable.readonly;
    }

    @Override
    public OperandWithCode generateIntermediateCode(Executable executable) {
        return new OperandWithCode(new Instructions(), Operand.createFromVariable(variable));
    }
}
