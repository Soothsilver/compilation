package compiler.analysis;

import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;

public class SubroutineToken {
    public Subroutine subroutine;
    public ArrayList<Type> types; // Type arguments
    public boolean inferred;
    public int badness = 0;

    @Override
    public String toString() {
        return subroutine + (inferred ? "{inferred}" : "{inferring}");
    }

    public SubroutineToken copy()
    {
        SubroutineToken stoken = new SubroutineToken(subroutine, inferred);
        stoken.types = new ArrayList<>();
        stoken.badness = this.badness;
        return stoken;
    }
    public SubroutineToken(Subroutine subroutine, boolean inferred) {
        this.subroutine = subroutine;
        this.inferred = inferred;
    }

    public Types createFormalTypes() {
        for (int i = 0; i < subroutine.typeParameterNames.size(); i++) {
            types.add(Type.createNewTypeVariable());
        }
        Types formalTypes = new Types();
        // TODO improve this
        for (int i = 0; i < subroutine.parameters.size(); i++) {
            formalTypes.add(subroutine.parameters.get(i).type); // TODO COPY here?
        }
        formalTypes.add(subroutine.returnType); // TODO copy here?

        return formalTypes;
    }
}

