package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.analysis.*;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;
import java.util.Set;

public class CallExpression extends Expression {
    public SubroutineGroup group;
    public Expressions arguments;
    public ArrayList<Type> typeArguments = null;
    public ArrayList<SubroutineToken> subroutineTokens = new ArrayList<>();
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
            return "UNRESOLVED-CALL";
        } else {
            Subroutine sub = callee.subroutine;
            String callstring = sub.getSignature(false, true) + "(" + arguments.toWithoutBracketsString() + ")";
            return callstring;
        }
    }
}
