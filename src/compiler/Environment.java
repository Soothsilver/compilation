package compiler;

import compiler.nodes.Parameter;
import compiler.nodes.declarations.*;

import java.util.LinkedList;

public class Environment {
    public boolean inProcedure = false;
    public boolean inFunction = false;
    public TypeOrTypeTemplate returnType = null;
    private Compilation compilation;
    private ScopeTree<Subroutine> subroutineTable = null;
    private ScopeTree<Variable> variableTable = null;
    private ScopeTree<TypeOrTypeTemplate> typeTable = null;
    private LinkedList<ScopeTree<Subroutine>> subroutineStack;
    private LinkedList<ScopeTree<Variable>> variableStack;
    private LinkedList<ScopeTree<TypeOrTypeTemplate>> typeStack;
    private int depth = 0;

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
            debug("Adding " + (decl == null ? "null" : decl) + " to " + (tree == null ? "null" : tree.decl) + ", this is " + (decl == null ? "null" : decl));
            if (tree == null)
                return new ScopeTree<T>(decl);
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

    public Variable findVariable(String identifier) {
        if (variableTable == null)
            return null;
        else
            return variableTable.find(identifier);
    }

    public LinkedList<Subroutine> findSubroutines(String identifier) {
        debug("ENV: Finding " + identifier + ".");
        if (subroutineTable == null) {
            debug("ENV: Returning null immediately.");
            return null;
        } else
            return subroutineTable.findGroup(identifier);
    }

    public void addSubroutine(Subroutine subroutine) {
        if (subroutineTable == null) {
            subroutineTable = new ScopeTree<Subroutine>(subroutine);
        } else
            subroutineTable = subroutineTable.add(subroutine, subroutineTable);
        debug("Added subroutine " + subroutine);
    }

    public void addType(TypeOrTypeTemplate type) {
        if (typeTable == null) {
            typeTable = new ScopeTree<TypeOrTypeTemplate>(type);
        } else
            typeTable = typeTable.add(type, typeTable);
    }


    public void addVariable(Variable variable) {
        if (variableTable == null) {
            variableTable = new ScopeTree<Variable>(variable);
        } else
            variableTable = variableTable.add(variable, variableTable);
    }


    public void enterFunction() {
        inFunction = true;
        enterScope();
    }

    public void enterProcedure() {
        inProcedure = true;
        enterScope();
    }

    public void leaveSubroutine() {
        inFunction = inProcedure = false;
        returnType = null;
        leaveScope();
        leaveVariableScope();
        leaveTypeScope();

    }


    public int cycleDepth = 0;

    public void enterCycle() {
        cycleDepth++;
    }

    public void leaveCycle() {
        cycleDepth++;
    }

    public boolean inCycle() {
        return cycleDepth > 0;
    }


    public void enterScope() {
        subroutineStack.addFirst(subroutineTable);
        variableStack.addFirst(variableTable);
        typeStack.addFirst(typeTable);
        depth++;
        debug("Enter scope.");
    }

    public void leaveScope() {
        depth--;
        subroutineTable = subroutineStack.removeFirst();
        variableTable = variableStack.removeFirst();
        typeTable = typeStack.removeFirst();
        debug("Leave scope.");
    }

    public void enterTypeScope() {
        typeStack.addFirst(typeTable);
        depth++;
        debug("Enter type scope.");
    }

    public void leaveTypeScope() {
        depth--;
        typeTable = typeStack.removeFirst();
        debug("Leave type scope.");
    }
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
        Subroutine writeLine = Subroutine.createPredefined(SubroutineKind.PROCEDURE, "writeln", Type.nullType);
        writeLine.parameters.add(new Parameter("text", Type.stringType));
        addSubroutine(writeLine);
    }

    private void addBinaryOperator(String symbol, Type firstOperand, Type secondOperand, Type returnType) {
        addSubroutine(OperatorFunction.create(symbol, firstOperand, secondOperand, returnType));
    }
    private void addUnaryOperator(String symbol, Type operand,  Type returnType) {
        addSubroutine(OperatorFunction.create(symbol, operand, returnType));
    }

    private void debug(String line) {
        line = "ENV: " + line;
        //System.out.println(line);
    }
    public <T extends Declaration> void debugPrint (ScopeTree<T> tree, String sign, int level) {
        if (tree == null) return;
        for (int i =0; i < level; i++)
            System.out.print(" ");
        System.out.print(sign);
        System.out.println(tree.decl.getSig());
        debugPrint(tree.left, "L", level+1);
        debugPrint(tree.right, "R", level+1);
    }
    public void debugPrintSubroutines() {
        debugPrint(subroutineTable, "ROOT", 0);
    }

}
