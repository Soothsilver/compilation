package compiler.nodes.statements;

import compiler.Compilation;
import compiler.intermediate.Executable;
import compiler.intermediate.OperandWithCode;
import compiler.intermediate.IntermediateFunction;
import compiler.intermediate.instructions.Instruction;
import compiler.intermediate.instructions.Instructions;
import compiler.intermediate.instructions.ReturnInstruction;
import compiler.nodes.declarations.Type;
import compiler.nodes.expressions.Expression;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Represents the statement "return (expression);" that must be at the end of all flows in a function.
 */
public class ReturnStatement extends Statement {
    /**
     * The expression to be evaluated and set to the caller function.
     */
    public Expression expression;

    /**
     * Initializes a new ReturnStatement. Launches phase 2 resolution for the expression.
     * @param line Source line.
     * @param column Source column.
     * @param expression The expression to be evaluated and set to the caller function.
     * @param compilation The compilation object.
     */
    public ReturnStatement(int line, int column, Expression expression, Compilation compilation) {
        this.line = line;
        this.expression = expression;
        this.column = column;
        if (!compilation.environment.inFunction) {
            compilation.semanticError("The return statement can be used only inside a function.", line, column);
            return;
        }
        this.expression.propagateTypes(new HashSet<>(Arrays.asList((Type) compilation.environment.returnType)), compilation);
    }

    @Override
    public String toString() {
        return "return " + this.expression + ";";
    }

    @Override
    public boolean flowAnalysis(Compilation compilation) {
        return false;
    }

    @Override
    public List<Instruction> generateIntermediateCode(Executable executable, IntermediateFunction function) {
        Instructions instructions = new Instructions();
        OperandWithCode eer = expression.generateIntermediateCode(executable);
        instructions.addAll(eer.code);
        instructions.add(new ReturnInstruction(eer.operand));
        return instructions;
    }
}
