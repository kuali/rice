package org.kuali.rice.krms.api.type;

import java.util.List;

import org.kuali.rice.krms.api.repository.KrmsAttributeDefinitionContract;

public interface KrmsTypeAttributeContract {
	/**
	 * This is the ID for the KrmsTypeAttribute 
	 *
	 * <p>
	 * It is a ID of a KrmsTypeAttribute
	 * </p>
	 * @return ID for KrmsTypeAttribute
	 */
	public String getId();

	/**
	 * This is the KrmsType to which the attribute applies 
	 *
	 * <p>
	 * It is a id of a KRMS type related to the attribute.
	 * </p>
	 * @return id for KRMS type related to the attribute.
	 */
	public String getTypeId();

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
	 * This is the sequence number of the attribute
	 * 
	 * @return the service name of the KrmsTypeAttribute
	 */
	public Integer getSequenceNumber();

	/**
	 * @return the active
	 */
	public boolean isActive();

	/**
	 * This is the definition of the attribute
	 */
	public KrmsAttributeDefinitionContract getAttributeDefinition();
}
