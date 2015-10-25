package compiler;

/**
 * Created by petrh on 21.10.15.
 */
public interface ErrorReporter {
    void semanticError(String message, int line, int column);
    void semanticError(String message);
}
