package compiler.intermediate.instructions;

import compiler.intermediate.IntermediateRegister;
import compiler.intermediate.MipsMacros;
import compiler.intermediate.MipsRegisters;
import compiler.intermediate.Operand;
import compiler.nodes.declarations.Type;

/**
 * Represents an intermediate code instruction that takes two operands, an operator a return register.
 */
public class BinaryOperatorInstruction extends Instruction {
    private String operator;
    private Operand left;
    private Operand right;
    private Type leftType;
    private Type rightType;
    private IntermediateRegister saveToWhere;

    /**
     * Initializes a new BinaryOperatorInstruction.
     * @param operator The operator. This could be "+", "<<" or "&&", for example.
     * @param leftOperandType The type of the left operand.
     * @param rightOperandType The type of the right operand.
     * @param leftOperand The left operand.
     * @param rightOperand The right operand.
     * @param returnRegister The intermediate register where the operation result should be saved. All binary operations have a return value.
     */
    public BinaryOperatorInstruction(String operator, Type leftOperandType, Type rightOperandType, Operand leftOperand, Operand rightOperand, IntermediateRegister returnRegister) {
        this.operator = operator;
        this.left = leftOperand;
        this.right = rightOperand;
        this.saveToWhere = returnRegister;
        this.leftType = leftOperandType;
        this.rightType = rightOperandType;
    }

    @Override
    public String toString() {
        return saveToWhere + " = (" + left + " " + operator + " " + right + ")";
    }

    @Override
    public String toMipsAssembler() {
        switch (operator) {
            case "=":
//        addSubroutine(OperatorFunction.createGeneralAssignment());
                    return left.toMipsAcquireFromOperand(right)
                            +
                           right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            +
                           saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            ;
            case "+":
//        addBinaryOperator("+", Type.integerType, Type.integerType, Type.integerType);
//        addBinaryOperator("+", Type.floatType, Type.floatType, Type.floatType);
//        addBinaryOperator("+", Type.stringType, Type.characterType, Type.stringType);
//        addBinaryOperator("+", Type.characterType, Type.stringType, Type.stringType);
//        addBinaryOperator("+", Type.stringType, Type.stringType, Type.stringType);
//        addBinaryOperator("+", Type.stringType, Type.integerType, Type.stringType);
//        addBinaryOperator("+", Type.integerType, Type.stringType, Type.stringType);
//        addBinaryOperator("+", Type.stringType, Type.floatType, Type.stringType);
//        addBinaryOperator("+", Type.floatType, Type.stringType, Type.stringType);
//        addBinaryOperator("+", Type.stringType, Type.booleanType, Type.stringType);
//        addBinaryOperator("+", Type.booleanType, Type.stringType, Type.stringType);
                if (leftType.equals(Type.integerType) && rightType.equals(Type.integerType)) {
                    return
                            left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                            right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                            "\tadd " + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_1 + "\n" +
                            saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            ;
                }
                else if (leftType.equals(Type.floatType) || rightType.equals(Type.floatType)) {
                    throw getNotImplementedException("Floating point operation... bleh.");
                }
                else if (leftType.equals(Type.stringType) || rightType.equals(Type.stringType)) {
                    return
                            MipsMacros.stringConcatenation(
                                left, right, leftType, rightType     , saveToWhere
                            );
                }
                throw new RuntimeException("This should never happen.");
            case "-":
//        addBinaryOperator("-", Type.integerType, Type.integerType, Type.integerType);
//        addBinaryOperator("-", Type.floatType, Type.floatType, Type.floatType);
                if (leftType.equals(Type.integerType) && rightType.equals(Type.integerType)) {
                    return
                            left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                                    right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                                    "\tsub " + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_1 + "\n" +
                                    saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            ;
                }
                throw getNotImplementedException("Floating point operation... bleh.");
            case "*":
//        addBinaryOperator("*", Type.integerType, Type.integerType, Type.integerType);
//        addBinaryOperator("*", Type.floatType, Type.floatType, Type.floatType);
                if (leftType.equals(Type.integerType) && rightType.equals(Type.integerType)) {
                    return
                            left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                                    right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                                    "\tmult " + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_1 + "\n" +
                                    "\tmflo " + MipsRegisters.TEMPORARY_VALUE_0 + "\n" +
                                    saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            ;
                }
                throw getNotImplementedException("Floating point operation... bleh.");
            case "/":
//        addBinaryOperator("/", Type.integerType, Type.integerType, Type.integerType);
//        addBinaryOperator("/", Type.floatType, Type.floatType, Type.floatType);
                if (leftType.equals(Type.floatType)) {
                    throw getNotImplementedException("Floating point operation... bleh.");
                }
                return MipsMacros.loadOperands(left, right) +
                        "\tdiv $t0,$t1\n" +
                        "\tmflo $t0\n" +
                        saveToWhere.mipsAcquireValueFromRegister("$t0");
            case "%":
//        addBinaryOperator("%", Type.integerType, Type.integerType, Type.integerType);
                return MipsMacros.loadOperands(left, right) +
                       "\tdiv $t0,$t1\n" +
                       "\tmfhi $t0\n" +
                        saveToWhere.mipsAcquireValueFromRegister("$t0");

            case "<":
//        addBinaryOperator("<", Type.integerType, Type.integerType, Type.booleanType);
//        addBinaryOperator("<", Type.floatType, Type.floatType, Type.booleanType);
                if (leftType.equals(Type.integerType) && rightType.equals(Type.integerType)) {
                    return
                            left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                                    right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                                    "\tslt " + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_1 + "\n" +
                                    saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            ;
                }
                throw getNotImplementedException("Floating point operation... bleh.");
            case ">":
//        addBinaryOperator(">", Type.integerType, Type.integerType, Type.booleanType);
//        addBinaryOperator(">", Type.floatType, Type.floatType, Type.booleanType);
                if (leftType.equals(Type.integerType) && rightType.equals(Type.integerType)) {
                    return
                            left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                                    right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                                    "\tsgt " + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_1 + "\n" +
                                    saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            ;
                }
                throw getNotImplementedException("Floating point operation... bleh.");
            case "<=":
//        addBinaryOperator("<=", Type.integerType, Type.integerType, Type.booleanType);
//        addBinaryOperator("<=", Type.floatType, Type.floatType, Type.booleanType);
                if (leftType.equals(Type.integerType) && rightType.equals(Type.integerType)) {
                    return
                            left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                                    right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                                    "\tsle " + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_1 + "\n" +
                                    saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            ;
                }
                throw getNotImplementedException("Floating point operation... bleh.");
            case ">=":
//        addBinaryOperator(">=", Type.integerType, Type.integerType, Type.booleanType);
//        addBinaryOperator(">=", Type.floatType, Type.floatType, Type.booleanType);
                if (leftType.equals(Type.integerType) && rightType.equals(Type.integerType)) {
                    return
                            left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                                    right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                                    "\tsge " + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_1 + "\n" +
                                    saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            ;
                }
                throw getNotImplementedException("Floating point operation... bleh.");

            case "&&":
            case "||":
//        addBinaryOperator("&&", Type.booleanType, Type.booleanType, Type.booleanType);
//        addBinaryOperator("||", Type.booleanType, Type.booleanType, Type.booleanType);
                return MipsMacros.nonShortCircuitingBooleanOperator(operator, left, right, saveToWhere);
            case "&":
            case "|":
            case "^":
            case "<<":
            case ">>":
//        addBinaryOperator("&", Type.integerType, Type.integerType, Type.integerType);
//        addBinaryOperator("|", Type.integerType, Type.integerType, Type.integerType);
//        addBinaryOperator("^", Type.integerType, Type.integerType, Type.integerType);
//        addBinaryOperator("<<", Type.integerType, Type.integerType, Type.integerType);
//        addBinaryOperator(">>", Type.integerType, Type.integerType, Type.integerType);
                throw getNotImplementedException("Bitwise operators are not implemented. Nobody uses them, anyway.");
//
            case "@":
//        addSubroutine(OperatorFunction.createConcatenate());
                throw getNotImplementedException();
            case "==":
            case "!=":
//        addSubroutine(OperatorFunction.createEquality());
//        addSubroutine(OperatorFunction.createInequality());
                return MipsMacros.equalityOperator(operator, left, right, leftType, rightType, saveToWhere);
            default:
//        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.integerType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.floatType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.stringType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.stringType, Type.floatType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.stringType, Type.characterType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.stringType, Type.stringType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.stringType, Type.booleanType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.floatType, Type.floatType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("-=", Type.integerType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("-=", Type.floatType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("-=", Type.floatType, Type.floatType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("*=", Type.integerType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("*=", Type.floatType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("*=", Type.floatType, Type.floatType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("/=", Type.integerType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("/=", Type.floatType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("/=", Type.floatType, Type.floatType));
//
//        addSubroutine(OperatorFunction.createSpecialAssignment("%=", Type.integerType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("<<=", Type.integerType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment(">>=", Type.integerType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("^=", Type.integerType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("|=", Type.integerType, Type.integerType));
//        addSubroutine(OperatorFunction.createSpecialAssignment("&=", Type.integerType, Type.integerType));
             throw getNotImplementedException("These special assignments are labor-intensive and boring.");
        }
    }

    private RuntimeException getNotImplementedException(String reason) {
        return new RuntimeException("This compiler is unable to generate code for the instruction '" + this + "'. Reason: " + reason);
    }
    private RuntimeException getNotImplementedException() {
        return new RuntimeException("We did not yet implement this instruction: '" + this + "'. We should either implement it or use the overload getNotImplementedException(String reason) and add a reason.");
    }
}
