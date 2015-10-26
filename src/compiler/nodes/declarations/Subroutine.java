package compiler.nodes.declarations;

import compiler.Compilation;
import compiler.Parameter;
import compiler.nodes.statements.Statement;

import java.util.ArrayList;
import java.util.List;

public class Subroutine extends Declaration {
    public Statement block;
    public SubroutineKind kind;
    public List<String> typeParameterNames = new ArrayList<>();
    public List<Parameter> parameters = new ArrayList<>();
    public Type returnType;
    private static Subroutine constructingWhatSubroutine = null;

    public static boolean typeParametersEntered = false;
    public static Subroutine create(SubroutineKind kind,
                                    String name,
                                    ArrayList<String> typeParameters,
                                    ArrayList<Parameter> parameters,
                                    Type returnType,
                                    Compilation compilation,
                                    int line,
                                    int column
                                    ) {
        Subroutine s = new Subroutine();
        s.kind = kind;
        s.line = line;
        s.column = column;
        s.name = name;
        s.typeParameterNames = (typeParameters == null ? s.typeParameterNames : typeParameters);
        s.parameters = (parameters == null ? s.parameters : parameters);
        s.returnType = (returnType == null ? Type.voidType : returnType);
        constructingWhatSubroutine = s;
        compilation.environment.enterTypeScope();
        return s;
    }
    public void setTypeParameters(ArrayList<TypeParameter> typeParameters) {
        this.typeParameterNames = new ArrayList<>();
        for(TypeParameter parameter : typeParameters) {
            this.typeParameterNames.add(parameter.name);
        }
    }
    public static void enterTypeParameters(ArrayList<TypeParameter> typeParameters, Compilation compilation) {
        typeParametersEntered = true;
        for (TypeParameter typename : typeParameters) {
            Type subroutineTypeVariable = Type.createSubroutineTypeVariable(typename.name, typename.line, typename.column);
            compilation.environment.addType(subroutineTypeVariable);
        }
    }
    public static void leaveTypeParameters(Compilation compilation) {
        typeParametersEntered = false;
    }


    @Override
    public String getSig() {
        return getSignature(true, false);
    }
    public String getSignature(boolean forSymbolTables, boolean callsite) {
        String signature = "";
        if (!callsite) {
            if (kind == SubroutineKind.FUNCTION) {
                signature += "function ";
            } else {
                signature += "procedure ";
            }
        }
        signature += name;
        if (typeParameterNames.size() > 0) {
            signature += "[[";
            if (forSymbolTables) {
                signature += typeParameterNames.size();
            }
            else {
                signature += String.join(",", typeParameterNames);
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
                    signature += param.type;
                } else {
                    signature += param.name + ":" + param.type; // TODO type variables
                }
            }
            if (i != parameters.size() - 1) {
                signature += ",";
            }
        }
        signature += callsite ? "]" : ")";
        if (kind == SubroutineKind.FUNCTION) {
            signature += ":";
            signature += returnType; // TODO type variables
        }
        return signature;
    }
    @Override
    public String toString() {
        return getSignature(false, false) + " " +  block;
    }

}
