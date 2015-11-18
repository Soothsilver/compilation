package compiler.nodes.statements;

import java.util.ArrayList;
import java.util.List;

import compiler.intermediate.instructions.*;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.IntermediateFunction;
import compiler.nodes.Node;

/**
 * Represents a Statement in the original language (not intermediate code).
 */
public abstract class Statement extends Node {
    /**
     * Gets a boolean that indicates whether the endpoint of this statement is reachable provided that the entry point of this statement is reachable. Reports a warning if it contains an embedded unreachable statement.
     * @return True if the end of this statement is reachable.
     */
    public abstract boolean flowAnalysis(Compilation compilation);

    /**
     * Generates a list of intermediate code instructions from this statement. This method should be overridden in all
     * statements.
     *
     * @param executable The Executable object used in code generation.
     * @param function The subroutine we are currently generating.
     * @return A list of 3-address code instructions to be executed for this statement, in order.
     */
    public List<Instruction> generateIntermediateCode(Executable executable, IntermediateFunction function) {
    	
		return new ArrayList<>();
		
    }
    
    protected Statement(int line, int column) {
        super(line, column);
    }
    protected Statement() {}
}
