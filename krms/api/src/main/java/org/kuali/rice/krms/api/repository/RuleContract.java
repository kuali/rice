package org.kuali.rice.krms.api.repository;

import java.util.List;

public interface RuleContract {
	/**
	 * This is the ID for the Rule
	 *
	 * <p>
	 * It is a ID of a Rule
	 * </p>
	 * @return ID for Rule
	 */
	public String getRuleId();

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
	
	public PropositionContract getProposition();
	
	public List<? extends ActionContract> getActions();

	/**
	 * This method returns a list of attributes associated with the 
	 * Rule
	 * 
	 * @return a list of RuleAttribute objects.
	 */
	public List<? extends RuleAttributeContract> getAttributes();
	

}
