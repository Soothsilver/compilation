package compiler;

import compiler.nodes.declarations.Type;

/**
 * Represents a formal parameter of a subroutine declaration. Not a type parameter. Not an actual argument.
 */
public class Parameter {
    public String name;
    public Type type;

    public Parameter(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}
