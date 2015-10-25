package compiler.nodes.statements;

public class BlockStatement extends Statement {
    public Statements statements;

    @Override
    public String toString() {
        return "{ " + statements.toString() + " }";
    }
}
