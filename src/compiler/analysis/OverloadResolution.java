package compiler.analysis;

import compiler.Compilation;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.CallExpression;
import compiler.nodes.expressions.SubroutineGroup;

import java.util.ArrayList;
import java.util.Set;

public final class OverloadResolution {
    public static void phaseOne(CallExpression call, Compilation compilation) {
//In the first phase, evaluate call expressions like this:
//(If at any point you signal an error, set the type of the expression to the special Type.getErrorType() and stop evaluating the first phase.)
//1. Retrieve all subroutines with the callsite name. This subroutines become the "considered subroutines".
        SubroutineGroup group = call.group;
//1.1 If none exist, signal an error.
        if (group.subroutines.size() == 0) {
            compilation.semanticError("There is no subroutine with the name '" + group.name + "' at this point.", group.line, group.column);
            call.setErrorType();
            return;
        }
//2. If type arguments were specified, remove from consideration all subroutines with a different number of type parameters. Also remove from consideration all non-generic subroutines.
//2.1. If none remain, signal an error.
        if (call.typeArguments != null) {
            group.subroutines.removeIf(sbrt -> sbrt.typeParameterNames.size() != call.typeArguments.size());
            if (group.subroutines.size() == 0) {
                compilation.semanticError("No subroutine with the name '" + group.name + "' has " + call.typeArguments.size() + " type parameters.", group.line, group.column);
                call.setErrorType();
                return;
            }
        }
//2a. Remove from consideration all subroutines with a different number of parameters than actual arguments.
//2a.1. If none remain, signal an error.
        group.subroutines.removeIf(sbrt -> sbrt.parameters.size() != call.arguments.size());
        if (group.subroutines.size() == 0) {
            compilation.semanticError("No overload of the subroutine '" + group.name + "' takes " + call.arguments.size() + " arguments.");
            call.setErrorType();
            return;
        }

//3. If type arguments were specified, transform all considered subroutines into inferred subroutines. Only those will now be considered.
        ArrayList<SubroutineToken> consideredSubroutines = new ArrayList<>();
        for (Subroutine sub : group.subroutines) {
            consideredSubroutines.add(new SubroutineToken(sub, call.typeArguments != null));
        }
//4. Do the following procedure for all considered subroutines:
        for (SubroutineToken subroutine : consideredSubroutines) {
            debug("Considering " + subroutine);
//4.1.If it is a generic subroutine and type arguments were not specified, it's called an "incomplete subroutine."
//4.2 For each combination of types of its parameters, perform unification in this way:
            ArrayList<Types> typeCombinations = call.arguments.getTypeCombinations();
            for (Types actualTypes : typeCombinations) {
                debug("Matching against " + actualTypes);
                SubroutineToken subroutineBeingInferred = subroutine.copy();
//4.2.1 We will try to unify the subroutine signature, including its return type,  with the function signature where all parameters were replaced by types from the combination. Type variables of the signature are renamed so they don't conflict with the free types of arguments.
                Types formalTypes = subroutineBeingInferred.createFormalTypes();
                Type returnType = Type.createNewTypeVariable();
                actualTypes.add(returnType);
//4.2.2 Perform the unification algorithm with some cave-ats. If the unification algorithm fails, then don't consider this combination.

                if (!unify(formalTypes, actualTypes)) {
                    debug("Unification failed.");
                    continue;
                    // This type combination failed.
                }

// These are the cave-ats:
//4.2.2.1 Unifying a null with a type variable puts the constraint "must be an object" on the variable.
//4.2.2.2 Unifying a null with a structured type or a class type succeeds.
//4.2.2.3 Unifying a null with anything else fails.
//4.2.2.4 Unifying an integer with a type variable only puts the constraint "must be an integer or float" on the variable.
//4.2.2.5 Unifying a variable that is under the constraint "must be an object" can only succeed if the other part is a null, a structured type, a class or another variable that is not under the constraint "must be an integer or float".
//4.2.2.6 Unifying a variable that is under the constraint "must be an integer or float" can only succeed if the other part is an integer, a float or a type variable not under the constraint "must be an object".
//4.2.2.7 It is possible to unify a "float" in the signature with an "int" in the type, but doing so puts a "+1 badness" to the resultant inferred subroutine.
//4.2.3 For each type parameter, if it remains free, but it has the constraint "must be an integer or float", it becomes an integer. (This will cause some unexpected behavior sometimes. TODO add a test for it)
                for (Type t : subroutineBeingInferred.types) {
                    if (t.boundToSpecificType == null && t.boundToNumeric) {
                        t.boundToSpecificType = Type.integerType;
                    }
                }
//4.2.4 The unified subroutine/type combination may still contain a free type variable. That does not remove it from consideration.
//4.2.5. Create an inferred subroutine from this subroutine and this combination of types. It may still be incomplete.
                while (returnType.boundToSpecificType != null) {
                    returnType = returnType.boundToSpecificType;
                }
                debug("Unification successful, adding return type " + returnType + ".");
                call.subroutineTokens.add(subroutineBeingInferred);
//4.2.6. Add this inferred's subroutine return type to the set of return types.
                call.possibleTypes.add(returnType);
            }
        }
//5. If the list of inferred subroutines is empty, signal an error.
        if (call.subroutineTokens.isEmpty()) {
            compilation.semanticError("No subroutine with the name '" + group.name + "' accepts arguments of the given types.", group.line, group.column);
            call.setErrorType();
            return;
        }
//6. The first phase is now complete.
        return;
    }
    static class Substitution {

    }
    public static boolean unify(Type formal, Type actual) {

        // Technical.
        if (formal.kind == Type.TypeKind.TypeVariable) {
            if (formal.boundToSpecificType != null) {
                return unify(formal.boundToSpecificType, actual);
            }
        }
        if (actual.kind == Type.TypeKind.TypeVariable) {
            if (actual.boundToSpecificType != null) {
                return unify(actual.boundToSpecificType, actual);
            }
        }

        // Type variables not fully bound.
        switch (formal.kind){
            case SimpleType:
                switch (actual.kind) {
                    case SimpleType:
                        return formal.name.equals(actual.name);
                    case TypeVariable:
                        actual.boundToSpecificType = formal;
                        return true;
                    default:
                        throw new RuntimeException("Unification of this kind of type parameter is not supported.");
                }
            case TypeVariable:
                // TODO more complex unification here
                formal.boundToSpecificType = actual;
                return true;
            default:
                throw new RuntimeException("Unification of this kind of type parameter is not supported.");
        }

    }
    public static boolean unify(Types formal, Types actual)
    {
        for (int i = 0; i < formal.size(); i++) {
            Type formalType = formal.get(i);
            Type actualType = actual.get(i);
            boolean unification = unify(formalType, actualType);
            if (!unification ) {
                debug("Cannot unify " + formalType + " with " + actualType + ".");
                return false;
            }
        }
        return true;
        /*
        Type firstFormal = formal.get(0);
        Type firstActual = actual.get(0);
        Substitution firstUnification = unify(firstFormal, firstActual);
        if (firstUnification == null) return null;*/

        /*function unify(E1, E2);
    begin
        case
            both E1 and E2 are constants or the empty list:
                if E1 = E2 then return {}
                else return FAIL;
            E1 is a variable:
                if E1 occurs in E2 then return FAIL
                 else return {E2/E1}
            E2 is a variable
                if E2 occurs in E1 then FAIL
                    else return {E1/E2}
            either E1 or E2 are empty then return FAIL
            otherwise:
                begin
                    HE1 := first element of E1;
                    HE2 := first element of E2;
                    SUBS1 := unify(HE1, HE2);
                    if SUBS1 := FAIL then return FAIL;
                    TE1 := apply(SUBS1, rest of E1);
                    TE2 := apply(SUBS1, rest of E2);
                    SUBS2 := unify(TE1, TE2);
                    if SUBS2 = FAIL then return FAIL;
                         else return composition(SUBS1, SUBS2)
                end
            end
        end
        */
    }

    public static void phaseTwo(CallExpression call, Set<Type> returnTypes, Compilation compilation) {

//        In the second phase, proceed like this:
//        (If during this phase you signal an error, stop evaluating, and set the type to error.)
//        1a. If we signalled an error in the first phase, end.
        if (call.type == Type.errorType) {
            return;
        }
//        1. We receive a set of possible types.
        ArrayList<SubroutineToken> subroutineTokens = call.subroutineTokens;
//        2. Remove from consideration all inferred subroutines whose return type cannot be unified with any type given in the set of possible types. A "float" possible type is unifiable with a subroutine return type of "int" but adds +1 badness.
//        3. Remove from consideration all inferred subroutines that still have a free variable left.
//        4. If there is no subroutine left, signal an error.
//        5. If there is exactly one subroutine left, launch this phase for all of its arguments with the unification made, and sending only a single possible return type.
        if (subroutineTokens.size() == 1) {
            // TODO
            call.callee = subroutineTokens.get(0);
            call.type = call.callee.subroutine.returnType;
            // TODO
        }
//        6. If there are still at least two inferred subroutines considered, then discover if one is better than all others. A subroutine is better than another subroutine if it has less badness. If there is one best subroutine, consider it best. If not, signal an error (TODO this can be made better perhaps).

    }
    private static void debug(String line) {
        System.out.println("- " + line);
    }
}
