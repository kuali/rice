package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

public class AgendaItemBo extends PersistableBusinessObjectBase {

	def String id
	def String agendaId
	def String ruleId
	def String subAgendaId
	def String nextTrueId
	def String nextFalseId
	def String nextAfterId 
	
	// consider getting full tree
//	def AgendaItemBo nextTrue
//	def AgendaItemBo nextFalse
//	def AgendaItemBo nextAfter
} 