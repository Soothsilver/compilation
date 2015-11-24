package compiler.nodes.statements;

import compiler.Compilation;
import compiler.analysis.Uniqueness;
import compiler.intermediate.Executable;
import compiler.intermediate.IntermediateFunction;
import compiler.intermediate.OperandWithCode;
import compiler.intermediate.instructions.BranchIfNotZeroInstruction;
import compiler.intermediate.instructions.BranchIfZeroInstruction;
import compiler.intermediate.instructions.Instruction;
import compiler.intermediate.instructions.Instructions;
import compiler.intermediate.instructions.JumpInstruction;
import compiler.intermediate.instructions.LabelInstruction;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Represents the statement "repeat { body; } while ( expression );".
 */
public class RepeatStatement extends CycleStatement {
    /**
     * A boolean expression tested at the end of each iteration.
     */
    public Expression booleanTest;

    /**
     * Initializes a new RepeatStatement. Launches phase 2 resolution for the boolean expression (forcing boolean).
     * @param booleanTest The expression to evaluate at the end of each iteration.
     * @param body The loop's statement.
     * @param line Source line.
     * @param column Source column.
     * @param compilation The compilation object.
     */
    public RepeatStatement(Expression booleanTest, Statement body, int line, int column, Compilation compilation) {
        super(line, column);
        this.body = body;
        this.booleanTest = booleanTest;
        this.booleanTest.propagateTypes(new HashSet<>(Arrays.asList(Type.booleanType)), compilation);

    }

    @Override
    public String toString() {
        return "repeat " + body + " while (" + this.booleanTest + ");";
    }
    
    @Override
    public List<Instruction> generateIntermediateCode(Executable executable, IntermediateFunction function) {
        Instructions instructions = new Instructions();

        OperandWithCode eer = booleanTest.generateIntermediateCode(executable);
        LabelInstruction cycleStart = new LabelInstruction("while_" + Uniqueness.getUniqueId());
        LabelInstruction cycleEnd = new LabelInstruction("endwhile_" + Uniqueness.getUniqueId());
        instructions.add(cycleStart);
        LabelInstruction previous = executable.enclosingLoopEnd;
        executable.enclosingLoopEnd = cycleEnd;
        instructions.addAll(this.body.generateIntermediateCode(executable, function));
        executable.enclosingLoopEnd = previous;
        instructions.addAll(eer.code);
        instructions.add(new BranchIfNotZeroInstruction(eer.operand, cycleStart));        
        instructions.add(cycleEnd);

        return instructions;
    }
}
