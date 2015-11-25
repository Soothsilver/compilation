package compiler.analysis;

/**
 * A boxed integer.
 */
public class IntegerHolder {
    /**
     * The integer boxed by the class.
     */
    private int value;
    public int getValue() {
        return value;
    }

    /**
     * Increases the value by one, and prints a debug message.
     */
    public void raise() {
        OverloadResolution.debug("Badness raised.");
        value++;
    }

    /**
     * Multiplies the value by two, and prints a debug message.
     */
    public void multiplyByTwo() {
        OverloadResolution.debug("Badness multiplied by two.");
        value *= 2;
    }
}
