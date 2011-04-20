package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.action.ActionAttribute;
import org.kuali.rice.krms.api.repository.action.ActionAttributeContract;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;

public class BaseAttributeBo extends PersistableBusinessObjectBase {

	def String id
	def String attributeDefinitionId
	def String value
	def KrmsAttributeDefinition attributeDefinition
	
} 