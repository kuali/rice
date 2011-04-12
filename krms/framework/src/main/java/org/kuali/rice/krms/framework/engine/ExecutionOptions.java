package org.kuali.rice.krms.framework.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// TODO: delete this?  We're only using the enum thus far
public class ExecutionOptions {
	private Set<String> executionFlags;
	private Map<String,String> executionOptions;

	public ExecutionOptions(){
		executionFlags = new HashSet<String>();
		executionOptions = new HashMap<String,String>();
	}
	
	public boolean isFlagSet(org.kuali.rice.krms.engine.ExecutionOptions flag){
		return false; 
	}
}
