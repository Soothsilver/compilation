package compiler.analysis;

import compiler.nodes.declarations.Type;

import java.util.ArrayList;

public class Types extends ArrayList<Type> {

    public void objectify() {
        for (int i = 0; i < this.size(); i++) {
            this.set(i, this.get(i).objectify());
        }
    }
}
