package compiler.intermediate.instructions;

/**
 * Represents the intermediate code instruction "JUMP(label)" that jumps unconditionally.
 */
public class JumpInstruction extends Instruction {
    private LabelInstruction target;

    /**
     * Initializes a new JumpInstruction.
     * @param target The label we should jump to unconditionally.
     */
    public JumpInstruction(LabelInstruction target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "JUMP " + target.getName();
    }

    @Override
    public String toMipsAssembler() {
        return "\tj " +target.getName() + "\n";
    }
}
