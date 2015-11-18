package compiler.nodes.statements;

import java.util.ArrayList;
import java.util.List;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.IntermediateFunction;
import compiler.intermediate.*;
import compiler.intermediate.instructions.Instruction;
import compiler.intermediate.instructions.ReturnInstruction;

public class StopStatement extends Statement {
    public StopStatement(int line, int column, Compilation compilation) {
        this.line = line;
        this.column = column;
        if (!compilation.environment.inProcedure) {
            compilation.semanticError("The stop statement can be used only inside a procedure.", line, column);
        }
    }

    @Override
    public String toString() {
        return "stop;";
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        return false;
    }
    
    @Override
    public List<Instruction> generateIntermediateCode(Executable executable, IntermediateFunction function) {
    	ArrayList<Instruction> instructions = new ArrayList<Instruction>();
    	Operand op = new Operand();
    	op.kind = OperandKind.Immediate;
    	op.integerValue = 0;
    	instructions.add(new ReturnInstruction(op));
    	return instructions;
    }
}
