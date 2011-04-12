package org.kuali.rice.krms.framework;

import java.util.Map;

import org.kuali.rice.krms.engine.Context;
import org.kuali.rice.krms.engine.SelectionCriteria;
import org.kuali.rice.krms.engine.Term;
import org.kuali.rice.krms.framework.engine.ContextProvider;

public class ManualContextProvider implements ContextProvider {

	private Context context;
	
	public ManualContextProvider(Context context) {
		this.context = context;
	}
	
	@Override
	public Context loadContext(SelectionCriteria selectionCriteria, Map<Term, Object> facts, Map<String, String> executionOptions) {
		return context;
	}

}
