package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.Node;
import compiler.nodes.declarations.Subroutine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class SubroutineGroup extends Node {
    public ArrayList<Subroutine> subroutines;
    public String name;

    private SubroutineGroup(LinkedList<Subroutine> linkedList) {
        subroutines = new ArrayList<>(linkedList);
    }

    public static SubroutineGroup create(String identifier, int line, int column, Compilation compilation) {
         SubroutineGroup g = new SubroutineGroup(compilation.environment.findSubroutines(identifier));
         g.name = identifier;
         g.line = line;
         g.column = column;
         return g;
    }
    public static SubroutineGroup create(Expression parent, String memberSubroutine, int line, int column, Compilation compilation) {
        SubroutineGroup g = new SubroutineGroup(new LinkedList<>()); // TODO to be implemented
        g.name = memberSubroutine; // TO BE bettered
        g.line = line;
        g.column = column;
        return g;
    }

    @Override
    public String toString() {
        // return Integer.toString(subroutines.size());
        return subroutines.stream().map(sr -> sr.getSig()).collect(Collectors.joining());
    }
}
