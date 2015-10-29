package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.declarations.Type;
import compiler.nodes.declarations.Variable;

import java.util.Set;

public class VariableExpression extends Expression {
    public String name;
    public Variable variable;

    public VariableExpression (String identifier, int line, int column, Compilation compilation) {
        name = identifier;
        this.line = line;
        this.column = column;
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
        // TODO but we must do as if in literals
    }

    @Override
    public boolean isAssignable() {
        return true;
    }
}
