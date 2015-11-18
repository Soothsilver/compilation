package compiler.nodes.expressions;

/**
 * Indicates whether the unary operator precedes or succeeds its operand.
 */
public enum UnaryExpressionSide {
    /**
     * The operator precedes the operand.
     */
    Prefix,
    /**
     * The operator succeeds the operand.
     */
    Postfix
}
