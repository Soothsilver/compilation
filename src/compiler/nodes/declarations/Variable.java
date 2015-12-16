package compiler.nodes.declarations;

import compiler.Compilation;

import java.util.ArrayList;

/**
 * Represents a global or a local variable or a member field, declared at one point in the source code.
 */
public class Variable extends Declaration {
    private Type type;
    /**
     * Indicates whether the variable is global, local, class member or a parameter.
     * This is only set during intermediate code generation.
     */
    public VariableKind kind;
    /**
     * If this is a parameter, then this is the zero-based index of the parameter (first parameter is zero).
     * If this is a local variable, then this is the zero-based index of declaration since the beginning of the subroutine.
     * If this is a class member, then TODO
     * Otherwise, this field has no meaning.
     */
    public int index;
    /**
     * If this is a parameter, then this is the zero-based index of the parameter, except that the latest parameter comes first.
     * For example, in the subroutine hello(a : float, b : float), the parameter "b" has reverseIndex = 0,
     * and the parameter "a" has reverseIndex = 1.
     */
    public int reverseIndex;
    /**
     * Indicates whether this is a foreach iteration variable. If yes, then it cannot be modified.
     */
    public boolean readonly;
    /**
     * If this is a member field of a class, then that class is the owner, otherwise null.
     */
    public TypeOrTypeTemplate owner;

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

    /**
     * Instantiates a specialized Variable from a generic one.
     * @param typeParameters Types to be replaced.
     * @param typeArguments Replacing types.
     * @return A specialized variable.
     */
    public Variable instantiate(ArrayList<Type> typeParameters, ArrayList<Type> typeArguments) {
        Variable v = this.copy();
        v.type = v.type.replaceTypes(typeParameters, typeArguments);
        return v;
    }
    private Variable copy() {
        return new Variable(name, type, line, column);
    }
}
