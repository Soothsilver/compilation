package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.Type;
import compiler.nodes.declarations.Variable;

import java.util.HashSet;

public class MemberVariableExpression extends VariableExpression {
    Expression parent;

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
                expression.setErrorType();;
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
