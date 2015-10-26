package compiler;

import compiler.generated.CompilerLexer;
import compiler.generated.CompilerParser;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;


public class OverloadingTests {
	public static void main(String[] args) {
		System.out.println("Running overloading tests:");
		File dir = new File("tests");
		dir = new File(dir, "overloading");
		File[] listing = dir.listFiles();
		int testno = 0;
		int successfulTests = 0;
        assert listing != null;
        for (File child : listing) {
            if (child.getName().endsWith(".expect.txt")) {
                continue;
            }
            testno++;
			System.out.print("Overloading test " + testno + " (" + child.getName() + "): ");
			 try {
	               FileReader  myFile = new FileReader(child);
	               Compilation compilation = new Compilation(child);
	               CompilerLexer myLex = new CompilerLexer(myFile);
	               myLex.compilation = compilation;
	               CompilerParser myParser = new CompilerParser(myLex);
	               myParser.compilation = compilation;
	                try {
	                        myParser.parse();
	                        if (compilation.hasTestRunOkay())
	                        {
                                if (!compilation.errorTriggered) {
                                    String actual = compilation.abstractSyntaxTree.toString();
                                    String expect = null;
                                    try {
                                        expect = new String(java.nio.file.Files.readAllBytes(Paths.get(new File(dir, child.getName().substring(0, child.getName().length()-4) + ".expect.txt").toURI())));
                                    } catch (Exception fileEx)
                                    {
                                        expect = "EXPECT FILE NOT FOUND";
                                    }
                                    if (actual.trim().replace("\r", "").equals(expect.trim().replace("\r", ""))) {
										successfulTests++;
                                        System.out.println("OK!");
                                    } else {
                                        System.out.println("OUTPUT MISMATCH!");
                                        System.out.println("ACTUAL:");
                                        System.out.println(actual);
                                        System.out.println("EXPECTED:");
                                        System.out.println(expect);
                                    }
                                } else {
									successfulTests++;
                                    System.out.println("OK (correct error triggered)!");
                                }
	                        } else {
	                        	System.out.println("TEST FAILED!");
	                        	for (String message : compilation.errorMessages)
	                        	{
	                        		System.out.println(" " + message);
	                        	}
	                        }
	                }
	                catch (Exception e) {
                        System.out.println(" parse exception (" + e.getMessage() + ")!");
                        System.out.println(e.toString());
                        for (StackTraceElement n : e.getStackTrace())
                        {
                            System.out.println(n.getFileName() + ":" + n.getLineNumber() + " in " + n.getClassName()+"::"+n.getMethodName());
                        }
	                }
	                catch (Error e)
	                {
	                	System.out.println(" parse error (" + e.toString() + ")!");
	                }
	        }
	        catch (Exception e){
	                System.out.println("file could not be opened!");
	        }
		}
		System.out.println(successfulTests + "/" + testno + " tests passed.");
	}
}
