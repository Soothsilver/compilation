package compiler.nodes.declarations;

import compiler.Compilation;

import java.util.ArrayList;

public class Type extends TypeOrTypeTemplate {
    public TypeKind kind;
    public ArrayList<Type> typeArguments;
    public boolean boundToNumeric;
    public boolean boundToReferenceType;
    public Type boundToSpecificType;
    public boolean isReferenceType;
    // public String name; <-- inherited
    public Type copy(ArrayList<Type> typeParameters) {
        if (typeParameters != null) {
            for (int i = 0; i < typeParameters.size(); i++) {
                if (typeParameters.get(i).name.equals(this.name)) {
                    return typeParameters.get(i); // TODO copy here? probably not
                }
            }
        }
        Type clone = new Type();
        clone.name = this.name;
        clone.isReferenceType = this.isReferenceType;
        clone.boundToNumeric = this.boundToNumeric;
        clone.boundToReferenceType = this.boundToReferenceType;
        clone.boundToSpecificType = this.boundToSpecificType;
        clone.kind = this.kind;
        if (this.typeArguments != null) {
            clone.typeArguments = new ArrayList<>();
            for (int i = 0; i < this.typeArguments.size(); i++) {
                clone.typeArguments.add(this.typeArguments.get(i).copy(typeParameters));
            }
        }
        return clone;
    }
    public Type copy() {
        return copy(null);
    }
    public UnificationKind getUnificationKind() {
        switch (kind) {
            case ArrayType:
            case GenericTypeInstance:
                return UnificationKind.Structured;
            case SimpleType:
            case ClassTypeParameter:
            case SubroutineTypeParameter:
                return UnificationKind.Simple;
            case TypeVariable:
                return UnificationKind.Variable;

        }
        throw new EnumConstantNotPresentException(TypeKind.class, "kind");
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
    public static Type nullType =  Type.createPredefinedType("!null");
    public static Type voidType = Type.createPredefinedType("!void");
    public static Type booleanType = Type.createPredefinedType("boolean");
    public static Type integerType = Type.createPredefinedType("integer");
    public static Type stringType = Type.createPredefinedType("string");
    public static Type characterType = Type.createPredefinedType("character");
    public static Type floatType = Type.createPredefinedType("float");

    @Override
    public String toString() {
        if (kind == TypeKind.TypeVariable) {
            return "VAR:" + name;
        } else return name;
    }

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
    public static Type createNewTypeVariable(String name) {
        Type t = new Type();
        t.name = name;
        t.kind = TypeKind.TypeVariable;
        return t;
    }
    public static Type createDebugStructure(String name) {
        Type t = new Type();
        t.name = name;
        t.kind = TypeKind.SimpleType;
        t.isReferenceType = true;
        return t;
    }

    public static Type createSubroutineTypeVariable(String typename, int line, int column) {
        Type t = new Type();
        t.name = typename;
        t.line = line;
        t.column = column;
        t.kind = TypeKind.SubroutineTypeParameter;
        return t;
    }

    @Override
    public String getFullString() {
        return "type " + name + " = structure {};";
    }

    public boolean isNull() {
        return this.equals(Type.nullType);
    }

    public boolean isIncomplete() {
        if (typeArguments != null) {
            for (Type argument : typeArguments)
                if (argument.isIncomplete()) return true;
        }
        return this.kind == TypeKind.TypeVariable && this.boundToSpecificType == null;
    }

    public Type objectify() {
        if (kind == TypeKind.TypeVariable) {
            if (this.boundToSpecificType != null) {
                return this.boundToSpecificType.objectify();
            }
        }
        if (typeArguments != null) {
            for (int i = 0 ; i < typeArguments.size(); i++) {
                typeArguments.set(i, typeArguments.get(i).objectify());
            }
        }
        return this;
    }

    public static enum UnificationKind {
        Variable,
        Simple,
        Structured
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
