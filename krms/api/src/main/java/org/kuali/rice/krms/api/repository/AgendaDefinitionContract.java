package org.kuali.rice.krms.api.repository;

import java.util.List;

public interface AgendaDefinitionContract {
	/**
	 * This is the ID for the Agenda
	 *
	 * <p>
	 * It is a ID of a Agenda
	 * </p>
	 * @return ID for Agenda
	 */
	public String getAgendaId();

	/**
	 * This is the name of the Agenda 
	 *
	 * <p>
	 * name - the name of the Agenda
	 * </p>
	 * @return the name of the Agenda
	 */
	public String getName();

	/**
	 * This is the namespace of the Agenda 
	 *
	 * <p>
	 * The namespace of the Agenda
	 * </p>
	 * @return the namespace of the Agenda
	 */
	public String getNamespaceCode();

	/**
	 * This is the KrmsType of the Agenda
	 *
	 * @return id for KRMS type related of the Agenda
	 */
	public String getTypeId();
	
	public String getContextId();
	
	public String getFirstItemId();
	
	
	/**
	 * This method returns a list of attributes associated with the 
	 * Agenda
	 * 
	 * @return a list of AgendaAttribute objects.
	 */
	public List<? extends AgendaAttributeContract> getAttributes();
	

}
