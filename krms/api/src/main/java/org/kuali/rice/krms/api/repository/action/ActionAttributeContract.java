package org.kuali.rice.krms.api.repository.action;

import org.kuali.rice.krms.api.repository.BaseAttributeContract;

public interface ActionAttributeContract extends BaseAttributeContract {

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
	 * This is the id of the action type of the attribute
	 * 
	 * @return the action type id of the Attribute
	 */
	public String getActionTypeId();

}
