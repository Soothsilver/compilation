package compiler.nodes;

import compiler.nodes.declarations.Type;
import compiler.nodes.declarations.Variable;

import java.util.ArrayList;

public class VariableDeclarations extends ArrayList<Variable> {
    public VariableDeclarations instantiate(ArrayList<Type> typeParameters, ArrayList<Type> typeArguments) {
        VariableDeclarations decs = new VariableDeclarations();
        for(Variable v : this) {
            decs.add(v.instantiate(typeParameters, typeArguments));
        }
        return decs;
    }
}
