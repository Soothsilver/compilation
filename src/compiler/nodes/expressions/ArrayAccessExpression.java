package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.intermediate.*;
import compiler.intermediate.instructions.BinaryOperatorInstruction;
import compiler.intermediate.instructions.Instructions;
import compiler.nodes.declarations.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the expression "ARRAY[INDEX]".
 */
public class ArrayAccessExpression extends Expression {
    /**
     * The expression that evaluates to an array.
     */
    public Expression array;
    /**
     * The expression that evaluates to an integer and serves as the index to the array.
     */
    public Expression index;

    /**
     * Creates a new ArrayAccessExpression.
     * Launches phase 2 resolution for both the array and the index expressions. If the array expression does not
     * evaluate into an array, triggers a semantic error. Sets this expression's type directly.
     * @param array The array expression.
     * @param index The integer-type index expression.
     * @param compilation The compilation object.
     * @return The created ArrayAccessExpression.
     */
    public static ArrayAccessExpression create(Expression array, Expression index, Compilation compilation) {
        ArrayAccessExpression expression = new ArrayAccessExpression();
        expression.line = array.line;
        expression.column = array.column;
        expression.array = array;
        expression.index = index;
        expression.kind = ExpressionKind.ArrayAccess;
        // We would prefer to get only arrays here, but the Overload Resolution Process does not permit it. So we have to accept everything.
        expression.array.propagateTypes(null, compilation);
        if (expression.array.type.kind == Type.TypeKind.ArrayType) {
            index.propagateTypes(new HashSet<>(Arrays.asList(Type.integerType)), compilation);
            expression.type = expression.array.type.typeArguments.get(0);
            expression.possibleTypes.add(expression.type);
            return  expression;
        } else {
            compilation.semanticError("The expression '" + array + "' does not evaluate into an array.", array.line, array.column);
            expression.type = Type.errorType;
            return  expression;
        }
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        if (types == null) return;
        for (Type t : types) {
            if (this.type.convertibleTo(t)) return;
        }
        compilation.semanticError("An array member of type '" + this.type + "' cannot be converted to any of the following types: " + types, line, column);
    }

    @Override
    public String toString() {
        return array + "[" + index + "]";
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public OperandWithCode generateIntermediateCode(Executable executable) {
        Instructions instructions = new Instructions();
        IntermediateRegister register = executable.summonNewRegister();
        IntermediateRegister shiftedIndex = executable.summonNewRegister();

        OperandWithCode eerArray = array.generateIntermediateCode(executable);
        OperandWithCode eerIndex = index.generateIntermediateCode(executable);

        instructions.addAll(eerArray.code);
        instructions.addAll(eerIndex.code);
        
        instructions.add(new BinaryOperatorInstruction("+", Type.integerType, Type.integerType,
                eerIndex.operand,
                new Operand(1, OperandKind.Immediate),
                shiftedIndex
                ));
        
        IntermediateRegister multipliedByFour = executable.summonNewRegister();
        instructions.add(new BinaryOperatorInstruction("*", Type.integerType, Type.integerType, new Operand(4, OperandKind.Immediate), new Operand(shiftedIndex, OperandKind.Register), multipliedByFour));
        instructions.add(new BinaryOperatorInstruction("+", Type.integerType, Type.integerType, eerArray.operand, new Operand(multipliedByFour, OperandKind.Register), register));
        Operand operand = new Operand(register, OperandKind.RegisterContainsHeapAddress);
        return new OperandWithCode(instructions, operand);
    }
}
