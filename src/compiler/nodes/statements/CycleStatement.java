package compiler.nodes.statements;

/**
 * All cycle statements (for, foreach, while, repeat) inherit from this class.
 */
public class CycleStatement extends Statement {

    public Statement body;
}
