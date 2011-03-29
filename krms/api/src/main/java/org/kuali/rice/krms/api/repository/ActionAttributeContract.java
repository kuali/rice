package org.kuali.rice.krms.api.repository;

import java.util.List;

public interface ActionAttributeContract {
	/**
	 * This is the ID for the ActionAttribute 
	 *
	 * <p>
	 * It is a ID of a ActionAttribute
	 * </p>
	 * @return ID for ActionAttribute
	 */
	public String getId();

	/**
	 * This is the id of the Action to which the attribute applies 
	 *
	 * <p>
	 * It is a id of a Action related to the attribute.
	 * </p>
	 * @return id for Action related to the attribute.
	 */
	public String getActionId();

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
