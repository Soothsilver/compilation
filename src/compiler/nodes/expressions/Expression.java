package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.ErrorReporter;
import compiler.nodes.Node;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.literals.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Expression extends Node {
    /**
     * This list is only used prior to the call of propagateTypes.
     * After that, it is no longer useful.
     */
    public Set<Type> possibleTypes = new HashSet<Type>();
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



    public static Expression createFromConstant(Integer data, int line, int column, Compilation compilation) {
        return new IntegerLiteralExpression(data, line, column, compilation);
    }
    public static Expression createFromConstant(Character data, int line, int column, Compilation compilation) {
        return null;
    }
    public static Expression createFromConstant(Float data, int line, int column, Compilation compilation) {
        return null;
    }
    public static Expression createFromConstant(String data, int line, int column, Compilation compilation) {
        return new StringLiteralExpression(data, line, column, compilation);
    }

    public abstract void propagateTypes(Set<Type> types, Compilation compilation);

    public void setErrorType() {
        this.type = Type.errorType;
        this.possibleTypes.clear();
        this.possibleTypes.add(this.type);
    }
}
