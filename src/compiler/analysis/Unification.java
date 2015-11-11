package compiler.analysis;

import compiler.nodes.declarations.Type;

import static compiler.analysis.OverloadResolution.debug;

public class Unification {

    /**
     * Attempts to unify the types of a formal parameter(s) with the types of an actual argument(s). This method is called for subroutine signatures and for type arguments of structured types.
     * @param formal The types of the formal parameter.
     * @param actual The types of the actual argument.
     * @param badness A boxed integer that will be increased if this unification increases badness.
     * @return True if the unification succeeds.
     */
    public static boolean unify(Types formal, Types actual, IntegerHolder badness, boolean exactly) {
        return unify(formal, actual, badness, exactly, new UnificationSubstitution());
    }

    /**
     * Attempts to unify the types of a formal parameter(s) with the types of an actual argument(s). This method is called for subroutine signatures and for type arguments of structured types.
     * @param formal The types of the formal parameter.
     * @param actual The types of the actual argument.
     * @param badness A boxed integer that will be increased if this unification increases badness.
     * @return True if the unification succeeds.
     */
    private static boolean unify(Types formal, Types actual, IntegerHolder badness, boolean exactly, UnificationSubstitution substitution) {
        OverloadResolution.debug_level++;
        for (int i = 0; i < formal.size(); i++) {
            Type formalType = formal.get(i);
            Type actualType = actual.get(i);
            if (actualType.objectify().equals(Type.voidType) && i != formal.size()-1) {
                debug("An argument is !void, cannot unify.");
                OverloadResolution.debug_level--;
                return false;
            }
            boolean unification = unify(formalType, actualType, badness, exactly, substitution);
            if (!unification ) {
                debug("Cannot unify " + formalType + " with " + actualType + ".");
                OverloadResolution.debug_level--;
                return false;
            }
        }
        OverloadResolution.debug_level--;
        return true;
    }
    /**
     * Attempts to unify the type of a formal parameter with the type of an actual argument. The actual argument may be a type variable as well, especially in the case of the return type.
     * @param formal The type of the formal parameter.
     * @param actual The type of the actual argument.
     * @param badness A boxed integer that will be increased if this unification increases badness.
     * @return True if the unification succeeds.
     */
    public static boolean unify(Type formal, Type actual, IntegerHolder badness, boolean exactly, UnificationSubstitution substitution) {
        // Technical.
        if (formal.kind == Type.TypeKind.TypeVariable) {
            if (formal.boundToSpecificType != null) {
                return unify(formal.boundToSpecificType, actual, badness, exactly, substitution);
            }
        }
        if (actual.kind == Type.TypeKind.TypeVariable) {
            if (actual.boundToSpecificType != null) {
                return unify(formal, actual.boundToSpecificType, badness, exactly, substitution);
            }
        }

        // Case tree
        Type.UnificationKind firstKind = formal.getUnificationKind();
        Type.UnificationKind secondKind = actual.getUnificationKind();
        debug("Unifying " + formal + " with " + actual + " (" + firstKind + "," + secondKind + ")");
        switch (firstKind) {
            case Simple:
                switch (secondKind) {
                    case Simple:
                        return unifySimpleTypes(formal, actual, badness, exactly, substitution);
                    case Structured:
                        if (formal.isNull()) return true;
                        return false;
                    case Variable:
                        //noinspection VariableNotUsedInsideIf
                        if (actual.boundToSpecificType != null) {
                            if (!actual.objectify().equals(formal.objectify())) {
                                return false;
                            }
                        }
                        if (actual.boundToReferenceType && !formal.isReferenceType) {
                            return false;
                        }
                        if (actual.boundToNumeric && !formal.equals(Type.integerType) && !formal.equals(Type.floatType)) {
                            return false;
                        }

                        actual.boundToSpecificType = formal;
                        return true;
                    // TODO check this is correct
                    // return unifyVariableWithSomething(actual, formal);
                }
                break;
            case Structured:
                switch (secondKind) {
                    case Simple:
                        if (actual.isNull()) return true;
                        return false;
                    case Structured:
                        if (!formal.name.equals(actual.name)) return false;
                        if (formal.typeArguments.size() != actual.typeArguments.size()) return false;
                        for (int i =0 ; i < formal.typeArguments.size(); i++) {
                            boolean unifyDaughters = unify(formal.typeArguments.get(i), actual.typeArguments.get(i), badness, true, substitution);
                            if (!unifyDaughters) return false;
                        }
                        return true;
                    case Variable:
                        return unifyVariableWithSomething(actual, formal, badness, exactly, substitution);
                }
                break;
            case Variable:
                return unifyVariableWithSomething(formal, actual, badness, exactly, substitution);
        }
        throw new RuntimeException("Unification of this kind of type parameter is not supported.");
    }

    /**
     * Attempts to unify the type of a formal parameter with the type of an actual argument. It is guaranteed that both types are simple, i.e. not type variables and not structured.
     * @param formal The type of the formal parameter.
     * @param actual The type of the actual argument.
     * @param badness A boxed integer that will be increased if this unification increases badness.
     * @return True if the unification succeeds.
     */
    private static boolean unifySimpleTypes(Type formal, Type actual, IntegerHolder badness, boolean exactly, UnificationSubstitution substitution) {
        if (formal.name.equals(actual.name)) return true;
        if (formal.name.equals(Type.floatType.name) && actual.name.equals(Type.integerType.name)) {
            badness.raise();
            return true;
        }
        // 4.2.2.2 Unifying a null with a structured type or a class type succeeds.
        if (formal.isReferenceType && actual.equals(Type.nullType)) {
            return true;
        }
        //4.2.2.3 Unifying a null with anything except structured type, class type or class variable fails.
        return false;
    }

    /**
     * Attempts to unify a type variable with another type. The other type could also be a type variable. It is not guaranteed that the first type is from formal parameters and the second is from arguments. This operation will never increase badness.
     * @param variable The variable type.
     * @param type The other type.
     * @return True if the unification succeeds.
     */
    private static boolean unifyVariableWithSomething(Type variable, Type type, IntegerHolder badness, boolean exactly, UnificationSubstitution substitution) {
        //4.2.2.5 Unifying a variable that is under the constraint "must be an object" can only succeed if the other part is a null, a structured type, a class or another variable that is not under the constraint "must be an integer or float". TODO
        //4.2.2.6 Unifying a variable that is under the constraint "must be an integer or float" can only succeed if the other part is an integer, a float or a type variable not under the constraint "must be an object".
        //4.2.2.7 It is possible to unify a "float" in the signature with an "int" in the type, but doing so puts a "+1 badness" to the resultant inferred subroutine.
        switch (type.getUnificationKind()) {
            case Variable:
                if (variable.boundToNumeric && type.boundToReferenceType) return false;
                if (variable.boundToReferenceType && type.boundToNumeric) return false;
                if (variable.boundToNumeric) type.boundToNumeric = true;
                if (variable.boundToReferenceType) type.boundToReferenceType = true;
                variable.boundToSpecificType = type;
                return true;
            case Simple:
                //4.2.2.5 Unifying a variable that is under the constraint "must be an object" can only succeed if the other part is ... another variable that is not under the constraint "must be an integer or float".
                if (!type.equals(Type.integerType) && !type.equals(Type.floatType) && variable.boundToNumeric) return false;
                //4.2.2.4 Unifying an integer with a type variable only puts the constraint "must be an integer or float" on the variable.
                if (type.equals(Type.integerType)) {
                    if (variable.boundToReferenceType) return false;

                    if (exactly) {
                        variable.boundToSpecificType = Type.integerType;
                    } else {
                        variable.boundToNumeric = true;
                        variable.integerBindCount++;
                    }
                    return true;
                }
                // 4.2.2.1 Unifying a null with a type variable puts the constraint "must be an object" on the variable.
                if (type.isNull()) {
                    variable.boundToReferenceType = true;
                    return true;
                }
                if (type.equals(Type.floatType)) {
                    OverloadResolution.debug("Raising badness due to previously bound numerics.");
                    for (int i = 0; i < variable.integerBindCount; i++) {
                        badness.raise();
                    }
                }
                variable.boundToSpecificType = type;
                return true;
            case Structured:
                // TODO test for cycles
                variable.boundToSpecificType = type;
                return true;
        }
        throw new RuntimeException("This unification type is impossible.");
    }
}
