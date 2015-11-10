package compiler.nodes.statements;

import compiler.Compilation;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;

import java.util.Arrays;
import java.util.HashSet;

public class ReturnStatement extends Statement {
    public Expression expression;
    public ReturnStatement(int line, int column, Expression expression, Compilation compilation) {
        this.line = line;
        this.expression = expression;
        this.column = column;
        if (!compilation.environment.inFunction) {
            compilation.semanticError("The return statement can be used only inside a function.", line, column);
            return;
        }
        this.expression.propagateTypes(new HashSet<>(Arrays.asList((Type) compilation.environment.returnType)), compilation);
    }

    @Override
    public String toString() {
        return "return " + this.expression + ";";
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        return false;
    }
}
