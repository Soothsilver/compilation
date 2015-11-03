package compiler.nodes.declarations;

import compiler.nodes.Declarations;
import compiler.nodes.Subroutines;
import compiler.nodes.VariableDeclarations;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.ArrayList;

public abstract class TypeOrTypeTemplate extends Declaration {
    public VariableDeclarations declarations;
    public Subroutines subroutines;

    public void setDeclarations(Declarations declarations) {
        for (Declaration declaration : declarations) {
            this.declarations.add((Variable)declaration);
        }
    }
    public void setSubroutines(Subroutines subroutines) {
        this.subroutines = subroutines;
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
