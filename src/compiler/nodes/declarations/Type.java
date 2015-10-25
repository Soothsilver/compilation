package compiler.nodes.declarations;

import compiler.Compilation;

public class Type extends TypeOrTypeTemplate {

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
        return t;
    }
    static {
    }
}
