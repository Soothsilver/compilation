package compiler.nodes.statements;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.IntermediateFunction;
import compiler.intermediate.instructions.Instruction;
import compiler.nodes.expressions.Expression;
import compiler.nodes.expressions.ExpressionKind;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the statement "EXPRESSION;". Verifies that the given expression can be used as a statement.
 */
public class ExpressionStatement extends Statement {
    public Expression expression;

    private static List<ExpressionKind> permittedKinds = Arrays.asList(
            ExpressionKind.Assignment,
            ExpressionKind.Increment,
            ExpressionKind.Subroutine,
            ExpressionKind.MemberSubroutine
    );									

    public ExpressionStatement(Expression expression, Compilation compilation) {
        this.expression = expression;
        if (expression == null) {
            compilation.semanticError("COMPILER INTERNAL ERROR. An expression was NULL.", -1,-1);
            return;
        }
        if (!permittedKinds.contains(expression.kind)) {
            compilation.semanticError("Only assignment, increment, decrement and subroutine call expressions are permitted as statements.", expression.line, expression.column);
        }
        this.line = expression.line;
        this.column = expression.column;
        this.expression.propagateTypes(null, compilation);
    }

    	
    @Override
    public String toString() {
        if (expression == null) {
            return "COMPILER-ERROR;";
        }
        return expression.toString() + ";";
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        return true;
    }
    
    @Override
    public List<Instruction> generateIntermediateCode(Executable executable,
    		IntermediateFunction function) {
    	
    	   return expression.generateIntermediateCode(executable).code;
    	
    	
    }
    
    
}
