package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.intermediate.*;
import compiler.intermediate.instructions.AllocateInstruction;
import compiler.intermediate.instructions.Instructions;
import compiler.nodes.declarations.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the array creation expression "new TYPE[ARRAY_LENGTH]".
 */
public class ArrayConstructorExpression extends Expression {
    protected Type innerType;
    protected Expression size;

    /**
     * Initializes a new ArrayConstructorExpression. Launches phase 2 resolution for the size expression, forcing it
     * to Type.integerType. Creates a new array type and sets it as the type of this expression.
     * @param innerType Type of the elements of this array.
     * @param size Length of the array.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
    public ArrayConstructorExpression(Type innerType, Expression size, int line, int column, Compilation compilation) {
        this.line = line;
        this.column = column;
        this.size = size;
        this.innerType = innerType;
        this.type = Type.createArray(innerType, line, column);
        this.size.propagateTypes(new HashSet<>(Arrays.asList(Type.integerType)), compilation);
        this.possibleTypes.add(this.type);
        this.kind = ExpressionKind.ArrayConstructor;
    }


    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        if (!types.contains(this.type)) {
            compilation.semanticError("An array of '" + this.innerType + "' cannot be converted to any of the following types: " + types, line, column);
        }
    }

    @Override
    public String toString() {
        return "new " + innerType + "[" + size + "]";
    }

    @Override
    public OperandWithCode generateIntermediateCode(Executable executable) {
        IntermediateRegister referenceRegister = executable.summonNewRegister();

        Instructions instructions = new Instructions();
        OperandWithCode sizeResult = size.generateIntermediateCode(executable);
        instructions.addAll(sizeResult.code);
        instructions.add(new AllocateInstruction(referenceRegister, sizeResult.operand));

        Operand operand = new Operand(referenceRegister, OperandKind.Register);
        return new OperandWithCode(instructions, operand);
    }
}
