package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.nodes.Node;
import compiler.nodes.declarations.Subroutine;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Represents a node of the syntax tree that contains all subroutines with a given identifier name (perhaps in a specified class).
 */
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
         //compilation.environment.debugPrintSubroutines();
         SubroutineGroup g = new SubroutineGroup(compilation.environment.findSubroutines(identifier));
         g.name = identifier;
         g.line = line;
         g.column = column;
         return g;
    }

    /**
     * Creates a subroutine group by creating a single-item list of the specified subroutine. This is useful in ArrayInferenceCreationExpression/
     * @param singleSubroutine The subroutine to pack into a group.
     * @param line Source line.
     * @param column Source column.
     * @return The subroutine group.
     */
    public static SubroutineGroup create(Subroutine singleSubroutine, int line, int column) {
        SubroutineGroup g = new SubroutineGroup(new LinkedList<>());
        g.subroutines.add(singleSubroutine);
        g.line = line;
        g.column = column;
        g.name = singleSubroutine.name;
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
        /* System.out.println(parent.type);
        System.out.println(parent.type.subroutines.size()); */
        if (!g.owner.type.isReferenceType) {
            compilation.semanticError("The parent expression's type (" + parent.type + ") is not a reference type and cannot contain subroutines.", line, column);
        }
        else if (g.owner.type.subroutines.size() == 0) {
            compilation.semanticError("The parent expression's type (" + parent.type + ") does not contain any subroutines.", line, column);
        }
        else {
            for(Subroutine member : g.owner.type.subroutines) {
                /* System.out.println("Testing against: " + member.name); */
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
        if (owner == null) return name;
        else return owner + "." + name;
    }
}
