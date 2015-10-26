package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.ErrorReporter;
import compiler.nodes.declarations.Type;

import java.util.List;

public class IntegerLiteralExpression extends LiteralExpression {
    int data;

    public IntegerLiteralExpression(int data, int line, int column, Compilation compilation) {
        super(line, column, compilation);
        this.data = data;
        this.type = Type.integerType;
        this.possibleTypes.add(this.type);
    }

    @Override
    public String toString() {
        return Integer.toString(data);
    }
}
