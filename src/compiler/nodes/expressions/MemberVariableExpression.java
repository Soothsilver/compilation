package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.declarations.Type;
import compiler.nodes.declarations.Variable;

import java.util.HashSet;

/**
 * Represents an expression that is the member field of a type. For example "typeInstance.memberVariable" is a MemberVariableExpression.
 */
public class MemberVariableExpression extends VariableExpression {
    Expression parent;

    /**
     * Creates a new MemberVariableExpression. Launches phase 2 resolution for the parent expression, then searches the
     * resulting type's symbol table for a variable of the given name. If it fails at any of these steps, it triggers an error
     * and returns a MemberVariableExpression with the error type only.
     * @param parent An expression that evaluates to the object owning this variable.
     * @param name Name of the member field.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     * @return The created MemberVariableExpression, perhaps with error type set.
     */
    public static MemberVariableExpression create(Expression parent, String name, int line, int column, Compilation compilation) {
        MemberVariableExpression expression = new MemberVariableExpression(line, column);
        expression.parent = parent;
        expression.name = name;
        expression.parent.propagateTypes(null, compilation);
        if (parent.type.equals(Type.errorType)) {
            expression.setErrorType();
        } else if (!parent.type.isReferenceType) {
            compilation.semanticError("The parent expression's type (" + parent.type + ") is not a reference type and cannot contain member fields.", line, column);
            expression.setErrorType();
        } else {
            Type parentType = parent.type;
            Variable variable = null;
            /* System.out.println(parentType); */
            for(Variable member : parentType.declarations) {
                /* System.out.println("Testing against: " + member.name); */
                if (member.name.equals(name))
                    variable = member;
            }
            if (variable == null) {
                compilation.semanticError("The type '" + parent.type + "' does not contain a field with the name '" + name + "'.", line, column);
                expression.setErrorType();
            } else {
                expression.variable = variable;
                expression.type = variable.getType();
                expression.possibleTypes = new HashSet<>();
                expression.possibleTypes.add(expression.type);
            }
        }
        return expression;
    }
    private MemberVariableExpression(int line, int column) {
        super(line, column);
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public String toString() {
        return parent + "." + name;
    }
}
