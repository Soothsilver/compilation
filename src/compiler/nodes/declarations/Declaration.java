package compiler.nodes.declarations;

import compiler.nodes.Node;

public abstract class Declaration extends Node {
    public String name = "!UNNAMED";
    public final String getId() {
        return name;
    }
    public String getSig() {
        // This should be overriden for subroutines.
        return name;
    }
}
