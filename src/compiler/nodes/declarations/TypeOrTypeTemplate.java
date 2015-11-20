package compiler.nodes.declarations;

import compiler.nodes.Declarations;
import compiler.nodes.Subroutines;
import compiler.nodes.VariableDeclarations;

/**
 * An abstract class that is subclassed by Type and TypeTemplate. Objects of this type are stored in the "type namespace"
 * in the symbol tables.
 */
public abstract class TypeOrTypeTemplate extends Declaration {
    /**
     * Member variables of this type or type template.
     */
    public VariableDeclarations declarations = new VariableDeclarations();
    /**
     * Member subroutines (perhaps generic) of this type or type template.
     */
    public Subroutines subroutines = new Subroutines();

    /**
     * Adds the declarations in argument to this type's declarations.
     * The caller's responsibility is to ensure that all of the declarations are variables.
     * The variables' kind is set to "VariableKind.Member". The variables' index is set.
     *
     * @param declarations Declarations to be added.
     */
    public void setDeclarations(Declarations declarations) {
        if (!this.declarations.isEmpty()) {
            throw new RuntimeException("setDeclaration was already called previously.");
        }
        int i =0;
        for (Declaration declaration : declarations) {
            ((Variable)declaration).kind = VariableKind.Member;
            ((Variable)declaration).owner = this;
            ((Variable)declaration).index = i;
            this.declarations.add((Variable)declaration);
            i++;
        }
    }
    public void setSubroutines(Subroutines subroutines) {
        this.subroutines = subroutines;
        for (Subroutine subroutine : this.subroutines) {
            subroutine.owner = this;
        }
    }

    protected TypeOrTypeTemplate(String name, int line, int column) {
        super(name, line, column);
        declarations = new VariableDeclarations();
        subroutines = new Subroutines();
    }

    @Override
    public String toString() {
        return name;
    }
}
