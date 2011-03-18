package org.kuali.rice.krms.api;

public interface Agenda {

	public void execute(ExecutionEnvironment environment);
	
	public boolean appliesTo(ExecutionEnvironment environment);
	
}
