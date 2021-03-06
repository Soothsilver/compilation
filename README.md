# A compiler for a Java-like language
This is a compiler for a Java-like language, Aura, with classes, generics, return type overloading, type inference 
and implicit casting. I created the compiler as a class project with Louis Nebout (@lnebout) during my stay at the University
of Bordeaux in 2015.

# Description of the Aura language
The language the compiler accepts is an imperative, structured, object-oriented Java-like language. It uses static strong typing but the compiler can infer types in many situations. Pointers cannot be used directly but user-constructed objects are generally on the heap and and can be accessed by references. Dynamic allocation happens each time a new object or array is constructed. The language supports overloading, including overloading based on return type, and coercion (implicit conversion) of numeric types. The language also supports traditional control flow structures (foreach, if, for, while...) and flow analysis. Syntactically, the language takes elements from Pascal and Java.

The full language specification can be found here (in French only): https://docs.google.com/document/d/1DqyyJlI5K2uXNHxpRqfZiQRm85ZbId6Q6ChVnTvI244/edit?usp=sharing

# Example
Suppose you have this source code in the Aura language:

```
1: result : integer;
2: function factorial(n : integer) : integer {
3:   if (n == 0) return 1;
4:   return n * factorial(n - 1);
5: }
6: procedure main() {
7:   result = factorial(5);
8:   print_int(result);
9: }
```

The command `java compiler.Main source.txt` will output the correct answer `120`. Note that recursion works correctly. The global function `print_int` on line 8 is not defined by the user, it's a "system call" of MARS, the MIPS emulator we use to execute the code.

# Brief overview of the compiler
The compiler accepts an input source file, uses code generated by the utilities JFlex and CUP to perform lexical, syntax and semantic analysis. This creates an abstract syntax tree. The compiler makes use of the syntax-directed translation paradigm. Lexical analysis never stops further error analysis. Syntax errors also usually don't abort the analysis but in some cases, an especially bad syntax error can end the translation immediately. Semantic errors never stop semantic analysis.

At the end of this process, if no error triggered, intermediate code is generated. This is a three-address-code that only exists in the memory of the compiler process (but it can be printed on standard output for debugging purposes). Intermediate code generation cannot fail.

Finally, the intermediate code is transformed into assembly code for the MIPS processor. This code can be interpreted by an MIPS interpreter. The MIPS emulator MARS is bundled with this compiler. The transformation into MIPS assembly may fail because we didn't implement the code generation fully. Most notably, we did not implement the translation of class methods and generic functions with type arguments into MIPS assembly.

# Tests
For examples of how source files of the Aura language look like, you can have a look at one of the hundreds of automatic tests bundled with the project.

# Final report
You may also look at the final report from this project (in French only): https://docs.google.com/document/d/1pIglqJoZaqQp8zAOrckuh7TzJPvqTCOz7ORQegNC4yA/edit?usp=sharing