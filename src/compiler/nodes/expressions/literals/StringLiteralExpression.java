package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.intermediate.*;
import compiler.intermediate.instructions.Instructions;
import compiler.nodes.declarations.Type;

/**
 * Represents a string literal such as "hello".
 * Problems may arise when the string contains unicode characters.
 */
public class StringLiteralExpression extends LiteralExpression {
    String data;

    /**
     * Initializes a new StringLiteralExpression.
     * @param data String contents.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
    public StringLiteralExpression(String data, int line, int column, Compilation compilation) {
        super(line, column);
        this.data = data;
        this.type = Type.stringType;
        this.possibleTypes.add(this.type);
    }

    @Override
    public String toString() {
        return "\"" + data + "\"";
    }

    @Override
    public OperandWithCode generateIntermediateCode(Executable executable) {
        IntermediateStringLiteral isl = executable.createStringLiteral(data);
        return new OperandWithCode(new Instructions(), new Operand(isl, OperandKind.StringLiteral));
    }
}
