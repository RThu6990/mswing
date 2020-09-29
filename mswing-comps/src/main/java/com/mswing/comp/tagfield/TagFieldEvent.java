package com.mswing.comp.tagfield;

import java.util.EventObject;

/**
 * 
 * @author RThu
 *
 */
public class TagFieldEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	
	private TagComponent addedTagComponent;
	private TagComponent removedTagComponent;
	private String selectedTag;

	public TagFieldEvent(TagField source, TagComponent addedTagComponent, TagComponent removedTagComponent, String selectedTag) {
		super(source);
		
		this.addedTagComponent = addedTagComponent;
		this.removedTagComponent = removedTagComponent;
		this.selectedTag = selectedTag;
	}
	
	public TagComponent getAddedTagComponent() {
		return addedTagComponent;
	}
	
	public TagComponent getRemovedTagComponent() {
		return removedTagComponent;
	}
	
	public String getSelectedTag() {
		return selectedTag;
	}
	
	public String getFilterText() {
		return ((TagField) source).getFilterText();
	}

}
