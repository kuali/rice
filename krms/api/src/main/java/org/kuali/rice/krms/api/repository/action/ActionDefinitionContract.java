package org.kuali.rice.krms.api.repository.action;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

import java.util.Map;

public interface ActionDefinitionContract extends Identifiable, Versioned {

	/**
	 * This is the name of the Action 
	 *
	 * <p>
	 * name - the name of the Action
	 * </p>
	 * @return the name of the Action
	 */
	public String getName();

	/**
	 * This is the namespace of the Action 
	 *
	 * <p>
	 * The namespace of the Action
	 * </p>
	 * @return the namespace of the Action
	 */
	public String getNamespace();

    /**
     * This is the description for what the parameter is used for.  This can be null or a blank string.
     * @return description
     */
	public String getDescription();

	/**
	 * This is the KrmsType of the Action
	 *
	 * @return id for KRMS type related of the Action
	 */
	public String getTypeId();
	
	/**
	 * This method returns the id of the rule associated with the action
	 * 
	 * @return id for the Rule associated with the action.
	 */
	public String getRuleId();
	
	/**
	 * This method returns the id of the rule associated with the action
	 * 
	 * @return id for the Rule associated with the action.
	 */
	public Integer getSequenceNumber();
	
	/**
	 * This method returns a set of attributes associated with the 
	 * Action
	 * 
	 * @return a set of ActionAttribute objects.
	 */
	public Map<String, String> getAttributes();
	

}
