package compiler.nodes.declarations;

import compiler.Compilation;

import java.util.ArrayList;

/**
 * Represents a global or a local variable or a member field, declared at one point in the source code.
 */
public class Variable extends Declaration {
    private Type type;
    public VariableKind kind;
    public int index;
    public boolean readonly;

    /**
     * Gets the complete type of this variables.
     * @return The type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Creates a new variable and attempts to add it to the current environment. If the variable already exists,
     * a semantic error is triggered.
     * @param name Name of the variable.
     * @param type Type of the variable.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     * @return The created variable.
     */
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

    public Variable instantiate(ArrayList<Type> typeParameters, ArrayList<Type> typeArguments) {
        Variable v = this.copy();
        v.type = v.type.replaceTypes(typeParameters, typeArguments);
        return v;
    }
    private Variable copy() {
        return new Variable(name, type, line, column);
    }
}
