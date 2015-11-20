package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.intermediate.*;
import compiler.intermediate.instructions.AllocateInstruction;
import compiler.intermediate.instructions.Instructions;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;
import java.util.Set;

/**
 * Represents an instance constructor expression, such as "new s()".
 */
public class ConstructorExpression extends Expression {
    private String typeName;

    /**
     * Initializes a new ConstructorExpression for a non-generic type. Sets the expression's type immediately.
     * Triggers a semantic error if the specified type does not exist or is generic.
     * @param typeName Identifier of the type to be created.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
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
        else if (!this.type.isReferenceType && this.type.kind != Type.TypeKind.ClassTypeParameter && this.type.kind != Type.TypeKind.SubroutineTypeParameter) {
            compilation.semanticError("The type '" + typeName + "' cannot be constructed. Only classes, arrays, structures and type parameters can be constructed.", line, column);
            this.setErrorType();
        }
    }

    /**
     * Initializes a new ConstructorExpression for a generic type. Creates a new type by instantiating the specified
     * template. Triggers an error if the instantiation fails (for example, because the template did not exist, or
     * was not a template. Sets the expression's type immediately.
     * @param typeName Identifier of the type template to be instantiated.
     * @param typeArguments Type arguments for the template.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
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
        if (types == null) {
            return;
        }
        if (!types.contains(this.type)) {
            compilation.semanticError("A constructed object of type " + this.type.name + " cannot be converted to any of the following types: " + types, line, column);
        }
    }

    @Override
    public String toString() {
        return "new " + this.type + "()";
    }

    @Override
    public OperandWithCode generateIntermediateCode(Executable executable) {
        IntermediateRegister referenceRegister = executable.summonNewRegister();

        Instructions instructions = new Instructions();
        instructions.add(new AllocateInstruction(referenceRegister, new Operand(  type.getSizeInWords()  , OperandKind.Immediate)  ));

        Operand operand = new Operand(referenceRegister, OperandKind.Register);
        return new OperandWithCode(instructions, operand);
    }
}
