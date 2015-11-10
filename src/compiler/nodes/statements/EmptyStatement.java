package compiler.nodes.statements;

import compiler.Compilation;

public class EmptyStatement extends Statement {
    public EmptyStatement(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        return true;
    }

    @Override
    public String toString() {
        return ";";
    }
}
