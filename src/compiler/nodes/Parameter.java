package compiler.nodes;

import compiler.Compilation;
import compiler.nodes.Node;
import compiler.nodes.declarations.Type;
import compiler.nodes.declarations.Variable;

/**
 * Represents a formal parameter of a subroutine declaration. Not a type parameter. Not an actual argument.
 */
public class Parameter extends Node {
    public String name;
    public Type type;
    private Variable variable;

    /**
     * Creates a parameter and adds it to the current environment.
     */
    public Parameter(String name, Type type, int line, int column, Compilation compilation) {
        super(line, column);
        this.name = name;
        this.type = type;
        this.variable = Variable.createAndAddToEnvironment(name, type, line, column, compilation);
    }

    /**
     * Creates a parameter and does not add it to the environment. Use this for predefined functions and operators only.
     * @param name Name.
     * @param type Type.
     */
    public Parameter(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}