package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.KrmsAttributeDefinition
import org.kuali.rice.krms.api.repository.action.ActionAttribute;
import org.kuali.rice.krms.api.repository.action.ActionAttributeContract;

public class ActionAttributeBo extends PersistableBusinessObjectBase implements ActionAttributeContract{

	def String id
	def String actionId
	def String attributeDefinitionId
	def String value
	def String actionTypeId
	def KrmsAttributeDefinition attributeDefinition
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static ActionAttribute to(ActionAttributeBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.action.ActionAttribute.Builder.create(bo).build();
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static ActionAttributeBo from(ActionAttribute im) {
	   if (im == null) { return null }

	   ActionAttributeBo bo = new ActionAttributeBo()
	   bo.id = im.id
	   bo.actionId = im.actionId
	   bo.attributeDefinitionId = im.attributeDefinitionId
	   bo.value = im.value
	   bo.actionTypeId = im.actionTypeId
	   bo.attributeDefinition = KrmsAttributeDefinitionBo.from(im.attributeDefinition)
	   return bo
   }
 
	@Override
	KrmsAttributeDefinitionBo getAttributeDefinition() {
		return attributeDefinition
	}
	
} 