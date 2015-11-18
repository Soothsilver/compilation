package compiler.nodes.declarations;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a predefined subroutine that maps to a MIPS system call.
 */
public class SystemCall extends Subroutine {
    /**
     * The MIPS sytem call code for this system call.
     */
    public int systemCallCode;

    /**
     * Initializes a new SystemCall declaration. Parameters must be declared separately.
     */
    public SystemCall(
            String name,
            int systemCallCode,
            Type returnType) {
        super(name, -1, -1);
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
        this.typeParameterNames = new ArrayList<>();
        this.kind = Objects.equals(returnType, Type.voidType)
                ? SubroutineKind.PROCEDURE : SubroutineKind.FUNCTION;
        this.systemCallCode = systemCallCode;
    }
}
