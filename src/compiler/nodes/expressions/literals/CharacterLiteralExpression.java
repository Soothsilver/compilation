package compiler.nodes.expressions.literals;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

public class CharacterLiteralExpression extends LiteralExpression {
    char data;

    public CharacterLiteralExpression(char data, int line, int column, Compilation compilation) {
        super(line, column, compilation);
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


}
