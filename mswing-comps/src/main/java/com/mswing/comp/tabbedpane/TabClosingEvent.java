package com.mswing.comp.tabbedpane;

import java.awt.Component;
import java.util.EventObject;

/**
 * 
 * @author RThu
 *
 */
public class TabClosingEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	
	private int closingTabIndex;
	
	private Component closingTabComponent;
	
	public TabClosingEvent(TabbedPane source, int closingTabIndex, Component closingTabComponent) {
		super(source);
		this.closingTabIndex = closingTabIndex;
		this.closingTabComponent = closingTabComponent;
	}
	
	public int getClosingTabIndex() {
		return this.closingTabIndex;
	}
	
	public Component getClosingTabComponent() {
		return this.closingTabComponent;
	}
	
}
