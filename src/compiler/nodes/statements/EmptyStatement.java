package compiler.nodes.statements;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.IntermediateFunction;
import compiler.intermediate.instructions.Instruction;
import compiler.intermediate.instructions.Instructions;

import java.util.List;

/**
 * Represent the empty statement ";". This statement is used for while cycles without bodies, for example.
 * It is also generated from syntax errors.
 */
public class EmptyStatement extends Statement {
    /**
     * Initializes a new EmptyStatement.
     * @param line Source line.
     * @param column Source column.
     */
    public EmptyStatement(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        return true;
    }

    @Override
    public String toString() {
        return ";";
    }

    @Override
    public List<Instruction> generateIntermediateCode(Executable executable, IntermediateFunction function) {
        return new Instructions();
    }
}
