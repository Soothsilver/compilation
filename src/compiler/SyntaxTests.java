package compiler;

import compiler.generated.CompilerLexer;
import compiler.generated.CompilerParser;

import java.io.File;
import java.io.FileReader;


public class SyntaxTests {
	public static void main(String[] args) {
		System.out.println("Running syntax tests:");
		File dir = new File("syntax-tests");
		File[] listing = dir.listFiles();
		int testno = 0;
        int successfulTests = 0;
        assert listing != null;
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
                        for (StackTraceElement n : e.getStackTrace())
                        {
                            System.out.println(n.getFileName() + ":" + n.getLineNumber() + " in " + n.getClassName()+"::"+n.getMethodName());
                        }
	                }
	                catch (java.lang.Error e)
	                {
	                	System.out.println(" parse error (" + e.toString() + ")!");
	                }
	        }
	        catch (Exception e){
	                System.out.println("file could not be opened!");
	        }
		}
		System.out.println(successfulTests + "/" + listing.length + " tests passed.");
	}
}
