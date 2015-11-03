package compiler.nodes;

import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;

public class Subroutines extends ArrayList<Subroutine> {
    public void addSubroutine(Subroutine subroutine) {
        this.add(subroutine);
    }

    @Override
    public String toString() {
        String res = "";
        for (Subroutine subroutine : this) {
            res += subroutine.toString() + "\n";
        }
        return res;
    }

    public Subroutines instantiate(ArrayList<Type> typeParameters, ArrayList<Type> typeArguments) {
        Subroutines ss = new Subroutines();
        for (Subroutine s : this) {
            ss.add(s.instantiate(typeParameters, typeArguments));
        }
        return ss;
    }
}
