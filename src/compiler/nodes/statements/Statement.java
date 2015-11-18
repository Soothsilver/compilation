package compiler.nodes.statements;

import java.util.ArrayList;
import java.util.List;

import compiler.intermediate.instructions.*;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.IntermediateFunction;
import compiler.nodes.Node;

public abstract class Statement extends Node {
    /**
     * Gets a boolean that indicates whether the endpoint of this statement is reachable provided that the entry point of this statement is reachable. Reports a warning if it contains an embedded unreachable statement.
     * @return True if the end of this statement is reachable.
     */
    public abstract boolean flowAnalysis(Compilation compilation);
    
    public List<Instruction> generateIntermediateCode(Executable executable, IntermediateFunction function) {
    	
		return new ArrayList<Instruction>();
		
    }
    
    protected Statement(int line, int column) {
        super(line, column);
    }
    protected Statement() {}
}
