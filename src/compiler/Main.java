package compiler;

import compiler.generated.*;

import java.io.File;
import java.io.FileReader;

/**
 * This class contains the main entry-point for running the compiler.
 */
public class Main {
    /**
     * Runs the compiler on the filename specified as a command-line argument.
     *
     * @param args The first argument should be a filename relative to the working directory.
     */
    public static void main(String[] args) {

        try {
            FileReader myFile = new FileReader(args[0]);
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
            } catch (Error t) {
                System.out.println("Error: " + t + "\n");
                t.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e + "\n");
            e.printStackTrace();
        } catch (Error e) {
            System.out.println("Unexpected error.");
        }
    }
}