package compiler.intermediate;

/**
 * Represents a string literal stored during intermediate code generation.
 */
public class IntermediateStringLiteral {
    private String stringContents;
    private String label;

    /**
     * Initializes a new IntermediateStringLiteral.
     * @param label A label that both the intermediate code and the MIPS assembly will use to refer to this string.
     * @param contents The text of this string literal.
     */
    public  IntermediateStringLiteral(String label, String contents) {
        this.label = label;
        this.stringContents = contents;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Gets the contents of this literal, BUT replaces all newlines by their escape characters.
     */
    public String getData() {
        return stringContents.replace("\r", "").replace("\n", "\\n");
    }
}
