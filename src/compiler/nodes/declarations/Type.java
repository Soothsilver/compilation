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
    public static Type errorType;
    public static Type voidType;
    public static Type booleanType;
    public static Type integerType;
    public static Type getBooleanType() {
        return booleanType;
    }
    static {
        voidType = new Type();
        voidType.name = "!void";
        booleanType = new Type();
        booleanType.name = "boolean";
        errorType = new Type();
        errorType.name = "!error";
        integerType = new Type();
        integerType.name = "integer";
    }
}
