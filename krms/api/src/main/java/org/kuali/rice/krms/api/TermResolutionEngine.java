package org.kuali.rice.krms.api;

/**
 * Interface for the engine that is used to resolve {@link Term}s.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface TermResolutionEngine {

	/**
	 * Resolves a given term into a fact
	 * @param term the {@link Term} to resolve
	 * @return the fact value for the given {@link Term}
	 * @throws TermResolutionException if the given {@link Term} can't be resolved
	 */
	<T> T resolveTerm(Term term) throws TermResolutionException;
	
	/**
	 * Adds a fact value to the {@link TermResolutionEngine}'s internal state
	 * @param term the named Term
	 * @param value the fact value
	 */
	void addTermValue(Term term, Object value);
	
	/**
	 * Adds a {@link TermResolver} to the {@link TermResolutionEngine}.  Once added, it may
	 * be used (unsurprisingly) by the engine to resolve {@link Term}s.
	 * @param termResolver the {@link TermResolver} to add.
	 */
	void addTermResolver(TermResolver<?> termResolver);
	
}
