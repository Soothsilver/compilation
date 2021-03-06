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
    public final Subroutine subroutine;
    /**
     * List of type arguments for the subroutine. This will be "null" if the underlying subroutine is not generic/
     */
    public ArrayList<Type> types;
    /**
     * Overloading-relevant badness of this subroutine token.
     */
    private int badness = 0;
    public int getBadness() {
        return badness;
    }
    /**
     * Types of the subroutine's arguments, from first to last. The final type in this array is the return type.
     */
    public Types formalTypes;

    @SuppressWarnings("NonFinalFieldReferenceInEquals")
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
    public int hashCode() {
        return 0; // We don't need no efficiency.
    }

    @Override
    public String toString() {
        return subroutine.getSignature(false, false) +  (types == null ? "" : "[" + types.stream().map(Type::toString).collect(Collectors.joining(","))+"]")
                + (badness != 0 ? ": badness " + badness : "");
    }

    /**
     * Creates a shallow copy of this object.
     */
    public SubroutineToken copy()
    {
        SubroutineToken subroutineToken = new SubroutineToken(subroutine);
        subroutineToken.types = this.types; // NO COPY INTENDED.
        subroutineToken.badness = this.badness;
        return subroutineToken;
    }

    /**
     * Instantiates a new SubroutineToken from a subroutine.
     */
    public SubroutineToken(Subroutine subroutine) {
        this.subroutine = subroutine;
    }

    /**
     * This should be improved.
     */
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

    /**
     * Replaces all bound type variables in "types" and "formalTypes" by their actual types.
     */
    public void objectifySelf() {
        if (types != null) {
            for (int i = 0; i < types.size(); i++) {
                types.set(i, types.get(i).objectify());
            }
        }
        for (int i = 0; i < formalTypes.size(); i++) {
            formalTypes.set(i, formalTypes.get(i).objectify());
        }
    }

    public void setBadness(int badness) {
        this.badness = badness;
    }
}

