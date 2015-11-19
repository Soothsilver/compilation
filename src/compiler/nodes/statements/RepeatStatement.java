package compiler.nodes.statements;

import compiler.Compilation;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Represents the statement "repeat { body; } while ( expression );".
 */
public class RepeatStatement extends CycleStatement {
    /**
     * A boolean expression tested at the end of each iteration.
     */
    public Expression booleanTest;

    /**
     * Initializes a new RepeatStatement. Launches phase 2 resolution for the boolean expression (forcing boolean).
     * @param booleanTest The expression to evaluate at the end of each iteration.
     * @param body The loop's statement.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
    public RepeatStatement(Expression booleanTest, Statement body, int line, int column, Compilation compilation) {
        super(line, column);
        this.body = body;
        this.booleanTest = booleanTest;
        this.booleanTest.propagateTypes(new HashSet<>(Arrays.asList(Type.booleanType)), compilation);

    }

    @Override
    public String toString() {
        return "repeat " + body + " while (" + this.booleanTest + ");";
    }
}
