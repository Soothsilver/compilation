package compiler.nodes.declarations;

import compiler.Compilation;

/**
 * Represents a global or a local variable, declared at one point in the source code.
 */
public class Variable extends Declaration {
    private Type type;
    public Type getType() {
        return type;
    }

    public static Variable createAndAddToEnvironment(String name, Type type, int line, int column, Compilation compilation) {
        Variable variable = new Variable(name, type, line, column);
        compilation.environment.addVariable(variable);
        return variable;
    }

    private Variable(String name, Type type, int line, int column) {
        super(name, line, column);
        this.type = type;
    }

    @Override
    public String getFullString() {
        return name + " : " + type + ";";
    }
}
