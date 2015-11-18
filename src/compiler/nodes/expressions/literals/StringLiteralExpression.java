package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.Operand;
import compiler.intermediate.OperandKind;
import compiler.nodes.declarations.Type;

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
