package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;

public class ContextAttributeBo extends PersistableBusinessObjectBase {

	def String id
	def String contextId
	def String attributeDefinitionId
	def String value
	
	def KrmsAttributeDefinitionBo attributeDefinition
	
	
   KrmsAttributeDefinitionBo getAttributeDefinition() {
	   return attributeDefinition
   }
} 