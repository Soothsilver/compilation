package compiler.nodes;

import compiler.nodes.declarations.Declaration;
import compiler.nodes.declarations.Subroutine;

import java.util.ArrayList;

public class Declarations extends ArrayList<Declaration> {

    @Override
    public String toString() {
        String res = "";
        for (Declaration s : this) {
            res += s.getFullString() + "\n";
        }
        return res;
    }
}
