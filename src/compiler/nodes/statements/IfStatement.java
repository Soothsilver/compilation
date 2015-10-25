package compiler.nodes.statements;

import compiler.Compilation;
import compiler.ErrorReporter;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;

import java.util.Arrays;
import java.util.HashSet;

public class IfStatement extends Statement {
    public Expression test;
    public Statement thenStatement;
    public Statement elseStatement;
    public IfStatement(Expression booleanTest, Statement thenStatement, Statement elseStatement, Compilation errorReporter) {
        this.test = booleanTest;
        this.test.propagateTypes(new HashSet<>(Arrays.asList(Type.booleanType)), errorReporter);
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    @Override
    public String toString() {
        if (elseStatement == null) {
            return "if (" + test + ") " + thenStatement;
        } else {
            return "if (" + test + ") " + thenStatement + " else " + elseStatement;
        }
    }
}
