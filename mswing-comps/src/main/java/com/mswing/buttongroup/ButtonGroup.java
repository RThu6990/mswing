package com.mswing.buttongroup;

import javax.swing.ButtonModel;
import javax.swing.event.EventListenerList;

/**
 * 
 * @author RThu
 *
 */
public class ButtonGroup extends javax.swing.ButtonGroup {
	private static final long serialVersionUID = 1L;
	
	private ButtonModel selectedButtonModel;
	
	private final EventListenerList listenerList = new EventListenerList();

	@Override
	public void setSelected(ButtonModel m, boolean b) {
		super.setSelected(m, b);
		
		if(selectedButtonModel != m && b) {
			selectedButtonModel = m;
		}
		else {
			if(selectedButtonModel == m && b) {
				fireEvent();
			}
		}
	}
	
	@Override
	public void clearSelection() {
		super.clearSelection();
		
		selectedButtonModel = null;
	}

	public void addSelectionChangedListener(ButtonGroupSelectionChangedListener listener) {
		listenerList.add(ButtonGroupSelectionChangedListener.class, listener);
	}
	
	public void removeSelectionChangedListener(ButtonGroupSelectionChangedListener listener) {
		listenerList.remove(ButtonGroupSelectionChangedListener.class, listener);
	}
	
	private void fireEvent() {
		if(listenerList.getListenerCount() == 0) {
			return;
		}
		
		Object[] listeners = listenerList.getListenerList();
		for(int i = 0; i < listeners.length; i += 2) {
			if(listeners[i] == ButtonGroupSelectionChangedListener.class) {
				((ButtonGroupSelectionChangedListener) listeners[i + 1]).selectionChanged(new ButtonGroupSelectionChangeEvent(this));
			}
		}
	}
	
}
