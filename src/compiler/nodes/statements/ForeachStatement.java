package compiler.nodes.statements;

import compiler.Compilation;
import compiler.nodes.Declarations;
import compiler.nodes.declarations.Variable;
import compiler.nodes.expressions.Expression;

/**
 * Represents the statement "foreach ( identifier in array ) statement;"
 */
public class ForeachStatement extends CycleStatement {
    private Expression array;
    private String iterationVariableName;
    private Variable iterationVariable;

    /**
     * Initializes a new ForeachStatement. Launches phase 2 resolution for the array expression.
     * This does not create a complete foreach statement. The creation still has to be completed by calling "finish".
     * @param iterationVariableName Name of the iteration variable, such as "i".
     * @param array The array to iterate through.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
    public ForeachStatement(
            String iterationVariableName,
            Expression array,
            int line,
            int column,
            Compilation compilation) {

        super(line, column);
        this.iterationVariableName = iterationVariableName;
        this.array = array;
        this.array.propagateTypes(null, compilation);
        compilation.environment.enterScope();
        if (!this.array.type.name.equals("!array")) {
            compilation.semanticError("The expression '" + array + "' is not an array.",
                    array.line, array.column);
            return;
        }
        iterationVariable  = Variable.createAndAddToEnvironment(iterationVariableName, array.type.typeArguments.get(0), line, column, compilation);
        iterationVariable.readonly = true;
    }

    /**
     * Finishes the creation of the ForeachStatement.
     * This is done by replacing its body "body;" with the block statement
     * {
     *     iterationVariable : iterationVariableType;
     *     body;
     * }
     */
    public void finish() {
        if (iterationVariable != null) {
            BlockStatement block = new BlockStatement();
            block.line = body.line;
            block.column = body.column;
            block.declarations = new Declarations();
            block.declarations.add(iterationVariable);
            block.statements = new Statements();
            block.statements.add(this.body);
            this.body = block;
        }
    }

    @Override
    public String toString() {
        return "foreach (" + iterationVariableName + " in " + array + ") " + this.body;
    }
}
