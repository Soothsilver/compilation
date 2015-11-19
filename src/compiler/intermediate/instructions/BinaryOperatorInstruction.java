package compiler.intermediate.instructions;

import compiler.intermediate.IntermediateRegister;
import compiler.intermediate.MipsRegisters;
import compiler.intermediate.Operand;
import compiler.nodes.declarations.Type;

public class BinaryOperatorInstruction extends Instruction {
    private String operator;
    private Operand left;
    private Operand right;
    private Type leftType;
    private Type rightType;
    private IntermediateRegister saveToWhere;

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
                if (leftType.equals(Type.integerType) && rightType.equals(Type.integerType)) {
                    return left.toMipsAcquireFromOperand(right)
                            +
                           left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            +
                           saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            ;
                }

                break;
            case "+":
                if (leftType.equals(Type.integerType) && rightType.equals(Type.integerType)) {
                    return
                            left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                            right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                            "\tadd " + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_1 + "\n" +
                            saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            ;
                }
            case "*":
                if (leftType.equals(Type.integerType) && rightType.equals(Type.integerType)) {
                    return
                            left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                                    right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                                    "\tmult " + MipsRegisters.TEMPORARY_VALUE_0 + "," + MipsRegisters.TEMPORARY_VALUE_1 + "\n" +
                                    "\tmflo " + MipsRegisters.TEMPORARY_VALUE_0 + "\n" +
                                    saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0)
                            ;
                }

        }
        return "";
    }
}