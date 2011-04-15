package org.kuali.rice.krms.framework.engine;

import java.util.Map;

import org.kuali.rice.krms.api.engine.ExecutionOptions;
import org.kuali.rice.krms.api.engine.SelectionCriteria;
import org.kuali.rice.krms.api.engine.Term;

/**
 * Loads a {@link Context} for the given set of criteria.  Applications who
 * want to provide their own means for creating a context and supplying it to
 * the KRMS engine can do so by implementing a custom ContextProvider. 
 * 
 * @see Context
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface ContextProvider {

	/**
	 * Loads the context for the given selection criteria, facts, and execution
	 * options.  If no context can be located based on the given criteria, then
	 * this method should return null.
	 * 
	 * <p>In the case where multiple Contexts could potentially be identified
	 * from the same set of selection criteria, it is up to the implementer
	 * of the ContextProvider to ensure that the most appropriate Context is
	 * returned.  Or alternatively, an exception could be thrown indicating
	 * context ambiguity.
	 * 
	 * <p>The sectionCriteria, facts, and executionOptions which are passed to
	 * this method should never be null.  However, they might be empty.
	 * 
	 * @param selectionCriteria the criteria to use during the selection phase of engine operation
	 * @param facts the set of facts that are supplied to the engine at execution time
	 * @param executionOptions a collection of options that can be used to customize engine execution behavior
	 * 
	 * @return the context which matches the given criteria, or null if no context matches
	 */
	public Context loadContext(SelectionCriteria selectionCriteria, Map<Term, Object> facts, ExecutionOptions executionOptions);
	
}
