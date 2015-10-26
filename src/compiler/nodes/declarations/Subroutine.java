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

    public static boolean typeParametersEntered = false;
    public static Subroutine create(SubroutineKind kind,
                                    String name,
                                    ArrayList<String> typeParameters,
                                    ArrayList<Parameter> parameters,
                                    Type returnType,
                                    Compilation compilation
                                    ) {
        Subroutine s = new Subroutine();
        s.kind = kind;
        s.name = name;
        s.typeParameterNames = (typeParameters == null ? s.typeParameterNames : typeParameters);
        s.parameters = (parameters == null ? s.parameters : parameters);
        s.returnType = (returnType == null ? Type.voidType : returnType);
        compilation.environment.returnType = s.returnType;
        compilation.environment.addSubroutine(s);
        if (kind == SubroutineKind.FUNCTION) {
            compilation.environment.enterFunction();
        } else {
            compilation.environment.enterProcedure();
        }
        return s;
    }
    public static void enterTypeParameters(ArrayList<String> typeParameters, Compilation compilation) {
        typeParametersEntered = true;
        for (String typename : typeParameters) {
            Type subroutineTypeVariable = Type.createSubroutineTypeVariable(typename);
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
