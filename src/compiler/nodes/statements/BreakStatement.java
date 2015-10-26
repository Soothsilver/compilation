package compiler.nodes.statements;

import compiler.Compilation;

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
}
