package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

import org.kuali.rice.krms.api.repository.TermResolverAttribute
import org.kuali.rice.krms.api.repository.TermResolverAttributeContract
import org.kuali.rice.krms.api.repository.KrmsAttributeDefinition

public class TermResolverAttributeBo extends PersistableBusinessObjectBase implements TermResolverAttributeContract{

	def String id
	def String termResolverId
	def String attributeDefinitionId
	def String value
	
	def KrmsAttributeDefinition attributeDefinition
	
	
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static TermResolverAttribute to(TermResolverAttributeBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.TermResolverAttribute.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static TermResolverAttributeBo from(TermResolverAttribute im) {
	   if (im == null) { return null }

	   TermResolverAttributeBo bo = new TermResolverAttributeBo()
	   bo.id = im.id
	   bo.termResolverId = im.termResolverId
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