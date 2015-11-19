package compiler.nodes.declarations;

/**
 * Indicates whether the variable is global, local, class member or a parameter.
 */
public enum VariableKind {
    /**
     * A global variable is declared outside any subroutine and class.
     */
    Global,
    /**
     * A local variable is declared inside a block.
     */
    Local,
    /**
     * A member variable is declared inside a class or a structure.
     */
    Member,
    /**
     * A parameter is passed to a subroutine by its caller.
     */
    Parameter
}