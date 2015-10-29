package compiler.analysis;

import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents either an inferred subroutine or a subroutine in the process of being inferred.
 * An inferred subroutine is a subroutine with associated type arguments and badness.
 */
public class SubroutineToken {
    /**
     * The underlying subroutine. This field is read only.
     */
    public Subroutine subroutine;
    public ArrayList<Type> types; // Type arguments
    public boolean inferred;
    public int badness = 0;
    public Types formalTypes;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof  SubroutineToken)) return false;
        SubroutineToken second = (SubroutineToken)obj;
        if (!subroutine.equals(second.subroutine)) return false;
        if (types.size() != second.types.size()) return false;
        for (int i = 0; i < types.size(); i++) {
            if (!types.get(i).equals(second.types.get(i))) {
                return false;
            }
            // TODO MAYBE FORMAL TYPES, TOO?
        }
        return true;
    }

    @Override
    public String toString() {
        return subroutine.getSignature(false, false) +  (types == null ? "" : "[" + types.stream().map(tp -> tp.toString()).collect(Collectors.joining(","))+"]");
    }

    public SubroutineToken copy()
    {
        SubroutineToken stoken = new SubroutineToken(subroutine, inferred);
        stoken.types = this.types; // NO COPY INTENDED.
        stoken.badness = this.badness;
        return stoken;
    }
    public SubroutineToken(Subroutine subroutine, boolean inferred) {
        this.subroutine = subroutine;
        this.inferred = inferred;
    }

    public Types createFormalTypes() {
        if (types == null) {
            types = new ArrayList<>();
            for (int i = 0; i < subroutine.typeParameterNames.size(); i++) {
                types.add(Type.createNewTypeVariable(subroutine.typeParameterNames.get(i)));
            }
        }
        Types formalTypes = new Types();
        // TODO improve this
        for (int i = 0; i < subroutine.parameters.size(); i++) {
            formalTypes.add(subroutine.parameters.get(i).type.copy(types)); // TODO COPY here?
        }
        formalTypes.add(subroutine.returnType.copy(types)); // TODO copy here?

        return formalTypes;
    }
}

