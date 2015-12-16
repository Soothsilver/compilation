package compiler.nodes.expressions;

import compiler.Compilation;
import compiler.intermediate.*;
import compiler.intermediate.instructions.Instructions;
import compiler.intermediate.instructions.PureAllocateInstruction;
import compiler.nodes.declarations.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the expression "allocate(integer)" that is used by Aura library code to create strings of a given size.
 */
public class PureAllocateExpression extends Expression {
	Expression size;

	/**
	 * Initializes a new PureAllocateExpression. Launches phase 2 resolution for the "size" parameter. Sets this expression's
     * type strictly to "string".
	 * @param size Expression that must have the "integer" type.
	 * @param compilation The compilation object.
	 */
	public PureAllocateExpression(Expression size, Compilation compilation) {
		this.size = size;
		this.type = Type.stringType;
		this.possibleTypes.add(Type.stringType);
		this.size.propagateTypes(new HashSet<>(Arrays.asList(Type.integerType)), compilation);
	}
	
	@Override
	public void propagateTypes(Set<Type> types, Compilation compilation) {
	    if (types == null) {
            return;
        }
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
