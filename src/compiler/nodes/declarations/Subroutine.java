package compiler.nodes.declarations;

import compiler.Compilation;
import compiler.nodes.Parameter;
import compiler.nodes.statements.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a subroutine. There are several subclasses to this class, specifically OperatorFunction and SystemCall,
 * but this class functions well enough by itself.
 *
 * A subroutine maybe a generic or a non-generic subroutine. For generic subroutines, they cannot be called directly,
 * but first a SubroutineToken (a type instantiation of a Subroutine) must be created for them.
 */
public class Subroutine extends Declaration {
    /**
     * The statement executed when the subroutine is called.
     */
    public Statement block;
    /**
     * Whether it's a procedure or a function.
     */
    public SubroutineKind kind;
    /**
     * Names of the subroutine's type parameters (in [[ and ]] brackets).
     */
    public List<String> typeParameterNames = new ArrayList<>();
    /**
     * Ordered list of the subroutine's formal parameters.
     */
    public List<Parameter> parameters = new ArrayList<>();
    /**
     * The subroutine's return type. If this subroutine is a procedure, the return type is Type.voidType. If
     * the subroutine is generic, the return type may contain the formal type parameters.
     */
    public Type returnType;
    /**
     * The type that contains this subroutine. This will be null if it's a global subroutine.
     */
    public TypeOrTypeTemplate owner;

    private static Subroutine constructingWhatSubroutine = null;

    private Subroutine copy() {
        Subroutine subroutine = new Subroutine(name, line, column);
        subroutine.block = block;
        subroutine.kind = kind;
        subroutine.typeParameterNames = typeParameterNames;
        subroutine.parameters = parameters;
        subroutine.returnType = returnType;
        subroutine.owner = owner;
        return subroutine;
    }


    protected Subroutine(String name, int line, int column) {
        super(name, line, column);
    }

    public static Subroutine create(SubroutineKind kind,
                                    String name,
                                    ArrayList<String> typeParameters,
                                    ArrayList<Parameter> parameters,
                                    Type returnType,
                                    Compilation compilation,
                                    int line,
                                    int column
                                    ) {
        Subroutine s = new Subroutine(name, line, column);
        s.kind = kind;
        s.typeParameterNames = typeParameters == null ? s.typeParameterNames : typeParameters;
        s.parameters = parameters == null ? s.parameters : parameters;
        s.returnType = returnType == null ? Type.voidType : returnType;
        Subroutine.constructingWhatSubroutine = s;
        compilation.environment.enterTypeScope();
        compilation.environment.enterVariableScope();
        return s;
    }
    public void setTypeParameters(ArrayList<TypeParameter> typeParameters) {
        this.typeParameterNames = new ArrayList<>();
        for(TypeParameter parameter : typeParameters) {
            this.typeParameterNames.add(parameter.name);
        }
    }

    /**
     * Set "Subroutine.typeParametersEntered = true;" and then creates a type variable from each type parameter
     * and adds them to the environment.
     * @param typeParameters Type parameters for the subroutine being declared.
     * @param compilation The compilation object.
     */
    public static void enterTypeParameters(ArrayList<TypeParameter> typeParameters, Compilation compilation) {
        for (TypeParameter typename : typeParameters) {
            Type subroutineTypeVariable = Type.createSubroutineTypeVariable(typename.name, typename.line, typename.column);
            compilation.environment.addType(subroutineTypeVariable);
        }
    }



    @Override
    public String getSig() {
        return getSignature(true, false);
    }

    @Override
    public String getFullString() {
        return toString();
    }

    @Override
    public String getHumanSig() {
        return getSignature(true, false, null, true);
    }

    public String getSignature(boolean forSymbolTables, boolean callsite) {
        return getSignature(forSymbolTables, callsite, null);
    }
    public String getSignature(boolean forSymbolTables, boolean callsite, ArrayList<Type> types) {
        return getSignature(forSymbolTables, callsite, types, false);
    }
    public String getSignature(boolean forSymbolTables, boolean callsite, ArrayList<Type> types, boolean humanSignature) {
        String signature = "";
        if (!callsite && !forSymbolTables) {
            if (kind == SubroutineKind.FUNCTION) {
                signature += "function ";
            } else {
                signature += "procedure ";
            }
        }

        signature += name;
        if (forSymbolTables && !humanSignature) {
            signature += " ";
        }
        if (typeParameterNames.size() > 0) {
            signature += "[[";
            if (forSymbolTables) {
                signature += typeParameterNames.size();
            }
            else {
                if (types == null) {
                    signature += String.join(",", typeParameterNames);
                } else {
                    for (int i = 0; i < typeParameterNames.size(); i++) {
                        signature += typeParameterNames.get(i) + "=" + types.get(i);
                        if (i != typeParameterNames.size() - 1) {
                            signature += ",";
                        }
                    }
                }
            }
            signature += "]]";
        }
        signature+=  callsite ? "[" : "(";
        for (int i = 0; i < parameters.size(); i++) {
            Parameter param = parameters.get(i);
            if (callsite) {
                signature += param.type;
            } else {
                if (forSymbolTables) {
                    signature += param.type.toSymbolTableString(typeParameterNames);
                } else {
                    signature += param.name + ":" + param.type;
                }
            }
            if (i != parameters.size() - 1) {
                signature += ",";
            }
        }
        signature += callsite ? "]" : ")";
        if (kind == SubroutineKind.FUNCTION) {
            signature += ":";
            if (forSymbolTables) {
                signature += returnType.toSymbolTableString(typeParameterNames);
            } else {
                signature += returnType;
            }
        }
        return signature;
    }
    @Override
    public String toString() {
        return getSignature(false, false) + " " +  block;
    }

    /**
     * Creates a subroutine with no type parameters and no parameters at line -1, column -1. Does not add the subroutine to the environment.
     * @param kind Procedure or function.
     * @param name Name of the predefined subroutine.
     * @param returnType Return type of the function (or !void).
     * @return The created subroutine.
     */
    public static Subroutine createPredefined(SubroutineKind kind, String name, Type returnType) {
        Subroutine subroutine = new Subroutine(name, -1,-1);
        subroutine.parameters = new ArrayList<>();
        subroutine.typeParameterNames = new ArrayList<>();
        subroutine.returnType = returnType;
        subroutine.kind = kind;
        return subroutine;
    }

    public Subroutine instantiate(ArrayList<Type> typeParameters, ArrayList<Type> typeArguments) {
        Subroutine subroutine = copy();
        subroutine.returnType = subroutine.returnType.replaceTypes(typeParameters, typeArguments);
        subroutine.parameters = new ArrayList<>();
        for(Parameter p : parameters) {
            Parameter pNew = p.copy();
            pNew.type = p.type.replaceTypes(typeParameters, typeArguments);
            subroutine.parameters.add(pNew);
        }
        return subroutine;
    }

    public String getUniqueLabel() {
        Subroutine subroutine = this;
        // TODO This does not work with generics but so what.
        return subroutine.name + "_" + subroutine.parameters.stream().map(param -> param.type.name).collect(Collectors.joining("_"))
                + (subroutine.kind == SubroutineKind.PROCEDURE ? "procedure" :
                (subroutine.returnType.name + "_function"));
    }
}
