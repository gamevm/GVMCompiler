package com.gamevm.execution.ast;

import javax.swing.table.AbstractTableModel;

import com.gamevm.compiler.assembly.Field;

public class DebugModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Environment env;
	
	public DebugModel(Environment env) {
		this.env = env;
	}

	@Override
	public int getRowCount() {
		return getFieldCount() + env.getLocals().length;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}
	
	private int getFieldCount() {
		ClassInstance thisClass = env.getThis();
		if (thisClass != null) {
			return thisClass.getLoadedClass().getDefinition().getFieldCount();
		} else {
			return env.getCurrentClass().getStaticFieldCount();
		}
	}
	
	public void update() {
		fireTableStructureChanged();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Field f = null;
		if (rowIndex < getFieldCount()) {
			f = env.getCurrentClass().getClassInformation().getField(rowIndex);
		}
		switch (columnIndex) {
		case 0:
			if (f != null) {
				return f.getName();
			} else {
				return "$" + (rowIndex - getFieldCount());
			}
		case 1:
			if (f != null) {
				if (f.isStatic()) {
					return env.getCurrentClass().getValue(rowIndex);
				} else {
					return env.getThis().getValue(rowIndex);
				}
			} else {
				return env.getLocals()[rowIndex - getFieldCount()];
			}
		}
		return null;
	}

}
