package com.mswing.buttongroup;

import java.util.EventListener;

/**
 * 
 * @author RThu
 *
 */
public interface ButtonGroupSelectionChangedListener extends EventListener {
	
	void selectionChanged(ButtonGroupSelectionChangeEvent event);
	
}
