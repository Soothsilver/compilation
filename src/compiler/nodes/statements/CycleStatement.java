package compiler.nodes.statements;

import compiler.Compilation;

/**
 * All cycle statements (for, foreach, while, repeat) inherit from this class.
 */
public abstract class CycleStatement extends Statement {

    /**
     * The body of a cycle statement. Most often, this will be a BlockStatement.
     */
    public Statement body;

    protected CycleStatement(int line, int column) {
        super(line, column);
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        body.flowAnalysis(compilation);
        return true;
    }
}
