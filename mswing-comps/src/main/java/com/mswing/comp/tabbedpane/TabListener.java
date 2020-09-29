package com.mswing.comp.tabbedpane;

import java.util.EventListener;

/**
 * 
 * @author RThu
 *
 */
public interface TabListener extends EventListener {
	
	void tabClosing(TabClosingEvent e);
	
}
