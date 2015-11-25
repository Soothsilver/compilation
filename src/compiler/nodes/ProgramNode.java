package compiler.nodes;

/**
 * Represents the root of the abstract syntax tree.
 */
public class ProgramNode extends Node {
    /**
     * All top-level types and variables go here.
     */
    public Declarations Declarations = new Declarations();
    /**
     * All top-level subroutines go here.
     */
    public Subroutines Subroutines = new Subroutines();

    @Override
    public String toString() {
        return "PROGRAM[\n" + Declarations.toString() +Subroutines.toString() + "]";
    }
}
