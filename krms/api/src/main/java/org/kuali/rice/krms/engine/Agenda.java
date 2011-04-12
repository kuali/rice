package org.kuali.rice.krms.engine;

public interface Agenda {

	public void execute(ExecutionEnvironment environment);
	
	public boolean appliesTo(ExecutionEnvironment environment);
	
}
