package com.gamevm.compiler.tools.ast;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.gamevm.compiler.assembly.ClassDefinition;
import com.gamevm.compiler.assembly.Method;
import com.gamevm.compiler.assembly.code.TreeCode;
import com.gamevm.compiler.parser.ASTNode;

public class ASTTreeAdapter implements TreeModel {

	private ClassDefinition<TreeCode<ASTNode>> classDef;

	public ASTTreeAdapter(ClassDefinition<TreeCode<ASTNode>> classDef) {
		this.classDef = classDef;
	}

	@Override
	public Object getRoot() {
		return classDef;
	}

	private int getMethodIndex(Method m) {
		for (int i = 0; i < classDef.getMethodCount(); i++) {
			if (classDef.getMethod(i) == m)
				return i;
		}
		return -1;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof ClassDefinition) {
			if (index > 1)
				return classDef.getMethod(index-2);
			else if (index == 1) {
				return ClassDefinition.IMPLICIT_CONSTRUCTOR;
			} else if (index == 0) {
				return ClassDefinition.STATIC_CONSTRUCTOR;
			}
		} else if (parent == ClassDefinition.IMPLICIT_CONSTRUCTOR) {
			return classDef.getImplicitConstructor().getRoot().getChildAt(index);
		} else if (parent == ClassDefinition.STATIC_CONSTRUCTOR) {
			return classDef.getStaticConstructor().getRoot().getChildAt(index);
		} else if (parent instanceof Method) {
			return classDef.getImplementation(getMethodIndex((Method) parent)).getRoot().getChildAt(index);
		} else if (parent instanceof ASTNode) {
			return ((ASTNode) parent).getChildAt(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof ClassDefinition) {
			return classDef.getMethodCount() + 2;
		} else if (parent == ClassDefinition.IMPLICIT_CONSTRUCTOR) {
			return classDef.getImplicitConstructor().getRoot().getChildCount();
		} else if (parent == ClassDefinition.STATIC_CONSTRUCTOR) {
			return classDef.getStaticConstructor().getRoot().getChildCount();
		} else if (parent instanceof Method) {
			return classDef.getImplementation(getMethodIndex((Method) parent)).getRoot().getChildCount();
		} else if (parent instanceof ASTNode) {
			return ((ASTNode) parent).getChildCount();
		}
		return 0;
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// ignore
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof ClassDefinition) {
			return getMethodIndex((Method) child);
		} else if (parent == ClassDefinition.IMPLICIT_CONSTRUCTOR) {
			return classDef.getImplicitConstructor().getRoot().indexOf((ASTNode)child);
		} else if (parent == ClassDefinition.STATIC_CONSTRUCTOR) {
			return classDef.getStaticConstructor().getRoot().indexOf((ASTNode)child);
		} else if (parent instanceof Method) {
			return classDef.getImplementation(getMethodIndex((Method) parent)).getRoot().indexOf((ASTNode)child);
		} else if (parent instanceof ASTNode) {
			return ((ASTNode) parent).indexOf((ASTNode) child);
		}
		return -1;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		// ignore
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		// ignore
	}

}
