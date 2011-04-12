package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;

public interface Proposition {

	public boolean evaluate(ExecutionEnvironment environment);
	
}
