package compiler;

import compiler.nodes.Parameter;
import compiler.nodes.declarations.*;

import java.util.LinkedList;

/**
 * Represents the symbol tables and other information that must be stored during syntactic analysis.
 */
@SuppressWarnings("Convert2Diamond")
public class Environment {
    /**
     * We are currently processing a procedure definition.
     */
    public boolean inProcedure = false;
    /**
     * We are currently processing a function definition.
     */
    public boolean inFunction = false;
    /**
     * The declared return type of the function we are currently processing. This is used to check that
     * ReturnStatement has a correct type.
     */
    public TypeOrTypeTemplate returnType = null;
    private Compilation compilation;
    private ScopeTree<Subroutine> subroutineTable = null;
    private ScopeTree<Variable> variableTable = null;
    private ScopeTree<TypeOrTypeTemplate> typeTable = null;
    private LinkedList<ScopeTree<Subroutine>> subroutineStack;
    private LinkedList<ScopeTree<Variable>> variableStack;
    private LinkedList<ScopeTree<TypeOrTypeTemplate>> typeStack;
    private int depth = 0;

    /**
     * Initializes a new Environment object with empty symbol tables.
     * @param compilation The compilation object.
     */
    public Environment(Compilation compilation) {
        this.compilation = compilation;
        subroutineStack = new LinkedList<ScopeTree<Subroutine>>();
        variableStack = new LinkedList<ScopeTree<Variable>>();
        typeStack = new LinkedList<ScopeTree<TypeOrTypeTemplate>>();
    }



    // All "find" methods return null if they cannot find the thing specified.
    // All "add" methods report an error if an attempt is made to add a thing that already exists (and in case of subroutines, is absolutely identical).
    //  To report this error, use the Compilation class.

    private class ScopeTree<T extends Declaration> {

        private ScopeTree<T> left;
        private ScopeTree<T> right;
        private T decl;
        int scope;

        private ScopeTree(ScopeTree<T> left, ScopeTree<T> right, T decl, int scope) {
            this.left = left;
            this.right = right;
            this.decl = decl;
            this.scope = scope;
        }

        public ScopeTree(T decl) {
            this(null, null, decl, depth);
        }

        public T find(String id) {
            if (id.compareTo(decl.getId()) == 0)
                return decl;
            else if (id.compareTo(decl.getId()) < 0) {
                if (left == null)
                    return null;
                else
                    return left.find(id);
            } else {
                if (right == null)
                    return null;
                else
                    return right.find(id);
            }
        }

        private LinkedList<T> findGroup(String id, LinkedList<T> group) {
            debug("ENV: Comparing (" + id + ") to (" + decl.getId() + ").");
            if (id.compareTo(decl.getId()) == 0) {
                debug("ENV: Yes!");
                group.addFirst(decl);
            }
            if (id.compareTo(decl.getId()) <= 0) {
                debug("ENV: Going left.");
                if (left != null) {
                    left.findGroup(id, group);
                } else {
                    debug("ENV: There's nothing.");
                }
            }
            if (id.compareTo(decl.getId()) >= 0) {
                debug("ENV: Going right.");
                if (right != null) {
                    right.findGroup(id, group);
                } else {
                    debug("ENV: There's nothing.");
                }
            }
            return group;
        }

        public LinkedList<T> findGroup(String id) {
            LinkedList<T> group = new LinkedList<T>();
            debug("ENV: Finding subroutine group.");
            return findGroup(id, group);
        }

        public ScopeTree<T> add(T decl, ScopeTree<T> tree) {
            assert  decl != null;
            debug("Adding " + (decl) + " to " + (tree == null ? "null" : tree.decl) + ", this is " + (decl));
            if (tree == null) {
                throw new RuntimeException("This should never happen.");
                //return new ScopeTree<T>(decl);
            }
            else if (decl.getSig().compareTo(this.decl.getSig()) == 0 && scope != depth) {
                //System.out.println("Adding :" + decl.getId() + ":");
                return new ScopeTree<T>(left, right, decl, depth);
            } else if (decl.getSig().compareTo(this.decl.getSig()) == 0) {
                compilation.semanticError("The symbol '" + decl.getHumanSig() + "' was already defined at line " + (1 + this.decl.line) + ", column " + (1 + this.decl.column) + ".", decl.line, decl.column);
                return tree;
            } else if (decl.getSig().compareTo(this.decl.getSig()) < 0)
                if (left == null)
                    return new ScopeTree<T>(new ScopeTree<T>(decl), right, this.decl, scope);
                else
                    return new ScopeTree<T>(left.add(decl, left), right, this.decl, scope);
            else if (right == null)
                return new ScopeTree<T>(left, new ScopeTree<T>(decl), this.decl, scope);
            else
                return new ScopeTree<T>(left, right.add(decl, right), this.decl, scope);
        }
    }

    /**
     * Searches the symbol tables for a type or a type template with the specified name. Returns null if it cannot be found.
     * @param identifier Name of the type to find.
     * @return The found type, or null.
     */
    public TypeOrTypeTemplate findType(String identifier) {
        // This means we don't support type overloading.
        // i.e. this code is an error:
        // class A { }
        // class A<T> { }
        if (typeTable == null)
            return null;
        else
            return typeTable.find(identifier);
    }

    /**
     * Searches the symbol tables for a variable with the specified name. Returns null if it doesn't find the variable.
     * @param identifier The variable name.
     */
    public Variable findVariable(String identifier) {
        if (variableTable == null)
            return null;
        else
            return variableTable.find(identifier);
    }

    /**
     * Searches the symbol tables for a subroutine with the specified name. Returns a list of all subroutines,
     * in an unspecified order, that have the same. It returns both generic and non-generic subroutines.
     * @param identifier The subroutine name.
     */
    public LinkedList<Subroutine> findSubroutines(String identifier) {
        if (subroutineTable == null) {
            return null;
        } else
            return subroutineTable.findGroup(identifier);
    }

    /**
     * Adds a new subroutine to the symbol tables. Triggers an error if a subroutine with the specified signature
     * already exists.
     *
     * Once the subroutine enters the symbol tables, it is the user's responsibility that the
     * subroutine's signature does not change because it is put in a search tree.
     *
     * @param subroutine The subroutine to add.
     */
    public void addSubroutine(Subroutine subroutine) {
        if (subroutineTable == null) {
            subroutineTable = new ScopeTree<Subroutine>(subroutine);
        } else
            subroutineTable = subroutineTable.add(subroutine, subroutineTable);
        debug("Added subroutine " + subroutine);
    }
    /**
     * Adds a new type or type template to the symbol tables. Triggers an error if a type or type template with specified
     * name already exists. Types and type templates share the same namespace.
     *
     * Once the type enters the symbol tables, it is the user's responsibility to ensure that the
     * type's name does not change because it is put in a search tree.
     *
     * @param type The type or type template to add.
     */
    public void addType(TypeOrTypeTemplate type) {
        if (typeTable == null) {
            typeTable = new ScopeTree<TypeOrTypeTemplate>(type);
        } else
            typeTable = typeTable.add(type, typeTable);
    }


    /**
     * Adds a new variable to the symbol tables. Triggers an error if a variable with the same name was already declared
     * in the innermost active scope. Hiding is an option.
     *
     * Once the variable enters the symbol tables, it is the user's responsibility to ensure that the
     * variable's name does not change because it is put in a search tree.
     *
     * @param variable The variable to add.
     */
    public void addVariable(Variable variable) {
        if (variableTable == null) {
            variableTable = new ScopeTree<Variable>(variable);
        } else
            variableTable = variableTable.add(variable, variableTable);
    }


    /**
     * Sets "inFunction = true". This is useful to analyze whether a return statement is legal.
     */
    public void enterFunction() {
        inFunction = true;
    }

    /**
     * Sets "inProcedure = "true". This is useful to analyze whether a stop statement is legal.
     */
    public void enterProcedure() {
        inProcedure = true;
    }

    /**
     * Clears the flags inProcedure and inFunction, clears the return type and leaves variable and type scope,
     * but not subroutine scope.
     */
    public void leaveSubroutine() {
        inFunction = inProcedure = false;
        returnType = null;
        leaveVariableScope();
        leaveTypeScope();
    }


    /**
     * The number of cycles we are in. This is used to determine whether a break statement is legal.
     */
    public int cycleDepth = 0;

    /**
     * Executes "cycleDepth++".
     */
    public void enterCycle() {
        cycleDepth++;
    }

    /**
     * Executes "cycleDepth--".
     */
    public void leaveCycle() {
        cycleDepth--;
    }

    /**
     * Indicates whether the node currently analyzed is inside a loop.
     * Implementation Notes: This is done by testing "cycleDepth > 0".
     */
    public boolean inCycle() {
        return cycleDepth > 0;
    }


    /**
     * Descends to a new block for all three namespaces.
     */
    public void enterScope() {
        subroutineStack.addFirst(subroutineTable);
        variableStack.addFirst(variableTable);
        typeStack.addFirst(typeTable);
        depth++;
        debug("Enter scope.");
    }

    /**
     * Returns to an upper block for all three namespaces.
     */
    public void leaveScope() {
        depth--;
        subroutineTable = subroutineStack.removeFirst();
        variableTable = variableStack.removeFirst();
        typeTable = typeStack.removeFirst();
        debug("Leave scope.");
    }

    /**
     * Creates a new scope for types and enters it.
     * This means the types declared there won't be available when this scope is exited.
     */
    public void enterTypeScope() {
        typeStack.addFirst(typeTable);
        depth++;
        debug("Enter type scope.");
    }

    /**
     * Exists the innermost scope for the type namespace only. Types declared since the last enterTypeScope call
     * will no longer be accessible.
     */
    public void leaveTypeScope() {
        depth--;
        typeTable = typeStack.removeFirst();
        debug("Leave type scope.");
    }

    /**
     * Creates a new scope for variables and enters it.
     * Variables declared after this call will hide previous variables and will cease to to be visible after the
     * corresponding leaveVariableScope call. The leaveVariableScope function is usually called by leaveSubroutine.
     */
    public void enterVariableScope() {
        variableStack.addFirst(variableTable);
        depth++;
        debug("Enter variable scope.");
    }

    private void leaveVariableScope() {
        depth--;
        variableTable = variableStack.removeFirst();
        debug("Leave variable scope.");
    }

    /**
     * Adds vast amount of symbols to the symbol tables - notably all five predefined types
     * (integer, boolean, character, float, string), all operators and all system calls.
     */
    public void addPredefinedTypesConstantsAndFunctions() {
        addType(Type.integerType);
        addType(Type.booleanType);
        addType(Type.characterType);
        addType(Type.floatType);
        addType(Type.stringType);

        // Add operators
//terminal TIMES, DIVIDE, MODULO;
        addBinaryOperator("*", Type.integerType, Type.integerType, Type.integerType);
        addBinaryOperator("*", Type.floatType, Type.floatType, Type.floatType);
        addBinaryOperator("/", Type.integerType, Type.integerType, Type.integerType);
        addBinaryOperator("/", Type.floatType, Type.floatType, Type.floatType);
        addBinaryOperator("%", Type.integerType, Type.integerType, Type.integerType);
//terminal LESSTHAN, LESSOREQUAL, GREATERTHAN, GREATEROREQUAL;
        addBinaryOperator("<", Type.integerType, Type.integerType, Type.booleanType);
        addBinaryOperator("<", Type.floatType, Type.floatType, Type.booleanType);
        addBinaryOperator(">", Type.integerType, Type.integerType, Type.booleanType);
        addBinaryOperator(">", Type.floatType, Type.floatType, Type.booleanType);
        addBinaryOperator("<=", Type.integerType, Type.integerType, Type.booleanType);
        addBinaryOperator("<=", Type.floatType, Type.floatType, Type.booleanType);
        addBinaryOperator(">=", Type.integerType, Type.integerType, Type.booleanType);
        addBinaryOperator(">=", Type.floatType, Type.floatType, Type.booleanType);
//terminal LOGICALAND, LOGICALOR;
        addBinaryOperator("&&", Type.booleanType, Type.booleanType, Type.booleanType);
        addBinaryOperator("||", Type.booleanType, Type.booleanType, Type.booleanType);
//terminal BITWISEAND, BITWISEOR, XOR, SHIFTLEFT, SHIFTRIGHT;
        addBinaryOperator("&", Type.integerType, Type.integerType, Type.integerType);
        addBinaryOperator("|", Type.integerType, Type.integerType, Type.integerType);
        addBinaryOperator("^", Type.integerType, Type.integerType, Type.integerType);
        addBinaryOperator("<<", Type.integerType, Type.integerType, Type.integerType);
        addBinaryOperator(">>", Type.integerType, Type.integerType, Type.integerType);
//terminal INCREMENT, DECREMENT;
        addUnaryOperator("PRE++", Type.integerType, Type.integerType);
        addUnaryOperator("POST++", Type.integerType, Type.integerType);
        addUnaryOperator("PRE--", Type.integerType, Type.integerType);
        addUnaryOperator("POST--", Type.integerType, Type.integerType);
//terminal LOGICALNEGATION, BITWISENEGATION;
        addUnaryOperator("!", Type.booleanType, Type.booleanType);
        addUnaryOperator("~", Type.integerType, Type.integerType);
//terminal MINUS, PLUS;
        addBinaryOperator("+", Type.integerType, Type.integerType, Type.integerType);
        addBinaryOperator("+", Type.floatType, Type.floatType, Type.floatType);
        addBinaryOperator("+", Type.stringType, Type.characterType, Type.stringType);
        addBinaryOperator("+", Type.characterType, Type.stringType, Type.stringType);
        addBinaryOperator("+", Type.stringType, Type.stringType, Type.stringType);
        addBinaryOperator("+", Type.stringType, Type.integerType, Type.stringType);
        addBinaryOperator("+", Type.integerType, Type.stringType, Type.stringType);
        addBinaryOperator("+", Type.stringType, Type.floatType, Type.stringType);
        addBinaryOperator("+", Type.floatType, Type.stringType, Type.stringType);
        addBinaryOperator("+", Type.stringType, Type.booleanType, Type.stringType);
        addBinaryOperator("+", Type.booleanType, Type.stringType, Type.stringType);
        addBinaryOperator("-", Type.integerType, Type.integerType, Type.integerType);
        addBinaryOperator("-", Type.floatType, Type.floatType, Type.floatType);
        addUnaryOperator("+", Type.integerType, Type.integerType);
        addUnaryOperator("-", Type.integerType, Type.integerType);
        addUnaryOperator("+", Type.floatType, Type.floatType);
        addUnaryOperator("-", Type.floatType, Type.floatType);


//terminal CONCATENATE;
        addSubroutine(OperatorFunction.createConcatenate());
//terminal EQUAL;
        addSubroutine(OperatorFunction.createGeneralAssignment());
//terminal EQUALEQUAL, UNEQUAL;
        addSubroutine(OperatorFunction.createEquality());
        addSubroutine(OperatorFunction.createInequality());
//terminal UNARYPLUS, UNARYMINUS, UNARYTIMES, UNARYDIVIDE, UNARYMODULO
        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.integerType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.floatType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.stringType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.stringType, Type.floatType));
        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.stringType, Type.characterType));
        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.stringType, Type.stringType));
        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.stringType, Type.booleanType));
        addSubroutine(OperatorFunction.createSpecialAssignment("+=", Type.floatType, Type.floatType));
        addSubroutine(OperatorFunction.createSpecialAssignment("-=", Type.integerType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("-=", Type.floatType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("-=", Type.floatType, Type.floatType));
        addSubroutine(OperatorFunction.createSpecialAssignment("*=", Type.integerType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("*=", Type.floatType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("*=", Type.floatType, Type.floatType));
        addSubroutine(OperatorFunction.createSpecialAssignment("/=", Type.integerType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("/=", Type.floatType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("/=", Type.floatType, Type.floatType));
//terminal UNARYSHIFTLEFT, UNARYSHIFTRIGHT, UNARYBITWISEAND, UNARYXOR, UNARYBITWISEOR;
        addSubroutine(OperatorFunction.createSpecialAssignment("%=", Type.integerType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("<<=", Type.integerType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment(">>=", Type.integerType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("^=", Type.integerType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("|=", Type.integerType, Type.integerType));
        addSubroutine(OperatorFunction.createSpecialAssignment("&=", Type.integerType, Type.integerType));
        // Add predefined subroutines

        SystemCall exit = new SystemCall("exit", 10, Type.voidType);
        addSubroutine(exit);
        SystemCall printInt = new SystemCall("print_int", 1, Type.voidType);
        printInt.parameters.add((new Parameter("$a0", Type.integerType)));
        addSubroutine(printInt);
        SystemCall printFloat = new SystemCall("print_float", 2, Type.voidType);
        printFloat.parameters.add((new Parameter("$f12", Type.floatType)));
        addSubroutine(printFloat);
        SystemCall printString = new SystemCall("print_string", 4, Type.voidType);
        printString.parameters.add((new Parameter("$a0", Type.stringType)));
        addSubroutine(printString);
        SystemCall printCharacter = new SystemCall("print_character", 11, Type.voidType);
        printCharacter.parameters.add((new Parameter("$a0", Type.characterType)));
        addSubroutine(printCharacter);

    }

    private void addBinaryOperator(String symbol, Type firstOperand, Type secondOperand, Type returnType) {
        addSubroutine(OperatorFunction.create(symbol, firstOperand, secondOperand, returnType));
    }
    private void addUnaryOperator(String symbol, Type operand,  Type returnType) {
        addSubroutine(OperatorFunction.create(symbol, operand, returnType));
    }

    private void debug(String line) {
       //     System.out.println(line);
    }

    /**
     * Prints the tree that stores symbols for a particular namespace in a way that is readable for humans.
     * @param tree The namespace to print out.
     * @param sign Text that should be prepended to the symbol.
     * @param level Number of spaces to put before each symbol.
     * @param <T> Namespace type.
     */
    private  <T extends Declaration> void debugPrint (ScopeTree<T> tree, String sign, int level) {
        if (tree == null) return;
        for (int i =0; i < level; i++)
            System.out.print(" ");
        System.out.print(sign);
        System.out.println(tree.decl.getSig());
        debugPrint(tree.left, "L", level+1);
        debugPrint(tree.right, "R", level+1);
    }

    /**
     * Prints a human-readable version of the subroutine namespace.
     */
    public void debugPrintSubroutines() {
        debugPrint(subroutineTable, "ROOT", 0);
    }

}
