package compiler.nodes.statements;

import compiler.nodes.Declarations;

/**
 * Represents a block of scoped variables and statements. It is printed on one line if there are no declarations,
 * and on multiple lines if there is a variable declaration. It's clearest this way, I think.
 * All subroutines have a BlockStatement as a member.
 */
public class BlockStatement extends Statement {
    public Statements statements = null;
    public Declarations declarations = null;

    @Override
    public String toString() {
        if (declarations == null || declarations.isEmpty())
             return "{ " + statements.toString() + " }";
        else
            return "{ \n" + declarations.toString() + statements.toLongString() + "\n}";
    }
}
