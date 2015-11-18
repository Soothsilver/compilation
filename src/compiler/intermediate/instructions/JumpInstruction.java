package compiler.intermediate.instructions;

public class JumpInstruction extends Instruction {
    private LabelInstruction target;
    public JumpInstruction(LabelInstruction target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "JUMP " + target.getName();
    }

    @Override
    public String toMipsAssembler() {
        return "\tj " +target.getName() + " # Unconditional jump to " + target.getName() + "\n";
    }
}
