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
    public void raise() {
        OverloadResolution.debug("Badness raised.");
        value++;
    }

    public void multiplyByTwo() {
        OverloadResolution.debug("Badness multiplied by two.");
        value *= 2;
    }
}
