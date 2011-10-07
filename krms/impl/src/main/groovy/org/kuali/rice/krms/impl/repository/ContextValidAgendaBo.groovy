package org.kuali.rice.krms.impl.repository

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

/**
 * Indicates an AgendaType that is valid for a Context
 */
public class ContextValidAgendaBo extends PersistableBusinessObjectBase {

	String id
	String contextId
	String agendaTypeId
	
	Long versionNumber

    KrmsTypeBo agendaType
} 