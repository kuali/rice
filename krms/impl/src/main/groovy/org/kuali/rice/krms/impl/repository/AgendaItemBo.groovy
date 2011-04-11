package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

public class AgendaItemBo extends PersistableBusinessObjectBase {

	def String id
	def String agendaId
	def String priorItemId
	def String entryCondition
	def String ruleId
	def String subAgendaId
} 