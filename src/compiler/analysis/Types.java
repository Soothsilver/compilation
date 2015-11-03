package compiler.analysis;

import compiler.nodes.declarations.Type;

import java.util.ArrayList;

/**
 * A named list of types with the possibility to objectify.
 * Objectification causes all types in the list to become complete types, by recursively replacing all bound type variables within
 *   by the types they are bound to.
 */
public class Types extends ArrayList<Type> {
    /**
     * Replaces all type variables in this list by the types they represent, recursively.
     */
    public void objectify() {
        for (int i = 0; i < this.size(); i++) {
            this.set(i, this.get(i).objectify());
        }
    }
}
