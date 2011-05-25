package org.kuali.rice.krms.api.repository.agenda;

import java.util.Map;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

public interface AgendaDefinitionContract extends Identifiable, Versioned {
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
	
	/**
	 * This is the ID of the Context relative to the Agenda. 
	 *
	 * @return id for Context relative to the Agenda
	 */	
	public String getContextId();
	
	/**
	 * This is the ID of the first AgendaItem to be executed in the Agenda. 
	 *
	 * @return id of the first AgendaItem.
	 */	
	public String getFirstItemId();
	
	/**
	 * This method returns a list of attributes associated with the 
	 * Agenda
	 * 
	 * @return a list of AgendaAttribute objects.
	 */
	public Map<String, String> getAttributes();
	
}
