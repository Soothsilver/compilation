package compiler.nodes.declarations;

import compiler.nodes.Node;

/**
 * Represents a type, type template, a variable or a subroutine.
 */
public abstract class Declaration extends Node {
    /**
     * The identifier of this declaration, i.e. name of variable, of type or type template or of a subroutine.
     */
    public String name;

    protected Declaration(String name, int line, int column) {
        super(line, column);
        this.name = name;
    }


    /**
     * Gets the identifier of this declaration, i.e. name of variable, of type or type template or of a subroutine.
     * @return The identifier.
     */
    public final String getId() {
        return name;
    }

    /**
     * Gets the unique identifier of this declaration for symbol tables.
     * @return The identifier.
     */
    public String getSig() {
        // This should be overridden for subroutines.
        return name;
    }

    /**
     * Returns the declaration as it would appear in source code.
     * @return For example, for the variable "hello" of type "integer", this returns "hello : integer".
     */
    public abstract String getFullString();

    /**
     * Returns the signature that is equal to all other signature-equivalent declarations from the view of symbol tables, but is readable by a human. Override this in subroutine.
     * @return The signature string.
     */
    public String getHumanSig() {
        return name;
    }
}
