package compiler.nodes.statements;

import java.util.ArrayList;

public class Statements extends ArrayList<Statement> {
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
}
