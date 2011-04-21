package org.kuali.rice.krms.api.repository.rule;

import java.util.List;
import java.util.Set;

import org.kuali.rice.krms.api.repository.action.ActionDefinitionContract;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinitionContract;

public interface RuleDefinitionContract {
	/**
	 * This is the ID for the Rule
	 *
	 * <p>
	 * It is a ID of a Rule
	 * </p>
	 * @return ID for Rule
	 */
	public String getId();

	/**
	 * This is the name of the Rule 
	 *
	 * <p>
	 * name - the name of the Rule
	 * </p>
	 * @return the name of the Rule
	 */
	public String getName();

	/**
	 * This is the namespace of the Rule 
	 *
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
	
	public String getPropId();
	
	public PropositionDefinitionContract getProposition();
	
	public List<? extends ActionDefinitionContract> getActions();

	/**
	 * This method returns a set of attributes associated with the 
	 * Rule
	 * 
	 * @return a set of RuleAttribute objects.
	 */
	public Set<? extends RuleAttributeContract> getAttributes();
	

}
