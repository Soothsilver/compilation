package compiler.nodes.statements;

import compiler.Compilation;

/**
 * Represents the statement "break;".
 */
public class BreakStatement extends Statement {
    public BreakStatement(int line, int column, Compilation compilation) {
        this.line = line;
        this.column = column;
        if (!compilation.environment.inCycle()) {
            compilation.semanticError("No enclosing loop out of which to break.", line, column);
        }
    }

    @Override
    public String toString() {
        return "break;";
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        return false;
    }
}
