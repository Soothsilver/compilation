package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.analysis.OverloadResolution;
import compiler.nodes.Parameter;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.SubroutineKind;
import compiler.nodes.declarations.Type;

import java.util.Set;

public class ArrayInferenceCreationExpression extends CallExpression {
    private Type innerType;
    public static ArrayInferenceCreationExpression create(Expressions expressions, int line, int column, Compilation compilation) {
        String innerTypeName = "!I";
        Type innerType = Type.createNewTypeVariable(innerTypeName);
        Type expressionType = Type.createArray(innerType, line, column);
        Subroutine inferenceConstructor = Subroutine.createPredefined(SubroutineKind.FUNCTION, "!cons", expressionType);
        inferenceConstructor.typeParameterNames.add(innerTypeName);
        for (int index = 0; index < expressions.size(); index++) {
            inferenceConstructor.parameters.add(new Parameter("!p" + index, innerType));
        }
        ArrayInferenceCreationExpression thisExpression = new ArrayInferenceCreationExpression();
        thisExpression.arguments = expressions;
        thisExpression.kind = ExpressionKind.ArrayConstructor;
        thisExpression.line = line;
        thisExpression.column = column;
        thisExpression.innerType = innerType;
        thisExpression.group = SubroutineGroup.create(inferenceConstructor, line, column);
        OverloadResolution.phaseOne(thisExpression, compilation);
        return thisExpression;
    }

    @Override
    public String getErrorMessageTypeMismatch() {
        return "The expressions in the array creation expression do not share a common bottom type.";
    }

    @Override
    public void propagateTypes(Set<Type> types, Compilation compilation) {
        super.propagateTypes(types, compilation);
    }

    @Override
    public String toString() {
        if (this.callee != null && !this.callee.types.get(0).objectify().isIncomplete()) {
            return "[I=" + this.callee.types.get(0).objectify() + ";" + this.arguments.toWithoutBracketsString() + "]";
        }
        else return "?[" + this.arguments.toWithoutBracketsString() + "]";
    }
}
