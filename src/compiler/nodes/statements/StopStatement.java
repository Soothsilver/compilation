package compiler.nodes.statements;

import compiler.Compilation;

public class StopStatement extends Statement {
    public StopStatement(int line, int column, Compilation compilation) {
        this.line = line;
        this.column = column;
        if (!compilation.environment.inProcedure) {
            compilation.semanticError("The stop statement can be used only inside a procedure.", line, column);
        }
    }

    @Override
    public String toString() {
        return "stop;";
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        return false;
    }
}
