package compiler.nodes.statements;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import compiler.Compilation;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;
import compiler.nodes.expressions.ExpressionKind;

public class ForStatement extends CycleStatement{
	
	public Expression test;
	public Expression initialisaton;
	public Expression incrementation;
	
	 private static List<ExpressionKind> permittedKinds = Arrays.asList(
	            ExpressionKind.Assignment,
	            ExpressionKind.Increment,
	            ExpressionKind.Subroutine,
	            ExpressionKind.MemberSubroutine
	    );									


	public ForStatement(Expression initialisaton, Expression test,Expression incrementation, Compilation compilation) {
		this.initialisaton = initialisaton;
		this.test = test;
		this.incrementation = incrementation;
		this.test.propagateTypes(new HashSet<Type>(Arrays.asList(Type.booleanType)), compilation);
		this.initialisaton.propagateTypes(null, compilation);
		this.incrementation.propagateTypes(null, compilation);
		
		 if (!permittedKinds.contains(incrementation.kind)) {
	            compilation.semanticError("Only assignment, increment, decrement and subroutine call expressions are permitted as statements.", 
	            		incrementation.line, incrementation.column);
	        }
		 
		 if (!permittedKinds.contains(initialisaton.kind)) {
	            compilation.semanticError("Only assignment, increment, decrement and subroutine call expressions are permitted as statements.", 
	            		initialisaton.line, initialisaton.column);
	        }

		 
		
	}
	
	 public String toString() {
	      	return "for(" + initialisaton + ";" + test + ";" + incrementation + ") " + body;	      
	    }
}
