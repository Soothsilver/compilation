package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

public class BooleanLiteralExpression extends LiteralExpression {
    boolean data;

    public BooleanLiteralExpression(boolean data, int line, int column, Compilation compilation) {
        super(line, column, compilation);
        this.data = data;
        this.type = Type.booleanType;
        this.possibleTypes.add(this.type);
    }

    @Override
    public String toString() {
        return (data ? "true" : "false");
    }


}
