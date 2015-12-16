package compiler.nodes;

import compiler.nodes.declarations.Type;
import compiler.nodes.declarations.Variable;

import java.util.ArrayList;

/**
 * List of variables.
 */
public class VariableDeclarations extends ArrayList<Variable> {
    /**
     * Instantiates a specialized list from a generic one.
     * @param typeParameters Types to be replaced.
     * @param typeArguments Replacing types.
     * @return The new list.
     */
    public VariableDeclarations instantiate(ArrayList<Type> typeParameters, ArrayList<Type> typeArguments) {
        VariableDeclarations decs = new VariableDeclarations();
        for(Variable v : this) {
            decs.add(v.instantiate(typeParameters, typeArguments));
        }
        return decs;
    }
}
