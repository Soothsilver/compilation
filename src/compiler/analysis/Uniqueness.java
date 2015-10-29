package compiler.analysis;

public class Uniqueness {
    private static int lastId = 0;
    public static int getUniqueId() {
        lastId++;
        return lastId;
    }
}
