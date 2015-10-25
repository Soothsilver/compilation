package compiler;

import compiler.generated.*;

import java.io.File;
import java.io.FileReader;

public class Main {
        public static void main(String[] args) {
        	
                try {
                       FileReader  myFile = new FileReader(args[0]);
                       Compilation compilation = new Compilation(new File(args[0]));
                       CompilerLexer myLex = new CompilerLexer(myFile);
                       myLex.compilation = compilation;
                       CompilerParser myParser = new CompilerParser(myLex);
                       myParser.compilation = compilation;
                       try {
                            myParser.parse();
                            System.out.println("Errors: " + compilation.errorMessages.size());
                            for (String message : compilation.errorMessages) {
                                System.out.println(" " + message);
                            }
                            if (compilation.abstractSyntaxTree != null) {
                                System.out.println(compilation.abstractSyntaxTree.toString());
                            }
                       }
                       catch (java.lang.Error t) {
                              System.out.println("Error: " + t + "\n");
                            t.printStackTrace();
                       }
                }
                catch (Exception e){
                        System.out.println("Exception: " + e + "\n");
                        e.printStackTrace();
                }
                catch (java.lang.Error e)
                {      
                    System.out.println("Unexpected error.");   
                	
                }
        }
}