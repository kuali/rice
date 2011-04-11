package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.AgendaAttribute
import org.kuali.rice.krms.api.repository.AgendaAttributeContract
import org.kuali.rice.krms.api.repository.KrmsAttributeDefinition

public class AgendaAttributeBo extends PersistableBusinessObjectBase implements AgendaAttributeContract{

	def String id
	def String agendaId
	def String attributeDefinitionId
	def String value
	
	def KrmsAttributeDefinition attributeDefinition
	
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static AgendaAttribute to(AgendaAttributeBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.AgendaAttribute.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static AgendaAttributeBo from(AgendaAttribute im) {
	   if (im == null) { return null }

	   AgendaAttributeBo bo = new AgendaAttributeBo()
	   bo.id = im.id
	   bo.AgendaId = im.agendaId
	   bo.attributeDefinitionId = im.attributeDefinitionId
	   bo.value = im.value
	   bo.attributeDefinition = KrmsAttributeDefinitionBo.from( im.attributeDefinition )

	   return bo
   }

   @Override
   KrmsAttributeDefinitionBo getAttributeDefinition() {
	   return attributeDefinition
   }
} 