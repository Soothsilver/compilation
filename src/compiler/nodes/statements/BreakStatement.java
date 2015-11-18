package compiler.nodes.statements;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.IntermediateFunction;
import compiler.intermediate.instructions.Instruction;
import compiler.intermediate.instructions.Instructions;
import compiler.intermediate.instructions.JumpInstruction;

import java.util.List;

/**
 * Represents the statement "break;".
 */
public class BreakStatement extends Statement {
    /**
     * Initializes a new break statement. Triggers an error if there is no enclosing loop to break from.
     * @param line Source line.
     * @param column Source column.
     * @param compilation Compilation object.
     */
    public BreakStatement(int line, int column, Compilation compilation) {
        this.line = line;
        this.column = column;
        if (!compilation.environment.inCycle()) {
            compilation.semanticError("No enclosing loop out of which to break.", line, column);
        }
    }

    @Override
    public String toString() {
        return "break;";
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        return false;
    }

    @Override
    public List<Instruction> generateIntermediateCode(Executable executable, IntermediateFunction function) {
        Instructions instructions = new Instructions();
        instructions.add(new JumpInstruction(executable.enclosingLoopEnd));
        return instructions;
    }
}
