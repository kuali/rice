package org.kuali.rice.krms.api.repository.rule;

import java.util.List;
import java.util.Set;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krms.api.repository.action.ActionDefinitionContract;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinitionContract;

public interface RuleDefinitionContract extends Identifiable, Versioned {
	/**
	 * This is the name of the Rule 
	 * <p>
	 * name - the name of the Rule
	 * </p>
	 * @return the name of the Rule
	 */
	public String getName();

	/**
	 * This is the namespace of the Rule 
	 * <p>
	 * The namespace of the Rule
	 * </p>
	 * @return the namespace of the Rule
	 */
	public String getNamespace();

	/**
	 * This is the KrmsType of the Rule
	 *
	 * @return id for KRMS type related of the Rule
	 */
	public String getTypeId();
	
	/**
	 * This method returns the ID of the Proposition associated with the rule.
	 * <p>
	 * Each Rule has exactly one Proposition associated with it.
	 * <p>
	 * @return the id of the Proposition associated with the Rule
	 */
	public String getPropId();
	
	/**
	 * This method returns the Proposition associated with the rule.
	 * <p>
	 * Each Rule has exactly one Proposition associated with it.
	 * <p>
	 * @return an immutable represtentation of the Proposition associated with the Rule
	 */
	public PropositionDefinitionContract getProposition();
	
	/**
	 * This method returns a list of Actions associated with the Rule.
	 * <p>
	 * A Rule may have zero or more Actions associated with it.
	 * <p>
	 * @return An ordered list of Actions associated with a Rule.
	 */
	public List<? extends ActionDefinitionContract> getActions();

	/**
	 * This method returns a set of attributes associated with the 
	 * Rule
	 * 
	 * @return a set of RuleAttribute objects.
	 */
	public Set<? extends RuleAttributeContract> getAttributes();
	
}
