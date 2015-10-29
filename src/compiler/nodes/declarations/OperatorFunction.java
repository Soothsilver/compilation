package compiler.nodes.declarations;

import compiler.Compilation;
import compiler.Parameter;
import compiler.nodes.statements.Statement;

import java.util.ArrayList;
import java.util.List;

public class OperatorFunction extends Subroutine {
    public static OperatorFunction create(
                                    String name,
                                    Type left,
                                    Type right,
                                    Type functionReturnType
                                    ) {
        OperatorFunction s = new OperatorFunction();
        s.kind = SubroutineKind.FUNCTION;
        s.line = 0;
        s.column = 0;
        s.name = name;
        s.typeParameterNames = new ArrayList<>();
        s.parameters = new ArrayList<>();
        s.parameters.add(new Parameter("firstOperand", left));
        s.parameters.add(new Parameter("secondOperand", right));
        s.returnType = functionReturnType;
        return s;
    }


    public static OperatorFunction createGeneralAssignment() {
        OperatorFunction s = new OperatorFunction();
        s.kind = SubroutineKind.FUNCTION;
        s.line = 0;
        s.column = 0;
        s.name = "=";
        s.typeParameterNames = new ArrayList<>();
        String assignmentType = "!ASSIGNMENT_TYPE_VARIABLE";
        s.typeParameterNames.add(assignmentType);
        Type standInType = Type.createSubroutineTypeVariable(assignmentType, 0,0);
        s.parameters = new ArrayList<>();
        s.parameters.add(new Parameter("lefthand", standInType));
        s.parameters.add(new Parameter("righthand", standInType));
        s.returnType = standInType;
        return s;
    }
}
