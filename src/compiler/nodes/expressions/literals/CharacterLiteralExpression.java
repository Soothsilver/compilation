package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.intermediate.Operand;
import compiler.intermediate.OperandKind;
import compiler.nodes.declarations.Type;

/**
 * Represents expression of the kind 'a' or 'ř'.
 * Non-ascii characters are accepted in source code but produce errors during code generation.
 */
public class CharacterLiteralExpression extends LiteralExpression {
    char data;

    /**
     * Initializes a new CharacterLiteralExpression.
     * @param data The character literal.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
    public CharacterLiteralExpression(char data, int line, int column, Compilation compilation) {
        super(line, column);
        this.data = data;
        this.type = Type.characterType;
        this.possibleTypes.add(this.type);
    }

    @Override
    public String toString() {
        String s = Character.toString(data);
        if (s.equals("\n")) s = "\\n";
        if (s.equals("\r")) s = "\\r";
        return "'" + s + "'";
    }

    @Override
    public Operand generateOperand() {
        int charAsInt = (int)data;
        return new Operand(charAsInt, OperandKind.Immediate);
    }
}
