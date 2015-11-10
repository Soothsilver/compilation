package compiler.nodes.statements;

import compiler.Compilation;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;

import java.util.Arrays;
import java.util.HashSet;

public class WhileStatement extends CycleStatement {
    public Expression booleanTest;

    public WhileStatement(Expression booleanTest, int line, int column, Compilation compilation) {
        this.line = line;
        this.column = column;
        this.booleanTest = booleanTest;
        this.booleanTest.propagateTypes(new HashSet<>(Arrays.asList(Type.booleanType)), compilation);

    }

    @Override
    public String toString() {
        return "while (" + this.booleanTest + ") " + body;
    }
}
