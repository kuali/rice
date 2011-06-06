package org.kuali.rice.krms.impl.repository

/**
 * This class represents an AgendaAttribute business object.
 * The agenda attributes are used to associate an agenda with an event and to 
 * distinguish agendas from each other.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AgendaAttributeBo extends BaseAttributeBo{

	// reference to the agenda associated with this attribute
	def String agendaId

} 