package compiler.analysis;

import compiler.Compilation;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class groups together the algorithms used during type inference and overload resolution.
 */
@SuppressWarnings("OverlyComplexClass")
public final class OverloadResolution {
    /**
     * Performs phase one of the Overload Resolution Process (see the txt file in this folder).
     * @param call The call expression to disambugate.
     * @param compilation The compilation process class.
     */
    public static void phaseOne(CallExpression call, Compilation compilation) {
//In the first phase, evaluate call expressions like this:
//(If at any point you signal an error, set the type of the expression to the special Type.getErrorType() and stop evaluating the first phase.)
//1. Retrieve all subroutines with the callsite name. This subroutines become the "considered subroutines".
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
            SubroutineToken stoken = new SubroutineToken(sub, call.typeArguments != null);
            consideredSubroutines.add(stoken);
            //3. If type arguments were specified, transform all considered subroutines into inferred subroutines. Only those will now be considered.
            /* TODO clean this up
            if (call.typeArguments != null) {
                stoken.types = new ArrayList<>();
                stoken.types.addAll(call.typeArguments);
            }
            */
        }
        debug ("Core of phase one begins for " + call + " with " + consideredSubroutines.size() + " considered subroutines.");
//4. Do the following procedure for all considered subroutines:
        for (SubroutineToken subroutine : consideredSubroutines) {
            debug("Considering " + subroutine + "...");
//4.1.If it is a generic subroutine and type arguments were not specified, it's called an "incomplete subroutine."
//4.2 For each combination of types of its parameters, perform unification in this way:
            for(Expression arg : call.arguments) {
                if (arg == null) {
                    compilation.semanticError("COMPILER INTERNAL ERROR. An argument of call '" + call + "' is null.", call.line, call.column);
                    return;
                }
            }
            Iterable<Types> typeCombinations = call.arguments.getTypeCombinations();
            for (Types actualTypes : typeCombinations) {
                Type returnType = Type.createNewTypeVariable("!RET" + Uniqueness.getUniqueId());
                actualTypes.add(returnType);
                debug("Matching against " + actualTypes);
                SubroutineToken subroutineBeingInferred = subroutine.copy();
//4.2.1 We will try to unify the subroutine signature, including its return type,  with the function signature where all parameters were replaced by types from the combination. Type variables of the signature are renamed so they don't conflict with the free types of arguments.
                Types formalTypes = subroutineBeingInferred.createFormalTypes();
                if (call.typeArguments != null) {
                    for(int i =0; i < subroutineBeingInferred.types.size(); i++) {
                       subroutineBeingInferred.types.get(i).boundToSpecificType = call.typeArguments.get(i);
                        debug("Type parameter " + i + " (" + subroutineBeingInferred.types.get(i) + ") is " + call.typeArguments.get(i));
                    }
                }
                subroutineBeingInferred.formalTypes = formalTypes;
                debug ("Formal types are: " + formalTypes);
//4.2.2 Perform the unification algorithm with some cave-ats. If the unification algorithm fails, then don't consider this combination.

                IntegerHolder badness = new IntegerHolder();
                if (!unify(formalTypes, actualTypes, badness)) {
                    debug("Unification failed.");
                    continue;
                    // This type combination failed.
                }
                badness.value *= 2;
                if (subroutineBeingInferred.types != null && subroutineBeingInferred.types.size() > 0) {
                    badness.value += 1;
                    // A generic subroutine is worse than a non-generic subroutine.
                    // But fewer conversions still take precedence over this.
                }
                subroutineBeingInferred.badness = badness.value;


//4.2.3 For each type parameter, if it remains free, but it has the constraint "must be an integer or float", it becomes an integer. (This will cause some unexpected behavior sometimes. TODO add a test for it)
                for (Type t : subroutineBeingInferred.types) {
                    if (t.objectify().boundToSpecificType == null && t.objectify().boundToNumeric) {
                        t.objectify().boundToSpecificType = Type.integerType;
                    }
                }
//4.2.4 The unified subroutine/type combination may still contain a free type variable. That does not remove it from consideration.
//4.2.5. Create an inferred subroutine from this subroutine and this combination of types. It may still be incomplete.
                returnType = returnType.objectify();
                debug("Unification successful, adding return type " + returnType + ", subroutine has types " + formalTypes + ".");
                call.subroutineTokens.add(subroutineBeingInferred);
//4.2.6. Add this inferred's subroutine return type to the set of return types.
                call.possibleTypes.add(returnType);
            }
        }
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
     * Attempts to unify the type of a formal parameter with the type of an actual argument. The actual argument may be a type variable as well, especially in the case of the return type.
     * @param formal The type of the formal parameter.
     * @param actual The type of the actual argument.
     * @param badness A boxed integer that will be increased if this unification increases badness.
     * @return True if the unification suceeeds.
     */
    public static boolean unify(Type formal, Type actual, IntegerHolder badness) {
        // Technical.
        if (formal.kind == Type.TypeKind.TypeVariable) {
            if (formal.boundToSpecificType != null) {
                return unify(formal.boundToSpecificType, actual, badness);
            }
        }
        if (actual.kind == Type.TypeKind.TypeVariable) {
            if (actual.boundToSpecificType != null) {
                return unify(formal, actual.boundToSpecificType, badness);
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
                        return unifySimpleTypes(formal, actual, badness);
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
                            boolean unifyDaughters = unify(formal.typeArguments.get(i), actual.typeArguments.get(i), badness);
                            if (!unifyDaughters) return false;
                        }
                        return true;
                    case Variable:
                        return unifyVariableWithSomething(actual, formal);
                }
                break;
            case Variable:
                return unifyVariableWithSomething(formal, actual);
        }
        throw new RuntimeException("Unification of this kind of type parameter is not supported.");
    }

    /**
     * Attempts to unify the type of a formal parameter with the type of an actual argument. It is guaranteed that both types are simple, i.e. not type variables and not structured.
     * @param formal The type of the formal parameter.
     * @param actual The type of the actual argument.
     * @param badness A boxed integer that will be increased if this unification increases badness.
     * @return True if the unification suceeeds.
     */
    public static boolean unifySimpleTypes(Type formal, Type actual, IntegerHolder badness) {
        if (formal.name.equals(actual.name)) return true;
        if (formal.name.equals(Type.floatType.name) && actual.name.equals(Type.integerType.name)) {
            badness.value++;
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
     * @return True if the unification suceeeds.
     */
    public static boolean unifyVariableWithSomething(Type variable, Type type) {
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
                    variable.boundToNumeric = true;
                    return true;
                }
                // TODO advanced stuff
                // 4.2.2.1 Unifying a null with a type variable puts the constraint "must be an object" on the variable.
                if (type.isNull()) {
                    variable.boundToReferenceType = true;
                    return true;
                }

                variable.boundToSpecificType = type;
                return true;
            case Structured:
                // TODO advanced stuff - cycles
                // TODO test for cycles
                variable.boundToSpecificType = type;
                return true;
        }
        throw new RuntimeException("This unification type is impossible.");
    }
    /**
     * Attempts to unify the types of a formal parameter(s) with the types of an actual argument(s). This method is called for subroutine signatures and for type arguments of structured types.
     * @param formal The types of the formal parameter.
     * @param actual The types of the actual argument.
     * @param badness A boxed integer that will be increased if this unification increases badness.
     * @return True if the unification suceeeds.
     */
    public static boolean unify(Types formal, Types actual, IntegerHolder badness) {
        for (int i = 0; i < formal.size(); i++) {
            Type formalType = formal.get(i);
            Type actualType = actual.get(i);
            boolean unification = unify(formalType, actualType, badness);
            if (!unification ) {
                debug("Cannot unify " + formalType + " with " + actualType + ".");
                return false;
            }
        }
        return true;
    }

    /**
     * The second phase of Overload Resolution makes sure that the the expression (and all expressions under this in the expression tree) has exactly one type.
     * @param call The call expression that needs to be type-resolved.
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
        debug ("Phase 2 for " + call.group);
        debug ("Token count: " + call.subroutineTokens.size());
          ArrayList<SubroutineToken> legalSubroutines = call.subroutineTokens;
//        2. Remove from consideration all inferred subroutines whose return type cannot be unified with any type given in the set of possible types. A "float" possible type is unifiable with a subroutine return type of "int" but adds +1 badness. The unifications remain. TODO
          if (returnTypes != null) {
              for (int sti = legalSubroutines.size() - 1; sti >= 0; sti--) {
                  boolean unifiable = false;
                  SubroutineToken st = legalSubroutines.get(sti);
                  //noinspection LoopStatementThatDoesntLoop
                  for (Type rType : returnTypes) {
                      // Test unification
                      // TODO For now, let's suppose only a single return type.
                      IntegerHolder isbad = new IntegerHolder();
                      unifiable = unify(rType, st.formalTypes.get(st.formalTypes.size() -1 ), isbad);
                      st.badness += isbad.value  * 2;
                      break;
                  }
                  if (!unifiable) {
                      legalSubroutines.remove(sti);
                  }
              }
          }
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

            for (Type t : token.formalTypes) {
                Type tFinalBind = t.objectify();
                if (tFinalBind.boundToSpecificType == null && tFinalBind.boundToNumeric) {
                    debug("Binding " + tFinalBind + " to integer.");
                    tFinalBind.boundToSpecificType = Type.integerType;
                }
            }
        }

//        3. Remove from consideration all inferred subroutines that still have a free variable left.
          legalSubroutines.removeIf(sbrt -> {
             for (Type formalType : sbrt.formalTypes) {
                 if (formalType.isIncomplete()) return true;
             }
              return false;
          });

//        4. If there is no subroutine left, signal an error.
        if (legalSubroutines.size() == 0) {
            compilation.semanticError("Type inference failed for '" + call.group.name + "' because some type variables remain free. You may need to specify type arguments directly.", call.line, call.column);
            call.setErrorType();
            return;
        }




        // TODO make a complete overhaul of this

//        5. If there is exactly one subroutine left, launch this phase for all of its arguments with the unification made, and sending only a single possible return type.
        if (legalSubroutines.size() == 1) {
            bestSubroutineSelected(legalSubroutines.get(0), call, compilation);
            return;
            // TODO
            // TODO
        }

        debug ("Phase 2: We are choosing among " + legalSubroutines.size() + " subroutines.");

//        6. If there are still at least two inferred subroutines considered, then discover if one is better than all others. A subroutine is better than another subroutine if it has less badness. If there is one best subroutine, consider it best. If not, signal an error (TODO this can be made better perhaps).
        int bestBadness = Integer.MAX_VALUE;
        SubroutineToken bestSubroutine = null;
        for (SubroutineToken consideredSubroutine : legalSubroutines) {
            debug (consideredSubroutine + ": badness " + consideredSubroutine.badness);
            if (consideredSubroutine.badness < bestBadness) {
                bestBadness = consideredSubroutine.badness;
                bestSubroutine = consideredSubroutine;
            } else if (consideredSubroutine.badness == bestBadness) {
                bestSubroutine = null;
            }
        }
        if (bestSubroutine != null) {
            bestSubroutineSelected(bestSubroutine, call, compilation);
        } else {
            final int finalBestBadness = bestBadness;
            legalSubroutines.removeIf(sbrt -> sbrt.badness > finalBestBadness);
            compilation.semanticError("The call is ambiguous between the following subroutines: " + legalSubroutines.stream().map(sbtk -> "'" + sbtk.subroutine.getSignature(false, false) + "'").collect(Collectors.joining(",")) + ".", call.line, call.column);
            call.setErrorType();
        }
    }
    private static void bestSubroutineSelected(SubroutineToken bestSubroutine, CallExpression call, Compilation compilation) {
        call.callee = bestSubroutine;
        call.type = bestSubroutine.formalTypes.get(bestSubroutine.formalTypes.size() - 1); // TODO iterate through to get the last final in type unification procedure
        debug("Best subroutine: " + call);
        for (int i = 0; i < call.arguments.size(); i++) {
            Expression argument = call.arguments.get(i);
            Type formalType = bestSubroutine.formalTypes.get(i);
            // TODO if still contains a free variable, throw error? or is that already guaranteed?
            debug("Propagating at index " + i + " to " + argument);
            argument.propagateTypes(new HashSet<>(Arrays.asList(formalType)), compilation);
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
        // TODO Propagate
    }

    // Utilities
    private static void debug(String line) {
        /* System.out.println("- " + line); */
    }

    /**
     * A boxed integer.
     */
    private static class IntegerHolder {
        /**
         * The integer boxed by the class.
         */
        public int value;
    }
}
