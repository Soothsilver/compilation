package compiler.nodes.declarations;

import compiler.Compilation;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Type extends TypeOrTypeTemplate {
    public TypeKind kind;
    public ArrayList<Type> typeArguments;
    public boolean boundToNumeric;
    public boolean boundToReferenceType;
    public Type boundToSpecificType;
    public boolean isReferenceType;

    public boolean canBeNulled() {
        return isReferenceType || kind == TypeKind.ClassTypeParameter || kind == TypeKind.SubroutineTypeParameter;
    }

    protected Type(String name, int line, int column) {
        super(name, line, column);
    }

    // public String name; <-- inherited
    public Type copy(ArrayList<Type> typeParameters) {
        if (typeParameters != null) {
            for (int i = 0; i < typeParameters.size(); i++) {
                if (typeParameters.get(i).name.equals(this.name)) {
                    return typeParameters.get(i); // TODO copy here? probably not
                }
            }
        }
        Type clone = new Type(this.name, this.line, this.column);
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
        return (Type) type;
    }


    /**
     * BASIC TYPES
     */
    public static Type errorType = Type.createPredefinedType("!error");
    public static Type nullType = Type.createPredefinedType("!null");
    public static Type voidType = Type.createPredefinedType("!void");
    public static Type booleanType = Type.createPredefinedType("boolean");
    public static Type integerType = Type.createPredefinedType("integer");
    public static Type stringType = Type.createPredefinedType("string");
    public static Type characterType = Type.createPredefinedType("character");
    public static Type floatType = Type.createPredefinedType("float");

    @Override
    public String toString() {
        if (kind == TypeKind.TypeVariable) {
            if (boundToSpecificType != null)
                return "VAR:" + name + "(BOUND)";
            else if (boundToNumeric)
                return "VAR:" + name + "(INT)";
            else if (boundToReferenceType)
                return "VAR:" + name + "(REF)";
            else
                return "VAR:" + name;
        } else if (typeArguments != null) {
            return name + "[[" + typeArguments.stream().map(t -> t.toString()).collect(Collectors.joining(",")) + "]]";
        } else return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!( obj instanceof Type )) return false;
        Type other = (Type)obj;
        if (!name.equals(other.name)) return false;
        if (typeArguments == null && other.typeArguments != null) return false;
        if (typeArguments != null) {
            for(int i = 0; i < typeArguments.size(); i++) {
                if (!typeArguments.get(i).equals(other.typeArguments.get(i))) return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static Type getBooleanType() {
        return Type.booleanType;
    }

    public static Type createPredefinedType(String name) {
        Type t = new Type(name, -1, -1);
        t.kind = TypeKind.SimpleType;
        return t;
    }
    public static Type createClass(String name, int line, int column) {
        Type t = new Type(name, line, column);
        t.kind = TypeKind.SimpleType;
        // TODO (elsewhere) make a note in the rapport that we are using named types only
        return t;
    }

    public static Type createNewTypeVariable(String name) {
        Type t = new Type(name, -1, -1);
        t.kind = TypeKind.TypeVariable;
        return t;
    }

    public static Type createDebugStructure(String name) {
        Type t = new Type(name, -1, -1);
        t.kind = TypeKind.SimpleType;
        t.isReferenceType = true;
        return t;
    }

    public static Type createSubroutineTypeVariable(String typename, int line, int column) {
        Type t = new Type(typename, line, column);
        t.kind = TypeKind.SubroutineTypeParameter;
        return t;
    }
    public static Type createClassTypeVariable(String typename, int line, int column) {
        Type t = new Type(typename, line, column);
        t.kind = TypeKind.ClassTypeParameter;
        return t;
    }
    public static Type instantiateTemplate(String name, ArrayList<Type> typeArguments, int line, int column, Compilation compilation) {
        TypeOrTypeTemplate templateMaybe = compilation.environment.findType(name);
        if (templateMaybe == null) {
            compilation.semanticError("The type template '" + name + "' could not be found.", line, column);
            return Type.errorType;
        }
        if (!(templateMaybe instanceof TypeTemplate)) {
            compilation.semanticError("The non-generic type '" + name + "' cannot be used with type arguments.", line, column);
            return Type.errorType;
        }
        TypeTemplate template = (TypeTemplate)templateMaybe;
        if (template.typeParameters.size() != typeArguments.size()) {
            compilation.semanticError("The generic type '" + template.name + "' requires " + template.typeParameters.size() + " type arguments.", line, column);
            return Type.errorType;
        }
        Type t = new Type(name, line, column);
        t.isReferenceType = true;
        t.kind = TypeKind.GenericTypeInstance;
        t.typeArguments = typeArguments;
        return t;
    }
    public static Type createArray(Type inner, int line, int column) {
        Type tArray = new Type("!array", line, column);
        tArray.kind = TypeKind.ArrayType;
        tArray.typeArguments = new ArrayList<>();
        tArray.typeArguments.add(inner);
        return tArray;
        // TODO (elsewhere) unifying should work differently for method calls (int-to-float) and types (exactitude)
    }

    @Override
    public String getFullString() {
        return "type " + name + " = class {\n"
                + declarations.stream().map(decl->decl.getFullString()).collect(Collectors.joining(","))
                + subroutines
                + "};";
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
            for (int i = 0; i < typeArguments.size(); i++) {
                typeArguments.set(i, typeArguments.get(i).objectify());
            }
        }
        return this;
    }

    public enum UnificationKind {
        Variable,
        Simple,
        Structured
    }

    public enum TypeKind {
        ClassTypeParameter,
        SubroutineTypeParameter,
        ArrayType,
        GenericTypeInstance,
        SimpleType,
        TypeVariable
    }
}
