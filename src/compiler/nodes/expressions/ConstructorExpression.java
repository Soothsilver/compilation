package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.Set;

public class ConstructorExpression extends Expression {
    public String typeName;

    public ConstructorExpression(String typeName, int line, int column, Compilation compilation) {
        this.typeName = typeName;
        this.line = line;
        this.column = column;
        this.type = (Type)compilation.environment.findType(typeName);
        this.possibleTypes.add(this.type);
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        if (!types.contains(this.type)) { //todo unify
            compilation.semanticError("An object of type " + this.type.name + " cannot be converted to any of the following types: " + types, line, column);
        }
    }
}
