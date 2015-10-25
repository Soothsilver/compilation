package compiler.nodes.declarations;

public abstract class TypeOrTypeTemplate extends Declaration {
    @Override
    public String toString() {
        return name;
    }
}
