package compiler.analysis;

import compiler.Compilation;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.*;

import java.util.*;
import java.util.stream.Collectors;

import static compiler.analysis.Unification.unify;

/**
 * This class groups together the algorithms used during type inference and overload resolution.
 */
@SuppressWarnings("OverlyComplexClass")
public final class OverloadResolution {
    /**
     * Performs phase one of the Overload Resolution Process (see the txt file in this folder).
     *
     * @param call        The call expression to disambiguate.
     * @param compilation The compilation process class.
     */
    public static void phaseOne(CallExpression call, Compilation compilation) {
//In the first phase, evaluate call expressions like this:
//(If at any point you signal an error, set the type of the expression to the special Type.getErrorType() and stop evaluating the first phase.)
//1. Retrieve all subroutines with the callsite name. This subroutines become the "considered subroutines".
        debug_level = 1;
        SubroutineGroup group = call.group;
        debug("Overload resolution begins for " + call + ", subroutine group has " + group.subroutines.size() + " candidates.");
//1.1 If none exist, signal an error.
        if (group.subroutines.isEmpty()) {
            compilation.semanticError("There is no subroutine with the name '" + group + "' at this point.", group.line, group.column);
            call.setErrorType();
            return;
        }
//2. If type arguments were specified, remove from consideration all subroutines with a different number of type parameters. Also remove from consideration all non-generic subroutines.
//2.1. If none remain, signal an error.
        if (call.typeArguments != null) {
            group.subroutines.removeIf(sbrt -> sbrt.typeParameterNames.size() != call.typeArguments.size());
            if (group.subroutines.isEmpty()) {
                compilation.semanticError("No subroutine with the name '" + group + "' has " + call.typeArguments.size() + " type parameters.", group.line, group.column);
                call.setErrorType();
                return;
            }
        }
//2a. Remove from consideration all subroutines with a different number of parameters than actual arguments.
//2a.1. If none remain, signal an error.
        group.subroutines.removeIf(sbrt -> sbrt.parameters.size() != call.arguments.size());
        if (group.subroutines.size() == 0) {
            compilation.semanticError("No overload of the subroutine '" + group + "' takes " + call.arguments.size() + " arguments.", call.line, call.column);
            call.setErrorType();
            return;
        }

        ArrayList<SubroutineToken> consideredSubroutines = new ArrayList<>();
        for (Subroutine sub : group.subroutines) {
            SubroutineToken sToken = new SubroutineToken(sub);
            consideredSubroutines.add(sToken);
        }
//4. Do the following procedure for all considered subroutines:
        debug_level++;
        for (SubroutineToken subroutine : consideredSubroutines) {
            debug("Considering " + subroutine + "...");
//4.1.If it is a generic subroutine and type arguments were not specified, it's called an "incomplete subroutine."
//4.2 For each combination of types of its parameters, perform unification in this way:
            Iterable<Types> typeCombinations = call.arguments.getTypeCombinations();
            debug_level++;
            for (Types actualTypes : typeCombinations) {
                Type returnType = Type.createNewTypeVariable("!RET" + Uniqueness.getUniqueId());
                actualTypes.add(returnType);
                debug("Matching against " + actualTypes);
                SubroutineToken subroutineBeingInferred = subroutine.copy();
//4.2.1 We will try to unify the subroutine signature, including its return type,  with the function signature where all parameters were replaced by types from the combination. Type variables of the signature are renamed so they don't conflict with the free types of arguments.
                Types formalTypes = subroutineBeingInferred.createFormalTypes();
                if (call.typeArguments != null) {
                    for (int i = 0; i < subroutineBeingInferred.types.size(); i++) {
                        subroutineBeingInferred.types.get(i).boundToSpecificType = call.typeArguments.get(i);
                        debug("Type parameter " + i + " (" + subroutineBeingInferred.types.get(i) + ") is " + call.typeArguments.get(i));
                    }
                }
                subroutineBeingInferred.formalTypes = formalTypes;
                debug("Formal types are: " + formalTypes);
//4.2.2 Perform the unification algorithm with some caveats. If the unification algorithm fails, then don't consider this combination.
                IntegerHolder badness = new IntegerHolder();
                if (!unify(formalTypes, actualTypes, badness, false)) {
                    debug("Unification failed.");
                    continue;
                    // This type combination failed.
                }
                badness.multiplyByTwo();
                if (subroutineBeingInferred.types != null && subroutineBeingInferred.types.size() > 0) {
                    badness.raise();
                    // A generic subroutine is worse than a non-generic subroutine.
                    // But fewer conversions still take precedence over this.
                }
                subroutineBeingInferred.setBadness(badness.getValue());

//4.2.4 The unified subroutine/type combination may still contain a free type variable. That does not remove it from consideration.
//4.2.5. Create an inferred subroutine from this subroutine and this combination of types. It may still be incomplete.
                returnType = returnType.objectify();
                subroutineBeingInferred.objectifySelf();
                debug("Unification successful, subroutine has types " + formalTypes + " (return type " + returnType + ").");
                call.subroutineTokens.add(subroutineBeingInferred);
//4.2.6. Add this inferred subroutine's return type to the set of return types.
                call.possibleTypes.add(returnType);
            }
            debug_level--;
        }
        debug_level = 1;

        call.removeRedundantSubroutineTokens();
//5. If the list of inferred subroutines is empty, signal an error.
        if (call.subroutineTokens.isEmpty()) {
            compilation.semanticError(call.getErrorMessageTypeMismatch(), group.line, group.column);
            call.setErrorType();
            //noinspection UnnecessaryReturnStatement
            return;
        }
//6. The first phase is now complete.
    }

    /**
     * The second phase of Overload Resolution makes sure that the the expression (and all expressions under this in the expression tree) has exactly one type.
     *
     * @param call        The call expression that needs to be type-resolved.
     * @param returnTypes The types that the expression can legally take, as determined by its parent.
     * @param compilation The compilation object.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static void phaseTwo(CallExpression call, Set<Type> returnTypes, Compilation compilation) {
//        In the second phase, proceed like this:
//        (If during this phase you signal an error, stop evaluating, and set the type to error.)
//        1. We receive a set of possible types. These are guaranteed to be complete.
//        1a. If we signalled an error in the first phase, end.
        if (Objects.equals(call.type, Type.errorType)) {
            return;
        }
        debug_level = 1;
        debug("Phase 2 begins for " + call + ", there are " + call.subroutineTokens.size() + " legal tokens.");
        ArrayList<SubroutineToken> legalSubroutines = call.subroutineTokens;
        ArrayList<HashSet<Type>> argumentTypes = new ArrayList<>();
        for (int i = 0; i < call.arguments.size(); i++) {
            argumentTypes.add(new HashSet<>());
        }
        debug_level++;
        for (SubroutineToken token : legalSubroutines) {
            debug(token.toString());
        }
        debug_level--;
        debug ("Possible return types are: " + returnTypes);
        //        2. Remove from consideration all inferred subroutines whose return type cannot be unified with any type given in the set of possible types. A "float" possible type is unifiable with a subroutine return type of "int" but adds +1 badness. The unifications remain.
        debug_level++;
            for (int sti = legalSubroutines.size() - 1; sti >= 0; sti--) {
                boolean unifiableAtLeastInOneWay = false;
                boolean unifiableWithoutBadnessIncrease = false;
                SubroutineToken st = legalSubroutines.get(sti);
                if (returnTypes != null) {
                    for (Type rType : returnTypes) {
                        // Test unification
                        IntegerHolder isBad = new IntegerHolder();
                        UnificationSubstitution substitution = new UnificationSubstitution();
                        boolean unifiable = unify(rType, st.formalTypes.get(st.formalTypes.size() - 1), isBad, false, substitution);
                        if (unifiable) {
                            unifiableAtLeastInOneWay = true;
                            for (int i = 0; i < call.arguments.size(); i++) {
                                argumentTypes.get(i).add(st.formalTypes.get(i).objectify());
                            }
                            unifiableWithoutBadnessIncrease = isBad.getValue() == 0;
                            substitution.undoAll();
                            if (unifiableWithoutBadnessIncrease) {
                                break;
                            }
                        } else {
                            substitution.undoAll();
                        }
                    }
                } else {
                    for (int i = 0; i < call.arguments.size(); i++) {
                        argumentTypes.get(i).add(st.formalTypes.get(i).objectify());
                    }
                    unifiableAtLeastInOneWay = true;
                }
                if (!unifiableWithoutBadnessIncrease) {
                    st.setBadness(st.getBadness() + 2);
                }
                if (!unifiableAtLeastInOneWay) {
                    debug("Removing " + st + " from consideration.");
                    legalSubroutines.remove(sti);
                }
            }

        debug_level--;
        if (legalSubroutines.size() == 0) {
            compilation.semanticError("No function with the name '" + call.group.name + "' has a return type unifiable with one of the possible return types: " + returnTypes, call.line, call.column);
            call.setErrorType();
            return;
        }

        // 2a.  Objectify all variables in formal types.
        for (SubroutineToken token : legalSubroutines) {
            token.formalTypes.objectify();
            for (int i = 0; i < token.types.size(); i++) {
                token.types.set(i, token.types.get(i).objectify());
            }
        }
        // 2aa. Numerically-bound type variables become integers.
        for (SubroutineToken token : legalSubroutines) {
            for (Type t : token.types) {
                Type tFinalBind = t.objectify();
                if (tFinalBind.boundToSpecificType == null && tFinalBind.boundToNumeric) {
                    debug("Binding type parameter " + tFinalBind + " to integer.");
                    tFinalBind.boundToSpecificType = Type.integerType;
                }
            }
            for (Type t : token.formalTypes) {
                Type tFinalBind = t.objectify();
                if (tFinalBind.boundToSpecificType == null && tFinalBind.boundToNumeric) {
                    debug("Binding type variable " + tFinalBind + " to integer.");
                    tFinalBind.boundToSpecificType = Type.integerType;
                }
            }
        }
        // Objectify all formal types
        for (SubroutineToken token : legalSubroutines) {
            for (int i = 0; i < token.formalTypes.size(); i++) {
                token.formalTypes.set(i, token.formalTypes.get(i).objectify());
            }
            for (int i = 0; i < token.types.size(); i++) {
                token.types.set(i, token.types.get(i).objectify());
            }
        }

//      3. Remove from consideration all inferred subroutines that still have a free variable left.
        legalSubroutines.removeIf(sbrt -> {
            for (Type formalType : sbrt.formalTypes) {
                if (formalType.isIncomplete()) {
                    debug(sbrt + " will be removed, because it has an incomplete type: " + formalType);
                    return true;
                }
            }
            return false;
        });

//      4. If there is no subroutine left, signal an error.
        if (legalSubroutines.size() == 0) {
            compilation.semanticError("Type inference failed for '" + call.group.name + "' because some type variables remain free. You may need to specify type arguments directly.", call.line, call.column);
            call.setErrorType();
            return;
        }


        debug("Phase 2: We are propagating from among " + legalSubroutines.size() + " subroutines.");
        int old_level = debug_level;
//        5. If there is exactly one subroutine left, launch this phase for all of its arguments with the unification made, and sending only a single possible return type.
//        6. If there are still at least two inferred subroutines considered, send their combined types for each argument to the parents.
        Types types = new Types();
        for (int i = 0; i < call.arguments.size(); i++) {
            HashSet<Type> hashSet = new HashSet<>();
            for (Type t : argumentTypes.get(i) ) {
                hashSet.add(t.objectify());
            }
            // A hack for assignments:
            if (
                    call.kind == ExpressionKind.Assignment &&
                        hashSet.contains(Type.integerType) &&
                        hashSet.contains(Type.floatType)
                    ) {
                hashSet.clear();
                hashSet.add(Type.integerType);
            }


            call.arguments.get(i).propagateTypes(hashSet, compilation);
            types.add(call.arguments.get(i).type);
        }
        Type returnType = Type.createNewTypeVariable("!PHASE" + Uniqueness.getUniqueId());
        types.add(returnType);
//        6a. After that, with the specific types from parents, discover if one is better than all others. A subroutine is better than another subroutine if it has less badness. If there is one best subroutine, consider it best. If not, signal an error.

        debug_level = old_level;
        debug("Phase 3 begins for " + call + ": We are selecting from among " + legalSubroutines.size() + " subroutines.");
        debug_level++;
        for (SubroutineToken consideredSubroutine : legalSubroutines) {
            debug(consideredSubroutine.toString());
        }
        debug_level--;

        int bestBadness = Integer.MAX_VALUE;
        SubroutineToken bestSubroutine = null;

        nextSubroutine:
        for (SubroutineToken consideredSubroutine : legalSubroutines) {
            for (int i = 0; i < call.arguments.size(); i++) {
                if (!types.get(i).convertibleTo(consideredSubroutine.formalTypes.get(i))) {
                    debug("Removing " + consideredSubroutine + " from consideration because " + types.get(i) + " is not convertible to " + consideredSubroutine.formalTypes.get(i));
                    continue nextSubroutine;
                }
            }
            if (consideredSubroutine.getBadness() < bestBadness) {
                bestBadness = consideredSubroutine.getBadness();
                bestSubroutine = consideredSubroutine;
            } else if (consideredSubroutine.getBadness() == bestBadness) {
                bestSubroutine = null;
            }
        }
        // TODO Were all removed?
        final int finalBestBadness = bestBadness;
        if (bestSubroutine == null) {
            legalSubroutines.removeIf(sbrt -> sbrt.getBadness() > finalBestBadness);
            compilation.semanticError("The call is ambiguous between the following subroutines: " + legalSubroutines.stream().map(sbtk -> "'" + sbtk.subroutine.getSignature(false, false) + "'").collect(Collectors.joining(",")) + ".", call.line, call.column);
            call.setErrorType();
        } else {
            call.callee = bestSubroutine;
            call.type = bestSubroutine.formalTypes.get(bestSubroutine.formalTypes.size() - 1).objectify();
            for (int i = 0; i < call.arguments.size(); i++) {
                Expression argument = call.arguments.get(i);
                Type formalType = bestSubroutine.formalTypes.get(i).objectify();
                Type actualType = argument.type.objectify();
                if (Objects.equals(actualType.name, Type.integerType.name) && Objects.equals(formalType.name, Type.floatType.name)) {
                    call.arguments.set(i, new IntegerToFloatExpression(argument));
                    if (i == 0 && call.kind == ExpressionKind.Assignment) {
                        compilation.semanticError("Cannot implicitly convert from float to integer.", call.line, call.column);
                        call.setErrorType();
                        return;
                    }
                }
            }
        }
        debug_level--;
//        6b. End.
    }



    // Utilities
    /**
     * Prints a line of debugging information on standard output.
     * Prepends a number of spaces to the line equal to the number "debug_level".
     *
     * @param line The line to print out.
     */
    @SuppressWarnings("UnusedParameters")
    public static void debug(String line) {
        /*
        for (int i = 0; i < debug_level; i++) {
            System.out.print("-");
        }
        System.out.println(" " + line);
        */
    }

    /**
     * The number of spaces that should be prepended to each debugging message.
     */
    public static int debug_level;

}
