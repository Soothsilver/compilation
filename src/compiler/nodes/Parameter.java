package compiler.nodes;

import compiler.Compilation;
import compiler.nodes.declarations.Type;
import compiler.nodes.declarations.Variable;
import compiler.nodes.declarations.VariableKind;

/**
 * Represents a formal parameter of a subroutine declaration. Not a type parameter. Not an actual argument.
 */
public class Parameter extends Node {
    /**
     * Name of the formal parameter, e.g. "a" for "a : integer"
     */
    public String name;
    /**
     * Type of the formal parameter, e.g. Type.integerType for "a : integer"
     */
    public Type type;
    /**
     * Variable representing this parameter.
     * If the variable's type is generic in a specific way, it stays that way... it's complicated.
     */
    public Variable variable;

    /**
     * Creates a parameter and adds it to the current environment.
     */
    public Parameter(String name, Type type, int line, int column, Compilation compilation) {
        super(line, column);
        this.name = name;
        this.type = type;
        this.variable = Variable.createAndAddToEnvironment(name, type, line, column, compilation);
        this.variable.kind = VariableKind.Parameter;
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

    public Parameter copy() {
        Parameter pNew = new Parameter(name, type);
        pNew.line = line;
        pNew.column =column;
        pNew.variable = variable;
        return pNew;
    }
}
