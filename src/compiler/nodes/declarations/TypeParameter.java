package compiler.nodes.declarations;

import compiler.nodes.Node;

public class TypeParameter extends Node {
    public String name;
    public TypeParameter(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }
}
