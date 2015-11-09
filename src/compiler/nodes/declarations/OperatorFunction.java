package compiler.nodes.declarations;

import compiler.nodes.Parameter;

import java.util.ArrayList;

/**
 * Represents a predefined subroutine that represents one of the operators. For example, the operator "<<" is represented by
 * the OperatorFunction "<<(integer,integer)". For some operator, there are multiple OperatorFunctions, for example, the operator "+"
 * has very many overloads.
 */
public class OperatorFunction extends Subroutine {
    protected OperatorFunction(String name, Type returnType) {
        super(name, -1, -1);
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
        this.typeParameterNames = new ArrayList<>();
        this.kind = SubroutineKind.FUNCTION;
    }

    public static OperatorFunction create(
                                    String name,
                                    Type left,
                                    Type right,
                                    Type functionReturnType
                                    ) {
        OperatorFunction s = new OperatorFunction(name, functionReturnType);
        s.parameters.add(new Parameter("firstOperand", left));
        s.parameters.add(new Parameter("secondOperand", right));
        return s;
    }
    public static OperatorFunction create(
            String name,
            Type operand,
            Type functionReturnType
    ) {
        OperatorFunction s = new OperatorFunction(name, functionReturnType);
        s.parameters.add(new Parameter("operand", operand));
        return s;
    }


    public static OperatorFunction createGeneralAssignment() {
        String assignmentType = "!T";
        Type standInType = Type.createSubroutineTypeVariable(assignmentType, -1,-1);
        OperatorFunction s = new OperatorFunction("=", standInType);
        s.kind = SubroutineKind.FUNCTION;
        s.typeParameterNames.add(assignmentType);
        s.parameters.add(new Parameter("leftHand", standInType));
        s.parameters.add(new Parameter("rightHand", standInType));
        return s;
    }

    public static OperatorFunction createConcatenate() {
        String assignmentType = "!T";
        Type standInType = Type.createSubroutineTypeVariable(assignmentType, -1,-1);
        Type listOfStandInType = Type.createArray(standInType, -1,-1);
        OperatorFunction s = new OperatorFunction("@", listOfStandInType );
        s.typeParameterNames.add(assignmentType);
        s.parameters.add(new Parameter("leftHand", listOfStandInType));
        s.parameters.add(new Parameter("rightHand", listOfStandInType));
        return s;
    }

    public static OperatorFunction createEquality() {
        OperatorFunction s = new OperatorFunction("==", Type.booleanType);
        String assignmentType = "!T";
        s.typeParameterNames.add(assignmentType);
        Type standInType = Type.createSubroutineTypeVariable(assignmentType, -1,-1);
        s.parameters.add(new Parameter("leftHand", standInType));
        s.parameters.add(new Parameter("rightHand", standInType));
        return s;
    }

    public static OperatorFunction createInequality() {
        OperatorFunction s = new OperatorFunction("!=", Type.booleanType);
        String assignmentType = "!T";
        Type standInType = Type.createSubroutineTypeVariable(assignmentType, -1,-1);
        s.typeParameterNames.add(assignmentType);
        s.parameters.add(new Parameter("leftHand", standInType));
        s.parameters.add(new Parameter("rightHand", standInType));
        return s;
    }

    public static OperatorFunction createSpecialAssignment(String symbol, Type left, Type right) {
        OperatorFunction s = new OperatorFunction(symbol, left);
        s.parameters.add(new Parameter("leftHand", left));
        s.parameters.add(new Parameter("rightHand", right));
        return s;
    }
}
