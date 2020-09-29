package com.mswing.comp.tagfield;

import java.util.EventListener;
import java.util.List;

/**
 * 
 * @author RThu
 *
 */
public interface TagFieldListener extends EventListener {
	
	List<String> collectingExpectedTags(TagFieldEvent event);
	
	void tagAdded(TagFieldEvent event);
	
	void tagRemoved(TagFieldEvent event);
	
}
