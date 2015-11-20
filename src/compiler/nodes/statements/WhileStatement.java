package compiler.nodes.statements;

import compiler.Compilation;
import compiler.analysis.Uniqueness;
import compiler.intermediate.Executable;
import compiler.intermediate.OperandWithCode;
import compiler.intermediate.IntermediateFunction;
import compiler.intermediate.instructions.*;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Represent the while cycle "while (test) statement;".
 */
public class WhileStatement extends CycleStatement {
    /**
     * The expression to be evaluated at the beginning of each iteration.
     */
    public Expression booleanTest;

    /**
     * Initializes a new WhileStatement. Launches phase 2 resolution for the boolean expression. The resolution may fail,
     * but the constructor will always succeed.
     *
     * @param booleanTest The expression to be evaluated at the beginning of each iteration.
     * @param line Source line.
     * @param column Source column.
     * @param compilation Compilation object.
     */
    public WhileStatement(Expression booleanTest, int line, int column, Compilation compilation) {
        super(line, column);
        this.booleanTest = booleanTest;
        this.booleanTest.propagateTypes(new HashSet<>(Arrays.asList(Type.booleanType)), compilation);

    }

    @Override
    public String toString() {
        return "while (" + this.booleanTest + ") " + body;
    }

    @Override
    public List<Instruction> generateIntermediateCode(Executable executable, IntermediateFunction function) {
        Instructions instructions = new Instructions();

        OperandWithCode eer = booleanTest.generateIntermediateCode(executable);
        LabelInstruction cycleStart = new LabelInstruction("while_" + Uniqueness.getUniqueId());
        LabelInstruction cycleEnd = new LabelInstruction("endwhile_" + Uniqueness.getUniqueId());
        instructions.add(cycleStart);
        instructions.addAll(eer.code);
        instructions.add(new BranchIfZeroInstruction(eer.operand, cycleEnd));
        LabelInstruction previous = executable.enclosingLoopEnd;
        executable.enclosingLoopEnd = cycleEnd;
        instructions.addAll(this.body.generateIntermediateCode(executable, function));
        executable.enclosingLoopEnd = previous;
        instructions.add(new JumpInstruction(cycleStart));
        instructions.add(cycleEnd);

        return instructions;
    }
}
