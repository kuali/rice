package org.kuali.rice.krms.framework.engine;

import java.util.Collections;
import java.util.Map;

public class PropositionResult {

	final boolean result;
	Map<String,?> executionDetails;

	public PropositionResult(boolean result) {
	    this(result, null);
	}

	public PropositionResult(boolean result, Map<String,?> executionDetails) {
		this.result = result;
		
		if (executionDetails == null) {
		    this.executionDetails = Collections.emptyMap();
		} else {
		    this.executionDetails = Collections.unmodifiableMap(executionDetails);
		}
	}

	public boolean getResult() {
		return result;
	}
	
	public Map<String,?> getExecutionDetails() {
		return executionDetails;
	}
		
}