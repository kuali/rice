package org.kuali.rice.krms.api.repository.agenda;

import org.kuali.rice.krms.api.repository.BaseAttributeContract;

public interface AgendaAttributeContract extends BaseAttributeContract {

	/**
	 * This is the id of the Agenda to which the attribute applies 
	 *
	 * <p>
	 * It is a id of a Agenda related to the attribute.
	 * </p>
	 * @return id for Agenda related to the attribute.
	 */
	public String getAgendaId();

}
