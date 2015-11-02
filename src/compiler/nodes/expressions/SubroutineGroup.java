package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.Node;
import compiler.nodes.declarations.Subroutine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class SubroutineGroup extends Node {
    /**
     * List of all subroutines with the given name (and object)  that are visible at the point this group appear in the source code.
     */
    public ArrayList<Subroutine> subroutines;
    /**
     * Identifier of the subroutine.
     */
    public String name;
    /**
     * Th object that should be the only one that is searched for subroutines.
     */
    public Expression owner;

    private SubroutineGroup(LinkedList<Subroutine> linkedList) {
        subroutines = new ArrayList<>(linkedList);
    }

    /**
     * Searches the table of symbols for all subroutines with the specified name and returns them as a group.
     * @param identifier Only subroutines with this name will be returned.
     * @param line Line in source code of this expression.
     * @param column Column in source code of this expression.
     * @param compilation The compilation data (needed for Environment access).
     * @return A created subroutine group.
     */
    public static SubroutineGroup create(String identifier, int line, int column, Compilation compilation) {
         compilation.environment.debugPrintSubroutines();
         SubroutineGroup g = new SubroutineGroup(compilation.environment.findSubroutines(identifier));
         g.name = identifier;
         g.line = line;
         g.column = column;
         return g;
    }

    /**
     * Searches the type for all subroutines with the specified name and returns them as a group. The type is determined from the parent expression.
     *
     * @param parent This expression's type will be evaluated and then used to determine where to look for subroutines.
     * @param memberSubroutine Only subroutines with this name will be returned.
     * @param line Line in source code of this expression.
     * @param column Column in source code of this expression.
     * @param compilation The compilation data (needed for Environment access).
     * @return A created subroutine group.
     */
    public static SubroutineGroup create(Expression parent, String memberSubroutine, int line, int column, Compilation compilation) {
        SubroutineGroup g = new SubroutineGroup(new LinkedList<>());
        g.owner = parent;
        g.owner.propagateTypes(null, compilation);
        if (!g.owner.type.isReferenceType) {
            compilation.semanticError("The parent expression's type (" + parent.type + ") is not a reference type and cannot contain subroutines.", line, column);
        }
        else if (g.owner.type.subroutines == null) {
            compilation.semanticError("The parent expression's type (" + parent.type + ") is not a class and cannot contain subroutines.", line, column);
        }
        else {
            for(Subroutine member : g.owner.type.subroutines) {
                if (member.name.equals(memberSubroutine))
                    g.subroutines.add(member);
            }
        }
        g.name = memberSubroutine;
        g.line = line;
        g.column = column;
        return g;
    }

    @Override
    public String toString() {
        return "[" + subroutines.stream().map(sr -> sr.getSig()).collect(Collectors.joining(" AND ")) + "]";
    }
}
