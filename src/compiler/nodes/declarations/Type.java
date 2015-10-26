package compiler.nodes.declarations;

import compiler.Compilation;

import java.util.ArrayList;

public class Type extends TypeOrTypeTemplate {
    public TypeKind kind;
    public ArrayList<Type> typeArguments;
    public boolean boundToNumeric;
    public boolean boundToReferenceType;
    public Type boundToSpecificType;
    // public String name; <-- inherited
    public Type copy() {
        Type clone = new Type();
        clone.name = this.name;
        clone.boundToNumeric = this.boundToNumeric;
        clone.boundToReferenceType = this.boundToReferenceType;
        clone.boundToSpecificType = this.boundToSpecificType;
        clone.kind = this.kind;
        clone.typeArguments = this.typeArguments; // TODO ATTENTION HERE!
        return clone;
    }

    public static Type findType(String identifier, int line, int column, Compilation compilation) {
        TypeOrTypeTemplate type = compilation.environment.findType(identifier);
        if (type == null) {
            compilation.semanticError("The type '" + identifier + "' could not be found.", line, column);
            return Type.errorType;
        }
        if (type instanceof TypeTemplate) {
            compilation.semanticError("The type '" + identifier + "' is generic and requires type arguments.", line, column);
            return Type.errorType;
        }
        return (Type)type;
    }


    /**
     * BASIC TYPES
     */
    public static Type errorType =  Type.createPredefinedType("!error");
    public static Type voidType = Type.createPredefinedType("!void");
    public static Type booleanType = Type.createPredefinedType("boolean");
    public static Type integerType = Type.createPredefinedType("integer");
    public static Type stringType = Type.createPredefinedType("string");
    public static Type characterType = Type.createPredefinedType("character");
    public static Type floatType = Type.createPredefinedType("float");

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Type &&
                name.equals(((Type)obj).name);
    }

    public static Type getBooleanType() {
        return booleanType;
    }
    public static Type createPredefinedType(String name) {
        Type t = new Type();
        t.name = name;
        t.kind = TypeKind.SimpleType;
        return t;
    }
    public static Type createNewTypeVariable() {
        Type t = new Type();
        t.name = "!TYPEVARIABLE";
        t.kind = TypeKind.TypeVariable;
        return t;
    }

    public static Type createSubroutineTypeVariable(String typename) {
        Type t = new Type();
        t.name = typename;
        t.kind = TypeKind.SubroutineTypeParameter;
        return t;
    }



    public static enum TypeKind {
        ClassTypeParameter,
        SubroutineTypeParameter,
        ArrayType,
        GenericTypeInstance,
        SimpleType,
        TypeVariable
    }
}
