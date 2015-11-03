package compiler.nodes;

/**
 * Represents a node of the abstract syntax tree.
 */
public abstract class Node {
    /**
     * Zero-indexed line number where this node's first terminal appears in source code. A number of "-1" means that the node
     * does not appear in source code, but is predefined.
     */
    public int line = -1;
    /**
     * Zero-indexed column number where this node's first terminal appears in source code. A number of "-1" means that the node
     * does not appear in source code, but is predefined.
     */
    public int column = -1;

    protected Node() {

    }
    protected Node(int line, int column) {
        this.line = line;
        this.column = column;
    }
}
