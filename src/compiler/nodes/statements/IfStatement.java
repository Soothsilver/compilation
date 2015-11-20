package compiler.nodes.statements;

import compiler.Compilation;
import compiler.analysis.Uniqueness;
import compiler.intermediate.OperandWithCode;
import compiler.intermediate.Executable;
import compiler.intermediate.IntermediateFunction;
import compiler.intermediate.instructions.*;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Represent the statement "if" in the abstract syntax tree.
 */
public class IfStatement extends Statement {
    /**
     * An expression that will be evaluated.
     */
    public Expression test;
    /**
     * The statement to execute if the test evaluates to true.
     */
    public Statement thenStatement;
    /**
     * The statement to execute if the test evaluates to false. This may be null. In that case, no statement will be executed on false.
     */
    public Statement elseStatement;

    /**
     * Initializes a new IfStatement node for the abstract syntax tree. This constructor cannot fail,
     * but it will launch phase 2 resolution for the condition expression and this may generate a compiler error.
     *
     * @param booleanTest An expression that will be evaluated.
     * @param thenStatement The statement to execute if the test evaluates to true.
     * @param elseStatement The statement to execute if the test evaluates to false. This may be null. In that case, no statement will be executed on false.
     * @param errorReporter The compilation object, passed for phase 2 resolution of the condition.
     */
    public IfStatement(Expression booleanTest, Statement thenStatement, Statement elseStatement, Compilation errorReporter) {
        this.test = booleanTest;
        this.test.propagateTypes(new HashSet<>(Arrays.asList(Type.booleanType)), errorReporter);
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    @Override
    public String toString() {
        if (elseStatement == null) {
            return "if (" + test + ") " + thenStatement;
        } else {
            return "if (" + test + ") " + thenStatement + " else " + elseStatement;
        }
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        if (elseStatement == null) {
            thenStatement.flowAnalysis(compilation);
            return true;
        } else {
            boolean bothBlocked = !thenStatement.flowAnalysis(compilation) && !elseStatement.flowAnalysis(compilation);
            if (bothBlocked) return false;
            else return true;
        }
    }

    @Override
    public List<Instruction> generateIntermediateCode(Executable executable, IntermediateFunction function) {
        Instructions instructions = new Instructions();
        OperandWithCode expressionResult = test.generateIntermediateCode(executable);
        instructions.addAll(expressionResult.code);
        LabelInstruction labelElse = new LabelInstruction("else_" + Uniqueness.getUniqueId());
        LabelInstruction labelEnd = new LabelInstruction("endif_" + Uniqueness.getUniqueId());
        if (elseStatement == null) {
            instructions.add( new BranchIfZeroInstruction(expressionResult.operand, labelEnd) );
            instructions.addAll( thenStatement.generateIntermediateCode(executable, function) );
            instructions.add( labelEnd );
        } else {
            instructions.add( new BranchIfZeroInstruction(expressionResult.operand, labelElse) );
            instructions.addAll( thenStatement.generateIntermediateCode(executable, function) );
            instructions.add( new JumpInstruction( labelEnd ));
            instructions.add( labelElse );
            instructions.addAll( elseStatement.generateIntermediateCode(executable, function) );
            instructions.add( labelEnd );
        }

        return instructions;
    }
}
