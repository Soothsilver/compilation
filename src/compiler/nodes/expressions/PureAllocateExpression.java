package compiler.nodes.expressions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.IntermediateRegister;
import compiler.intermediate.Operand;
import compiler.intermediate.OperandKind;
import compiler.intermediate.OperandWithCode;
import compiler.intermediate.instructions.AllocateInstruction;
import compiler.intermediate.instructions.BinaryOperatorInstruction;
import compiler.intermediate.instructions.Instructions;
import compiler.intermediate.instructions.PureAllocateInstruction;
import compiler.nodes.declarations.Type;

public class PureAllocateExpression extends Expression {
	Expression size;

	public PureAllocateExpression(Expression size, Compilation compilation) {
		this.size = size;
		this.type = Type.stringType;
		this.possibleTypes.add(Type.stringType);
		this.size.propagateTypes(new HashSet<>(Arrays.asList(Type.integerType)), compilation);
	}
	
	@Override
	public void propagateTypes(Set<Type> types, Compilation compilation) {
	    if (types == null) return;
        if (!types.contains(this.type)) {
            compilation.semanticError("Only strings can be allocated using the Pure Allocate Expression.", line, column);
        }	
    }
	
	@Override
	public String toString() {
		return "allocate(" + size + ")";
	}
	
	@Override
	public OperandWithCode generateIntermediateCode(Executable executable) {
		
		    IntermediateRegister referenceRegister = executable.summonNewRegister();

	        Instructions instructions = new Instructions();
	        
	        OperandWithCode sizeResult = size.generateIntermediateCode(executable);
	        
	        instructions.addAll(sizeResult.code);
	        instructions.add(new PureAllocateInstruction(referenceRegister, sizeResult.operand));
	        		
	        Operand operand = new Operand(referenceRegister, OperandKind.Register);
	        return new OperandWithCode(instructions, operand);
	}
}
