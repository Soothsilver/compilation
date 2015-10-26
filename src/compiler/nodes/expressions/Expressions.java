package compiler.nodes.expressions;

import compiler.analysis.Types;
import compiler.nodes.declarations.Type;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Expressions extends ArrayList<Expression> {
    public void addExpression(Expression ex) {
        this.add(ex);
    }

    public ArrayList<Types> getTypeCombinations() {
        ArrayList<Types> combinations = new ArrayList<>();
        if (this.size() == 0)
            return combinations;
        else {
            formTypeCombinations(0, new Types(), combinations);
            return combinations;
        }
    }

    private void formTypeCombinations(int index, Types leftTypes, ArrayList<Types> combinations) {
        if (index == this.size()) {
            combinations.add(leftTypes);
            return;
        }
        for(Type expressionType : this.get(index).possibleTypes) {
            Types types = new Types();
            for (Type type : leftTypes) {
                types.add(type.copy());
            }
            types.add(expressionType.copy());
            formTypeCombinations(index+1, types, combinations);
        }
    }

    public String toWithoutBracketsString() {
        return this.stream().map(expr -> expr.toString()).collect(Collectors.joining(","));
    }
}
