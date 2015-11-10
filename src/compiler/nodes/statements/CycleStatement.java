package compiler.nodes.statements;

import compiler.Compilation;

/**
 * All cycle statements (for, foreach, while, repeat) inherit from this class.
 */
public class CycleStatement extends Statement {

    public Statement body;

    protected CycleStatement(int line, int column) {
        super(line, column);
    }

    public CycleStatement() {
        super();
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        body.flowAnalysis(compilation);
        return true;
    }
}
