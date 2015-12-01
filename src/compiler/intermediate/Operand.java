package compiler.intermediate;

import compiler.nodes.declarations.Variable;
import compiler.nodes.declarations.VariableKind;

/**
 * Represents an operand of an intermediate 3-address instruction.
 */
public class Operand {
    /**
     * The operand's addressing mode.
     */
    public OperandKind kind;
    /**
     * The register referred to by this operand. This is an intermediate code register, not an MIPS register.
     * Only used by the "Register" kind.
     */
    public IntermediateRegister register;
    /**
     * The operand's 4-byte value. Not necessarily a "real" integer.
     * Only used by the "Immediate" kind.
     */
    public int integerValue;
    /**
     * The operand may refer to a string literal.
     * Only used by the "StringLiteral" kind.
     */
    public IntermediateStringLiteral intermediateStringLiteral;
    /**
     * The operand's variable.
     * Only used by the "GlobalVariable", "LocalVariable" kinds.
     */
    private Variable variable;

    /**
     * Initializes a new instance of the Operand class.
     * @param integerValue The operand's value.
     * @param kind The operand's addressing mode.
     */
    public Operand(int integerValue, OperandKind kind) {
		this.integerValue = integerValue;
		this.kind = kind;
	}

    /**
     * Initializes a new instance of the Operand class.
     * @param register The operand's intermediate register.
     * @param kind The operand's addressing mode.
     */
    public Operand(IntermediateRegister register, OperandKind kind) {
        this.register = register;
        this.kind = kind;
    }
    private Operand(Variable variable, OperandKind kind) {
        this.variable = variable;
        this.kind = kind;
    }

    /**
     * Initializes a new operand with a string literal.
     * This constructor should only be used to construct StringLiteral operands.
     * @param isl The string literal.
     * @param stringLiteral This should always be OperandKind.StringLiteral.
     */
    public Operand(IntermediateStringLiteral isl, OperandKind stringLiteral) {
        this.kind = OperandKind.StringLiteral;
        this.intermediateStringLiteral = isl;
    }

    @Override
    public String toString() {
        switch (kind){
            case Immediate:
                return Integer.toString(integerValue);
            case Register:
                return register.toString();
            case Parameter:
                return "PARAM(" + variable.index + ")";
            case LocalVariable:
                return "LOCAL(" + variable.index + ")";
            case GlobalVariable:
                return "GLOBAL(" + variable.name + ")";
            case RegisterContainsHeapAddress:
                return "HEAP(" + register + ")";
            case StringLiteral:
                return "STRING(" + intermediateStringLiteral.getLabel() + ")";

        }
        throw new EnumConstantNotPresentException(OperandKind.class, "kind");
    }



    /**
     * Creates an operand that should never be used. This is useful for example for procedures that don't return any
     * value but syntactically must return an Operand. The value '77' is unusual enough that it should arouse suspicion
     * if seen in generated code.
     *
     * The operand from this function must be equal to the nullOperand.
     *
     * @return An operand that should never be used.
     */
    public static Operand createOperandWithoutValue() {
        return nullOperand;
    }

    /**
     * The null operand is used when an expression does not return a value (for example, its type is Type.voidType type).
     */
    public static Operand nullOperand = new Operand(77, OperandKind.Immediate);

    /**
     * Creates an operand from a variable.
     * Sets the operand's kind based on the variable kind.
     * @param variable Any kind of variable.
     * @return An operand encapsulating the variable.
     */
    public static Operand createFromVariable(Variable variable) {
        switch (variable.kind) {
            case Global:
                return new Operand(variable, OperandKind.GlobalVariable);
            case Local:
                return new Operand(variable, OperandKind.LocalVariable);
            case Parameter:
                return new Operand(variable, OperandKind.Parameter);
            case Member:
                // Members require code, not only operand. They also require the parent expression.
                throw new RuntimeException("This must be handled in other ways.");
            default:
                throw new EnumConstantNotPresentException(VariableKind.class, "variable");
        }
    }

    /**
     * Generates MIPS instructions that load the value of the specified operand into this one.
     * The instructions are terminated by a newline character, if any are generated at all.
     * @param fromOperand The operand whose value should be loaded into here.
     * @return MIPS instructions.
     */
    public String toMipsAcquireFromOperand(Operand fromOperand) {
        String mipsCode = fromOperand.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0);
        switch (this.kind) {
            case StringLiteral:
            case Immediate:
                throw new RuntimeException("Values cannot be loaded into an immediate value or into a string literal.");
            case GlobalVariable:
                mipsCode += "\tsw " + MipsRegisters.TEMPORARY_VALUE_0  + "," + this.variable.name + "\n";
                break;
            case RegisterContainsHeapAddress:
                mipsCode +=  this.register.mipsSaveValueToRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                        "\tsw " +  MipsRegisters.TEMPORARY_VALUE_0 + ",(" + MipsRegisters.TEMPORARY_VALUE_1 + ")\n";
                break;
            case Parameter:
                mipsCode +=
                        "\tsw " + MipsRegisters.TEMPORARY_VALUE_0 + "," + (4 * this.variable.reverseIndex) + "($sp)\n";
                break;
            case LocalVariable:
                mipsCode +=
                        "\tsw " + MipsRegisters.TEMPORARY_VALUE_0 + "," + (-4 * (this.variable.index+1)) + "($sp)\n";
                break;
            case Register:
                mipsCode +=
                        register.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0);
                break;
            default:
                throw new RuntimeException("The operand kind " + this.kind + " was not yet implemented.");


        }
        return mipsCode;
    }
    /**
     * Generates MIPS instructions that load the value of this operand into the specified register.
     * The instructions are terminated by a newline character, if any are generated at all.
     * The generated code must not use TEMPORARY_VALUE_0 nor TEMPORARY_VALUE_1.
     * @param registerName Mnemonic for the register.
     * @return MIPS instructions.
     */
    public String toMipsLoadIntoRegister(String registerName, int stackDisplacement) {
    	if (!MipsMacros.isFloatRegister(registerName))
        switch (kind) {
            case Immediate:
                return "\tli " + registerName + "," + integerValue + "\n";
            case GlobalVariable:
                return "\tlw " + registerName + "," + variable.name + "\n";
            case Register:
                return register.mipsSaveValueToRegister(registerName);
            case RegisterContainsHeapAddress:
                return register.mipsSaveValueToRegister(registerName) +
                       "\tlw " + registerName + ",(" + registerName + ")\n";
            case Parameter:
                return "\tlw " + registerName + "," + (4*variable.reverseIndex + 4 * stackDisplacement) + "($sp)\n";
            case LocalVariable:
                return "\tlw " + registerName + "," + (-4*(variable.index+1) + 4 * stackDisplacement) + "($sp)\n";
            case StringLiteral:
                return "\tla " + registerName + "," + intermediateStringLiteral.getLabel() + "\n";
            default:
                throw new RuntimeException("This addressing mode ('" + kind + "') is not yet supported.");
        }
    	else
            switch (kind) {
            case Immediate:
                return "\tli " + MipsRegisters.TEMPORARY_VALUE_0 + "," + integerValue + "\n" +
            		"\tmtc1 " + MipsRegisters.TEMPORARY_VALUE_0 + "," + registerName + "\n";
            default:
                throw new RuntimeException("This addressing mode ('" + kind + "') for floating-point numbers is not yet supported.");
        }
    }

    public String toMipsLoadIntoRegister(String registerName) {
        return toMipsLoadIntoRegister(registerName, 0);
    }
}



