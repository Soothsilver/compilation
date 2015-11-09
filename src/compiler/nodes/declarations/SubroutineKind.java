package compiler.nodes.declarations;

/**
 * Indicates whether a subroutine has a return type.
 */
public enum SubroutineKind {
    /**
     * A procedure is a subroutine with the return type "!void".
     */
    PROCEDURE,
    /**
     * A function has a real return type.
     */
    FUNCTION
}
