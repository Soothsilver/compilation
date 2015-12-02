package compiler.intermediate.instructions;

import compiler.intermediate.IntermediateRegister;
import compiler.intermediate.MipsMacros;
import compiler.intermediate.MipsRegisters;
import compiler.intermediate.Operand;
import compiler.nodes.declarations.Type;

/**
 * Represents an intermediate code instruction that takes one operand, an operator a return register.
 */
public class UnaryOperatorInstruction extends Instruction {
	 private String operator;
	 private Operand operand;
	 private Type operandType;
	 private IntermediateRegister saveToWhere;

	 /**
	  * Initializes a new UnaryOperatorInstruction.
	  * @param operator The operator. This could be "++", "-" or "!", for example.
	  * @param operandType The type of the operand.
	  * @param operand The operand.
	  * @param returnRegister The intermediate register where the operation result should be saved. All unary operations have a return value.
	  */
	 public UnaryOperatorInstruction(String operator, Type operandType, Operand operand, IntermediateRegister returnRegister) {
	     this.operator = operator;
	     this.operand = operand;
	     this.saveToWhere = returnRegister;
	     this.operandType = operandType;
	 }

	 @Override
	 public String toString() {
	     return saveToWhere + " = " + operator + " " + operand;
	 }

	 @Override
	 public String toMipsAssembler() {
	     switch (operator) {
//	               addUnaryOperator("PRE++", Type.integerType, Type.integerType);
//	               addUnaryOperator("POST++", Type.integerType, Type.integerType);
//	               addUnaryOperator("PRE--", Type.integerType, Type.integerType);
//	               addUnaryOperator("POST--", Type.integerType, Type.integerType);
//	               addUnaryOperator("!", Type.booleanType, Type.booleanType);
//	               addUnaryOperator("~", Type.integerType, Type.integerType);
//	               addUnaryOperator("+", Type.integerType, Type.integerType);
//	               addUnaryOperator("-", Type.integerType, Type.integerType);
//	               addUnaryOperator("+", Type.floatType, Type.floatType);
//	               addUnaryOperator("-", Type.floatType, Type.floatType);
	     case "(character)" :
	    	 if (operandType.equals(Type.integerType)) {
	    		 return
	    				 operand.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
	    				 	saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0);
	    	 }
	         throw getNotImplementedException("Floating point operation... bleh.");
	     
	     case "(integer)" :
	    	 if (operandType.equals(Type.characterType)) {
	    		 return
	    				 operand.toMipsLoadIntoRegister(MipsRegisters.TEMPORARY_VALUE_0) +
	    				 	saveToWhere.mipsAcquireValueFromRegister(MipsRegisters.TEMPORARY_VALUE_0);
	    	 }
	         throw getNotImplementedException("Floating point operation... bleh.");
	     default :
             throw getNotImplementedException("These special assignments are labor-intensive and boring.");	    	 
	    }
	}

	private RuntimeException getNotImplementedException(String reason) {
        return new RuntimeException("This compiler is unable to generate code for the instruction '" + this + "'. Reason: " + reason);
    }
    private RuntimeException getNotImplementedException() {
        return new RuntimeException("We did not yet implement this instruction: '" + this + "'. We should either implement it or use the overload getNotImplementedException(String reason) and add a reason.");
    }
}
