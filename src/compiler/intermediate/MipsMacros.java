package compiler.intermediate;

import compiler.analysis.Uniqueness;
import compiler.nodes.declarations.Type;

/**
 * Contains static function useful when generating MIPS assembly.
 */
public class MipsMacros {
    /**
     * Generates MIPS code that pushes the value in the specified MIPS register on top of the stack.
     *
     * @param registerName The register from where value should be extracted.
     * @return MIPS code.
     */
    public static String pushOntoStack(String registerName) {
        return "\tsub $sp,$sp,4 # Push 4 bytes onto stack\n\tsw " + registerName + ",0($sp)\n";
    }

    /**
     * Generates MIPS code that pops the value on top of the stack and stores it in the specified MIPS register.
     * @param registerName The register where the value should be stored.
     * @return MIPS code.
     */
    public static String popIntoRegister(String registerName) {
        return "\tlw " + registerName + ",0($sp)\n\taddiu $sp,$sp,4 # Pop 4 bytes from stack\n";

                /* pop would be the opposite of a push. So if you use this to push $t2:

sub $sp,$sp,4
sw $t2,($sp)
You would pop it with:

lw $t2,($sp)
addiu $sp,$sp,4*/
    }

    /**
     * Generates MIPS code that pops the specified number of integer (4-byte) values off the stack and forgets those values.
     *
     * @param count The number of items to pop from stack.
     * @return MIPS code.
     */
    public static String clearStackItems(int count) {
        return "\taddiu $sp,$sp," + 4 * count + " # Pop " + (count) + " words from stack\n";
    }
	public static String moveStackPointer(int words) {
		return "\tsub $sp,$sp," + 4 * words + " # Push " + (words) + " virtual words on stack\n";
	}
    
    /**
     * Tests if a string represents the name of a MIPS floating point register.
     */
    public static Boolean isFloatRegister(String registerName) {
    	return registerName.charAt(1) == 'f';
    }

	/**
	 * Returns MIPS code that handles the "&&" and "||" operators.
	 * @param operator "&&" or "||"
	 * @param left The left boolean operand.
	 * @param right The right boolean operand.
	 * @param saveToWhere The register where "1" or "0" should be saved.
	 * @return MIPS code.
	 */
	public static String nonShortCircuitingBooleanOperator(String operator, Operand left, Operand right, IntermediateRegister saveToWhere) {
        if (operator.equals("&&")) {
            String labelNo  = "_no"  + Uniqueness.getUniqueId();
            String labelEnd = "_end" + Uniqueness.getUniqueId();

            return
                    left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                    MipsAssembly.beqz(MipsRegisters.TEMPORARY_VALUE_0, labelNo) +
                    right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                    MipsAssembly.beqz(MipsRegisters.TEMPORARY_VALUE_0, labelNo) +
                    MipsAssembly.li(MipsRegisters.TEMPORARY_VALUE_0, 1) +
                    saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                    MipsAssembly.jmp(labelEnd) +
                    MipsAssembly.label(labelNo) +
                    MipsAssembly.li(MipsRegisters.TEMPORARY_VALUE_0, 0) +
                    saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0) +
                    MipsAssembly.label(labelEnd);

        } else {
            // ||
            return
            		left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
            		right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1) +
            		MipsAssembly.or(MipsRegisters.TEMPORARY_VALUE_0, MipsRegisters.TEMPORARY_VALUE_0, MipsRegisters.TEMPORARY_VALUE_1) +
            		saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0);
        }
    }

    /**
     * Generates MIPS code handling the "==" and "!=" operators.
     * @param operator "==" or "!="
     * @param left The left operand.
     * @param right The right operand.
     * @param leftType Type of the left operand.
     * @param rightType Type of the right operand.
     * @param saveToWhere The register where "0" or "1" should be saved.
     * @return MIPS code.
     */
    public static String equalityOperator(String operator,
			Operand left, Operand right, 
			Type leftType, Type rightType,
			IntermediateRegister saveToWhere) {
		
		// Perhaps floating point types should be handled using IEEE rules rather than by bit comparison ?
		String intro =
				left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
				right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1) +
				MipsAssembly.xor(MipsRegisters.TEMPORARY_VALUE_0,
								 MipsRegisters.TEMPORARY_VALUE_0,
								 MipsRegisters.TEMPORARY_VALUE_1);
				// $t0 = 0  => IDENTICAL
				// $t0 != 0 => NOT IDENTICAL
				
			    // == => WANT 1 IF IDENTICAL, 0 OTHERWISE
			    // != => WANT 0 IF IDENTICAL, 1 OTHERWISE
		String mainPart;
		String labelEnd = "_end" + Uniqueness.getUniqueId();
		String labelIdentical = "_identical" + Uniqueness.getUniqueId();
		if (operator.equals("==")) {
			mainPart =
					MipsAssembly.beqz(MipsRegisters.TEMPORARY_VALUE_0, labelIdentical) +
					MipsAssembly.li(MipsRegisters.TEMPORARY_VALUE_0, 0) +
					MipsAssembly.jmp(labelEnd) +
					MipsAssembly.label(labelIdentical) +
					MipsAssembly.li(MipsRegisters.TEMPORARY_VALUE_0, 1) +
					MipsAssembly.label(labelEnd);
		
		} else {
			// !=
				mainPart =
						MipsAssembly.beqz(MipsRegisters.TEMPORARY_VALUE_0, labelIdentical) +
						MipsAssembly.li(MipsRegisters.TEMPORARY_VALUE_0, 1) +
						MipsAssembly.jmp(labelEnd) +
						MipsAssembly.label(labelIdentical) +
						MipsAssembly.li(MipsRegisters.TEMPORARY_VALUE_0, 0) +
						MipsAssembly.label(labelEnd);
			
		}
		return
				intro +
				mainPart +
				saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0);
		
		
		
		
		
		
	}

	public static String loadOperands(Operand left, Operand right) {
		return left.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
			   right.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_1);
	}

    public static String stringConcatenation(Operand left, Operand right, Type leftType, Type rightType, IntermediateRegister saveToWhere) {
        String instructions = "";
        // Let's use t2-t4 for internal calculations.
        instructions += MipsMacros.stringifyTo(left, leftType, MipsRegisters.TEMPORARY_VALUE_0);
        instructions += MipsMacros.stringifyTo(right, rightType, MipsRegisters.TEMPORARY_VALUE_1);
        instructions += "\t# Concatenation of strings:\n";
        // t0 = address of string "a"
        // t1 = address of string "b"
        // t2 = length
        // t3

        throw new RuntimeException("String concatenation was not implemented.");
    }

    private static String stringifyTo(Operand left, Type leftType, String targetRegister) {
        if (leftType.equals(Type.stringType)) {
            return left.toMipsLoadIntoRegister(targetRegister);
        } else if (leftType.equals(Type.floatType)) {
            throw new RuntimeException("Floating points to string? No way I am implementing that.");
        } else if (leftType.equals(Type.characterType)) {
            throw new RuntimeException("This would be easy to implement, but I don't want to.");
        } else if (leftType.equals(Type.integerType)) {
            throw new RuntimeException("Conversion to decimal? No way.");
        }
        throw new RuntimeException("This can never happen.");
    }
}