package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;

public interface Agenda {

	public void execute(ExecutionEnvironment environment);
	
	public boolean appliesTo(ExecutionEnvironment environment);
	
}
