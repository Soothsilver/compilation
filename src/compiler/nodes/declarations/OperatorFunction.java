package compiler.nodes.declarations;

import compiler.nodes.Parameter;

import java.util.ArrayList;

public class OperatorFunction extends Subroutine {
    protected OperatorFunction(String name, int line, int column) {
        super(name, line, column);
    }

    public static OperatorFunction create(
                                    String name,
                                    Type left,
                                    Type right,
                                    Type functionReturnType
                                    ) {
        OperatorFunction s = new OperatorFunction(name, 0, 0);
        s.kind = SubroutineKind.FUNCTION;
        s.typeParameterNames = new ArrayList<>();
        s.parameters = new ArrayList<>();
        s.parameters.add(new Parameter("firstOperand", left));
        s.parameters.add(new Parameter("secondOperand", right));
        s.returnType = functionReturnType;
        return s;
    }


    public static OperatorFunction createGeneralAssignment() {
        OperatorFunction s = new OperatorFunction("=", 0,0);
        s.kind = SubroutineKind.FUNCTION;
        s.typeParameterNames = new ArrayList<>();
        String assignmentType = "!T";
        s.typeParameterNames.add(assignmentType);
        Type standInType = Type.createSubroutineTypeVariable(assignmentType, 0,0);
        s.parameters = new ArrayList<>();
        s.parameters.add(new Parameter("lefthand", standInType));
        s.parameters.add(new Parameter("righthand", standInType));
        s.returnType = standInType;
        return s;
    }
}
