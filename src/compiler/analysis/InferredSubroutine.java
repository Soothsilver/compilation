package compiler.analysis;

import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;

public class InferredSubroutine {
    public Subroutine subroutine;
    public ArrayList<Type> types;
    public InferredSubroutine(Subroutine subroutine) {
        this.subroutine = subroutine;
    }
}
