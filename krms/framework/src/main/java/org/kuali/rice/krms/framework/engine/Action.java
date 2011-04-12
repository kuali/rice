package org.kuali.rice.krms.framework.engine;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;

public interface Action {

	public void execute(ExecutionEnvironment environment);

	/**
	 * The engine may be run in a simulation mode and in this case,
	 * most actions should not be executed.  However, if part or all of 
	 * an action needs to be run in order for proper rule evaluation to 
	 * proceed, it should be called herein.
	 */
	public void executeSimulation(ExecutionEnvironment environment);
}
