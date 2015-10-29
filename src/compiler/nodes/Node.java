package compiler.nodes;

public abstract class Node {
    public int line = -1;
    public int column = -1;

    protected Node() {

    }
    protected Node(int line, int column) {
        this.line = line;
        this.column = column;
    }
}
