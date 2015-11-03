package compiler.nodes.expressions;

public enum ExpressionKind {
    /**
     * An identifier that resolves in a variable.
     */
    Variable,
    /**
     * A literal integer, boolean, float, string or character, or the null value.
     */
    Literal,
    /**
     * Expression of the form EXPRESSION.IDENTIFIER
     */
    MemberVariable,
    /**
     * A subroutine call.
     */
    Subroutine,
    /**
     * A subroutine call of the form EXPRESSION.IDENTIFIER()
     */
    MemberSubroutine,
    /**
     * A mathematical expression without side effects, such as "2 + 3".
     */
    ArithmeticExpression,
    /**
     * Any assignment, such as "x += 2" or "y = 3".
     */
    Assignment,
    /**
     * Any increment or decrement expression.
     */
    Increment,
    /**
     * Expression of the form "new IDENTIFIER()"
     */
    ClassConstructor,
    /**
     * Expression of the syntax production newlist, such as "[2, 3]" or "new IDENTIFIER[INTEGER]"
     */
    ArrayConstructor,
    /**
     * Expression of the form ARRAY-EXPRESSION[INTEGER].
     */
    ArrayAccess
}
