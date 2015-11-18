package compiler.intermediate;

/**
 * Contains static function useful when generating MIPS assembly.
 */
public class MipsMacros {
    public static String pushOntoStack(String registerName) {
        return "\tsub $sp,$sp,4 # Push 4 bytes onto stack\n\tsw " + registerName + ",0($sp)\n";
    }

    public static String popIntoRegister(String registerName) {
        return "\tlw " + registerName + ",0($sp)\n\taddiu $sp,$sp,4 # Pop 4 bytes from stack\n";

                /* pop would be the opposite of a push. So if you use this to push $t2:

sub $sp,$sp,4
sw $t2,($sp)
You would pop it with:

lw $t2,($sp)
addiu $sp,$sp,4*/
    }

    public static String clearStackItems(int count) {
        return "\taddiu $sp,$sp," + 4*count + " # Pop " + (count) + " words from stack\n";
    }
}
