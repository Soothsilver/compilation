package compiler;

import java.util.*;
import compiler.nodes.*;
import compiler.nodes.declarations.*;
import compiler.nodes.statements.Statement;

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
        
        public T find (String id) {
        	if (id.compareTo(decl.getId()) == 0)
    		    return decl;
        	else if (id.compareTo(decl.getId()) < 0) {
    		    if (left==null)
    				return null;
    		    else
    				return left.find(id);
    		}
    		else {
    		    if (right==null)
    				return null;
    		    else
    				return right.find(id);
    		}
        }
        
        private LinkedList<T> findGroup (String id, LinkedList<T> group) {
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
        
        public LinkedList<T> findGroup (String id) {
        	LinkedList<T> group = new LinkedList<T>();
			debug("ENV: Finding subroutine group.");
        	return findGroup(id, group);
        }
        
        public ScopeTree<T> add (T decl, ScopeTree<T> tree) {
			debug("Adding " + (decl == null ? "null" : decl) + " to " + (tree == null ? "null" : tree.decl) + ", this is " + (decl == null ? "null" : decl));
        	if (tree == null)
        		return new ScopeTree<T>(decl);
        	else if (decl.getSig().compareTo(this.decl.getSig()) == 0 && scope != depth) {
				//System.out.println("Adding :" + decl.getId() + ":");
				return new ScopeTree<T>(left, right, decl, depth);
			}
        	else if (decl.getSig().compareTo(this.decl.getSig()) == 0) {
        		compilation.semanticError("The symbol " + decl.getSig() + " was already defined at line " + this.decl.line + ", column " + this.decl.column + ".", decl.line, decl.column);
        		return tree;
        	}
        	else if (decl.getSig().compareTo(this.decl.getSig()) < 0)
        		if (left == null)
        			return new ScopeTree<T>(new ScopeTree<T>(decl), right, this.decl, scope);
        		else 
        			return new ScopeTree<T>(left.add(decl, left), right, this.decl, scope);
        	else
        		if (right == null)
        			return new ScopeTree<T>(left, new ScopeTree<T>(decl), this.decl, scope);
        		else
        			return new ScopeTree<T>(left, right.add(decl, right), this.decl, scope);
        }
    }
    
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
    
    /* TODO : make this return a SubroutineGroup */
    public LinkedList<Subroutine> findSubroutines(String identifier) {
		debug("ENV: Finding " + identifier + ".");
    	if (subroutineTable == null) {
			debug("ENV: Returning null immediately.");
			return null;
		}
    	else
    		return subroutineTable.findGroup(identifier);
    }
    
    public void addSubroutine(Subroutine subroutine) {
		if (subroutineTable == null) {
			subroutineTable = new ScopeTree<Subroutine>(subroutine);
		} else
			subroutineTable = subroutineTable.add(subroutine, subroutineTable);

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
 
    
    
    /* TODO */
    public void enterFunction() {
    	inFunction = true;
        enterScope();
        // Add parameters here
    }
    public void enterProcedure() {
    	inProcedure = true;
        enterScope();
        // Add parameters here
    }
    public void leaveSubroutine() {
    	inFunction = inProcedure = false;
    	returnType = null;
        leaveScope();
    }
    /* TODO */


    public void enterScope() {
    	ScopeTree<Subroutine> subroutineTemp = subroutineTable;
    	ScopeTree<Variable> variableTemp = variableTable;
    	ScopeTree<TypeOrTypeTemplate> typeTemp = typeTable;
        subroutineStack.addFirst(subroutineTable);
        variableStack.addFirst(variableTable);
        typeStack.addFirst(typeTable);
        depth++;
    }
    
    public void leaveScope() {
    	depth--;
    	subroutineTable = subroutineStack.removeFirst();
    	variableTable = variableStack.removeFirst();
    	typeTable = typeStack.removeFirst();
    }

	public void addPredefinedTypesConstantsAndFunctions() {
		addType(Type.integerType);
		addType(Type.booleanType);
		addType(Type.characterType);
		addType(Type.floatType);
		addType(Type.stringType);
	}
	private void debug(String line) {
		// System.out.println(line);
	}
}
