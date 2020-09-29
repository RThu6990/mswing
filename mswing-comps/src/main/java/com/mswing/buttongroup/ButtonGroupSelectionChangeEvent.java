package com.mswing.buttongroup;

import java.util.EventObject;
import java.util.Iterator;

import javax.swing.AbstractButton;


/**
 * 
 * @author RThu
 *
 */
public class ButtonGroupSelectionChangeEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	
	private AbstractButton selectedButton;

	public ButtonGroupSelectionChangeEvent(ButtonGroup source) {
		super(source);
		
		Iterator<AbstractButton> itr = source.getElements().asIterator();
		while(itr.hasNext()) {
			if((selectedButton = itr.next()).getModel() == source.getSelection()) {
				break;
			}
		}
	}
	
	public AbstractButton getSelectedButton() {
		return selectedButton;
	}
	
}
