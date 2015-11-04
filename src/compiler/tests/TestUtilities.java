package compiler.tests;

import compiler.Compilation;
import compiler.generated.CompilerLexer;
import compiler.generated.CompilerParser;
import junit.framework.TestCase;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class TestUtilities {
    public static Collection<Object[]> getFiles(String folder) {
        File dir = new File("tests");
        dir = new File(dir, folder);
        File[] listing = dir.listFiles();
        ArrayList<Object[]> files = new ArrayList<>();
        Arrays.sort(listing, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (File child : listing) {
            if (child.getName().endsWith(".expect.txt")) {
                continue;
            }
            Object[] ar = new Object[2];
            ar[0] = child.getName();
            ar[1] = child;
            files.add(ar);
        }
        return files;
    }
    public static void runTest(File file, boolean syntaxOnly, TestCase testCase) {
        try {
            Compilation compilation = new Compilation(file);
            if (syntaxOnly) compilation.ignoreSemanticErrors = true;
            CompilerLexer myLex = new CompilerLexer(new FileReader(file));
            myLex.compilation = compilation;
            CompilerParser myParser = new CompilerParser(myLex);
            myParser.compilation = compilation;
            try {
                myParser.parse();
                if (compilation.hasTestRunOkay())
                {
                    if (syntaxOnly) {

                    } else {
                        if (!compilation.errorTriggered) {
                            String actual = compilation.abstractSyntaxTree.toString();
                            String expect = null;
                            try {
                                expect = new String(java.nio.file.Files.readAllBytes(Paths.get(new File(file.getParentFile(), file.getName().substring(0, file.getName().length() - 4) + ".expect.txt").toURI())));
                            } catch (Exception fileEx) {
                                System.out.println("ACTUAL:");
                                System.out.println(actual);
                                testCase.fail("Expect file not found.");
                            }
                            if (actual.trim().replace("\r", "").replace("\n", "").replace(" ", "").replace("\t", "").equals(
                                expect.trim().replace("\r", "").replace("\n", "").replace(" ", "").replace("\t", ""))) {
                            } else {
                                System.out.println("OUTPUT MISMATCH!");
                                System.out.println("ACTUAL:");
                                System.out.println(actual);
                                System.out.println("EXPECTED:");
                                System.out.println(expect);
                                System.out.println("SHORT FORM ACTUAL::" + actual.trim().replace("\r", "").replace("\n", "").replace(" ", "").replace("\t", "") + "::");
                                System.out.println("SHORT FORM FORMAL::" + expect.trim().replace("\r", "").replace("\n", "").replace(" ", "").replace("\t", "") + "::");
                                System.out.println("END.");
                                testCase.fail("Output mismatch!");
                            }
                        } else {
                            System.out.println("OK (correct error triggered)!");
                        }
                    }
                } else {
                    for (String message : compilation.errorMessages)
                    {
                        System.out.println(" " + message);
                    }
                    String actual = compilation.abstractSyntaxTree.toString();
                    System.out.println(actual);
                    testCase.fail("Test has not run okay.");
                }
            }
            catch (Exception e) {
                System.out.println(" parse exception (" + e.getMessage() + ")!");
                System.out.println(e.toString());
                for (StackTraceElement n : e.getStackTrace())
                {
                    System.out.println(n.getFileName() + ":" + n.getLineNumber() + " in " + n.getClassName()+"::"+n.getMethodName());
                }
                testCase.fail("parse exception");
            }
            catch (Error e)
            {
                System.out.println(" parse error (" + e.toString() + ")!");
                testCase.fail("parse error");
            }
        }
        catch (Exception e){
            System.out.println("file could not be opened (" + e + ")");
            e.printStackTrace();
            testCase.fail("file error");
        }
    }

}
