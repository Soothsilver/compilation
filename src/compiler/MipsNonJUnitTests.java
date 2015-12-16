/*
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * This class does not work.
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package compiler;

import compiler.generated.CompilerLexer;
import compiler.generated.CompilerParser;
import compiler.intermediate.Executable;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;


/**
 * This is a class you can use to run tests in the "semantics" folder
 * if you cannot run JUnit.
 */
@SuppressWarnings("Duplicates")
public class MipsNonJUnitTests {
    /**
     * Runs the tests.
     * @param args Unused.
     */
    public static void main(String[] args) {

        System.out.println("Running MIPS tests:");
        File dir = new File("tests");
        dir = new File(dir, "mips-simple");
        File[] listing = dir.listFiles();
        assert listing != null;
        int testNumber = 0;
        int successfulTests = 0;
        for (File child : listing) {
            testNumber++;
            System.out.print("MIPS test " + testNumber + " (" + child.getName() + "): ");
            

            Compilation compilation = new Compilation(child);
            
            try {
                CompilerLexer myLex = new CompilerLexer(new FileReader(child));
                myLex.compilation = compilation;
                CompilerParser myParser = new CompilerParser(myLex);
                myParser.compilation = compilation;
                try {
                    myParser.parse();
                    if (compilation.hasTestRunOkay()) {

                        if (compilation.errorTriggered) {
                            System.out.println("OK (correct error triggered)!");
                        } else {
                            System.out.println("OK!");
                        }

                    } else {
                        for (String message : compilation.errorMessages) {
                            System.out.println(" " + message);
                        }
                        String actual = compilation.abstractSyntaxTree.toString();
                        System.out.println(actual);
                        TestCase.fail("Test has not run okay.");
                    }
                } catch (Exception e) {
                    System.out.println(" parse exception (" + e.getMessage() + ")!");
                    System.out.println(e.toString());
                    for (StackTraceElement n : e.getStackTrace()) {
                        System.out.println(n.getFileName() + ":" + n.getLineNumber() + " in " + n.getClassName() + "::" + n.getMethodName());
                    }
                    TestCase.fail("parse exception");
                } catch (Error e) {
                    System.out.println(" parse error (" + e.toString() + ")!");
                    TestCase.fail("parse error");
                }
                
            } catch (Exception e) {
                System.out.println("file could not be opened (" + e + ")");
                e.printStackTrace();
                TestCase.fail("file error");
                continue;
            } catch (Error er) {
            	 System.out.println("other error");
            	 continue;
            }
            
            
            
            
            
            
            
            
            
            
            if (compilation != null && !compilation.errorTriggered) try {
                Executable executable = new Executable(compilation);
                String assemblerCode = executable.toMipsAssembler();
                String expectedOutput = compilation.firstLine != null ? compilation.firstLine.substring(2).trim() : "";
                try {
                    java.nio.file.Files.write(Paths.get("test.asm"), assemblerCode.getBytes());
                } catch (IOException e) {
                    System.out.println("Due to an I/O exception, the assembler could not be saved to a file.");
                    continue;
                }
                Process mars;
                try {
                    mars = Runtime.getRuntime().exec("java -jar ../lib/Mars4_5.jar 100000 nc test.asm");
                } catch (IOException e) {
                    System.out.println("Due to an I/O exception, the MARS emulator could not be launched.");
                    continue;
                }
                try {
                    mars.waitFor();
                } catch (InterruptedException e) {
                    System.out.println("This will never happen.");
                    continue;
                }
                java.io.InputStream is = mars.getInputStream();
                byte b[];
                String marsOutput;
                try {
                    b = new byte[is.available()];
                    is.read(b, 0, b.length);
                    marsOutput = new String(b);
                } catch (IOException e) {
                    System.out.println("MARS output could not be redirected.");
                    continue;
                }
                marsOutput = marsOutput.trim();
                if (expectedOutput.equals(marsOutput)) {
                    System.out.println("OK!");
                    successfulTests++;
                } else {
                    System.out.println("ERROR (expected " + expectedOutput + ", actual " + marsOutput + ")");
                }
            } catch (Exception ex) {
                System.out.println("EXCEPTION " + ex + ")");
            } catch (Error er) {
                System.out.println("ERROR " + er + ")");
            }
            else {
            	System.out.println("An error triggered.");
            }
        }
        System.out.println(successfulTests + " / " + testNumber + " tests passed.");
    }
}
