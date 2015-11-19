package compiler.intermediate;

import compiler.nodes.declarations.SystemCall;
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
     * The register referred to by this operand.
     * Only used by the "Register" kind.
     */
    public IntermediateRegister register;
    /**
     * The operand's 4-byte value. Not necessarily a "real" integer.
     * Only used by the "Immediate" kind.
     */
    public int integerValue;
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
     * @param register The operand's register.
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

    @Override
    public String toString() {
        switch (kind){
            case Immediate:
                return Integer.toString(integerValue);
            case Register:
                return register.toString();
            case LocalVariable:
                return "LOCAL(" + variable.index + ")";
            case GlobalVariable:
                return "GLOBAL(" + variable.name + ")";
            case RegisterContainsHeapAddress:
                return "HEAP(" + register + ")";
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
        System.out.println(variable.kind);
        switch (variable.kind) {
            case Global:
                return new Operand(variable, OperandKind.GlobalVariable);
            case Local:
                return new Operand(variable, OperandKind.LocalVariable);
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
        switch (fromOperand.kind) {
            case Immediate:
                switch(this.kind) {
                    case Immediate:
                        throw new RuntimeException("Immediate operand is not an l-value.");
                    case GlobalVariable:
                         return
                                "\tli " + MipsRegisters.TEMPORARY_VALUE_0 + "," + fromOperand.integerValue + "\n" +
                                "\tsw " + MipsRegisters.TEMPORARY_VALUE_0  + "," + this.variable.name + "\n";
                    case RegisterContainsHeapAddress:
                        return
                                "\tli " + MipsRegisters.TEMPORARY_VALUE_0 + "," + fromOperand.integerValue + "\n" +
                                this.register.mipsSaveValueToRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                                "\tsw " +  MipsRegisters.TEMPORARY_VALUE_0 + ",(" + MipsRegisters.TEMPORARY_VALUE_1 + ")\n"
                                ;
                }
                break;
            case Register:
                switch (this.kind) {
                    case RegisterContainsHeapAddress:
                        return
                               fromOperand.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                               this.register.mipsSaveValueToRegister(MipsRegisters.TEMPORARY_VALUE_1) +
                                "\tsw " +  MipsRegisters.TEMPORARY_VALUE_0 + ",(" + MipsRegisters.TEMPORARY_VALUE_1 + ")\n"
                                ;
                    case GlobalVariable:
                        return
                                fromOperand.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                                "\tsw " + MipsRegisters.TEMPORARY_VALUE_0  + "," + this.variable.name + "\n";
                }
        }
        return "\t!!ERROR(Operand " + fromOperand + " was not saved to " + this + ".)\n";
    }
    /**
     * Generates MIPS instructions that load the value of this operand into the specified register.
     * The instructions are terminated by a newline character, if any are generated at all.
     * The generated code must not use TEMPORARY_VALUE_0 nor TEMPORARY_VALUE_1.
     * @param registerName Mnemonic for the register.
     * @return MIPS instructions.
     */
    public String toMipsLoadIntoRegister(String registerName) {
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
            default:
                return "\t!!ERROR This addressing mode is not yet supported.!!\n";
        }
    }
}



