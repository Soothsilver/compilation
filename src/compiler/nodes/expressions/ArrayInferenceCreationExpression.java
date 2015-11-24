package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.analysis.OverloadResolution;
import compiler.intermediate.*;
import compiler.intermediate.instructions.AllocateInstruction;
import compiler.intermediate.instructions.BinaryOperatorInstruction;
import compiler.intermediate.instructions.Instructions;
import compiler.nodes.Parameter;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.SubroutineKind;
import compiler.nodes.declarations.Type;

import java.util.Set;

/**
 * Represents the expression of the type "[2, 3, 5.2]" that allocates an array of an inferred type.
 */
public class ArrayInferenceCreationExpression extends CallExpression {
    private Type innerType;
    private int size;

    /**
     * Creates a new ArrayInferenceCreationExpression.
     *
     * Implementation Notes:
     * Creates a new generic pseudosubroutine named "!cons" in order to be able to participate in type inference.
     * Launches phase 1 resolution only, and only for itself. Remember that this inherits from CallExpression.
     * @param expressions Expressions that will be the initial array members.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     * @return The created ArrayInferenceCreationExpression.
     */
    public static ArrayInferenceCreationExpression create(Expressions expressions, int line, int column, Compilation compilation) {
        String innerTypeName = "!I";
        Type innerType = Type.createNewTypeVariable(innerTypeName);
        Type expressionType = Type.createArray(innerType, line, column);
        Subroutine inferenceConstructor = Subroutine.createPredefined(SubroutineKind.FUNCTION, "!cons", expressionType);
        inferenceConstructor.typeParameterNames.add(innerTypeName);
        for (int index = 0; index < expressions.size(); index++) {
            inferenceConstructor.parameters.add(new Parameter("!p" + index, innerType));
        }
        ArrayInferenceCreationExpression thisExpression = new ArrayInferenceCreationExpression();
        thisExpression.arguments = expressions;
        thisExpression.kind = ExpressionKind.ArrayConstructor;
        thisExpression.line = line;
        thisExpression.column = column;
        thisExpression.size = expressions.size();
        thisExpression.group = SubroutineGroup.create(inferenceConstructor, line, column);
        OverloadResolution.phaseOne(thisExpression, compilation);
        return thisExpression;
    }

    @Override
    public String getErrorMessageTypeMismatch() {
        return "The expressions in the array creation expression do not share a common bottom type.";
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        super.propagateTypes(types, compilation);
        if (this.type != null && !this.type.equals(Type.errorType) && this.type.typeArguments != null && this.type.typeArguments.size() > 0) {
            this.innerType = this.type.typeArguments.get(0);
        }
        else {
            this.innerType = Type.errorType;
        }
    }

    @Override
    public String toString() {
        if (this.callee != null && !this.callee.types.get(0).objectify().isIncomplete()) {
            return "[I=" + this.callee.types.get(0).objectify() + ";" + this.arguments.toWithoutBracketsString() + "]";
        }
        else return "?[" + this.arguments.toWithoutBracketsString() + "]";
    }

    @Override
    public OperandWithCode generateIntermediateCode(Executable executable) {
        Instructions instructions = new Instructions();
        IntermediateRegister reference = executable.summonNewRegister();

        instructions.add(new AllocateInstruction(reference, new Operand( size, OperandKind.Immediate)));
        IntermediateRegister junkRegister = executable.summonNewRegister();
        for (int i = 0; i < arguments.size(); i++) {
            OperandWithCode eer = arguments.get(i).generateIntermediateCode(executable);
            instructions.addAll(eer.code);
            IntermediateRegister regAddressOfElement = executable.summonNewRegister();
            instructions.add(new BinaryOperatorInstruction("+", Type.integerType, Type.integerType,
                    new Operand(reference, OperandKind.Register),
                    new Operand(4* i, OperandKind.Immediate),
                    regAddressOfElement
                    ));
            instructions.add(new BinaryOperatorInstruction("=", innerType, innerType,
                    new Operand(regAddressOfElement, OperandKind.RegisterContainsHeapAddress),
                    eer.operand,
                    junkRegister
                    ));
        }
        Operand operand = new Operand(reference, OperandKind.Register);
        return new OperandWithCode(instructions, operand);
    }
}
