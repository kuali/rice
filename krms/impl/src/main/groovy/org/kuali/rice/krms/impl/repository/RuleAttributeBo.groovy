package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.RuleAttribute
import org.kuali.rice.krms.api.repository.RuleAttributeContract
import org.kuali.rice.krms.api.repository.KrmsAttributeDefinition

public class RuleAttributeBo extends PersistableBusinessObjectBase implements RuleAttributeContract{

	def String id
	def String ruleId
	def String attributeDefinitionId
	def String value
	
	def KrmsAttributeDefinition attributeDefinition
	
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static RuleAttribute to(RuleAttributeBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.RuleAttribute.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static RuleAttributeBo from(RuleAttribute im) {
	   if (im == null) { return null }

	   RuleAttributeBo bo = new RuleAttributeBo()
	   bo.id = im.id
	   bo.ruleId = im.ruleId
	   bo.attributeDefinitionId = im.attributeDefinitionId
	   bo.attributeValue = im.value
	   bo.attributeDefinition = KrmsAttributeDefinitionBo.from( im.attributeDefinition )

	   return bo
   }

   @Override
   KrmsAttributeDefinitionBo getAttributeDefinition() {
	   return attributeDefinition
   }
} 