package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.intermediate.OperandWithCode;
import compiler.intermediate.Executable;
import compiler.intermediate.Operand;
import compiler.intermediate.OperandKind;
import compiler.nodes.Node;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.literals.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an expression in the abstract syntax tree.
 */
public abstract class Expression extends Node {
    /**
     * This list is only used prior to the call of propagateTypes.
     * After that, it is no longer useful.
     */
    public Set<Type> possibleTypes = new HashSet<>();
    /**
     * Some expressions are only valid in certain context. For example,
     * an expression of the kind 'arithmetic expression' cannot be used as a statement.
     */
    public ExpressionKind kind;
    /**
     * An expression's type CAN be null until propagateTypes is called.
     * After the resolution of propagateTypes, its type MUST be set.
     */
    public Type type;



    protected Expression() {   }
    protected Expression(int line, int column) {
        super(line, column);
    }


    // These five functions create new literal expression nodes. They are used only from the production "constant" in the CUP file.
    /**
     * Creates a new literal expression by delegating the responsibility to the appropriate LiteralExpression class.
     */
    public static Expression createFromConstant(Integer data, int line, int column, Compilation compilation) {
        return new IntegerLiteralExpression(data, line, column, compilation);
    }
    /**
     * Creates a new literal expression by delegating the responsibility to the appropriate LiteralExpression class.
     */
    public static Expression createFromConstant(Character data, int line, int column, Compilation compilation) {
        return new CharacterLiteralExpression(data, line, column, compilation);
    }
    /**
     * Creates a new literal expression by delegating the responsibility to the appropriate LiteralExpression class.
     */
    public static Expression createFromConstant(Float data, int line, int column, Compilation compilation) {
        return new FloatLiteralExpression(data, line, column, compilation);
    }
    /**
     * Creates a new literal expression by delegating the responsibility to the appropriate LiteralExpression class.
     */
    public static Expression createFromConstant(String data, int line, int column, Compilation compilation) {
        return new StringLiteralExpression(data, line, column, compilation);
    }
    /**
     * Creates a new literal expression by delegating the responsibility to the appropriate LiteralExpression class.
     */
    public static Expression createFromConstant(Boolean data, int line, int column, Compilation compilation) {
        return new BooleanLiteralExpression(data, line, column, compilation);
    }

    /**
     * Starts the second phase of overload resolution for this expression.
     * @param types The set of types that are permitted for this expression. All of these types are guaranteed to be complete and not contain any type variables. If this is null, it means that there is no limitation on type by the parent (such as in an ExpressionStatement).
     * @param compilation The compilation object.
     */
    public abstract void propagateTypes(Set<Type> types, Compilation compilation);

    /**
     * Sets the real type and the only possible type in possibleTypes of this expression to "!error". Use this method when an attempt
     * to evaluate the type of this expression fails (report a semantic error yourself).
     */
    public void setErrorType() {
        this.type = Type.errorType;
        this.possibleTypes.clear();
        this.possibleTypes.add(this.type);
    }

    /**
     * Indicates whether this expression is an l-value.
     * @return Is this expression an l-value>
     */
    public boolean isAssignable() {
        return false; // Override this in subclasses.
    }


    /**
     * Generates intermediate code that will find the result value of this expression and save it somewhere.
     * This function returns both the code and identification on where the result value was stored.
     *
     * This method should be overridden in all expressions.
     *
     * @param executable The Executable object.
     * @return Intermediate code, and return value access information.
     */
    public OperandWithCode generateIntermediateCode(Executable executable) {
    	return new OperandWithCode(new ArrayList<>(), new Operand(77, OperandKind.Immediate));
    }
    
}
