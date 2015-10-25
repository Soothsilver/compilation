package compiler.nodes;

public class ProgramNode extends Node {
    public Subroutines Subroutines = new Subroutines();

    @Override
    public String toString() {
        return "PROGRAM[\n" +Subroutines.toString() + "]";
    }
}
