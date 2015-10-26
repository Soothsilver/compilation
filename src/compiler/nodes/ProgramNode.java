package compiler.nodes;

public class ProgramNode extends Node {
    public Declarations Declarations = new Declarations();
    public Subroutines Subroutines = new Subroutines();

    @Override
    public String toString() {
        return "PROGRAM[\n" + Declarations.toString() +Subroutines.toString() + "]";
    }
}
