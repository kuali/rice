package org.kuali.rice.krms.api;

public interface Context {

	void execute(ExecutionEnvironment environment);
	
	boolean appliesTo(ExecutionEnvironment environment);
	
}
