package com.gamevm.compiler.tools.ast;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.antlr.runtime.tree.CommonTree;

public class CommonTreeModel implements TreeModel {
	
	private CommonTree tree;
	
	private List<TreeModelListener> listeners;
	
	public CommonTreeModel(CommonTree tree) {
		this.tree = tree;
		listeners = new ArrayList<TreeModelListener>();
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public Object getChild(Object node, int index) {
		return ((CommonTree)node).getChild(index);
	}

	@Override
	public int getChildCount(Object node) {
		return ((CommonTree)node).getChildCount();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((CommonTree)child).childIndex;
	}

	@Override
	public Object getRoot() {
		return tree;
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object node) {
		// ignore for now
	}

}
