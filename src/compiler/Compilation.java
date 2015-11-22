package compiler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

import compiler.generated.CompilerParser;
import compiler.generated.CompilerSymbol;
import compiler.nodes.*;
import compiler.nodes.declarations.Subroutine;
import compiler.nodes.declarations.SubroutineKind;
import compiler.nodes.declarations.Type;
import java_cup.runtime.*;

/**
 * This class maintains all data about the compilation of a single file.
 * It contains the abstract syntax tree, information on any triggered errors and provide access to the current environment.
 * It is also used to report errors.
 */
public class Compilation {
	private boolean warningsAreErrors = false;
	/**
     * Indicates whether any error triggered during compilation. If yes, we will not proceed to code generation.
     */
	public boolean errorTriggered = false;
    /**
     * Indicates whether any syntax error triggered. This information is perhaps not useful.
     */
    public boolean syntaxErrorTriggered = false;
	/**
	 * If true, then whenever a semantic error would trigger, instead it doesn't trigger.
	 */
	public boolean ignoreSemanticErrors = false;
    /**
     * List of error messages for errors triggered during the compilation.
     */
    public ArrayList<String> errorMessages = new ArrayList<>();
    /**
     * List of error messages expected to happen during this compilation. A compilation test succeeds only if, for
     * each expected error, an actual error message triggered. Each error message may be used only once.
     */
    public ArrayList<String> errorsExpected = new ArrayList<>();
    /**
     * The first line of the source code. This is used in code-generation tests because I was too lazy to write
     * a proper testing comment and writing "//2" on the first line is faster than writing "//EXPECT:2" on any line.
     */
    public String firstLine = null;
    /**
     * The symbol tables associated with the compilation object. The environment is initialized in the initializer.
     */
    public Environment environment = new Environment(this);
    /**
     * The abstract syntax tree generated at the end of semantic analysis.
     */
    public ProgramNode abstractSyntaxTree = null;

    /**
     * Performs final checks at the end of syntax and semantic analysis.
     */
    public void completeAnalysis() {
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
		for (Subroutine subroutine : root.Subroutines) {
            // A main procedure is a procedure, named main, has no type parameters and has either no parameters or parameters "integer" and "list of string".
            // The parameter names do not matter.
			if (!subroutine.name.equals("main")) continue;
			if (subroutine.kind == SubroutineKind.FUNCTION) continue;
			if (!subroutine.typeParameterNames.isEmpty()) continue;
			switch (subroutine.parameters.size())
			{
				case 0:
					mainMethodsFound++;
					break;
				case 2:
					if (subroutine.parameters.get(0).type.equals(Type.integerType) &&
						subroutine.parameters.get(1).type.equals(Type.createArray(Type.stringType, -1, -1))) {
                        mainMethodsFound++;
                        // The line and column arguments to the createArray function don't matter here.
                    }
					break;
			}
		}
		if (mainMethodsFound == 0) {
			semanticError("The program does not contain the global procedure 'main'.");
		} else if (mainMethodsFound > 1) {
			semanticError("The program has more than one valid 'main' entry point.");
		}
    }

    /**
     * Returns true if either both the expected errors list and the error messages list are empty, or,
     * if there is at least one expected error, if for each expected error there exists an actual triggered error
     * message that it matches. Each error message may be used only once.
     */
    public boolean hasTestRunOkay() {
		if (!errorsExpected.isEmpty()) {
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
			return copy.isEmpty();
		}
		else {
			return errorMessages.isEmpty();
		}
	}

    /**
     * Initializes a new compilation object. Create a new Compilation object at the beginning of each compilation.
     * This constructor will load the file from disk, extract compiler and test related information from it (contained
     * in its comments). But it will not run the parser.
     *
     * If the file cannot be loaded, the constructor fails silently and returns an object in an inconsistent state.
     *
     * @param source The file with the source code to compile.
     */
    public Compilation(File source) {
		if (source != null) {
			List<String> lines = null;
			try {
				lines = Files.readAllLines(source.toPath());
			} catch (IOException e) {
				// Ignore
			}
			assert lines != null; boolean isFirst = true;
			for (String line : lines) {
				if (isFirst) {
					firstLine = line;
					isFirst = false;
				}
				if (line.startsWith("//EXPECT-LEXICAL-ERROR:")) {
					errorsExpected.add(line.substring("//EXPECT-LEXICAL-ERROR:".length()).trim());
				}
				if (line.startsWith("//EXPECT-SYNTAX-ERROR:")) {
					errorsExpected.add(line.substring("//EXPECT-SYNTAX-ERROR:".length()).trim());
				}
				if (line.startsWith("//EXPECT-SEMANTICAL-ERROR:")) {
					errorsExpected.add(line.substring("//EXPECT-SEMANTICAL-ERROR:".length()).trim());
				}
				if (line.startsWith("//EXPECT:")) {
					errorsExpected.add(line.substring("//EXPECT:".length()).trim());
				}
				if (line.startsWith("// EXPECT:")) {
					errorsExpected.add(line.substring("// EXPECT:".length()).trim());
				}
				if (line.startsWith("//WARNINGS-ARE-ERRORS")) {
					warningsAreErrors = true;
				}
			}
		}

		// Start analysis
		environment.addPredefinedTypesConstantsAndFunctions();
	}

    /**
     * Causes a semantic error to trigger and be put into the list of errors.
     * @param message The error message. It will be prefixed with the text "Semantic error: ".
     * @param line Line at which the error occurred.
     * @param column Column at which the error occurred.
     */
    public void semanticError(String message, int line, int column) {
        if (ignoreSemanticErrors) return;
        errorTriggered = true;
        errorMessages.add("Semantic error at line " + (line+1) + ", column " + (column+1) + ": " + message);
    }
    private void semanticError(String message) {
        if (ignoreSemanticErrors) return;
        errorTriggered = true;
        errorMessages.add("Semantic error: " + message);
    }

    /**
     * Causes a semantic warning to trigger. If the file contains the "warnings are errors" special comment,
     * it also triggers an error. The warning text is added to the list of errors.
     * @param warningMessage The warning message. It will be prefixed with the text "Warning:".
     * @param line Source line.
     * @param column Source column.
     */
    public void warning(String warningMessage, int line, int column) {
		if (ignoreSemanticErrors) return;
		if (warningsAreErrors) errorTriggered = true;
		errorMessages.add("Warning at line " + (line+1) + ", column " + (column+1) + ": " + warningMessage);
	}

    /**
     * Causes a lexical error to trigger and be put into the list of errors.
     * @param message The error message. It will be prefixed with the text "Lexical error: ".
     * @param line Line at which the error occurred.
     * @param column Column at which the error occurred.
     */
    public void lexicalError(String message, int line, int column) {
		errorTriggered = true;
		errorMessages.add("Lexical error at line " + (line+1) + ", column " + (column+1) + ": " + message);
	}

    /**
     * Causes an error to trigger and be put into the list of errors.
     *
     * This method should be called during code generation when the code passes semantic analysis but we did not
     * code it in code generation.
     *
     * @param message The error message. It will be prefixed with the text "Internal error: ".
     * @param line Line at which the error occurred.
     * @param column Column at which the error occurred.
     */
    public void notImplementedError(String message, int line, int column) {
        errorTriggered = true;
        errorMessages.add("Internal error at line " + (line+1) + ", column " + (column+1) + ": " + message);
    }

    /**
     * This method catches all uncaught errors from the CUP parser, mostly CUP internal errors.
     * Normally, it should never be called.
     *
     * @param parser The CUP-generated parser object.
     * @param message The CUP error message.
     * @param something This is an argument that CUP passes to this method.
     */
    @SuppressWarnings("UnusedParameters")
    public void reportSyntaxError(CompilerParser parser, String message, Object something)
	{
        errorMessages.add(message);
	}

    /**
     * Adds a message to the end of the last triggered error.
     * @param message The message to add to the last triggered error.
     */
    public void addSuffix(String message) {
		if (errorMessages.size() > 0)
		{
			errorMessages.set(
					errorMessages.size()-1,
					errorMessages.get(errorMessages.size()-1) + message
					);
		}
	}

    /**
     * Causes a syntax error to trigger and be put in the list of errors.
     * @param parser The CUP-generated parser object.
     * @param cur_token The token at which CUP recognized that a syntax error occurred.
     */
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

    /**
     * Gets the name of the terminal identified by the given CUP identifier.
     * @param symbol Internal CUP terminal identifier.
     * @return A human-readable name of the terminal.
     */
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
