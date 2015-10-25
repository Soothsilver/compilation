package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.ErrorReporter;
import compiler.nodes.declarations.Type;
import compiler.nodes.declarations.Variable;

import java.util.List;
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
            // TODO elsewhere: if something is already defined, report the line and column of the new definition
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
    }
}
