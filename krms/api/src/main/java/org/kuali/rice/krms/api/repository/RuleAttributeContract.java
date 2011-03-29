package org.kuali.rice.krms.api.repository;

import java.util.List;

public interface RuleAttributeContract {
	/**
	 * This is the ID for the RuleAttribute 
	 *
	 * <p>
	 * It is a ID of a RuleAttribute
	 * </p>
	 * @return ID for RuleAttribute
	 */
	public String getId();

	/**
	 * This is the id of the Rule to which the attribute applies 
	 *
	 * <p>
	 * It is a id of a Rule related to the attribute.
	 * </p>
	 * @return id for Rule related to the attribute.
	 */
	public String getRuleId();

	/**
	 * This is the id of the definition of the attribute. 
	 *
	 * <p>
	 * It identifies the attribute definition
	 * </p>
	 * @return the attribute definition id.
	 */
	public String getAttributeDefinitionId();

	/**
	 * This is the value of the attribute
	 * 
	 * @return the value of the AAttribute
	 */
	public String getValue();

	/**
	 * This is the definition of the attribute
	 */
	public KrmsAttributeDefinitionContract getAttributeDefinition();

}
