package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.analysis.*;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;
import java.util.HashSet;
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
            return group + "?(" + arguments.stream().map(arg -> arg.toString()).collect(Collectors.joining(",")) + ")";
        } else {
            Subroutine sub = callee.subroutine;
            // TODO also display owner
            String callstring =  sub.getSignature(false, true, callee.types) + "(" + arguments.toWithoutBracketsString() + ")";
            return callstring;
        }
    }

    public void removeRedundantSubroutineTokens() {
        for (int i = 0; i < subroutineTokens.size(); i++) {
            SubroutineToken first = subroutineTokens.get(i);
            for (int j = i + 1; j < subroutineTokens.size(); j++) {
                SubroutineToken second = subroutineTokens.get(j);
                if (first.equals(second)) {
                    if (first.badness > second.badness) {
                        first.badness = second.badness;
                    }
                    subroutineTokens.remove(j);
                    j--;
                    continue;
                }
            }
        }
    }

    public String getErrorMessageTypeMismatch() {
        return "No considered subroutine with the name '" + group.name + "' accepts arguments of the given types.";
    }
}
