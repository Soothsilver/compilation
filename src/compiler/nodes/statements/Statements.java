package compiler.nodes.statements;

import java.util.ArrayList;

/**
 * A list of statements, used to collect statements from CUP.
 */
public class Statements extends ArrayList<Statement> {
    /**
     * Adds a statement at the end of this statement list.
     * @param statement The new statement.
     */
    public void addStatement(Statement statement) {
        this.add(0, statement);
    }

    @Override
    public String toString() {
        if (this.size() == 0) return "";
        String ss = "";
        for (Statement s : this) {
            ss += s;
            ss += " ";
        }
        return ss.substring(0, ss.length() - 1);
    }

    /**
     * Creates a text representation of this list of statement such that it will be prettily printed.
     *
     * If there is no statement, it will return an empty string.
     * Otherwise, it will print each statement on a single line, but will OMIT the final line break.
     */
    public String toLongString() {
        if (this.size() == 0) return "";
        String ss = "";
        for (Statement s : this) {
            ss += s;
            ss += "\n";
        }
        return ss.substring(0, ss.length() - 1);
    }
}
