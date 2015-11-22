package compiler.nodes.declarations;

import compiler.Compilation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a type.
 * There are several ways to create a type:
 * - By declaring a new non-generic type using the "type" keywords.
 * - Some types are predefined, such as "integer".
 * - Type parameters of classes and subroutines become types at their interior.
 * - By constructing a type from a type template using a constructor.
 *
 * Notably, type templates are not types, but type variables are types. However, type variables should only occur while
 * the overload resolution process is in progress. Before and after that point, no type variables should exist.
 */
public class Type extends TypeOrTypeTemplate {
    public TypeKind kind;
    public ArrayList<Type> typeArguments;
    /**
     * If this is a type variable and it is inferred that it must be an integer or a float, then this is true.
     */
    public boolean boundToNumeric;
    /**
     * If this is a type variable and it is inferred that it must be a reference type (because it is compatible with
     * null), then this is true.
     */
    public boolean boundToReferenceType;
    /**
     * If unification bound this type variable to a type, this field indicates where. If this field is non-null, then
     * the fields "boundToNumeric" and "boundToReferenceType" have no meaning.
     */
    public Type boundToSpecificType;
    /**
     * Indicates whether this is a reference type.
     * Arrays, structures and classes are reference types.
     */
    public boolean isReferenceType;
    /**
     * The number of times this type variable was bound to an integer while it was still only boundToNumeric and not yet
     * float. This is important for badness calculation. The first assignment (the one that causes it to be bound to numeric)
     * also counts.
     */
    public int integerBindCount;

    /**
     * Gets a value that indicates whether the literal "null" can be converted to an object of this type.
     */
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
                    return typeParameters.get(i);
                    // TODO copy here? probably not
                    // TODO this is strange?
                }
            }
        }
        Type clone = new Type(this.name, this.line, this.column);
        clone.isReferenceType = this.isReferenceType;
        clone.boundToNumeric = this.boundToNumeric;
        clone.boundToReferenceType = this.boundToReferenceType;
        clone.boundToSpecificType = this.boundToSpecificType;
        clone.kind = this.kind;
        clone.declarations = this.declarations;
        clone.subroutines = this.subroutines;
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

    /**
     * Searches the environment symbol table for a type and returns it. Triggers a semantic error if the type does not
     * exist or if the type is generic.
     * @param identifier Identifier of the type to find.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     * @return The found type.
     */
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


    // Predefined Types:
    public static Type errorType = Type.createPredefinedType("!error");
    public static Type nullType = Type.createPredefinedType("!null");
    public static Type voidType = Type.createPredefinedType("!void");
    public static Type booleanType = Type.createPredefinedType("boolean");
    public static Type integerType = Type.createPredefinedType("integer");
    public static Type stringType = Type.createPredefinedType("string");
    public static Type characterType = Type.createPredefinedType("character");
    public static Type floatType = Type.createPredefinedType("float");

    @SuppressWarnings("VariableNotUsedInsideIf")
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
            return name + "[[" + typeArguments.stream().map(Type::toString).collect(Collectors.joining(",")) + "]]";
        } else return name;
    }

    @SuppressWarnings("NonFinalFieldReferenceInEquals")
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



    private static Type createPredefinedType(String name) {
        Type t = new Type(name, -1, -1);
        t.kind = TypeKind.SimpleType;
        return t;
    }
    public static Type createClass(String name, int line, int column) {
        Type t = new Type(name, line, column);
        t.kind = TypeKind.SimpleType;
        t.isReferenceType = true;
        return t;
    }

    /**
     * Creates a new type variable with the specified name.
     * @param name Name of the type variable.
     * @return The type variable.
     */
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

    /**
     * Creates a type of the given name and sets its kind as SubroutineTypeParameter. This does not create a type variable and cannot be unified with other types.
     * @param typename Name of the type.
     * @param line Line where it is declared.
     * @param column Column where it is declared.
     * @return The type.
     */
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
        t.subroutines = template.subroutines.instantiate(template.typeParameters, typeArguments);
        for (Subroutine subroutine : t.subroutines) {
            subroutine.owner = t;
        }
        t.declarations = template.declarations.instantiate(template.typeParameters, typeArguments);
        return t;
    }
    public static Type createArray(Type inner, int line, int column) {
        Type tArray = new Type("!array", line, column);
        tArray.kind = TypeKind.ArrayType;
        tArray.typeArguments = new ArrayList<>();
        tArray.typeArguments.add(inner);
        tArray.isReferenceType = true;
        return tArray;
        // TODO (elsewhere) unifying should work differently for method calls (int-to-float) and types (exactitude)
    }


    @Override
    public String getFullString() {
        return "type " + name + " = class {\n"
                + declarations.stream().map(Variable::getFullString).collect(Collectors.joining("\n"))
                + (declarations.size() > 0 ? "\n" : "")
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

    /**
     * Returns the true type this represents.
     * @return If this type is a bound type variable, descends the bounds and returns the last type. If that type is has type arguments, those arguments are replaced by their true forms (recursively).
     */
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

    /**
     * Returns the name of this type, or a special zero-indexed type name for subroutine type variables. This is useful in determining the signatures of subroutines
     * in symbol tables where the parameter names don't matter, but their order does.
     * @param typeParameterNames Names, in order, of type parameters of the examined generic subroutine.
     * @return The altered name of this type for subroutine signature generation.
     */
    public String toSymbolTableString(List<String> typeParameterNames) {
        if (this.kind == TypeKind.SubroutineTypeParameter) {
            int index = typeParameterNames.indexOf(this.name);
            return "!T" + index;
        }
        else if (typeArguments != null && typeArguments.size() > 0) {
            return name + "[[" +  typeArguments.stream().map(targ -> targ.toSymbolTableString(typeParameterNames)).collect(Collectors.joining(","))      + "]]";
        }
        else return name;
    }

    /**
     * Indicates whether this type can be implicitly converted to the specified the type. This can be done if the types are identical,
     *  or if this type is an integer and the target type is a float.
     * @param type The target type.
     * @return Can the implicit conversion be done?
     */
    public boolean convertibleTo(Type type) {
        if (this.equals(type)) return true;
        if (this.equals(Type.integerType) && type.equals(Type.floatType)) return true;
        if (this.equals(Type.nullType) && type.isReferenceType) return true;
        return false;
    }

    public Type replaceTypes(ArrayList<Type> replaceWhat, ArrayList<Type> replaceInto) {
        for (int i = 0; i < replaceWhat.size(); i++) {
            if (replaceWhat.get(i).equals(this)) {
                return replaceInto.get(i);
            }
        }
        if (typeArguments != null) {
            Type tClone = this.copy();
            tClone.typeArguments = new ArrayList<>();
            for (Type t : this.typeArguments) {
                tClone.typeArguments.add(t.replaceTypes(replaceWhat, replaceInto));
            }
            return tClone;
        }
        return this;
    }

    /**
     * Gets the number of words this type occupies in memory.
     * Implementation Notes: This is 1*(number of local variables), because all of our types are 32 bits.
     */
    public int getSizeInWords() {
        return declarations.size();
    }

    /**
     * A utility enumeration that indicates how this type works during type unification.
     */
    public enum UnificationKind {
        /**
         * This is a type variable. It may be bound, partially bound or unbound.
         */
        Variable,
        /**
         * This is not a type variable and has no type arguments. For example, "s", "T" or "integer" are simple types.
         * (Provided T is the current class's type parameter, for example, rather than a type variable.
         */
        Simple,
        /**
         * This is not a type variable but may contain type variables. It has type arguments. For example, "List[[T]]"
         * and "List[[integer]]" are type arguments.
         */
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
