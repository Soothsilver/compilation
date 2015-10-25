package compiler.nodes.statements;

public class StopStatement extends Statement {
    public StopStatement(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "stop;";
    }
}
