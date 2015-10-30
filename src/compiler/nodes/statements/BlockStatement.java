package compiler.nodes.statements;

import compiler.nodes.Declarations;
import compiler.nodes.declarations.Declaration;

public class BlockStatement extends Statement {
    public Statements statements;
    public Declarations declarations;

    @Override
    public String toString() {
        return "{ " + declarations.toString() + statements.toString() + " }";
    }
}
