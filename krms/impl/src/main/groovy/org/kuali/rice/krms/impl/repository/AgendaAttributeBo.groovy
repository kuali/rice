package org.kuali.rice.krms.impl.repository

/**
 * This class represents an AgendaAttribute business object.
 * Agenda attributes provide a way to attach custom data to an agenda based on the agenda's type.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AgendaAttributeBo extends BaseAttributeBo {

	// reference to the agenda associated with this attribute
	def String agendaId

} 