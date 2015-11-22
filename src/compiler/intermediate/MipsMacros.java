package compiler.intermediate;

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
}