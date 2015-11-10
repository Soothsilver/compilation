package compiler.nodes.statements;

import compiler.Compilation;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;

import java.util.Arrays;
import java.util.HashSet;

public class RepeatStatement extends CycleStatement {
    public Expression booleanTest;

    public RepeatStatement(Expression booleanTest, Statement body, Compilation compilation) {
        this.body = body;
        this.booleanTest = booleanTest;
        this.booleanTest.propagateTypes(new HashSet<>(Arrays.asList(Type.booleanType)), compilation);

    }

    @Override
    public String toString() {
        return "repeat " + body + " while (" + this.booleanTest + ");";
    }
}
