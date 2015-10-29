package compiler.nodes.declarations;

import compiler.nodes.Node;

public abstract class Declaration extends Node {
    public String name;

    protected Declaration(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }



    public final String getId() {
        return name;
    }
    public String getSig() {
        // This should be overriden for subroutines.
        return name;
    }

    public String getFullString() {
        return name;
    }
}
