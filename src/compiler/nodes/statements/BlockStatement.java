package compiler.nodes.statements;

import java.util.ArrayList;
import java.util.List;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.IntermediateFunction;
import compiler.intermediate.instructions.Instruction;
import compiler.nodes.Declarations;
import compiler.nodes.declarations.Declaration;
import compiler.nodes.declarations.Variable;
import compiler.nodes.declarations.VariableKind;

/**
 * Represents a block of scoped variables and statements. It is printed on one line if there are no declarations,
 * and on multiple lines if there is a variable declaration. It's clearest this way, I think.
 * All subroutines have a BlockStatement as a member.
 */
public class BlockStatement extends Statement {
    /**
     * List of statements used in this block.
     * Should never be null.
     */
    public Statements statements = null;
    /**
     * List of variables declared at the beginning of this block.
     * Should never be null.
     */
    public Declarations declarations = null;

    @Override
    public String toString() {
        if (declarations == null || declarations.isEmpty())
             return "{ " + statements.toString() + " }";
        else
            return "{ \n" + declarations.toString() + statements.toLongString() + "\n}";
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        boolean reachable = true;
        for (Statement statement : statements) {
            if (!reachable) {
                compilation.warning("The statement '" + statement + "' is unreachable.", statement.line, statement.column);
                return false;
            }
            reachable = statement.flowAnalysis(compilation);
        }
        return reachable;
    }
   
    @Override
    public List<Instruction> generateIntermediateCode(Executable executable, IntermediateFunction function) {

        for (Declaration declaration : declarations) {
            Variable variable = (Variable)declaration;
            variable.kind = VariableKind.Local;
            variable.index = executable.localVariableMaximum;
            executable.localVariableMaximum++;
        }


    	ArrayList<Instruction> instructions = new ArrayList<>();
    	for( Statement stmt : statements) {
    		instructions.addAll(stmt.generateIntermediateCode(executable, function));
    	}

        executable.localVariableMaximum -= declarations.size();
    	return instructions;
    }
}
