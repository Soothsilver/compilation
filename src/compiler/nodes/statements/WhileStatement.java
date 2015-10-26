package compiler.nodes.statements;

import compiler.Compilation;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;

import java.util.Arrays;
import java.util.HashSet;

public class WhileStatement extends CycleStatement {
    public Expression booleanTest;

    public WhileStatement(Expression booleanTest, Statement body, Compilation compilation) {
        this.body = body;
        this.booleanTest = booleanTest;
        this.booleanTest.propagateTypes(new HashSet<Type>(Arrays.asList(Type.booleanType)), compilation);

    }

    @Override
    public String toString() {
        return "while (" + this.booleanTest + ") " + body;
    }
}
