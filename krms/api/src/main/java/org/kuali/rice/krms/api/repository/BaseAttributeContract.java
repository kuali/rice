package org.kuali.rice.krms.api.repository;

import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinitionContract;

/**
 * Base interface intended for extension by other AttributeContract interfaces 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface BaseAttributeContract {
	/**
	 * This is the ID for the concrete BaseAttribute extender
	 *
	 * <p>
	 * It is a ID of a concrete BaseAttribute extender
	 * </p>
	 * @return ID for concrete BaseAttribute extender
	 */
	public String getId();

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
	 * @return the value of the attribute
	 */
	public String getValue();

	/**
	 * This is the definition of the attribute
	 */
	public KrmsAttributeDefinitionContract getAttributeDefinition();

}
