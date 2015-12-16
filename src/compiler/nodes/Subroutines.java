package compiler.nodes;

import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;

/**
 * List of Subroutines.
 */
public class Subroutines extends ArrayList<Subroutine> {
    /**
     * Adds a new Subroutine to this list.
     */
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

    /**
     * Instantiates this, creating a specialized form based on a generic form.
     *
     * @param typeParameters Types to be replaced.
     * @param typeArguments The replacing types.
     * @return The specialized form of this object.
     */
    public Subroutines instantiate(ArrayList<Type> typeParameters, ArrayList<Type> typeArguments) {
        Subroutines ss = new Subroutines();
        for (Subroutine s : this) {
            ss.add(s.instantiate(typeParameters, typeArguments));
        }
        return ss;
    }
}
