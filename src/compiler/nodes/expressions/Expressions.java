package compiler.nodes.expressions;

import compiler.analysis.Types;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents an ordered list of expression. This class is used primarily for actual argument on subroutine calls.
 */
public class Expressions extends ArrayList<Expression> {

    /**
     * Returns all possible combinations of types of the expressions.
     *
     * Returns a list of newly created instances of the type "Types" which is itself a list of types. All types in
     * these lists are deep copies of the original types.
     *
     * All of the returned lists have the same length. At each index, the types permitted there are taken from the
     * possibleTypes property of the corresponding expression.
     *
     * If there are 0 expression, it returns a single newly created empty instance of "Types'.
     * @return A list of lists as described above.
     */
    public ArrayList<Types> getTypeCombinations() {
        ArrayList<Types> combinations = new ArrayList<>();
        if (this.size() == 0) {
            combinations.add(new Types());
            return combinations;
        } else {
            formTypeCombinations(0, new Types(), combinations);
            return combinations;
        }
    }

    private void formTypeCombinations(int index, Types leftTypes, ArrayList<Types> combinations) {
        if (index == this.size()) {
            combinations.add(leftTypes);
            return;
        }
        // debug: System.out.println("Expression " + index + ": " + this.get(index));
        for(Type expressionType : this.get(index).possibleTypes) {
            Types types = new Types();
            for (Type type : leftTypes) {
                types.add(type.copy());
            }
            types.add(expressionType.copy());
            formTypeCombinations(index+1, types, combinations);
        }
    }

    /**
     * Transforms all the expressions to string and joins them with a comma, omitting enclosing brackets. This is unlike
     * the non-overridden toString method which puts enclosing brackets.
     * @return The expression, joined together by a comma.
     */
    public String toWithoutBracketsString() {
        return this.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}
