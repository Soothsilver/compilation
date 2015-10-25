package compiler.nodes;

import compiler.nodes.Node;
import compiler.nodes.declarations.Subroutine;

import java.util.ArrayList;

public class Subroutines extends ArrayList<Subroutine> {
    public void addSubroutine(Subroutine subroutine) {
        this.add(subroutine);
    }

    @Override
    public String toString() {
        String res = "";
        for (Subroutine s : this) {
            res += s.toString() + "\n";
        }
        return res;
    }
}
