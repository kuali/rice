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
	EngineResults execute(SelectionCriteria selectionCriteria, Facts facts, ExecutionOptions executionOptions);

    /**
     * Initiates execution of the rules engine.
     *
     * @param selectionCriteria informs the engine of the criteria to use for selection of contexts and agendas
     * @param facts the facts that the rule engine can use during execution.  Since this signature does not pass in
     * {@link Term}s, all terms are defined with only a name, and term parameters can not be specified.
     * @param executionOptions defines various options that instruct the rules engine on how to perform it's execution
     *
     * @return the results of engine execution
     */
    EngineResults execute(SelectionCriteria selectionCriteria, Map<String, Object> facts, ExecutionOptions executionOptions);

}
