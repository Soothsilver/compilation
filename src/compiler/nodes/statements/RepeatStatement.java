package compiler.nodes.statements;

import compiler.nodes.expressions.Expression;

public class RepeatStatement extends CycleStatement {
    public Expression booleanTest;

    public RepeatStatement(Expression booleanTest, Statement body) {
        this.body = body;
        this.booleanTest = booleanTest;
    }

    @Override
    public String toString() {
        return "repeat " + body + " while (" + this.booleanTest + ");";
    }
}
