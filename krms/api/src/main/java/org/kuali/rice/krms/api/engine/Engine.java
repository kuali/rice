package org.kuali.rice.krms.api.engine;

import java.util.Map;

public interface Engine {

	/**
	 * Initiates execution of the rules engine.
	 * 
	 * @param selectionCriteria informs the engine of the criteria to use for selection of contexts and agendas
	 * @param facts the facts that the rule engine can use during execution
	 * @param executionOptions defines various options that instruct the rules engine on how to perform it's execution
	 * 
	 * @return the results of engine execution
	 */
	public EngineResults execute(SelectionCriteria selectionCriteria, Map<Term, Object> facts, ExecutionOptions executionOptions);
		
}
