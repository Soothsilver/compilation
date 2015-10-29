package compiler.nodes.declarations;

public abstract class TypeOrTypeTemplate extends Declaration {
    protected TypeOrTypeTemplate(String name, int line, int column) {
        super(name, line, column);
    }

    @Override
    public String toString() {
        return name;
    }
}
