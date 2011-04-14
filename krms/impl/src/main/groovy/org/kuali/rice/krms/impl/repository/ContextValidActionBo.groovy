package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.RuleAttribute
import org.kuali.rice.krms.api.repository.RuleAttributeContract
import org.kuali.rice.krms.api.repository.KrmsAttributeDefinition

public class ContextValidActionBo extends PersistableBusinessObjectBase {

	def String id
	def String contextId
	def String actionTypeId
	
	def Long versionNumber
} 