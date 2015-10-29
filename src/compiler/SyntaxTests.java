package compiler;

import compiler.generated.CompilerLexer;
import compiler.generated.CompilerParser;

import java.io.File;
import java.io.FileReader;

/**
 * This is a class you can use to run tests in the "syntax" folder
 * if you cannot run JUnit.
 */
public class SyntaxTests {
	/**
	 * Runs the tests, skipping the checks for semantics.
	 * @param args Unused.
	 */
	public static void main(String[] args) {
		System.out.println("Running syntax tests:");
		File dir = new File("tests");
		dir = new File(dir, "syntax");
		File[] listing = dir.listFiles();
		assert listing != null;
		int testno = 0;
		int successfulTests = 0;
		for (File child : listing) {
            testno++;
			System.out.print("Syntax test " + testno + " (" + child.getName() + "): ");
			 try {
	               FileReader  myFile = new FileReader(child);
	               Compilation compilation = new Compilation(child);	               
	               CompilerLexer myLex = new CompilerLexer(myFile);
	               myLex.compilation = compilation;
	               CompilerParser myParser = new CompilerParser(myLex);
	               myParser.compilation = compilation;
				   compilation.ignoreSemanticErrors = true;
	                try { 
	                        myParser.parse();
	                        if (compilation.hasTestRunOkay())
	                        {
                                System.out.println("OK!");
                                successfulTests++;
	                        } else {
	                        	System.out.println("TEST FAILED!");
	                        	for (String message : compilation.errorMessages)
	                        	{
	                        		System.out.println(" " + message);
	                        	}
	                        }
	                }
	                catch (Exception e) {
                        System.out.println(" parse exception (" + e.toString() + ")!");
                        System.out.println(e.toString());
                        for (StackTraceElement stackTraceElement : e.getStackTrace())
                        {
                            System.out.println(stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + " in " + stackTraceElement.getClassName() + "::" + stackTraceElement.getMethodName());
                        }
	                }
	                catch (Error e)
	                {
	                	System.out.println(" parse error (" + e.toString() + ")!");
	                }
	        }
	        catch (Exception e){
	                System.out.println("file could not be opened!: " +e);
                e.printStackTrace();
	        }
		}
		System.out.println(successfulTests + "/" + listing.length + " tests passed.");
	}
}
