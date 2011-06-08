package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

public class ContextValidEventBo extends PersistableBusinessObjectBase {

	def String id
	def String contextId
	def String eventName
	
	def Long versionNumber
} 