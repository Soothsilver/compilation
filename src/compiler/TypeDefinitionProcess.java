package compiler;

import compiler.nodes.declarations.Type;
import compiler.nodes.declarations.TypeOrTypeTemplate;
import compiler.nodes.declarations.TypeParameter;
import compiler.nodes.declarations.TypeTemplate;

import java.util.ArrayList;

/**
 * Static class that handles the process of defining a new class or structure.
 * Because of some bad design (notably the syntax and the fact that TypeTemplate and Type are not the same class),
 * we needed a special class to create types and templates from CUP.
 *
 * Implementation Notes: Inside this class, in the comments, we will refer to both Type and TypeTemplate as "type"
 * (lowercase).
 */
public final class TypeDefinitionProcess {
    // Static class: Not instantiable.
    private TypeDefinitionProcess() { }


    private static String typename;
    private static int line;
    private static int column;
    private static ArrayList<TypeParameter> typeParameters;
    /**
     * The type currently being constructed.
     */
    public static TypeOrTypeTemplate currentType;

    @SuppressWarnings("UnusedParameters")
    public static void beginTypeDefinition(String name, int line, int column, Compilation compilation) {
        TypeDefinitionProcess.line = line;
        TypeDefinitionProcess.column = column;
        TypeDefinitionProcess.typename = name;
        TypeDefinitionProcess.typeParameters = null;
    }
    public static TypeOrTypeTemplate endTypeDefinition(Compilation compilation) {
        compilation.environment.leaveScope();
        return TypeDefinitionProcess.currentType;
    }
    public static void setTypeParameters(ArrayList<TypeParameter> parameters) {
        TypeDefinitionProcess.typeParameters = parameters;
    }
    @SuppressWarnings("VariableNotUsedInsideIf")
    public static void createClass(Compilation compilation) {
        // 1. Create the type
        TypeOrTypeTemplate newType;
        if (TypeDefinitionProcess.typeParameters == null) {
            newType = Type.createClass(TypeDefinitionProcess.typename, TypeDefinitionProcess.line, TypeDefinitionProcess.column);
        } else {
            newType = TypeTemplate.createTemplate(TypeDefinitionProcess.typename, TypeDefinitionProcess.line, TypeDefinitionProcess.column);
        }


        // 2. Add it to the environment
        compilation.environment.addType(newType); // This may fail, but we don't care at this point.

        // 3. Enter scope
        compilation.environment.enterScope();

        // 4. Add type parameters, if any.
        if (TypeDefinitionProcess.typeParameters != null) {
            for (TypeParameter typeParameter : TypeDefinitionProcess.typeParameters) {
                Type type = Type.createClassTypeVariable(typeParameter.name, typeParameter.line, typeParameter.column);
                compilation.environment.addType(type);
                //noinspection ConstantConditions
                ((TypeTemplate)newType).typeParameters.add(type);
            }
        }

        // 5. Set as current type.
        TypeDefinitionProcess.currentType = newType;
    }

}
