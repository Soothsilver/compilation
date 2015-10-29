package compiler.nodes.declarations;

import compiler.Compilation;

public class Variable extends Declaration {
    private Type type;
    public Type getType() {
        return type;
    }

    public Variable(String name, Type type, int line, int column, Compilation compilation) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
        compilation.environment.addVariable(this);
    }

    @Override
    public String getFullString() {
        return name + " : " + type + ";";
    }
}
