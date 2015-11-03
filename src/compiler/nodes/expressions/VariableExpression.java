package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.declarations.Type;
import compiler.nodes.declarations.Variable;

import java.util.Set;

public class VariableExpression extends Expression {
    public String name;
    public Variable variable;




    protected VariableExpression(int line, int column) {
        super(line, column);
    }
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
        return true;
    }
}
