package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.Set;

public class StringLiteralExpression extends LiteralExpression {
    String data;

    public StringLiteralExpression(String data, int line, int column, Compilation compilation) {
        super(line, column, compilation);
        this.data = data;
        this.type = Type.stringType;
        this.possibleTypes.add(this.type);
    }

    @Override
    public String toString() {
        return "\"" + data + "\"";
    }
}