package org.kuali.rice.krms.api;

import java.util.Map;

public interface ContextProvider {

	public Context loadContext(SelectionCriteria selectionCriteria, Map<Asset, Object> facts, Map<String, String> executionOptions);
	
}
