package org.kuali.rice.krms.api.repository;

import java.util.List;

public interface AgendaAttributeContract {
	/**
	 * This is the ID for the AgendaAttribute 
	 *
	 * <p>
	 * It is a ID of a AgendaAttribute
	 * </p>
	 * @return ID for AgendaAttribute
	 */
	public String getId();

	/**
	 * This is the id of the Agenda to which the attribute applies 
	 *
	 * <p>
	 * It is a id of a Agenda related to the attribute.
	 * </p>
	 * @return id for Agenda related to the attribute.
	 */
	public String getAgendaId();

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
