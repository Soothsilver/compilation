package compiler.nodes;

import compiler.nodes.declarations.Declaration;

import java.util.ArrayList;

/**
 * This is a list of declarations that can print the declarations in a pretty way using toString.
 */
public class Declarations extends ArrayList<Declaration> {

    @Override
    public String toString() {
        String res = "";
        for (Declaration declaration : this) {
            res += declaration.getFullString() + "\n";
        }
        return res;
    }


}
