package org.kuali.rice.krms.engine;

import java.util.Map;

public interface ExecutionEnvironment {
	
	/**
	 * Returns the selection criteria that was used to initialize the environment.
	 * 
	 * @return the selection criteria for this environment
	 */
	public SelectionCriteria getSelectionCriteria();
	
	/**
	 * Returns an immutable Map of facts available within this environment.
	 * 
	 * @return the facts in this environment
	 */
	public Map<Term, Object> getFacts();

	/**
	 * Publishes a new fact
	 * 
	 * @param factName name of the fact to publish
	 * @param factValue value of the fact to publish
	 * // TODO: we don't support updating facts, refactor this method
	 * @return true if an existing fact was updated, false if this was a new fact
	 */
	public boolean publishFact(Term factName, Object factValue);
	
	public void addTermResolver(TermResolver<?> termResolver);

	public <T> T resolveTerm(Term term) throws TermResolutionException;
	
	public Map<String, String> getExecutionOptions();
	
	public EngineResults getEngineResults();
	
}
