package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.analysis.OverloadResolution;
import compiler.analysis.SubroutineToken;
import compiler.analysis.Uniqueness;
import compiler.intermediate.Executable;
import compiler.intermediate.ExpressionEvaluationResult;
import compiler.intermediate.Operand;
import compiler.intermediate.OperandKind;
import compiler.intermediate.instructions.CallInstruction;
import compiler.intermediate.instructions.Instruction;
import compiler.intermediate.instructions.Instructions;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.SystemCall;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a subroutine call expression or an operator expression.
 * A call of a member subroutine of a type is still a CallExpression.
 */
public class CallExpression extends Expression {
    /**
     * List of all subroutines with the name (or type and name) of the call.
     * Some subroutines may be removed from this list at the beginning of overload resolution, but none can ever be added.
     */
    public SubroutineGroup group;
    /**
     * List of all arguments of the call. For binary operators, the two operands are arguments, even for assignments.
     * This field is read only, but the arguments' properties can be modified.
     */
    public Expressions arguments;
    /**
     * If the call did not specify type arguments, then this is null. Otherwise, it is the list of explicitly specified type arguments.
     */
    public ArrayList<Type> typeArguments = null;
    /**
     * List of all currently considered inferred subroutines. This list is modified often during overload resolution/
     */
    public ArrayList<SubroutineToken> subroutineTokens = new ArrayList<>();
    /**
     * At the end of a successful overload resolution, this is the type-complete inferred subroutine that will be used
     * by this call. Until then, this is null. After an unsuccessful resolution, this remains null.
     */
    public SubroutineToken callee;


    /**
     * Initializes a new CallExpression. Launches phase 1 overload resolution that can easily fail. If it fails,
     * the constructor still succeeds but an error is triggered and the error type is set.
     * @param subroutineGroup Subroutine group to search for applicable subroutines.
     * @param typeArguments Type arguments, if any were specified, otherwise null.
     * @param arguments Actual arguments, if any were specified, otherwise an empty list or null.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     * @return A CallExpression, possibly with error type set.
     */
    public static CallExpression create(
            SubroutineGroup subroutineGroup,
            ArrayList<Type> typeArguments,
            Expressions arguments,
            int line,
            int column,
            Compilation compilation
    ) {
        CallExpression ex = new CallExpression();
        ex.group = subroutineGroup;
        ex.line = line;
        ex.arguments = arguments;
        ex.typeArguments = typeArguments;
        if (arguments == null) { ex.arguments = new Expressions(); }
        ex.kind = ExpressionKind.Subroutine;
        ex.column = column;
        OverloadResolution.phaseOne(ex, compilation);
        return ex;
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        OverloadResolution.phaseTwo(this, types, compilation);
    }

    @Override
    public String toString() {
        if (callee == null) {
            return group + "?(" + arguments.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
        } else {
            // TODO display what object is the subroutine called on
            Subroutine sub = callee.subroutine;
            return sub.getSignature(false, true, callee.types, true) + "(" + arguments.toWithoutBracketsString() + ")";
        }
    }

    /**
     * Performs magic. Notice the line "first.equals(second)".
     */
    public void removeRedundantSubroutineTokens() {
        // TODO: This function appears to contain a bug. Shouldn't we remove the subroutine token with greater badness?
        for (int i = 0; i < subroutineTokens.size(); i++) {
            SubroutineToken first = subroutineTokens.get(i);
            for (int j = i + 1; j < subroutineTokens.size(); j++) {
                SubroutineToken second = subroutineTokens.get(j);
                if (first.equals(second)) {
                    if (first.getBadness() > second.getBadness()) {
                        first.setBadness(second.getBadness());
                    }
                    subroutineTokens.remove(j);
                    j--;
                    // Continue with next cycle iteration.
                }
            }
        }
    }

    /**
     * Generates an error message with a text such as "No considered subroutine with the name 'name' accepts arguments of the given types.".
     * This method should be overridden in operator functions that can generate better error messages.
     * @return Error message text.
     */
    public String getErrorMessageTypeMismatch() {
        return "No considered subroutine with the name '" + group.name + "' accepts arguments of the given types.";
    }


    @Override
    public ExpressionEvaluationResult generateIntermediateCode(Executable executable) {
        Instructions instructions = new Instructions();
        ArrayList<Operand> operands = new ArrayList<>();
        for (Expression argument : arguments) {
            ExpressionEvaluationResult eer = argument.generateIntermediateCode(executable);
            operands.add(eer.operand);
            instructions.addAll(eer.code);
        }
        int returnRegisterIndex = Uniqueness.getUniqueId();
        CallInstruction call = new CallInstruction(callee, operands, returnRegisterIndex);
        instructions.add(call);
        if (type.equals(Type.voidType)) {
            return new ExpressionEvaluationResult(instructions, Operand.createOperandWithoutValue());
        } else {
            return new ExpressionEvaluationResult(instructions, new Operand(returnRegisterIndex, OperandKind.Register));
        }
    }
}
