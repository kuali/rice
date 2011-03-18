package org.kuali.rice.krms.framework;

import java.util.Map;

import org.kuali.rice.krms.api.Asset;
import org.kuali.rice.krms.api.Context;
import org.kuali.rice.krms.api.ContextProvider;
import org.kuali.rice.krms.api.SelectionCriteria;

public class ManualContextProvider implements ContextProvider {

	private Context context;
	
	public ManualContextProvider(Context context) {
		this.context = context;
	}
	
	@Override
	public Context loadContext(SelectionCriteria selectionCriteria, Map<Asset, Object> facts, Map<String, String> executionOptions) {
		return context;
	}

}
