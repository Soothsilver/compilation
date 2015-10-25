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
        compilation.environment.enterProcedure(name);
        return s;
    }

    /*
                        // TODO check that the name does not already exist
                        // TODO add generics
                        // TODO add parameters
                        /*
                        getEnvironment().enterProcedure(name);
                        Subroutine s = new Subroutine();
                        s.name = name;
                        RESULT = s;
                        */

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
        signature+= "(";
        for (int i = 0; i < parameters.size(); i++) {
            Parameter param = parameters.get(i);
            if (forSymbolTables) {
                signature += param.type;
            } else {
                signature += param.name + ":" + param.type; // TODO type variables
            }
            if (i != parameters.size() - 1) {
                signature += ",";
            }
        }
        signature += ")";
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
