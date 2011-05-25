package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinitionContract;

public class KrmsAttributeDefinitionBo extends PersistableBusinessObjectBase implements KrmsAttributeDefinitionContract, Inactivateable{

	def String id
	def String name
	def String namespace
	def String label
	def boolean active
	def String componentName
		
	/**
	* Converts a mutable bo to it's immutable counterpart
	* @param bo the mutable business object
	* @return the immutable object
	*/
   static KrmsAttributeDefinition to(KrmsAttributeDefinitionBo bo) {
	   if (bo == null) { return null }
	   return org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition.Builder.create(bo).build()
   }

   /**
	* Converts a immutable object to it's mutable bo counterpart
	* @param im immutable object
	* @return the mutable bo
	*/
   static KrmsAttributeDefinitionBo from(KrmsAttributeDefinition im) {
	   if (im == null) { return null }

	   KrmsAttributeDefinitionBo bo = new KrmsAttributeDefinitionBo()
	   bo.id = im.id
	   bo.name = im.name
	   bo.namespace = im.namespace
	   bo.label = im.label
	   bo.active = im.active
	   bo.componentName = im.componentName
	   bo.versionNumber = im.versionNumber
	   return bo
   }
 
} 