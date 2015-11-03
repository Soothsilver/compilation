package compiler.analysis;

/**
 * This static utility class generates new unique identifiers. These are used to differentiate type variables spontaneously created by the compilation process.
 */
public class Uniqueness {
    /**
     * Last integer that was generated.
     */
    private static int lastId = 0;

    /**
     * Returns a new integer that was not yet generated by this function.
     * @return The unique integer.
     */
    public static int getUniqueId() {
        lastId++;
        return lastId;
    }
}
