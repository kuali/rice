package org.kuali.rice.krms.framework.engine;

import java.util.List;

import org.kuali.rice.krms.api.engine.Engine;
import org.kuali.rice.krms.api.engine.ExecutionEnvironment;

/**
 * Interface for logical propositions that may be executed in the {@link Engine}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Proposition {

    /**
     * This method evaluates this proposition -- and in the case of {@link Proposition}s containing children,
     * those children as well -- and returns the boolean result; 
     * 
     * @param environment the {@link ExecutionEnvironment} that this {@link Proposition} is running in
     * @return the boolean result of evaluation
     */
	public PropositionResult evaluate(ExecutionEnvironment environment);
	
	/**
	 * This method returns the {@link List} of child {@link Proposition}s that belong to this object.
	 * If there are no children (e.g. for simple {@link Proposition} types), this must
	 * return an empty {@link List}.
	 * 
	 * @return a {@link List} containing any child {@link Proposition}s that belong to this object.  Must never return null.
	 */
	public List<Proposition> getChildren();
	
	/**
	 * Indicates whether this {@link Proposition} can have children.
	 * @return true if this {@link Proposition} can contain child {@link Proposition}s.
	 */
	public boolean isCompound();
		
}
