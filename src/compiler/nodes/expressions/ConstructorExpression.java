package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;
import java.util.Set;

public class ConstructorExpression extends Expression {
    public String typeName;

    public ConstructorExpression(String typeName, int line, int column, Compilation compilation) {
        this.typeName = typeName;
        this.line = line;
        this.kind = ExpressionKind.ClassConstructor;
        this.column = column;
        this.type = (Type)compilation.environment.findType(typeName);
        this.possibleTypes.add(this.type);
        if (this.type == null) {
            compilation.semanticError("The type '" + typeName + "' could not be found.", line, column);
            this.setErrorType();
        }
        else if (!this.type.isReferenceType) {
            compilation.semanticError("The type '" + typeName + "' cannot be constructed. Only classes, arrays and structures can be constructed.", line, column);
            this.setErrorType();
        }
    }
    public ConstructorExpression(String typeName, ArrayList<Type> typeArguments, int line, int column, Compilation compilation) {
        this.typeName = typeName;
        this.line = line;
        this.column = column;
        this.kind = ExpressionKind.ClassConstructor;
        this.type = Type.instantiateTemplate (typeName, typeArguments, line, column, compilation);
        this.possibleTypes.add(this.type);
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        if (!types.contains(this.type)) { //todo unify
            compilation.semanticError("A constructed object of type " + this.type.name + " cannot be converted to any of the following types: " + types, line, column);
        }
    }

    @Override
    public String toString() {
        return "new " + this.type + "()";
    }
}
