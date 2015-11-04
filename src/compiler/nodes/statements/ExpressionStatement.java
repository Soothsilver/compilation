package compiler.nodes.statements;

import compiler.Compilation;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;
import compiler.nodes.expressions.ExpressionKind;

import java.util.Arrays;
import java.util.List;

public class ExpressionStatement extends Statement {
    public Expression expression;

    private static List<ExpressionKind> permittedKinds = Arrays.asList(
            ExpressionKind.Assignment,
            ExpressionKind.Increment,
            ExpressionKind.Subroutine,
            ExpressionKind.MemberSubroutine
    );									

    public ExpressionStatement(Expression expression, Compilation compilation) {
        this.expression = expression;
        if (expression == null) {
            compilation.semanticError("COMPILER INTERNAL ERROR. An expression was NULL.", -1,-1);
            return;
        }
        if (!permittedKinds.contains(expression.kind)) {
            compilation.semanticError("Only assignment, increment, decrement and subroutine call expressions are permitted as statements.", expression.line, expression.column);
        }
        this.expression.propagateTypes(null, compilation);
    }

    	
    @Override
    public String toString() {
        if (expression == null) {
            return "COMPILER-ERROR;";
        }
        return expression.toString() + ";";
    }
}
