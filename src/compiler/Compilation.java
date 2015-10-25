package compiler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import compiler.generated.CompilerParser;
import compiler.generated.CompilerSymbol;
import compiler.nodes.*;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.SubroutineKind;
import compiler.nodes.declarations.Type;
import java_cup.runtime.*;

public class Compilation implements ErrorReporter {
	public boolean errorTriggered = false;
    public boolean syntaxErrorTriggered = false;
    public boolean ignoreSemanticErrors = false;
    public boolean syntaxAnalysisFatalFailure = false;
	public ArrayList<String> errorMessages = new ArrayList<>();
	public ArrayList<String> errorsExpected = new ArrayList<>();
	public Environment environment = new Environment(this);
    public ProgramNode abstractSyntaxTree = null;

    public void completeAnalysis(CompilerParser parser) {
        if (abstractSyntaxTree == null) {
            semanticError("Abstract syntax tree was not generated.");
            return;
        }
        if (syntaxErrorTriggered) {
            semanticError("Syntax errors prevent final semantic analysis from taking place.");
            return;
        }
        ProgramNode root = abstractSyntaxTree;
        // Final checks:
        // Is there a main procedure?
		int mainMethodsFound = 0;
		for (Subroutine s : root.Subroutines) {
			if (!s.name.equals("main")) continue;
			if (s.kind == SubroutineKind.FUNCTION) continue;
			if (s.typeParameterNames.size() != 0) continue;
			switch (s.parameters.size())
			{
				case 0:
					mainMethodsFound++;
					break;
				case 2:
					if (s.parameters.get(0).type.equals(Type.integerType))
						mainMethodsFound++; // TODO and the second type is "list of string"
					break;
			}
		}
		if (mainMethodsFound == 0) {
			semanticError("The program does not contain the global procedure 'main'.");
		} else if (mainMethodsFound > 1) {
			semanticError("The program has more than one valid 'main' entry point.");
		}
    }
	
	public boolean hasTestRunOkay() {
		if (errorsExpected.size() > 0) {
			ArrayList<String> copy = new ArrayList<>();
			for (String error : errorsExpected)
			{
				copy.add(error);
			}
			for (String error : errorMessages)
			{
				for (int i = 0; i < copy.size(); i++) {
					if (error.contains(copy.get(i))) {
						copy.remove(i);
						break;
					}
				}
 			}
			return copy.size() == 0;
		}
		else {
			return errorMessages.size() == 0;
		}
	}

	public Compilation(File source) {
		List<String> lines = null;
		try {
			lines = java.nio.file.Files.readAllLines(source.toPath());
		} catch (IOException e) {
			// Ignore
		}
        assert lines != null;
        for (String line : lines)
		{
			if (line.startsWith("//EXPECT-LEXICAL-ERROR:"))
			{
				errorsExpected.add(line.substring("//EXPECT-LEXICAL-ERROR:".length()).trim());
			}
			if (line.startsWith("//EXPECT-SYNTAX-ERROR:"))
			{
				errorsExpected.add(line.substring("//EXPECT-SYNTAX-ERROR:".length()).trim());
			}
			if (line.startsWith("//EXPECT-SEMANTICAL-ERROR:"))
			{
				errorsExpected.add(line.substring("//EXPECT-SEMANTICAL-ERROR:".length()).trim());
			}
		}

		// Start analysis
		environment.addPredefinedTypesConstantsAndFunctions();
	}

    public void semanticError(String message, int line, int column) {
        if (ignoreSemanticErrors) return;
        errorTriggered = true;
        errorMessages.add("Semantic error at line " + (line+1) + ", column " + (column+1) + ": " + message);
    }
    public void semanticError(String message) {
        if (ignoreSemanticErrors) return;
        errorTriggered = true;
        errorMessages.add("Semantic error: " + message);
    }
	public void lexicalError(String message, int line, int column) {
		errorTriggered = true;
		errorMessages.add("Lexical error at line " + (line+1) + ", column " + (column+1) + ": " + message);
	}
	public void reportSyntaxError(CompilerParser parser, String message, Object something)
	{
        errorMessages.add(message);
	}
	public void addSuffix(String message) {
		if (errorMessages.size() > 0)
		{
			errorMessages.set(
					errorMessages.size()-1,
					errorMessages.get(errorMessages.size()-1) + message
					);
		}
	}
	public void syntaxError(CompilerParser parser, Symbol cur_token) {
        syntaxErrorTriggered = true;
		errorTriggered = true;
		String message =
				"Syntax error at line " + (cur_token.left+1) + ", column " + (cur_token.right+1) + " at symbol " 
						+ get_name_from_symbol_id(cur_token.sym);
		
        if (cur_token.value != null)
        {
            message += " (with value \"" + cur_token.value + "\")";
        }
        message += ".";
		if (get_name_from_symbol_id(cur_token.sym).equals("EOF"))
		{
			message = "Syntax error at end of file. Perhaps you are missing closing braces or a semicolon?";
		}
        errorMessages.add(message);
      //  System.out.println("\n " + message);
	}
    public static String get_name_from_symbol_id(int symbol) {
        Class<CompilerSymbol> symbolClass = CompilerSymbol.class;
        Field[] fields = symbolClass.getDeclaredFields();
        for (Field f : fields)
        {
            try {
                if (f.getInt(null) == symbol) return f.getName();
            } catch (IllegalAccessException e) {
                // This will never happen.
            }
        }
        return "UNKNOWN_SYMBOL(" + symbol + ")";
    }
}
